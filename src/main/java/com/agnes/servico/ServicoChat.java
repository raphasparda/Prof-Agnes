package com.agnes.servico;

import com.agnes.dto.RespostaChat;
import com.agnes.modelo.Conversa;
import com.agnes.modelo.Mensagem;
import com.agnes.repositorio.RepositorioMensagem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class ServicoChat {

    private static final Logger log = LoggerFactory.getLogger(ServicoChat.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter
            .ofPattern("EEEE, d 'de' MMMM 'de' yyyy, HH:mm", new Locale("pt", "BR"));

    private final ChatClient chatClient;
    private final RepositorioMensagem repositorioMensagem;
    private final ServicoConversa servicoConversa;
    private final ServicoGuardaFiltro promptGuard;

    @Value("${agnes.system-prompt}")
    private String systemPrompt;

    @Value("${agnes.max-history-mensagens:20}")
    private int maxHistoryMessages;

    private static final String BLOCKED_RESPONSE = "Sou a Agnes e meu foco Ã© te ajudar com Java! â˜• Em que posso te ajudar?";

    public ServicoChat(ChatClient.Builder chatClientBuilder,
            RepositorioMensagem repositorioMensagem,
            ServicoConversa servicoConversa,
            ServicoGuardaFiltro promptGuard) {
        this.chatClient = chatClientBuilder.build();
        this.repositorioMensagem = repositorioMensagem;
        this.servicoConversa = servicoConversa;
        this.promptGuard = promptGuard;
    }

    private String buildSystemPromptWithDate() {
        String now = LocalDateTime.now().format(DATE_FORMATTER);
        return systemPrompt + "\n\n[DATA E HORA ATUAL: " + now + "]";
    }

    public Flux<RespostaChat> chatStream(Long conversationId, String userMessage) {
        // aqui valida se a conversa existe
        Conversa conversa = servicoConversa.findById(conversationId);

        // â”€â”€ Anti-Prompt Injection â”€â”€
        ServicoGuardaFiltro.GuardResult guardResult = promptGuard.analyze(userMessage);

        if (guardResult.isBlocked()) {
            Mensagem userMsg = new Mensagem(conversa, "user", userMessage);
            repositorioMensagem.save(userMsg);
            LocalDateTime now = LocalDateTime.now();
            Mensagem blockedReply = new Mensagem(conversa, "assistant", BLOCKED_RESPONSE);
            repositorioMensagem.save(blockedReply);
            servicoConversa.updateTimestamp(conversationId);
            autoTitleIfNeeded(conversationId, userMessage);
            return Flux.just(new RespostaChat(conversationId, BLOCKED_RESPONSE, now));
        }

        // 1. Persist user mensagem
        Mensagem userMsg = new Mensagem(conversa, "user", userMessage);
        repositorioMensagem.save(userMsg);

        // 2. Build conversa e historico
        List<org.springframework.ai.chat.messages.Message> aiMessages = buildMessageHistory(conversationId);

        StringBuffer fullReply = new StringBuffer();

        // 3. Call Gemini via Spring AI
        return chatClient.prompt()
                .system(buildSystemPromptWithDate())
                .messages(aiMessages)
                .stream()
                .content()
                .doOnNext(token -> fullReply.append(token))
                .doOnComplete(() -> {
                    // 4. persistencia da agnes
                    Mensagem assistantMsg = new Mensagem(conversa, "assistant", fullReply.toString());
                    repositorioMensagem.save(assistantMsg);
                    // 5. atual
                    servicoConversa.updateTimestamp(conversationId);
                    autoTitleIfNeeded(conversationId, userMessage);
                })
                .doOnError(e -> log.error("Erro ao chamar Gemini API: {}", e.getMessage(), e))
                .onErrorResume(e -> Flux.just(
                        "Poxa, tive um probleminha técnico aqui 😅 Tenta de novo daqui a pouquinho, por favor?"))
                .map(token -> new RespostaChat(conversationId, token, LocalDateTime.now()));
    }

    private List<org.springframework.ai.chat.messages.Message> buildMessageHistory(Long conversationId) {
        List<Mensagem> history = repositorioMensagem
                .findByConversa_IdOrderByTimestampAsc(conversationId);

        // Limit to last N mensagens to avoid token overflow
        int start = Math.max(0, history.size() - maxHistoryMessages);
        List<Mensagem> recentHistory = history.subList(start, history.size());

        List<org.springframework.ai.chat.messages.Message> aiMessages = new ArrayList<>();
        for (Mensagem msg : recentHistory) {
            if ("user".equals(msg.getRemetente())) {
                aiMessages.add(new UserMessage(msg.getConteudo()));
            } else if ("assistant".equals(msg.getRemetente())) {
                aiMessages.add(new AssistantMessage(msg.getConteudo()));
            } else {
                aiMessages.add(new SystemMessage(msg.getConteudo()));
            }
        }

        return aiMessages;
    }

    private void autoTitleIfNeeded(Long conversationId, String userMessage) {
        var conversa = servicoConversa.findById(conversationId);
        if ("Nova conversa".equals(conversa.getTitle())) {
            String title = userMessage.length() > 50
                    ? userMessage.substring(0, 47) + "..."
                    : userMessage;
            servicoConversa.updateTitle(conversationId, title);
        }
    }
}
