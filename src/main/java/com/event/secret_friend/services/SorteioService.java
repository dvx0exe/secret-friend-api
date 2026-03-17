package com.event.secret_friend.services;

import com.event.secret_friend.entities.Evento;
import com.event.secret_friend.entities.Participante;
import com.event.secret_friend.repositories.EventoRepository;
import com.event.secret_friend.repositories.ParticipanteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(SorteioService.class);

    public SorteioService(EventoRepository eventoRepository, ParticipanteRepository participanteRepository,
                          GroqService groqService, EmailService emailService) {
        this.eventoRepository = eventoRepository;
        this.participanteRepository = participanteRepository;
        this.groqService = groqService;
        this.emailService = emailService;
    }

    @Transactional
    public void realizarSorteio(String codigoConvite) {
        log.info("Iniciando sorteio para o evento: {}", codigoConvite);

        Evento evento = eventoRepository.findByCodigoConvite(codigoConvite)
                .orElseThrow(() -> new RuntimeException("Evento não encontrado com o código: " + codigoConvite));

        List<Participante> participantes = evento.getParticipantes();

        if (participantes == null || participantes.size() < 2) {
            log.warn("Tentativa de sorteio com menos de 2 participantes no evento {}", codigoConvite);
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
        }

        participanteRepository.saveAll(participantes);
        log.info("Sorteio circular concluído com sucesso para o evento {}", codigoConvite);

        notificarParticipantes(participantes);
    }

    private void notificarParticipantes(List<Participante> participantes) {
        for (Participante atual : participantes) {
            try {
                Participante sorteado = atual.getAmigoSorteado();
                String sugestao = groqService.gerarSugestaoPresente(sorteado.getGostosPessoais());

                String msg = String.format("Olá %s! O sorteio foi realizado.\n\nVocê tirou: %s\n\nDica de presente baseada nos gostos (%s):\n%s",
                        atual.getNome(), sorteado.getNome(), sorteado.getGostosPessoais(), sugestao);

                log.info("Enviando resultado para o e-mail: {}", atual.getEmail());
                emailService.enviarEmailSimples(atual.getEmail(), "Resultado Amigo Secreto", msg);
            } catch (Exception e) {
                log.error("Erro ao notificar participante {}: {}", atual.getNome(), e.getMessage());
            }
        }
    }
}