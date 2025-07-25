package br.com.meli.times_futebol.specification;

import br.com.meli.times_futebol.model.PartidaModel;
import org.springframework.data.jpa.domain.Specification;


public class PartidaSpecification {


    public static Specification<PartidaModel> porClube(Long clubeId) {
        return (root, query, cb) -> cb.or(
                cb.equal(root.get("clubeMandante").get("id"), clubeId),
                cb.equal(root.get("clubeVisitante").get("id"), clubeId));
    }


}
