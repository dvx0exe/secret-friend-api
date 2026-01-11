package com.event.secret_friend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.event.secret_friend.entities.Evento;
import java.util.Optional;

public interface EventoRepository extends JpaRepository<Evento, Long> {
    Optional<Evento> findByCodigoConvite(String codigoConvite);
}