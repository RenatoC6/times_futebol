package br.com.meli.times_futebol.controller;

import br.com.meli.times_futebol.dto.PartidaRequestDto;
import br.com.meli.times_futebol.model.PartidaModel;
import br.com.meli.times_futebol.repository.PartidaRepository;
import br.com.meli.times_futebol.service.PartidaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("partida")
public class PartidaController {

    @Autowired
    private PartidaService partidaService;

     @PostMapping
     public ResponseEntity<PartidaModel> criarPartida(@RequestBody PartidaRequestDto partidaRequestDto) {
         PartidaModel novaPartida = partidaService.criarPartida(partidaRequestDto);
         return ResponseEntity.status(HttpStatus.CREATED).body(novaPartida);


    }
}
