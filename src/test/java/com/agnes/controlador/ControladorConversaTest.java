package com.agnes.controlador;

import com.agnes.modelo.Conversa;
import com.agnes.servico.ServicoConversa;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ControladorConversa.class)
class ControladorConversaTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ServicoConversa servicoConversa;

    @Test
    void testListConversations() throws Exception {
        Conversa c = new Conversa();
        c.setId(1L);
        c.setTitle("Nova");

        Page<Conversa> page = new PageImpl<>(List.of(c));
        when(servicoConversa.listAll(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/conversas"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1));
    }

    @Test
    void testCreateConversation() throws Exception {
        Conversa c = new Conversa();
        c.setId(1L);
        c.setTitle("Nova conversa");

        when(servicoConversa.create(anyString())).thenReturn(c);

        mockMvc.perform(post("/api/conversas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("title", "Nova conversa"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void testUpdateTitle() throws Exception {
        doNothing().when(servicoConversa).updateTitle(anyLong(), anyString());

        mockMvc.perform(patch("/api/conversas/1/title")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("title", "Novo"))))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteConversation() throws Exception {
        doNothing().when(servicoConversa).delete(1L);

        mockMvc.perform(delete("/api/conversas/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetMessagesNotFound() throws Exception {
        when(servicoConversa.getMessages(anyLong(), any(Pageable.class)))
                .thenThrow(new IllegalArgumentException("Conversa nÃ£o encontrada: 999"));

        mockMvc.perform(get("/api/conversas/999/mensagens"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro").value("Conversa nÃ£o encontrada: 999"));
    }
}
