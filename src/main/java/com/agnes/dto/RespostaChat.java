package com.agnes.dto;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonProperty;

public class RespostaChat {

    @JsonProperty("conversaId")
    private Long conversationId;
    @JsonProperty("resposta")
    private String reply;
    private LocalDateTime timestamp;

    public RespostaChat() {
    }

    public RespostaChat(Long conversationId, String reply, LocalDateTime timestamp) {
        this.conversationId = conversationId;
        this.reply = reply;
        this.timestamp = timestamp;
    }

    public Long getConversationId() {
        return conversationId;
    }

    public void setConversationId(Long conversationId) {
        this.conversationId = conversationId;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
