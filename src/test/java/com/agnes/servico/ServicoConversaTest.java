package com.agnes.servico;

import com.agnes.modelo.Conversa;
import com.agnes.repositorio.RepositorioConversa;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServicoConversaTest {

    @Mock
    private RepositorioConversa repositorioConversa;

    @InjectMocks
    private ServicoConversa servicoConversa;

    private Conversa conversa;

    @BeforeEach
    void setUp() {
        conversa = new Conversa();
        conversa.setId(1L);
        conversa.setTitle("Nova conversa");
        conversa.setCreatedAt(LocalDateTime.now());
        conversa.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void testCreateConversa() {
        when(repositorioConversa.save(any(Conversa.class))).thenReturn(conversa);

        Conversa created = servicoConversa.create("Nova conversa");
        assertNotNull(created);
        assertEquals("Nova conversa", created.getTitle());
        verify(repositorioConversa, times(1)).save(any(Conversa.class));
    }

    @Test
    void testGetConversations() {
        Page<Conversa> page = new PageImpl<>(List.of(conversa));
        when(repositorioConversa.findAllByOrderByUpdatedAtDesc(any(PageRequest.class))).thenReturn(page);

        Page<Conversa> result = servicoConversa.listAll(PageRequest.of(0, 10));
        assertEquals(1, result.getTotalElements());
        verify(repositorioConversa, times(1)).findAllByOrderByUpdatedAtDesc(any(PageRequest.class));
    }

    @Test
    void testFindByIdSuccess() {
        when(repositorioConversa.findById(1L)).thenReturn(Optional.of(conversa));
        Conversa found = servicoConversa.findById(1L);
        assertEquals(1L, found.getId());
    }

    @Test
    void testFindByIdNotFound() {
        when(repositorioConversa.findById(2L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> servicoConversa.findById(2L));
    }

    @Test
    void testUpdateTitle() {
        when(repositorioConversa.findById(1L)).thenReturn(Optional.of(conversa));
        when(repositorioConversa.save(any(Conversa.class))).thenReturn(conversa);

        servicoConversa.updateTitle(1L, "Novo Titulo");
        assertEquals("Novo Titulo", conversa.getTitle());
    }

    @Test
    void testDelete() {
        when(repositorioConversa.findById(1L)).thenReturn(Optional.of(conversa));
        doNothing().when(repositorioConversa).delete(conversa);
        servicoConversa.delete(1L);
        verify(repositorioConversa, times(1)).delete(conversa);
    }
}
