package com.agnes.controlador;

import com.agnes.dto.RequisicaoChat;
import com.agnes.dto.RespostaChat;
import com.agnes.servico.ServicoChat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class ControladorChat {

    private final ServicoChat servicoChat;

    public ControladorChat(ServicoChat servicoChat) {
        this.servicoChat = servicoChat;
    }

    @PostMapping(value = "/chat", produces = org.springframework.http.MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<RespostaChat> chat(@Valid @RequestBody RequisicaoChat request) {
        return servicoChat.chatStream(
                request.getConversaId(),
                request.getMensagem());
    }
}
