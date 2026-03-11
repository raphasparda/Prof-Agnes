package com.agnes.servico;

import com.agnes.modelo.Conversa;
import com.agnes.modelo.Mensagem;
import com.agnes.repositorio.RepositorioConversa;
import com.agnes.repositorio.RepositorioMensagem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class ServicoConversa {

    private final RepositorioConversa repositorioConversa;
    private final RepositorioMensagem repositorioMensagem;

    public ServicoConversa(RepositorioConversa repositorioConversa,
            RepositorioMensagem repositorioMensagem) {
        this.repositorioConversa = repositorioConversa;
        this.repositorioMensagem = repositorioMensagem;
    }

    public Page<Conversa> listAll(Pageable pageable) {
        return repositorioConversa.findAllByOrderByUpdatedAtDesc(pageable);
    }

    public Conversa create(String title) {
        if (title == null || title.isBlank()) {
            title = "Nova conversa";
        }
        return repositorioConversa.save(new Conversa(title));
    }

    public Conversa findById(Long id) {
        return repositorioConversa.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Conversa nÃ£o encontrada: " + id));
    }

    @Transactional
    public void delete(Long id) {
        Conversa conversa = findById(id);
        repositorioConversa.delete(conversa); // cascade removes mensagens
    }

    @Transactional
    public void updateTimestamp(Long conversationId) {
        Conversa conversa = findById(conversationId);
        conversa.setUpdatedAt(LocalDateTime.now());
        repositorioConversa.save(conversa);
    }

    @Transactional
    public void updateTitle(Long conversationId, String title) {
        Conversa conversa = findById(conversationId);
        conversa.setTitle(title);
        repositorioConversa.save(conversa);
    }

    public Page<Mensagem> getMessages(Long conversationId, Pageable pageable) {
        findById(conversationId);
        return repositorioMensagem.findByConversa_IdOrderByTimestampAsc(conversationId, pageable);
    }
}
