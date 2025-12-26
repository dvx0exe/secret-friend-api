package com.event.secret_friend.controllers;

import com.event.secret_friend.entities.Event_Class;
import com.event.secret_friend.entities.Participante;
import com.event.secret_friend.repositories.EventoRepository;
import com.event.secret_friend.repositories.ParticipanteRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/participantes")
@CrossOrigin("*")
public class ParticipanteController {

    private final ParticipanteRepository participanteRepository;
    private final EventoRepository eventoRepository;

    public ParticipanteController(ParticipanteRepository participanteRepository, EventoRepository eventoRepository) {
        this.participanteRepository = participanteRepository;
        this.eventoRepository = eventoRepository;
    }

    @PostMapping("/{eventoId}")
    public Participante cadastrarParticipante(@PathVariable Long eventoId, @RequestBody Participante participante) {
        Event_Class evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new RuntimeException("Evento não encontrado"));

        participante.setEvento(evento);
        return participanteRepository.save(participante);
    }
}