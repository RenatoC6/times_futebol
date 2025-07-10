package br.com.meli.times_futebol.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "partida")
public class PartidaModel implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name= "gols_mandante")
    private Long golsMandante;
    @Column(name = "gols_visitante")
    private Long golsVisitante;
    @Column(name = "data_partida")
    private LocalDate dataPartida;

    @ManyToOne
    private ClubeModel clubeMandante;

    @ManyToOne
    private ClubeModel clubeVisitante;

    @ManyToOne
    private EstadioModel estadioPartida;

    // getter e setter


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getGolsMandante() {
        return golsMandante;
    }

    public void setGolsMandante(Long golsMandante) {
        this.golsMandante = golsMandante;
    }

    public Long getGolsVisitante() {
        return golsVisitante;
    }

    public void setGolsVisitante(Long golsVisitante) {
        this.golsVisitante = golsVisitante;
    }

    public LocalDate getDataPartida() {
        return dataPartida;
    }

    public void setDataPartida(LocalDate dataPartida) {
        this.dataPartida = dataPartida;
    }

    public ClubeModel getClubeMandante() {
        return clubeMandante;
    }

    public void setClubeMandante(ClubeModel clubeMandante) {
        this.clubeMandante = clubeMandante;
    }

    public ClubeModel getClubeVisitante() {
        return clubeVisitante;
    }

    public void setClubeVisitante(ClubeModel clubeVisitante) {
        this.clubeVisitante = clubeVisitante;
    }

    public EstadioModel getEstadioPartida() {
        return estadioPartida;
    }

    public void setEstadioPartida(EstadioModel estadioPartida) {
        this.estadioPartida = estadioPartida;
    }
}
