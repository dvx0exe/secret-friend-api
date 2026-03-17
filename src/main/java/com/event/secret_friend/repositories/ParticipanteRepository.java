package com.event.secret_friend.repositories;

import com.event.secret_friend.entities.Participante;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ParticipanteRepository extends JpaRepository<Participante, Long> {
    List<Participante> findByEmail(String email);
    List<Participante> findByAmigoSorteado(Participante p);
}