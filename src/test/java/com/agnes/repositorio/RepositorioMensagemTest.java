package com.agnes.repositorio;

import com.agnes.modelo.Conversa;
import com.agnes.modelo.Mensagem;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class RepositorioMensagemTest {

    @Autowired
    private RepositorioMensagem repositorioMensagem;

    @Autowired
    private RepositorioConversa repositorioConversa;

    @Test
    void shouldFindMessagesByConversationIdOrderByTimestampDesc() {
        // Given
        Conversa conversa = new Conversa("Test Chat");
        repositorioConversa.save(conversa);

        Mensagem m1 = new Mensagem(conversa, "user", "Hello");
        Mensagem m2 = new Mensagem(conversa, "assistant", "Hi");

        repositorioMensagem.saveAll(List.of(m1, m2));

        // When
        Page<Mensagem> msgPage = repositorioMensagem.findByConversa_IdOrderByTimestampAsc(
                conversa.getId(), PageRequest.of(0, 50));

        // Then
        assertThat(msgPage.getContent()).hasSize(2);
        assertThat(msgPage.getContent().get(0).getConteudo()).isEqualTo("Hello");
    }
}
