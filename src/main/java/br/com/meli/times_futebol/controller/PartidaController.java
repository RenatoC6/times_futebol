package br.com.meli.times_futebol.controller;

import br.com.meli.times_futebol.dto.PartidaRequestDto;
import br.com.meli.times_futebol.model.PartidaModel;
import br.com.meli.times_futebol.service.PartidaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PutMapping("/{idPartida}")
    public ResponseEntity<PartidaModel> atualizarPartida(@PathVariable Long idPartida,
                                                         @RequestBody PartidaRequestDto partidaRequestDto) {

        PartidaModel partidaExistente = partidaService.acharPartida(idPartida);
        PartidaModel partidaAtualizada = partidaService.atualizarPartida(partidaExistente,  partidaRequestDto, idPartida);

        return ResponseEntity.status(HttpStatus.OK).body(partidaAtualizada);
    }

    @GetMapping("/{idPartida}")
    public ResponseEntity<PartidaModel> buscarPartida(@PathVariable Long idPartida) {

        PartidaModel partida = partidaService.acharPartida(idPartida);
        return ResponseEntity.status(HttpStatus.OK).body(partida);
    }
    @GetMapping
    public ResponseEntity<?> listarTodasPartidas(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "dataPartida,asc") String[] sort) {

        return ResponseEntity.status(HttpStatus.OK).body(partidaService.listarTodasPartidas(page, size, sort));
    }

}
