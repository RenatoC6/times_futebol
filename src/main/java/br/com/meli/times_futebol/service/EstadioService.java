package br.com.meli.times_futebol.service;

import br.com.meli.times_futebol.dto.EstadioRequestDto;
import br.com.meli.times_futebol.model.EstadioModel;
import br.com.meli.times_futebol.repository.EstadioRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    public Optional<EstadioModel> acharEstadio(Long idValor) {

        Optional<EstadioModel> estadioModelOptional  = estadioRepository.findById(idValor);

        return estadioModelOptional;

    }

    public EstadioModel atualizarEstadio(Optional estadioModelOptional,  EstadioRequestDto estadioRequestDto) {

        EstadioModel estadioModel = (EstadioModel) estadioModelOptional.get();
        BeanUtils.copyProperties(estadioRequestDto, estadioModel);
        estadioRepository.save(estadioModel);

        return estadioModel;
    }

    public void deleteEstadio(Long id) {

        estadioRepository.deleteById(id);

    }

}
