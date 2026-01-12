package com.event.secret_friend.controllers;

import com.event.secret_friend.entities.Evento;
import com.event.secret_friend.repositories.EventoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

record LoginRequest(String codigo, String senha) {}

@RestController
@RequestMapping("/api/eventos")
@CrossOrigin(origins = "*")
public class EventoController {

    @Autowired
    private EventoRepository repository;

    @PostMapping
    public ResponseEntity<Evento> criarEvento(@RequestBody Evento novoEvento) {
        Evento eventoSalvo = repository.save(novoEvento);
        return ResponseEntity.ok(eventoSalvo);
    }


    @PostMapping("/admin-login")
    public ResponseEntity<?> acessarPainel(@RequestBody LoginRequest login) {
        Optional<Evento> eventoOpt = repository.findByCodigoConvite(login.codigo());

        if (eventoOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Evento não encontrado.");
        }

        Evento evento = eventoOpt.get();

        if (evento.getSenha() != null && evento.getSenha().equals(login.senha())) {
            return ResponseEntity.ok(evento.getParticipantes());
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Senha incorreta!");
        }
    }
}