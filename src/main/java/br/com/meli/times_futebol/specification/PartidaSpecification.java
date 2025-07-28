package br.com.meli.times_futebol.specification;

import br.com.meli.times_futebol.model.PartidaModel;
import org.springframework.data.jpa.domain.Specification;


public class PartidaSpecification {


    public static Specification<PartidaModel> porClube(Long clubeId) {
        return (root, query, cb) -> cb.or(
                cb.equal(root.get("clubeMandante").get("id"), clubeId),
                cb.equal(root.get("clubeVisitante").get("id"), clubeId));
    }

    public static Specification<PartidaModel> porClubeEGoleadas(Long clubeId) {
        return (root, query, cb) -> cb.or(
                // Goleada mandante
                cb.and(
                        cb.equal(root.get("clubeMandante").get("id"), clubeId),
                        cb.greaterThan(root.get("golsMandante"), root.get("golsVisitante")),
                        cb.greaterThan(
                                cb.diff(root.get("golsMandante"), root.get("golsVisitante")), 3
                        )
                ),
                // Goleada  visitante
                cb.and(
                        cb.equal(root.get("clubeVisitante").get("id"), clubeId),
                        cb.greaterThan(root.get("golsVisitante"), root.get("golsMandante")),
                        cb.greaterThan(
                                cb.diff(root.get("golsVisitante"), root.get("golsMandante")), 3
                        )
                )
        );
    }


}
