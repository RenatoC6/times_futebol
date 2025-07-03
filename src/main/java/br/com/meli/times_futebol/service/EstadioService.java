package br.com.meli.times_futebol.service;

import br.com.meli.times_futebol.dto.EstadioRequestDto;
import br.com.meli.times_futebol.model.EstadioModel;
import br.com.meli.times_futebol.repository.EstadioRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
