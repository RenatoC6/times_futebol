package br.com.meli.times_futebol.controller;

import br.com.meli.times_futebol.exception.GenericException;
import br.com.meli.times_futebol.dto.EstadioRequestDto;
import br.com.meli.times_futebol.model.EstadioModel;
import br.com.meli.times_futebol.service.EstadioService;
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
    public ResponseEntity<?> listartodosEstadios(
            @RequestParam(required = false) String nomeE,
            @RequestParam(defaultValue = "0") int page, //número da página a ser exibida (padrão: 0 = primeira página)
            @RequestParam(defaultValue = "10") int size, //quantidade de itens por página (padrão: 10)
            @RequestParam(defaultValue = "nomeEstadio,asc") String[] sort) //critério de ordenação — array com 2 posições, padrão "id,asc" (id é o campo, asc é a direção).
    {

        // Criando objeto Sort
        //Verifica se o segundo item do array sort é "desc" (ignora maiúsculo/minúsculo): Se sim, define o direction como descendente (DESC)
        //Se não, por padrão, define como ascendente (ASC)
        Sort.Direction direction = sort[1].equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;

        // Cria um objeto Sort do Spring Data que representa a ordenação. Campo a ser ordenado: sort[0] (exemplo: "id" ou "nome")
        //Direção: a direção definida acima (ASC ou DESC)
        Sort sortOrder = Sort.by(direction, sort[0]);

        // Cria um objeto do tipo PageRequest, que implementa Pageable.  Página: page (começa em 0), Tamanho da página: size (quantos registros mostrar por página)
        // Ordenação: sortOrder (definido acima)
        Pageable pageable = PageRequest.of(page, size, sortOrder);

        Page<EstadioModel> estadiosPage= estadioService.listarTodosEstadios(pageable);

        if(estadiosPage.isEmpty()){
            return ResponseEntity.status(HttpStatus.OK).
                    body(new GenericException("Nenhum estadio encontrado"));}

        return ResponseEntity.status(HttpStatus.OK).body(estadiosPage);
    }

    @GetMapping("/{idValor}")
    public ResponseEntity<?> ListarEstadios(@PathVariable Long idValor) {

        EstadioModel estadioModel = estadioService.acharEstadio(idValor);

        return ResponseEntity.status(HttpStatus.OK).body(estadioModel);
    }

    @PutMapping ("/{idValor}")
    public ResponseEntity<?> atualizarEstadio(@PathVariable Long idValor,
                                                   @RequestBody @Valid EstadioRequestDto estadioRequestDto) {

        EstadioModel estadioModel = estadioService.acharEstadio(idValor);

        EstadioModel estadioModelAtlz = estadioService.atualizarEstadio(estadioModel, estadioRequestDto);

        return ResponseEntity.status(HttpStatus.OK).body(estadioModelAtlz);

    }

    @DeleteMapping ("/{idValor}")
    public ResponseEntity<?> deleteEstadio(@PathVariable Long idValor) {

        EstadioModel estadioModel = estadioService.acharEstadio(idValor);

        estadioService.deleteEstadio(idValor);

        return ResponseEntity.status(HttpStatus.OK).body("estadio excluido com sucesso: " + ": "+ estadioModel.getNomeEstadio());

    }
}