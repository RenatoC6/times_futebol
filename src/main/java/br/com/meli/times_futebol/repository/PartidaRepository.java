package br.com.meli.times_futebol.repository;

import br.com.meli.times_futebol.model.ClubeModel;
import br.com.meli.times_futebol.model.EstadioModel;
import br.com.meli.times_futebol.model.PartidaModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface PartidaRepository extends JpaRepository<PartidaModel, Long> {

    boolean existsByEstadioPartidaAndDataPartida(EstadioModel estadioPartida, LocalDate date);
    boolean existsByClubeMandanteAndClubeVisitanteAndEstadioPartida(ClubeModel mandante, ClubeModel visitante, EstadioModel estadio);



}
