package com.event.secret_friend.controllers;

import com.event.secret_friend.entities.Evento;
import com.event.secret_friend.entities.Participante;
import com.event.secret_friend.repositories.EventoRepository;
import com.event.secret_friend.repositories.ParticipanteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/participantes")
public class ParticipanteController {

    private final ParticipanteRepository participanteRepository;
    private final EventoRepository eventoRepository;

    public ParticipanteController(ParticipanteRepository participanteRepository, EventoRepository eventoRepository) {
        this.participanteRepository = participanteRepository;
        this.eventoRepository = eventoRepository;
    }

    @PostMapping("/entrar")
    public ResponseEntity<?> entrarNoEvento(@RequestParam String codigo,
                                            @RequestBody Participante dadosTela,
                                            @AuthenticationPrincipal OAuth2User principal) {

        Evento evento = eventoRepository.findByCodigoConvite(codigo)
                .orElseThrow(() -> new RuntimeException("Código inválido."));

        String emailGoogle = principal.getAttribute("email");

        boolean jaParticipa = evento.getParticipantes().stream()
                .anyMatch(p -> p.getEmail().equals(emailGoogle));

        if(jaParticipa) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Você já está neste evento.");
        }

        Participante participante = new Participante();

        if (dadosTela.getNome() != null && !dadosTela.getNome().isBlank()) {
            participante.setNome(dadosTela.getNome());
        } else {
            participante.setNome(principal.getAttribute("name"));
        }

        participante.setEmail(emailGoogle);
        participante.setGostosPessoais(dadosTela.getGostosPessoais());
        participante.setEvento(evento);

        participanteRepository.save(participante);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> removerParticipante(@PathVariable Long id, @AuthenticationPrincipal OAuth2User principal) {
        String emailLogado = principal.getAttribute("email");

        Participante participante = participanteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Participante não encontrado"));

        if (participante.getEvento().getEmail().equals(emailLogado)) {
            participanteRepository.delete(participante);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Apenas o organizador pode remover participantes.");
        }
    }

    @DeleteMapping("/sair/{codigo}")
    public ResponseEntity<?> sairDoEvento(@PathVariable String codigo, @AuthenticationPrincipal OAuth2User principal) {
        String email = principal.getAttribute("email");

        Evento evento = eventoRepository.findByCodigoConvite(codigo)
                .orElseThrow(() -> new RuntimeException("Evento não encontrado"));

        Participante p = evento.getParticipantes().stream()
                .filter(x -> x.getEmail().equals(email))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Você não participa deste evento"));

        participanteRepository.delete(p);
        return ResponseEntity.ok().build();
    }
}