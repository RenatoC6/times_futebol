package br.com.meli.times_futebol.service;

import br.com.meli.times_futebol.dto.ClubeRequestDto;
import br.com.meli.times_futebol.model.ClubeModel;
import br.com.meli.times_futebol.repository.ClubeRepository;
import jakarta.persistence.Id;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Service
public class ClubeService {

    @Autowired
    ClubeRepository clubeRepository;

    public ClubeModel criarTime(ClubeRequestDto clubeRequestDto) {

        var ClubeModel = new ClubeModel();
        BeanUtils.copyProperties(clubeRequestDto, ClubeModel);
        clubeRepository.save(ClubeModel);

        return ClubeModel;
    }

    public List<ClubeModel> listarTodosTimes() {

        return clubeRepository.findAll();

    }

    public Optional<ClubeModel> listarTime(Long idValor) {

        Optional<ClubeModel> clubeModelOptional = clubeRepository.findById(idValor);

        return clubeModelOptional;


    }
}


