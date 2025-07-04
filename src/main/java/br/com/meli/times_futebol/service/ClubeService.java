package br.com.meli.times_futebol.service;

import br.com.meli.times_futebol.dto.ClubeRequestDto;
import br.com.meli.times_futebol.model.ClubeModel;
import br.com.meli.times_futebol.repository.ClubeRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ClubeService {

    @Autowired
    ClubeRepository clubeRepository;

    public ClubeModel criarTime(ClubeRequestDto clubeRequestDto) {

        var clubeModel = new ClubeModel();
        BeanUtils.copyProperties(clubeRequestDto, clubeModel);
        clubeRepository.save(clubeModel);

        return clubeModel;
    }

    public List<ClubeModel> listarTodosTimes() {

        return clubeRepository.findAll();

    }

    public Optional<ClubeModel> acharTime(Long idValor) {

        Optional<ClubeModel> clubeModelOptional = clubeRepository.findById(idValor);

        return clubeModelOptional;

    }

    public ClubeModel atualizarTime(Optional clubeModelOptional,  ClubeRequestDto clubeRequestDto) {

        ClubeModel clubeModel = (ClubeModel) clubeModelOptional.get();
        BeanUtils.copyProperties(clubeRequestDto, clubeModel);
        clubeRepository.save(clubeModel);

        return clubeModel;
    }

    public void deleteTime(Long id) {

        clubeRepository.deleteById(id);

    }
}


