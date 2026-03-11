package com.agnes.controlador;

import com.agnes.dto.RequisicaoChat;
import com.agnes.dto.RespostaChat;
import com.agnes.servico.ServicoChat;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ControladorChat.class)
class ControladorChatTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ServicoChat servicoChat;

    @Test
    void testChatEndpoint() throws Exception {
        RequisicaoChat requisicao = new RequisicaoChat(1L, "Oi");
        RespostaChat resposta = new RespostaChat(1L, "Tudo bem?", LocalDateTime.now());

        when(servicoChat.chatStream(anyLong(), anyString())).thenReturn(Flux.just(resposta));

        mockMvc.perform(post("/api/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requisicao)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resposta").value("Tudo bem?"));
    }
}
