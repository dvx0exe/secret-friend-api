package com.event.secret_friend.controllers;

import com.event.secret_friend.entities.Evento;
import com.event.secret_friend.entities.Participante;
import com.event.secret_friend.repositories.EventoRepository;
import com.event.secret_friend.repositories.ParticipanteRepository;
import org.springframework.web.bind.annotation.*;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

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
        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new RuntimeException("Evento não encontrado"));

        participante.setEvento(evento);
        return participanteRepository.save(participante);
    }

    @PostMapping("/entrar")
    public Participante entrarNoEvento(@RequestParam String codigo, @RequestBody Participante participante) {
        Evento evento = eventoRepository.findByCodigoConvite(codigo)
                .orElseThrow(() -> new RuntimeException("Código de convite inválido ou evento não encontrado."));

        participante.setEvento(evento);
        return participanteRepository.save(participante);
    }

    @GetMapping("/{codigoConvite}")
    public ResponseEntity<List<Participante>> listarPorEvento(@PathVariable String codigoConvite) {
        Optional<Evento> eventoOpt = eventoRepository.findByCodigoConvite(codigoConvite);

        if (eventoOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(eventoOpt.get().getParticipantes());
    }
}