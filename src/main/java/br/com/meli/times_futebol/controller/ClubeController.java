package br.com.meli.times_futebol.controller;

import br.com.meli.times_futebol.dto.ClubeRequestDto;
import br.com.meli.times_futebol.model.ClubeModel;
import br.com.meli.times_futebol.repository.ClubeRepository;
import br.com.meli.times_futebol.service.ClubeService;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("clube")
public class ClubeController {

    @Autowired // instancia (new) automaticamete a classe ClubeService -
    private ClubeService clubeService;

    @Autowired
    ClubeRepository clubeRepository;

    @GetMapping("/teste1")
    public String getMessage() {
        return "Öla Controller";
    }

    @GetMapping("/{id}")
    public String getMessageTest(@PathVariable Long id) {
        return "Öla Controller.. chave: " + id;
    }


    @PostMapping
    public ResponseEntity<ClubeModel> cadastrar(@RequestBody @Valid ClubeRequestDto clubeRequestDto) {

        var ClubeModel = new ClubeModel();
        BeanUtils.copyProperties(clubeRequestDto, ClubeModel);

        return ResponseEntity.status(HttpStatus.CREATED).body(clubeRepository.save(ClubeModel));
    }

    //@PostMapping
    //public ResponseEntity<String> cadastrar1(@RequestBody ClubeRequestDto clubeRequestDto) {

    //    String mensagem = clubeService.cadastrar(clubeRequestDto);

    //    return ResponseEntity.status(HttpStatus.CREATED).body(mensagem);
    //}

}
