package com.event.secret_friend.controllers;

import com.event.secret_friend.entities.Evento;
import com.event.secret_friend.repositories.EventoRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/eventos")
@CrossOrigin("*")
public class EventoController {

    private final EventoRepository eventoRepository;

    public EventoController(EventoRepository eventoRepository) {
        this.eventoRepository = eventoRepository;
    }

    @PostMapping
    public Evento criarEvento(@RequestBody Evento evento) {
        return eventoRepository.save(evento);
    }
}