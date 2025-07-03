package br.com.meli.times_futebol.controller;

import br.com.meli.times_futebol.dto.EstadioRequestDto;
import br.com.meli.times_futebol.model.EstadioModel;
import br.com.meli.times_futebol.service.EstadioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

//    @GetMapping
//    public ResponseEntity<?> listartodosEstadios() {
//
//    }
//
//    @GetMapping("/idvalor")
//    public ResponseEntity<Object> listarEstadios(@PathVariable Long idValor) {
//
//
//    }
//
//    @PutMapping("/idvalor")
//    public ResponseEntity<Object> atualizarEstadio(@PathVariable Long idValor,
//                                                   @RequestBody @Valid EstadioRequestDto estadioRequestDto) {
//
//
//    }
//
//    @DeleteMapping("/{idValor}")
//    public ResponseEntity<String> deleteEstadio(@PathVariable Long idValor) {
//
//
//    }
}