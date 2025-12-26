package com.event.secret_friend.controllers;

import com.event.secret_friend.services.SorteioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sorteio")
@CrossOrigin("*")
public class SorteioController {

    private final SorteioService sorteioService;

    public SorteioController(SorteioService sorteioService) {
        this.sorteioService = sorteioService;
    }

    @PostMapping("/{eventoId}")
    public ResponseEntity<String> dispararSorteio(@PathVariable Long eventoId) {
        try {
            sorteioService.realizarSorteio(eventoId);
            return ResponseEntity.ok("Sorteio realizado e e-mails enviados com sucesso!");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erro ao realizar sorteio: " + e.getMessage());
        }
    }
}