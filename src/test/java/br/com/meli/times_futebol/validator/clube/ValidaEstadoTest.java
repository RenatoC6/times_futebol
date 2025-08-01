package br.com.meli.times_futebol.validator.clube;


import br.com.meli.times_futebol.dto.ClubeRequestDto;
import br.com.meli.times_futebol.exception.GenericException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

public class ValidaEstadoTest {

    private final ValidaEstado validaEstado = new ValidaEstado();

    @Test
    void testValidarEstadoInvalido() {
        ClubeRequestDto dto = new ClubeRequestDto("Clube Teste", "XX", LocalDate.now(), true);
        try {
            validaEstado.validar(dto,null);
            assert false : "Deveria ter lançado uma exceção";
        } catch (GenericException e) {
            assert e.getMessage().equals("Estado: " + dto.estado() + " invalido");
        }
    }


}
