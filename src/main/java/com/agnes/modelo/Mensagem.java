package com.agnes.modelo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "mensagens", indexes = {
        @Index(name = "idx_message_conversation", columnList = "conversation_id")
})
public class Mensagem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id", nullable = false)
    private Conversa conversa;

    @JsonProperty("role")
    @Column(name = "role", nullable = false)
    private String remetente;

    @JsonProperty("content")
    @Column(name = "content", columnDefinition = "CLOB", nullable = false)
    private String conteudo;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    public Mensagem() {
    }

    public Mensagem(Conversa conversa, String remetente, String conteudo) {
        this.conversa = conversa;
        this.remetente = remetente;
        this.conteudo = conteudo;
        this.timestamp = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Conversa getConversa() {
        return conversa;
    }

    public void setConversa(Conversa conversa) {
        this.conversa = conversa;
    }

    public Long getConversaId() {
        return conversa != null ? conversa.getId() : null;
    }

    public String getRemetente() {
        return remetente;
    }

    public void setRemetente(String remetente) {
        this.remetente = remetente;
    }

    public String getConteudo() {
        return conteudo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
