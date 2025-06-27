package br.com.meli.times_futebol.controller;

import br.com.meli.times_futebol.dto.ClubeRequestDto;
import br.com.meli.times_futebol.service.ClubeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("clube")
public class ClubeController {

    @Autowired
    private ClubeService clubeService;

    @GetMapping("/teste1")
    public String getMessage() {
        return "Öla Controller";
    }

    @GetMapping("/{id}")
    public String getMessageTest(@PathVariable Long id) {
        return "Öla Controller.. chave: " + id;
    }

    @PostMapping
    public ResponseEntity<String> cadastrar(@RequestBody ClubeRequestDto clubeRequestDto) {

        String mensagem = clubeService.cadastrar(clubeRequestDto);

        return  ResponseEntity.status(HttpStatus.CREATED).body(mensagem) ;
    }

}
