package br.com.meli.times_futebol.controller;

import br.com.meli.times_futebol.Exception.GenericException;
import br.com.meli.times_futebol.dto.EstadioRequestDto;
import br.com.meli.times_futebol.model.EstadioModel;
import br.com.meli.times_futebol.service.EstadioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("estadio")
public class EstadioController {

    @Autowired // instancia (new) automaticamete a classe ClubeService -
    private EstadioService  estadioService;

    @PostMapping
    public ResponseEntity<EstadioModel> cadastrarEstadio(@RequestBody @Valid EstadioRequestDto estadioRequestDto) {
        EstadioModel estadioModel = estadioService.criarEstadio(estadioRequestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(estadioModel);
    }

    @GetMapping
    public ResponseEntity<?> listartodosEstadios() {
        List<EstadioModel> estadioModelList= estadioService.listarTodosEstadios();

        if(estadioModelList.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new GenericException("Nenhum estadio encontrado"));
        }

        return ResponseEntity.status(HttpStatus.OK).body(estadioModelList);
    }

    @GetMapping("/{idValor}")
    public ResponseEntity<Object> ListarEstadios(@PathVariable Long idValor) {

        Optional<EstadioModel> estadioModelOptional = estadioService.acharEstadio(idValor);
        if (estadioModelOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("estadio nao encontrado");
        }
        return ResponseEntity.status(HttpStatus.OK).body(estadioModelOptional);
    }

    @PutMapping ("/{idValor}")
    public ResponseEntity<Object> atualizarEstadio(@PathVariable Long idValor,
                                                   @RequestBody @Valid EstadioRequestDto estadioRequestDto) {
        Optional<EstadioModel> estadioModelOptional = estadioService.acharEstadio(idValor);
        if (estadioModelOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("estadio nao encontrado");
        }

        EstadioModel estadioModel = estadioService.atualizarEstadio (estadioModelOptional, estadioRequestDto);

        return ResponseEntity.status(HttpStatus.OK).body(estadioModel);

    }

    @DeleteMapping ("/{idValor}")
    public ResponseEntity<String> deleteEstadio(@PathVariable Long idValor) {

        Optional<EstadioModel> estadioModelOptional = estadioService.acharEstadio(idValor);

        if (estadioModelOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("estadio nao encontrado");
        }

        estadioService.deleteEstadio(idValor);

        return ResponseEntity.status(HttpStatus.OK).body("estadio excluido com sucesso: " + ": "+ estadioModelOptional.get().getNomeEstadio());

    }
}