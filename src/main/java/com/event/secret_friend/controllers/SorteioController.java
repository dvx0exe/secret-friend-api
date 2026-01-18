package com.event.secret_friend.controllers;

import com.event.secret_friend.entities.Evento;
import com.event.secret_friend.repositories.EventoRepository;
import com.event.secret_friend.services.SorteioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sorteio")
// @CrossOrigin removido intencionalmente
public class SorteioController {

    private final SorteioService sorteioService;
    private final EventoRepository eventoRepository;

    public SorteioController(SorteioService sorteioService, EventoRepository eventoRepository) {
        this.sorteioService = sorteioService;
        this.eventoRepository = eventoRepository;
    }

    @PostMapping("/{codigoConvite}")
    public ResponseEntity<String> dispararSorteio(@PathVariable String codigoConvite,
                                                  @AuthenticationPrincipal OAuth2User principal) {

        Evento evento = eventoRepository.findByCodigoConvite(codigoConvite)
                .orElseThrow(() -> new RuntimeException("Evento não encontrado"));

        String emailLogado = principal.getAttribute("email");
        if (!evento.getEmail().equals(emailLogado)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Apenas o organizador pode realizar o sorteio.");
        }

        try {
            sorteioService.realizarSorteio(codigoConvite);
            return ResponseEntity.ok("Sorteio iniciado! Os participantes receberão e-mails em breve.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erro: " + e.getMessage());
        }
    }
}