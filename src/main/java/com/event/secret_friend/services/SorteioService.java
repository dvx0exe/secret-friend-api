package com.event.secret_friend.services;

import com.event.secret_friend.entities.Evento;
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
    public void realizarSorteio(String codigoConvite) {
        Evento evento = eventoRepository.findByCodigoConvite(codigoConvite)
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
        }
        participanteRepository.saveAll(participantes);

        new Thread(() -> notificarParticipantes(participantes)).start();
    }

    private void notificarParticipantes(List<Participante> participantes) {
        for (Participante atual : participantes) {
            try {
                Participante sorteado = atual.getAmigoSorteado();
                String sugestao = groqService.gerarSugestaoPresente(sorteado.getGostosPessoais());

                String msg = String.format("""
                        Olá %s! 
                        
                        O sorteio foi realizado!
                        
                        🎁 Você tirou: %s
                        
                        ℹ️ Gostos dele(a): %s
                        
                        🤖 Dica da IA:
                        %s
                        """,
                        atual.getNome(), sorteado.getNome(), sorteado.getGostosPessoais(), sugestao);

                emailService.enviarEmailSimples(atual.getEmail(), "Resultado Amigo Secreto", msg);
                System.out.println("Email enviado para " + atual.getEmail());
                Thread.sleep(1000);
            } catch (Exception e) {
                System.err.println("Erro notificando " + atual.getNome() + ": " + e.getMessage());
            }
        }
    }
}