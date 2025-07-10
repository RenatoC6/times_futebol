package br.com.meli.times_futebol.repository;

import br.com.meli.times_futebol.model.ClubeModel;
import br.com.meli.times_futebol.model.EstadioModel;
import br.com.meli.times_futebol.model.PartidaModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartidaRepository extends JpaRepository<PartidaModel, Long> {

    boolean existsByClubeMandante(ClubeModel clubeMandante);
    boolean existsByClubeVisitante(ClubeModel clubeVisitante);

    boolean existsByEstadioPartida(EstadioModel estadioPartida);
}
