package br.com.meli.times_futebol.controller;

import br.com.meli.times_futebol.exception.GenericException;
import br.com.meli.times_futebol.dto.ClubeRequestDto;
import br.com.meli.times_futebol.model.ClubeModel;
import br.com.meli.times_futebol.service.ClubeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("clube")
public class ClubeController {

    @Autowired // instancia (new) automaticamete a classe ClubeService -
    private ClubeService clubeService;


    @PostMapping
    public ResponseEntity<ClubeModel> cadastrarClube(@RequestBody @Valid ClubeRequestDto clubeRequestDto) {

        ClubeModel clubeModel = clubeService.criarTime(clubeRequestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(clubeModel);
    }

    @GetMapping
    public ResponseEntity<?> listarTodosClubes() {

        List<ClubeModel> clubeModelsList = clubeService.listarTodosTimes();

        if(clubeModelsList.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new GenericException("Nenhum clube encontrado"));
        }

        return ResponseEntity.status(HttpStatus.OK).body(clubeModelsList);

    }

    @GetMapping("/{idValor}")
    public ResponseEntity<?> listarClube(@PathVariable Long idValor) {

        ClubeModel clubeModel = clubeService.acharTime(idValor);

        return ResponseEntity.status(HttpStatus.OK).body(clubeModel);
    }

    @PutMapping ("/{idValor}")
    public ResponseEntity<?> atualizarClube(@PathVariable Long idValor,
                                            @RequestBody @Valid ClubeRequestDto  clubeRequestDto) {

        ClubeModel clubeModel = clubeService.acharTime(idValor);

        ClubeModel clubeModelAtlz = clubeService.atualizarTime(clubeModel, clubeRequestDto);

        return ResponseEntity.status(HttpStatus.OK).body("Clube: " + clubeModelAtlz.getId() + ": "
                                                                   + clubeModelAtlz.getNome() + " alterado com sucesso");

    }

    @DeleteMapping ("/{idValor}")
    public ResponseEntity<String> deleteClube(@PathVariable Long idValor) {

        ClubeModel clubeModel = clubeService.acharTime(idValor);

        clubeService.inativaTime(clubeModel);

        return ResponseEntity.status(HttpStatus.OK).body("clube inativado com sucesso: " + clubeModel.getNome());

    }
}
