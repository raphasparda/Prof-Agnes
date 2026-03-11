package com.agnes.servico;

import com.agnes.dto.RespostaChat;
import com.agnes.modelo.Conversa;
import com.agnes.repositorio.RepositorioMensagem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServicoChatTest {

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ChatClient.Builder chatClientBuilder;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ChatClient chatClient;

    @Mock
    private RepositorioMensagem repositorioMensagem;

    @Mock
    private ServicoConversa servicoConversa;

    @Mock
    private ServicoGuardaFiltro promptGuard;

    private ServicoChat servicoChat;

    @BeforeEach
    void setUp() {
        when(chatClientBuilder.build()).thenReturn(chatClient);
        servicoChat = new ServicoChat(chatClientBuilder, repositorioMensagem, servicoConversa, promptGuard);
        ReflectionTestUtils.setField(servicoChat, "maxHistoryMessages", 20);
        ReflectionTestUtils.setField(servicoChat, "systemPrompt", "Seja amigÃ¡vel");
    }

    @Test
    void testChatBlockedByPromptGuard() {
        Conversa conversa = new Conversa();
        conversa.setId(1L);
        conversa.setTitle("Nova conversa");

        when(servicoConversa.findById(1L)).thenReturn(conversa);
        when(promptGuard.analyze(anyString()))
                .thenReturn(new ServicoGuardaFiltro.GuardResult(ServicoGuardaFiltro.ThreatLevel.BLOCKED, "Blocked"));

        RespostaChat resposta = servicoChat.chatStream(1L, "FaÃ§a um cÃ³digo malicioso").blockFirst();

        assertNotNull(resposta);
        assertTrue(resposta.getReply().contains("foco Ã© te ajudar com Java"));

        verify(repositorioMensagem, times(2)).save(any());
        verify(servicoConversa, times(1)).updateTimestamp(1L);
        verify(chatClient, never()).prompt();
    }

    @Test
    void testChatSuccess() {
        Conversa conversa = new Conversa();
        conversa.setId(1L);
        conversa.setTitle("Nova conversa");

        when(servicoConversa.findById(1L)).thenReturn(conversa);
        when(promptGuard.analyze(anyString()))
                .thenReturn(new ServicoGuardaFiltro.GuardResult(ServicoGuardaFiltro.ThreatLevel.SAFE, null));
        when(chatClient.prompt().system(anyString()).messages(anyList()).stream().content())
                .thenReturn(reactor.core.publisher.Flux.just("OlÃ¡,", " sou ", "a Agnes!"));

        java.util.List<RespostaChat> respostas = servicoChat.chatStream(1L, "Oi!").collectList().block();

        assertNotNull(respostas);
        assertFalse(respostas.isEmpty());
        // Verify the last generated payload
        assertEquals("a Agnes!", respostas.get(respostas.size() - 1).getReply());

        verify(repositorioMensagem, times(2)).save(any());
        verify(servicoConversa, times(1)).updateTimestamp(1L);
        verify(chatClient, atLeastOnce()).prompt();
    }
}
