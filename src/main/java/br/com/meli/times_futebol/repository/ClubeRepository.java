package br.com.meli.times_futebol.repository;

import br.com.meli.times_futebol.model.ClubeModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClubeRepository extends JpaRepository<ClubeModel, Long>{

    // aqui vale um comentario basico de um iniciante:
    // se o metodo abaixo esta dentro de uma interface, por que náo necessita implementacao
    //    Como isso funciona?
    //    O Spring Data JPA faz "mágica" por trás dos panos:
    //    Ele gera a implementação automaticamente em runtime com base no nome do metodo
    //    O nome existsByNomeIgnoreCase segue um padrão reconhecido pelo Spring (chamamos de "query method name").
    //    existsBy... diz ao Spring que deve gerar uma consulta que checa se existe algum registro.

    // ENTAO ESSE NOME DE METODO NAO PODE SER ALTERADO SENAO DA ERRO DE EXECUCAO:
    boolean existsByNomeIgnoreCase(String nome);

    Page<ClubeModel> findAll(Specification<ClubeModel> specs, Pageable pageable);
}
