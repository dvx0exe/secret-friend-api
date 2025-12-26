package com.event.secret_friend.services;

import com.event.secret_friend.entities.Event_Class;
import com.event.secret_friend.entities.Participante;
import com.event.secret_friend.repositories.EventoRepository;
import com.event.secret_friend.repositories.ParticipanteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Collections;
import java.util.List;

@Service
public class SorteioService {
    private final EventoRepository eventoRepository;
    private final ParticipanteRepository participanteRepository;
    private final GroqService groqService;
    private final EmailService emailService;

    public SorteioService(EventoRepository eventoRepository, ParticipanteRepository participanteRepository,
                          GroqService groqService, EmailService emailService) {
        this.eventoRepository = eventoRepository;
        this.participanteRepository = participanteRepository;
        this.groqService = groqService;
        this.emailService = emailService;
    }

    @Transactional
    public void realizarSorteio(Long eventoId) {
        Event_Class evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new RuntimeException("Evento não encontrado"));

        List<Participante> participantes = evento.getParticipantes();

        if (participantes == null || participantes.size() < 2) {
            throw new RuntimeException("Mínimo 2 participantes para o sorteio");
        }
        for (Participante p : participantes) {
            p.setAmigoSorteado(null);
        }
        participanteRepository.saveAll(participantes);
        participanteRepository.flush();
        Collections.shuffle(participantes);

        for (int i = 0; i < participantes.size(); i++) {
            Participante atual = participantes.get(i);
            Participante sorteado = (i == participantes.size() - 1) ? participantes.get(0) : participantes.get(i + 1);

            atual.setAmigoSorteado(sorteado);

            String sugestao = groqService.gerarSugestaoPresente(sorteado.getGostosPessoais());

            String msg = String.format("Olá %s! Tiraste %s. Sugestão IA: %s",
                    atual.getNome(), sorteado.getNome(), sugestao);
            System.out.println("DEBUG SORTEIO: " + msg);

            emailService.enviarEmailSimples(atual.getEmail(), "Resultado Amigo Secreto", msg);
        }
        participanteRepository.saveAll(participantes);
    }
}