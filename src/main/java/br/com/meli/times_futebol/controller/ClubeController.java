package br.com.meli.times_futebol.controller;

import br.com.meli.times_futebol.dto.ClubeRequestDto;
import br.com.meli.times_futebol.model.ClubeModel;
import br.com.meli.times_futebol.service.ClubeService;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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
    public ResponseEntity<List<ClubeModel>> listarTodosClubes() {

        List<ClubeModel> clubeModelsList= clubeService.listarTodosTimes();

        return ResponseEntity.status(HttpStatus.OK).body(clubeModelsList);

    }

    @GetMapping("/{idValor}")
    public ResponseEntity<Object> listarClube(@PathVariable Long idValor) {

        Optional<ClubeModel> clubeModelOptional = clubeService.acharTime(idValor);
        if (clubeModelOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("time nao encontrado");
        }
        return ResponseEntity.status(HttpStatus.OK).body(clubeModelOptional);
    }

    @PutMapping ("/{idValor}")
    public ResponseEntity<Object> atualizarClube(@PathVariable Long idValor,
                                                 @RequestBody @Valid ClubeRequestDto  clubeRequestDto) {

        Optional<ClubeModel> clubeModelOptional = clubeService.acharTime(idValor);
        if (clubeModelOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("time nao encontrado");
        }

        ClubeModel clubeModel = clubeService.atualizarTime(clubeModelOptional, clubeRequestDto);

        return ResponseEntity.status(HttpStatus.OK).body(clubeModel);

    }

}
