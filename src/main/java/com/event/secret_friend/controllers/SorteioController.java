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

    @PostMapping("/{codigoConvite}")
    public ResponseEntity<String> dispararSorteio(@PathVariable String codigoConvite) {
        try {
            sorteioService.realizarSorteio(codigoConvite);
            return ResponseEntity.ok("Sorteio realizado e e-mails enviados com sucesso!");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erro ao realizar sorteio: " + e.getMessage());
        }
    }
}