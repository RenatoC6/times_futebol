package br.com.meli.times_futebol.validator.clube;

import br.com.meli.times_futebol.dto.ClubeRequestDto;
import br.com.meli.times_futebol.exception.GenericException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;


public class ValidaDataCriacaoTest {

    private final ValidaDataCriacao validaDataCriacao = new ValidaDataCriacao();

    @Test
    public void testValidarDataCriacaoFutura() {
        ClubeRequestDto dto = new ClubeRequestDto("Clube Teste", "SP", LocalDate.now().plusDays(1), true);
        try {
            validaDataCriacao.validar(dto,null);
            assert false : "Deveria ter lançado uma exceção";
        } catch (GenericException e) {
            assert e.getMessage().equals("data de criacao invalido ou no futuro");
        }
    }
}
