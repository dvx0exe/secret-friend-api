package com.event.secret_friend.controllers;

import com.event.secret_friend.entities.Evento;
import com.event.secret_friend.entities.Participante;
import com.event.secret_friend.repositories.EventoRepository;
import com.event.secret_friend.repositories.ParticipanteRepository;
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
    public ResponseEntity<Evento> criarEvento(@RequestBody Evento novoEvento,
                                              @AuthenticationPrincipal OAuth2User principal) {
        String emailOrganizador = principal.getAttribute("email");
        novoEvento.setEmail(emailOrganizador);
        return ResponseEntity.ok(repository.save(novoEvento));
    }

    @GetMapping("/meus-eventos")
    public ResponseEntity<List<Evento>> listarMeusEventos(@AuthenticationPrincipal OAuth2User principal) {
        String email = principal.getAttribute("email");
        return ResponseEntity.ok(repository.findByEmail(email));
    }

    @GetMapping("/participando")
    public ResponseEntity<List<Evento>> listarEventosParticipando(@AuthenticationPrincipal OAuth2User principal) {
        String email = principal.getAttribute("email");
        List<Participante> participacoes = participanteRepository.findByEmail(email);

        List<Evento> eventos = participacoes.stream()
                .map(Participante::getEvento)
                .toList();

        return ResponseEntity.ok(eventos);
    }
}