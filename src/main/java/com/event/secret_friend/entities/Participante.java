package com.event.secret_friend.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
public class Participante {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String email;
    private String gostosPessoais;

    @ManyToOne
    @JoinColumn(name = "evento_id")
    @JsonBackReference
    private Evento evento;


    @ManyToOne
    @JoinColumn(name = "amigo_sorteado_id")
    @JsonIgnore
    private Participante amigoSorteado;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getGostosPessoais() { return gostosPessoais; }
    public void setGostosPessoais(String gostosPessoais) { this.gostosPessoais = gostosPessoais; }
    public Evento getEvento() { return evento; }
    public void setEvento(Evento evento) { this.evento = evento; }
    public Participante getAmigoSorteado() { return amigoSorteado; }
    public void setAmigoSorteado(Participante amigoSorteado) { this.amigoSorteado = amigoSorteado; }
}