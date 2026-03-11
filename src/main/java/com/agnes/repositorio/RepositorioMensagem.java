package com.agnes.repositorio;

import com.agnes.modelo.Mensagem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepositorioMensagem extends JpaRepository<Mensagem, Long> {

    List<Mensagem> findByConversa_IdOrderByTimestampAsc(Long conversationId);

    Page<Mensagem> findByConversa_IdOrderByTimestampAsc(Long conversationId, Pageable pageable);

    void deleteByConversa_Id(Long conversationId);
}
