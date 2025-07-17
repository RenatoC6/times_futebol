package br.com.meli.times_futebol.repository;

import br.com.meli.times_futebol.model.ClubeModel;
import br.com.meli.times_futebol.model.EstadioModel;
import br.com.meli.times_futebol.model.PartidaModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PartidaRepository extends JpaRepository<PartidaModel, Long> {

    boolean existsByEstadioPartidaAndDataPartida(EstadioModel estadioPartida, LocalDateTime date);
    boolean existsByClubeMandanteAndClubeVisitanteAndEstadioPartida(ClubeModel mandante, ClubeModel visitante, EstadioModel estadio);
    boolean existsByClubeMandante_AndDataPartidaBetween(ClubeModel mandate, LocalDateTime inicio, LocalDateTime fim);
    boolean existsByClubeVisitante_AndDataPartidaBetween(ClubeModel visitante, LocalDateTime inicio, LocalDateTime fim);

    List<PartidaModel> findByClubeMandanteOrClubeVisitante(ClubeModel clubeMandante, ClubeModel clubeVisitante);

    List<PartidaModel> findByClubeMandanteAndClubeVisitante(ClubeModel clubeMandante, ClubeModel clubeVisitante);

//    @Query("SELECT p FROM PartidaModel p WHERE " +
//            "(p.clubeMandante.id = :clube1 AND p.clubeVisitante.id = :clube2) " +
//            "OR " +
//            "(p.clubeMandante.id = :clube2 AND p.clubeVisitante.id = :clube1)")
//    List<PartidaModel> findPartidaEntreClubes(@Param("clube1") Long clube1, @Param("clube2") Long clube2);
}

