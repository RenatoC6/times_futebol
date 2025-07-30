package br.com.meli.times_futebol.service;

import br.com.meli.times_futebol.dto.EstadioRequestDto;
import br.com.meli.times_futebol.dto.EstadioResponseDto;
import br.com.meli.times_futebol.exception.EntidadeNaoEncontradaException;
import br.com.meli.times_futebol.exception.GenericException;
import br.com.meli.times_futebol.exception.GenericExceptionConflict;
import br.com.meli.times_futebol.model.EstadioModel;
import br.com.meli.times_futebol.repository.EstadioRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class EstadioService {

    @Autowired
    EstadioRepository estadioRepository;

    public EstadioModel criarEstadio(EstadioRequestDto estadioRequestDto) {

        validaNomeEstadio(estadioRequestDto);
        validaEstadoExistente(estadioRequestDto);

        EstadioResponseDto estadioResponseDto = buscarCep(estadioRequestDto.cep());

        var estadioModel = new EstadioModel();
        BeanUtils.copyProperties(estadioResponseDto, estadioModel);
        estadioModel.setNomeEstadio(estadioRequestDto.nomeEstadio());
        estadioRepository.save(estadioModel);

        return estadioModel;
    }

    public EstadioModel atualizarEstadio(EstadioModel estadioModel, EstadioRequestDto estadioRequestDto) {

        validaNomeEstadio(estadioRequestDto);

        if (!estadioModel.getNomeEstadio().equals(estadioRequestDto.nomeEstadio())) {
            validaEstadoExistente(estadioRequestDto);
        }

        EstadioResponseDto estadioResponseDto = buscarCep(estadioRequestDto.cep());

        BeanUtils.copyProperties(estadioResponseDto, estadioModel);
        estadioModel.setId(estadioModel.getId());
        estadioModel.setNomeEstadio(estadioRequestDto.nomeEstadio());
        estadioRepository.save(estadioModel);

        return estadioModel;
    }

    public Page<EstadioModel> listarTodosEstadios(Pageable pageable) {

        return estadioRepository.findAll(pageable);

    }

    public EstadioModel acharEstadio(Long idValor) {

        return estadioRepository.findById(idValor)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Estadio: " + idValor + " nao encontrado"));

    }


    public void deleteEstadio(Long id) {

        estadioRepository.deleteById(id);

    }

    // metodos validacao

    public void validaNomeEstadio(EstadioRequestDto estadioRequestDto) {

        if (estadioRequestDto.nomeEstadio().trim().length() < 3 || estadioRequestDto.nomeEstadio().isEmpty()) {
            throw new GenericException("nome do estadio deve ter no minimo 3 caracteres");
        }

    }

    public void validaEstadoExistente(EstadioRequestDto estadioRequestDto) {
        if (estadioRepository.existsByNomeEstadioIgnoreCase(estadioRequestDto.nomeEstadio())) {
            throw new GenericExceptionConflict("Estadio : " + estadioRequestDto.nomeEstadio() + " ja cadastrado");
        }
    }

    public EstadioResponseDto buscarCep(String cep) {
        final RestTemplate restTemplate = new RestTemplate();

        if (cep == null || cep.isEmpty() || !cep.matches("\\d{5}-?\\d{3}")) { //  \\d{5}: exatamente 5 dígitos (números) -?: hífen opcional (pode ou não ter um hífen entre os números) \\d{3}: exatamente 3 dígitos
            throw new GenericException("CEP invalido");
        }
        String url = UriComponentsBuilder
                .fromHttpUrl("https://viacep.com.br/ws/{cep}/json/")
                .buildAndExpand(cep)
                .toUriString();
        EstadioResponseDto estadioResponseDto = restTemplate.getForObject(url, EstadioResponseDto.class);

        if (estadioResponseDto == null || estadioResponseDto.erro()) {
            throw new GenericException("CEP invalido");
        }

        return estadioResponseDto;
    }

}
