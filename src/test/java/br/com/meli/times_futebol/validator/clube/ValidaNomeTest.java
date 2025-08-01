package br.com.meli.times_futebol.validator.clube;

import br.com.meli.times_futebol.dto.ClubeRequestDto;
import br.com.meli.times_futebol.exception.GenericException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

public class ValidaNomeTest {

    private final ValidaNome validaNome = new ValidaNome();

    @Test
    public void testValidarNomeComMenosDeTresCaracteres() {
        ClubeRequestDto dto = new ClubeRequestDto("xx", "SP", LocalDate.now(), true);
        try {
            validaNome.validar(dto, null);
            assert false : "Deveria ter lançado uma exceção";
        } catch (GenericException e) {
            assert e.getMessage().equals("nome deve ter no minimo 3 caracteres");
        }
    }
}
