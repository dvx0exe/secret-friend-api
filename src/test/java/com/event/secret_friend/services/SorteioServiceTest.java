package com.event.secret_friend.services;

import com.event.secret_friend.entities.Evento;
import com.event.secret_friend.entities.Participante;
import com.event.secret_friend.repositories.EventoRepository;
import com.event.secret_friend.repositories.ParticipanteRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SorteioServiceTest {

    @Mock
    private EventoRepository eventoRepository;

    @Mock
    private ParticipanteRepository participanteRepository;

    @Mock
    private GroqService groqService;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private SorteioService sorteioService;

    @Test
    @DisplayName("Não deve permitir sorteio com menos de 2 participantes")
    void deveLancarExcecaoComPoucosParticipantes() {
        Evento evento = new Evento();
        evento.setCodigoConvite("SOZINHO");
        evento.setParticipantes(List.of(new Participante()));

        when(eventoRepository.findByCodigoConvite("SOZINHO")).thenReturn(Optional.of(evento));

        assertThrows(RuntimeException.class, () -> {
            sorteioService.realizarSorteio("SOZINHO");
        });
    }

    @Test
    @DisplayName("Deve realizar o sorteio circular corretamente e salvar no banco")
    void deveRealizarSorteioComSucesso() {
        Evento evento = new Evento();
        evento.setCodigoConvite("FESTA123");

        Participante p1 = new Participante();
        p1.setNome("Dvx");
        p1.setEmail("dvx@email.com");
        p1.setGostosPessoais("Carros JDM");

        Participante p2 = new Participante();
        p2.setNome("Nanda");
        p2.setEmail("nanda@email.com");
        p2.setGostosPessoais("Livros");

        evento.setParticipantes(new ArrayList<>(List.of(p1, p2)));

        when(eventoRepository.findByCodigoConvite("FESTA123")).thenReturn(Optional.of(evento));
        when(groqService.gerarSugestaoPresente(anyString())).thenReturn("Sugestão da IA");

        sorteioService.realizarSorteio("FESTA123");

        assertNotNull(p1.getAmigoSorteado());
        assertNotNull(p2.getAmigoSorteado());
        assertNotEquals(p1, p1.getAmigoSorteado());

        verify(participanteRepository, atLeastOnce()).saveAll(anyList());
        verify(emailService, times(2)).enviarEmailSimples(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("O sorteio deve continuar mesmo se a IA (Groq) falhar para um participante")
    void deveContinuarSorteioSeIaFalhar() {
        Evento evento = new Evento();
        evento.setCodigoConvite("RESILIENCIA");

        Participante p1 = new Participante();
        p1.setNome("P1"); p1.setEmail("p1@test.com"); p1.setGostosPessoais("Valorant");

        Participante p2 = new Participante();
        p2.setNome("P2"); p2.setEmail("p2@test.com"); p2.setGostosPessoais("JDM Cars");

        evento.setParticipantes(new ArrayList<>(List.of(p1, p2)));

        when(eventoRepository.findByCodigoConvite("RESILIENCIA")).thenReturn(Optional.of(evento));

        // Simula falha na primeira chamada e sucesso na segunda para testar o isolamento do erro
        when(groqService.gerarSugestaoPresente(any()))
                .thenThrow(new RuntimeException("Erro na API da Groq"))
                .thenReturn("Dica de reserva");

        sorteioService.realizarSorteio("RESILIENCIA");

        // Verifica se o e-mail foi enviado mesmo com a falha pontual da IA
        verify(emailService, atLeastOnce()).enviarEmailSimples(anyString(), anyString(), anyString());
    }
}