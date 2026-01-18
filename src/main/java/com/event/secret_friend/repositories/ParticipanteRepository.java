package com.event.secret_friend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.event.secret_friend.entities.Participante;
import java.util.List;

public interface ParticipanteRepository extends JpaRepository<Participante, Long> {
    List<Participante> findByEventoId(Long eventoId);
    List<Participante> findByEmail(String email);
}