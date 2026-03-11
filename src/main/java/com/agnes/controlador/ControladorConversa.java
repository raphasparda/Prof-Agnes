package com.agnes.controlador;

import com.agnes.modelo.Conversa;
import com.agnes.modelo.Mensagem;
import com.agnes.servico.ServicoConversa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/conversas")
public class ControladorConversa {

    private final ServicoConversa servicoConversa;

    public ControladorConversa(ServicoConversa servicoConversa) {
        this.servicoConversa = servicoConversa;
    }

    @GetMapping
    public ResponseEntity<Page<Conversa>> listConversations(
            @PageableDefault(size = 50, sort = "updatedAt", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(servicoConversa.listAll(pageable));
    }

    @PostMapping
    public ResponseEntity<Conversa> createConversation(@RequestBody(required = false) Map<String, String> body) {
        String title = (body != null) ? body.get("title") : null;
        return ResponseEntity.ok(servicoConversa.create(title));
    }

    @GetMapping("/{id}/mensagens")
    public ResponseEntity<Page<Mensagem>> getMessages(
            @PathVariable Long id,
            @PageableDefault(size = 100, sort = "timestamp", direction = org.springframework.data.domain.Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(servicoConversa.getMessages(id, pageable));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteConversation(@PathVariable Long id) {
        servicoConversa.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/title")
    public ResponseEntity<Conversa> renameConversation(@PathVariable Long id,
            @RequestBody Map<String, String> body) {
        String title = body.get("title");
        if (title == null || title.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        servicoConversa.updateTitle(id, title.trim());
        return ResponseEntity.ok(servicoConversa.findById(id));
    }
}

