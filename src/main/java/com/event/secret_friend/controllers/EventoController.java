package com.event.secret_friend.controllers;

import com.event.secret_friend.entities.Evento;
import com.event.secret_friend.entities.Participante;
import com.event.secret_friend.repositories.EventoRepository;
import com.event.secret_friend.repositories.ParticipanteRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/eventos")
public class EventoController {

    private final EventoRepository repository;
    private final ParticipanteRepository participanteRepository;

    public EventoController(EventoRepository repository, ParticipanteRepository participanteRepository) {
        this.repository = repository;
        this.participanteRepository = participanteRepository;
    }

    @PostMapping
    public ResponseEntity<Evento> criarEvento(@RequestBody Evento novoEvento, @AuthenticationPrincipal OAuth2User principal) {
        novoEvento.setEmail(principal.getAttribute("email"));
        return ResponseEntity.ok(repository.save(novoEvento));
    }

    @GetMapping("/meus-eventos")
    public ResponseEntity<List<Evento>> listarMeusEventos(@AuthenticationPrincipal OAuth2User principal) {
        return ResponseEntity.ok(repository.findByEmail(principal.getAttribute("email")));
    }

    @GetMapping("/participando")
    public ResponseEntity<List<Evento>> listarEventosParticipando(@AuthenticationPrincipal OAuth2User principal) {
        String email = principal.getAttribute("email");
        List<Participante> participacoes = participanteRepository.findByEmail(email);
        List<Evento> eventos = participacoes.stream().map(Participante::getEvento).toList();
        return ResponseEntity.ok(eventos);
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<?> excluirEvento(@PathVariable Long id, @AuthenticationPrincipal OAuth2User principal) {
        String emailLogado = principal.getAttribute("email");
        Evento evento = repository.findById(id).orElseThrow(() -> new RuntimeException("Evento não encontrado"));

        if (!evento.getEmail().equals(emailLogado)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<Participante> participantes = evento.getParticipantes();
        for (Participante p : participantes) {
            prepararRemocao(p);
        }

        participanteRepository.deleteAll(participantes);
        repository.delete(evento);
        return ResponseEntity.ok().build();
    }

    private void prepararRemocao(Participante p) {
        List<Participante> quemOTirou = participanteRepository.findByAmigoSorteado(p);
        for (Participante q : quemOTirou) {
            q.setAmigoSorteado(null);
        }
        participanteRepository.saveAll(quemOTirou);
        p.setAmigoSorteado(null);
        participanteRepository.save(p);
    }
}