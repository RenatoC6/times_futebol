package br.com.meli.times_futebol.repository;

import br.com.meli.times_futebol.model.ClubeModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClubeRepository extends JpaRepository<ClubeModel, Long> {

}
