package com.event.secret_friend.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
public class Evento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nomeEvento;
    private String email;
    private LocalDateTime dataSorteio;

    @Column(unique = true, nullable = false, length = 10)
    private String codigoConvite;

    @OneToMany(mappedBy = "evento", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Participante> participantes;

    @PrePersist
    public void gerarCodigoAutomatico() {
        if (this.codigoConvite == null) {
            this.codigoConvite = UUID.randomUUID().toString()
                    .replace("-", "")
                    .substring(0, 6)
                    .toUpperCase();
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNomeEvento() { return nomeEvento; }
    public void setNomeEvento(String nomeEvento) { this.nomeEvento = nomeEvento; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public LocalDateTime getDataSorteio() { return dataSorteio; }
    public void setDataSorteio(LocalDateTime dataSorteio) { this.dataSorteio = dataSorteio; }

    public String getCodigoConvite() { return codigoConvite; }
    public void setCodigoConvite(String codigoConvite) { this.codigoConvite = codigoConvite; }

    public List<Participante> getParticipantes() { return participantes; }
    public void setParticipantes(List<Participante> participantes) { this.participantes = participantes; }
}