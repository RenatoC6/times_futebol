package br.com.meli.times_futebol.controller;

import br.com.meli.times_futebol.dto.PartidaRequestDto;
import br.com.meli.times_futebol.model.ClubeModel;
import br.com.meli.times_futebol.model.EstadioModel;
import br.com.meli.times_futebol.model.PartidaModel;
import br.com.meli.times_futebol.service.PartidaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

@WebMvcTest(PartidaController.class)
public class PartidaControllerWebTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PartidaService  partidaService;

    @Autowired
    private ObjectMapper objectMapper; // Para serializar/deserializar JSON

    @Test
    void testDeveBuscarPartidaPorId() throws Exception {

        PartidaModel partida = montarPartidaParaTestes(1L,
                montarClubeParaTestes(1L, "Clube 1", "SP", LocalDate.of(2025, 1, 1), true),
                montarClubeParaTestes(2L, "Clube 2", "RJ", LocalDate.of(2025, 1, 1), true),
                montarEstadioParaTestes(1L, "Estadio 1"),
                LocalDateTime.of(2025, 1, 1, 15, 0) ,2L,3L);

        when(partidaService.acharPartida(1L)).thenReturn(partida);

        mockMvc.perform(get("/partida/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.clubeMandante.id").value(1L))
                .andExpect(jsonPath("$.clubeVisitante.id").value(2L))
                .andExpect(jsonPath("$.estadioPartida.id").value(1L))
                .andExpect(jsonPath("$.dataPartida").value("2025-01-01T15:00:00"))
                .andExpect(jsonPath("$.golsMandante").value(2L))
                .andExpect(jsonPath("$.golsVisitante").value(3L));
    }

    @Test
    void testDeveCriarPartida() throws Exception {

        PartidaRequestDto novaPartida = new PartidaRequestDto(1L,2L,1L,2L,1L,         // ID do estádio
                LocalDateTime.of(2025, 1, 1, 15, 0));

        PartidaModel partidaSalva = montarPartidaParaTestes(2L,
                montarClubeParaTestes(1L, "Clube 1", "SP", LocalDate.of(2025, 1, 1), true),
                montarClubeParaTestes(2L, "Clube 2", "RJ", LocalDate.of(2025, 1, 1), true),
                montarEstadioParaTestes(1L, "Estadio 1"),
                LocalDateTime.of(2025, 1, 1, 15, 0) ,2L,3L);

        when(partidaService.criarPartida(novaPartida)).thenReturn(partidaSalva);

        mockMvc.perform(post("/partida")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(novaPartida)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.clubeMandante.id").value(1L))
                .andExpect(jsonPath("$.clubeVisitante.id").value(2L))
                .andExpect(jsonPath("$.estadioPartida.id").value(1L))
                .andExpect(jsonPath("$.dataPartida").value("2025-01-01T15:00:00"))
                .andExpect(jsonPath("$.golsMandante").value(2L))
                .andExpect(jsonPath("$.golsVisitante").value(3L));
    }

    @Test
    void testDeveAtualizarPartida() throws Exception {
        PartidaRequestDto partidaAtualizada = new PartidaRequestDto(1L, 2L, 1L, 3L, 4L,
                LocalDateTime.of(2025, 1, 1, 16, 0));
        PartidaModel partidaModel = montarPartidaParaTestes(1L,
                montarClubeParaTestes(1L, "Clube 1", "SP", LocalDate.of(2025, 1, 1), true),
                montarClubeParaTestes(2L, "Clube 2", "RJ", LocalDate.of(2025, 1, 1), true),
                montarEstadioParaTestes(1L, "Estadio 1"),
                LocalDateTime.of(2025, 1, 1, 16, 0), 3L, 4L);

        when(partidaService.atualizarPartida(partidaAtualizada,1L)).thenReturn(partidaModel);

        mockMvc.perform(put("/partida/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(partidaAtualizada)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.clubeMandante.id").value(1L))
                .andExpect(jsonPath("$.clubeVisitante.id").value(2L))
                .andExpect(jsonPath("$.estadioPartida.id").value(1L))
                .andExpect(jsonPath("$.dataPartida").value("2025-01-01T16:00:00"))
                .andExpect(jsonPath("$.golsMandante").value(3L))
                .andExpect(jsonPath("$.golsVisitante").value(4L));

    }

    @Test
    void testDeveListarTodasPartidas() throws Exception {

        PartidaModel partida1 = montarPartidaParaTestes(1L,
                montarClubeParaTestes(1L, "Clube 1", "SP", LocalDate.of(2025, 1, 1), true),
                montarClubeParaTestes(2L, "Clube 2", "RJ", LocalDate.of(2025, 1, 1), true),
                montarEstadioParaTestes(1L, "Estadio 1"),
                LocalDateTime.of(2025, 1, 1, 15, 0) ,2L,3L);

        PartidaModel partida2 = montarPartidaParaTestes(2L,
                montarClubeParaTestes(3L, "Clube 3", "MG", LocalDate.of(2025, 1, 1), true),
                montarClubeParaTestes(4L, "Clube 4", "RS", LocalDate.of(2025, 1, 1), true),
                montarEstadioParaTestes(2L, "Estadio 2"),
                LocalDateTime.of(2025, 1, 2, 16, 0) ,4L,5L);

        List<PartidaModel> partidas = Arrays.asList(partida1, partida2);
        Page<PartidaModel> page = new PageImpl<>(partidas);

        when(partidaService.listarTodasPartidas(anyInt(), anyInt(), any(String[].class))).thenReturn(page);

        mockMvc.perform(get("/partida")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "dataPartida,DESC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[0].clubeMandante.id").value(1L))
                .andExpect(jsonPath("$.content[0].clubeVisitante.id").value(2L))
                .andExpect(jsonPath("$.content[0].estadioPartida.id").value(1L))
                .andExpect(jsonPath("$.content[0].dataPartida").value("2025-01-01T15:00:00"));


    }








        public PartidaModel montarPartidaParaTestes(Long id, ClubeModel mandante, ClubeModel visitante, EstadioModel estadio, LocalDateTime dataPartida, Long golsMandante, Long golsVisitante) {

        PartidaModel partidaModel = new PartidaModel();
        partidaModel.setId(id);
        partidaModel.setClubeMandante(mandante);
        partidaModel.setClubeVisitante(visitante);
        partidaModel.setEstadioPartida(estadio);
        partidaModel.setDataPartida(dataPartida);
        partidaModel.setGolsMandante(golsMandante);
        partidaModel.setGolsVisitante(golsVisitante);

        return  partidaModel;
    }

    public ClubeModel montarClubeParaTestes(Long id, String nome, String estado, LocalDate dataCriacao, boolean status) {

        ClubeModel clubeModel = new ClubeModel();
        clubeModel.setId(id);
        clubeModel.setNome(nome);
        clubeModel.setEstado(estado);
        clubeModel.setDataCriacao(dataCriacao);
        clubeModel.setStatus(status);

        return  clubeModel;
    }

    public EstadioModel montarEstadioParaTestes(Long id, String nome) {

        EstadioModel estadioModel = new EstadioModel();
        estadioModel.setId(id);
        estadioModel.setNomeEstadio(nome);

        return  estadioModel;
    }

}






