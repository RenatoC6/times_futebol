package br.com.meli.times_futebol.controller;

import br.com.meli.times_futebol.dto.ClubeRequestDto;
import br.com.meli.times_futebol.dto.ClubeResponseRetrospectivaDto;
import br.com.meli.times_futebol.model.ClubeModel;
import br.com.meli.times_futebol.service.ClubeService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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

    @GetMapping("/listarClubes")
    public ResponseEntity<?> listarTodosClubes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "nome,asc") String[] sort,
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String estado,
            @Parameter(description = "Status -> 0 - ativo / 1 - inativo ")
            @RequestParam(required = false) boolean status) {


        return ResponseEntity.status(HttpStatus.OK).body(clubeService.listarTodosTimes(page, size, sort, nome, estado, status));

    }

    @GetMapping("/{idClube}")
    public ResponseEntity<?> listarClube(@PathVariable Long idClube) {

        ClubeModel clubeModel = clubeService.acharTime(idClube);

        return ResponseEntity.status(HttpStatus.OK).body(clubeModel);
    }

    @PutMapping("/{idClube}")
    public ResponseEntity<?> atualizarClube(@PathVariable Long idClube,
                                            @RequestBody @Valid ClubeRequestDto clubeRequestDto) {

        ClubeModel clubeModelAtlz = clubeService.atualizarTime(idClube, clubeRequestDto);

        return ResponseEntity.status(HttpStatus.OK).body("Clube: " + clubeModelAtlz.getId() + ": "
                + clubeModelAtlz.getNome() + " alterado com sucesso");

    }

    @DeleteMapping("/{idClube}")
    public ResponseEntity<String> deleteClube(@PathVariable Long idClube) {

        ClubeModel clubeModel = clubeService.inativaTime(idClube);

        return ResponseEntity.status(HttpStatus.OK).body("clube inativado com sucesso: " + clubeModel.getNome());

    }

    @GetMapping("retrospectiva/{idClube}")
    public ResponseEntity<?> listarRetrospectivaClube(@PathVariable Long idClube) {

        ClubeResponseRetrospectivaDto clubeResponseRetrospectivaDto = clubeService.buscaRetrospectivaClube(idClube);

        return ResponseEntity.status(HttpStatus.OK).body(clubeResponseRetrospectivaDto);

    }

    @GetMapping("/retrospectiva")
    public ResponseEntity<?> listarRetrospectivaClubesContraAdversario(@RequestParam Long clube1,
                                                                       @RequestParam Long clube2) {

        ClubeResponseRetrospectivaDto clubeResponseRetrospectivaDto = clubeService.buscaRetrospectivaClubesContraAdversario(clube1, clube2);

        return ResponseEntity.status(HttpStatus.OK).body(clubeResponseRetrospectivaDto);
    }


    @GetMapping("/ranking/{tipo}")
    public ResponseEntity<?> listarRanking(@Parameter(description = "Ranking primario é 'Pontos'. Selecione o ranking Secundario: 'gols', 'vitorias' ou 'jogos'")
                                           @RequestParam(defaultValue = "gols") String tipo) {

        return ResponseEntity.status(HttpStatus.OK).body(clubeService.listarRankingClubes(tipo));
    }

}