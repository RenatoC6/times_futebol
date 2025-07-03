package br.com.meli.times_futebol.model;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "estadio")
public class EstadioModel implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nomeEstadio;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomeEstadio() {
        return nomeEstadio;
    }

    public void setNomeEstadio(String nomeEstadio) {
        this.nomeEstadio = nomeEstadio;
    }

    @Override
    public String toString() {
        return "Estadio: " +
                "id: " + id +
                ", nome do Estadio: " + nomeEstadio ;

    }
}
