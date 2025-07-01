package br.com.meli.times_futebol.service;

import br.com.meli.times_futebol.dto.ClubeRequestDto;
import org.springframework.stereotype.Service;

@Service
public class ClubeService {
    public String criarTime(ClubeRequestDto clubeRequestDto) {

        return "clube " + clubeRequestDto.nome() + " cadastrado com sucesso";
    }
}
