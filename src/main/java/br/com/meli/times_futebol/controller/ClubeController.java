package br.com.meli.times_futebol.controller;

import br.com.meli.times_futebol.dto.ClubeResponseDto;
import br.com.meli.times_futebol.exception.GenericException;
import br.com.meli.times_futebol.dto.ClubeRequestDto;
import br.com.meli.times_futebol.model.ClubeModel;
import br.com.meli.times_futebol.service.ClubeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


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
    public ResponseEntity<?> listarTodosClubes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "nome,asc") String[] sort) {

        // Criando objeto Sort
        Sort.Direction direction = sort[1].equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;

        Sort sortOrder = Sort.by(direction, sort[0]);

        Pageable pageable = PageRequest.of(page, size, sortOrder);

        Page<ClubeModel> clubesPage = clubeService.listarTodosTimes(pageable);

        if (clubesPage.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new GenericException("Nenhum clube encontrado"));
        }

        return ResponseEntity.status(HttpStatus.OK).body(clubesPage);

    }

    @GetMapping("/{idValor}")
    public ResponseEntity<?> listarClube(@PathVariable Long idValor) {

        ClubeModel clubeModel = clubeService.acharTime(idValor);

        return ResponseEntity.status(HttpStatus.OK).body(clubeModel);
    }

    @PutMapping("/{idValor}")
    public ResponseEntity<?> atualizarClube(@PathVariable Long idValor,
                                            @RequestBody @Valid ClubeRequestDto clubeRequestDto) {

        ClubeModel clubeModelAtlz = clubeService.atualizarTime(idValor, clubeRequestDto);

        return ResponseEntity.status(HttpStatus.OK).body("Clube: " + clubeModelAtlz.getId() + ": "
                + clubeModelAtlz.getNome() + " alterado com sucesso");

    }

    @DeleteMapping("/{idValor}")
    public ResponseEntity<String> deleteClube(@PathVariable Long idValor) {

        ClubeModel clubeModel = clubeService.inativaTime(idValor);

        return ResponseEntity.status(HttpStatus.OK).body("clube inativado com sucesso: " + clubeModel.getNome());

    }

    @GetMapping("retrospectiva/{idValor}")
    public ResponseEntity<?> getRetrospectiva(@PathVariable Long idValor) {

        ClubeResponseDto clubeResponseDto = clubeService.buscaRetrospectivaClube(idValor);

        return ResponseEntity.status(HttpStatus.OK).body("Retrospectiva do Clube: " + clubeResponseDto.nome() + "\n" + "\n" +
                "Vitorias: " + clubeResponseDto.vitorias() + "\n" +
                "Empates: " + clubeResponseDto.empates() + "\n" +
                "Derrotas: " + clubeResponseDto.derrotas() + "\n" +
                "Gols Marcados: " + clubeResponseDto.golsMarcados() + "\n" +
                "Gols Sofridos: " + clubeResponseDto.golsSofridos());
    }

}