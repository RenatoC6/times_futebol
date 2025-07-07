package br.com.meli.times_futebol.service;

import br.com.meli.times_futebol.Exception.EntidadeNaoEncontradaException;
import br.com.meli.times_futebol.dto.ClubeRequestDto;
import br.com.meli.times_futebol.model.ClubeModel;
import br.com.meli.times_futebol.repository.ClubeRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

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

    public ClubeModel acharTime(Long idValor) {

        return clubeRepository.findById(idValor)
               .orElseThrow(() -> new EntidadeNaoEncontradaException("Time: "+ idValor +  " nao encontrado"));

    }

    public ClubeModel atualizarTime(ClubeModel clubeModel,  ClubeRequestDto clubeRequestDto) {

        clubeModel.setId(clubeModel.getId());
        BeanUtils.copyProperties(clubeRequestDto, clubeModel);
        clubeRepository.save(clubeModel);

        return clubeModel;
    }

    public void deleteTime(Long id) {

        clubeRepository.deleteById(id);

    }
}


