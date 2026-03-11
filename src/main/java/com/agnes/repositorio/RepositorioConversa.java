package com.agnes.repositorio;

import com.agnes.modelo.Conversa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepositorioConversa extends JpaRepository<Conversa, Long> {

    Page<Conversa> findAllByOrderByUpdatedAtDesc(Pageable pageable);
}

