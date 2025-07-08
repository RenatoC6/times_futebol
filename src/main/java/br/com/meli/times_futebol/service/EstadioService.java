package br.com.meli.times_futebol.service;

import br.com.meli.times_futebol.exception.EntidadeNaoEncontradaException;
import br.com.meli.times_futebol.dto.EstadioRequestDto;
import br.com.meli.times_futebol.exception.GenericException;
import br.com.meli.times_futebol.exception.GenericExceptionConflict;
import br.com.meli.times_futebol.model.EstadioModel;
import br.com.meli.times_futebol.repository.EstadioRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class EstadioService {

    @Autowired
    EstadioRepository estadioRepository;

    public EstadioModel criarEstadio(EstadioRequestDto estadioRequestDto) {

        // nome do clube tem qur ter mais de 2 digitos
        validaNomeEstadio(estadioRequestDto);
        // valida se ja existe esse nome de clube na base
        validaEstadoExistente(estadioRequestDto);

        var estadioModel = new EstadioModel();
        BeanUtils.copyProperties(estadioRequestDto, estadioModel);
        estadioRepository.save(estadioModel);

        return estadioModel;
    }


        public Page<EstadioModel> listarTodosEstadios(Pageable pageable) {

            return estadioRepository.findAll(pageable);

        }

    public EstadioModel acharEstadio(Long idValor) {

        return estadioRepository.findById(idValor)
                    .orElseThrow(() -> new EntidadeNaoEncontradaException("Estadio: "+ idValor +  " nao encontrado"));

    }

    public EstadioModel atualizarEstadio(EstadioModel estadioModel,  EstadioRequestDto estadioRequestDto) {

        // nome do clube tem qur ter mais de 2 digitos
        validaNomeEstadio(estadioRequestDto);

        if (!estadioModel.getNomeEstadio().equals(estadioRequestDto.nomeEstadio())) {
            // valida se ja existe esse nome de clube na base
            validaEstadoExistente(estadioRequestDto);

            estadioModel.setId(estadioModel.getId());
            BeanUtils.copyProperties(estadioRequestDto, estadioModel);
            estadioRepository.save(estadioModel);
    }
        return estadioModel;
    }

    public void deleteEstadio(Long id) {

        estadioRepository.deleteById(id);

    }

    // metodos validacao

    public void validaNomeEstadio(EstadioRequestDto estadioRequestDto) {

        if(estadioRequestDto.nomeEstadio().trim().length() < 3 || estadioRequestDto.nomeEstadio().isEmpty()){
            throw new GenericException("nome do estadio deve ter no minimo 3 caracteres");
        }

    }
    public void validaEstadoExistente(EstadioRequestDto estadioRequestDto) {
        if(estadioRepository.existsByNomeEstadioIgnoreCase(estadioRequestDto.nomeEstadio())){
            throw new GenericExceptionConflict("Estadio : " + estadioRequestDto.nomeEstadio() + " ja cadastrado");
        }
    }

}
