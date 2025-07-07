package br.com.meli.times_futebol.service;

import br.com.meli.times_futebol.exception.EntidadeNaoEncontradaException;
import br.com.meli.times_futebol.dto.EstadioRequestDto;
import br.com.meli.times_futebol.model.EstadioModel;
import br.com.meli.times_futebol.repository.EstadioRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EstadioService {

    @Autowired
    EstadioRepository estadioRepository;

    public EstadioModel criarEstadio(EstadioRequestDto estadioRequestDto) {

        var estadioModel = new EstadioModel();
        BeanUtils.copyProperties(estadioRequestDto, estadioModel);
        estadioRepository.save(estadioModel);

        return estadioModel;
    }

    public List<EstadioModel> listarTodosEstadios() {

        return estadioRepository.findAll();

    }

    public EstadioModel acharEstadio(Long idValor) {

        return estadioRepository.findById(idValor)
                    .orElseThrow(() -> new EntidadeNaoEncontradaException("Estadio: "+ idValor +  " nao encontrado"));

    }

    public EstadioModel atualizarEstadio(EstadioModel estadioModel,  EstadioRequestDto estadioRequestDto) {

        estadioModel.setId(estadioModel.getId());
        BeanUtils.copyProperties(estadioRequestDto, estadioModel);
        estadioRepository.save(estadioModel);

        return estadioModel;
    }

    public void deleteEstadio(Long id) {

        estadioRepository.deleteById(id);

    }

}
