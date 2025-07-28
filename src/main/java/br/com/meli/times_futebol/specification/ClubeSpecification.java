package br.com.meli.times_futebol.specification;

import br.com.meli.times_futebol.model.ClubeModel;
import org.springframework.data.jpa.domain.Specification;

public class ClubeSpecification {

    public static Specification<ClubeModel> porNome(String nome) {
        return (root, query, cb)
                -> cb.equal(cb.upper(root.get("nome")), nome.toUpperCase());
    }

    public static Specification<ClubeModel> porEstado(String nome) {
        return (root, query, cb)
                -> cb.equal(cb.upper(root.get("estado")), nome.toUpperCase());
    }

    public static Specification<ClubeModel> porStatus(boolean status) {
        return (root, query, cb)
                -> cb.equal(root.get("status"), status);
    }
}
