package br.com.meli.times_futebol.repository;

import br.com.meli.times_futebol.model.EstadioModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstadioRepository extends JpaRepository<EstadioModel, Long> {

    boolean existsByNomeEstadioIgnoreCase(String nomeEstadio);
}
