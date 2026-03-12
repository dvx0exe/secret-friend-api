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
@RequestMapping("/api/participantes")
public class ParticipanteController {

    private final ParticipanteRepository participanteRepository;
    private final EventoRepository eventoRepository;

    public ParticipanteController(ParticipanteRepository participanteRepository, EventoRepository eventoRepository) {
        this.participanteRepository = participanteRepository;
        this.eventoRepository = eventoRepository;
    }

    @PostMapping("/entrar")
    public ResponseEntity<?> entrarNoEvento(@RequestParam String codigo, @RequestBody Participante dadosTela, @AuthenticationPrincipal OAuth2User principal) {
        Evento evento = eventoRepository.findByCodigoConvite(codigo).orElseThrow(() -> new RuntimeException("Código inválido."));
        String emailGoogle = principal.getAttribute("email");

        if (evento.getParticipantes().stream().anyMatch(p -> p.getEmail().equals(emailGoogle))) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Você já está neste evento.");
        }

        Participante participante = new Participante();
        participante.setNome(dadosTela.getNome() != null && !dadosTela.getNome().isBlank() ? dadosTela.getNome() : principal.getAttribute("name"));
        participante.setEmail(emailGoogle);
        participante.setGostosPessoais(dadosTela.getGostosPessoais());
        participante.setEvento(evento);

        participanteRepository.save(participante);
        return ResponseEntity.ok().build();
    }

    @Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity<?> removerParticipante(@PathVariable Long id, @AuthenticationPrincipal OAuth2User principal) {
        Participante p = participanteRepository.findById(id).orElseThrow(() -> new RuntimeException("Não encontrado"));

        if (p.getEvento().getEmail().equals(principal.getAttribute("email"))) {
            prepararRemocao(p);
            participanteRepository.delete(p);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @Transactional
    @DeleteMapping("/sair/{codigo}")
    public ResponseEntity<?> sairDoEvento(@PathVariable String codigo, @AuthenticationPrincipal OAuth2User principal) {
        String email = principal.getAttribute("email");
        Evento evento = eventoRepository.findByCodigoConvite(codigo).orElseThrow(() -> new RuntimeException("Evento não encontrado"));
        Participante p = evento.getParticipantes().stream().filter(x -> x.getEmail().equals(email)).findFirst().orElseThrow(() -> new RuntimeException("Não participa"));

        prepararRemocao(p);
        participanteRepository.delete(p);
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