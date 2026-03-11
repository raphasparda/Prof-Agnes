package com.agnes.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RequisicaoChat {

    @NotNull(message = "O ID da conversa Ã© obrigatÃ³rio")
    @JsonProperty("conversaId")
    private Long conversaId;

    @NotBlank(message = "A mensagem nÃ£o pode estar vazia")
    @JsonProperty("mensagem")
    private String mensagem;

    public RequisicaoChat() {
    }

    public RequisicaoChat(Long conversaId, String mensagem) {
        this.conversaId = conversaId;
        this.mensagem = mensagem;
    }

    public Long getConversaId() {
        return conversaId;
    }

    public void setConversaId(Long conversaId) {
        this.conversaId = conversaId;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }
}
