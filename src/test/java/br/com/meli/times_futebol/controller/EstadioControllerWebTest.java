package br.com.meli.times_futebol.controller;

import br.com.meli.times_futebol.dto.EstadioRequestDto;
import br.com.meli.times_futebol.dto.EstadioResponseDto;
import br.com.meli.times_futebol.model.EstadioModel;
import br.com.meli.times_futebol.service.EstadioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EstadioController.class)
public class EstadioControllerWebTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EstadioService estadioService;

    @Autowired
    private ObjectMapper objectMapper; // Para serializar/deserializar JSON


    @Test
    void TestDeveBuscarEstadioPorId() throws Exception {

       EstadioModel estadioModel = montarEstadioModelParaTestes(1L, "Estadio 1");

        when(estadioService.acharEstadio(1L)).thenReturn(estadioModel);

        mockMvc.perform(get("/estadio/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nomeEstadio").value("Estadio 1"));
    }

    @Test
    void TestDeveCriarEstadio() throws Exception {

        EstadioRequestDto novoEstadio = new EstadioRequestDto("Estadio 1", "13208-600");
        EstadioResponseDto estadioSalvo = montarEstadioModelParaTestesDto( "Novo Estadio");
        when(estadioService.criarEstadio(novoEstadio)).thenReturn(estadioSalvo);

        mockMvc.perform(post("/estadio")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(novoEstadio)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.cep").value("13208-600"));

    }



    @Test
    void deleteEstadioDeveRetornarStatusOk() throws Exception {

        EstadioModel estadioSalvo = montarEstadioModelParaTestes(1L, "Novo Estadio");

        when(estadioService.acharEstadio(1L)).thenReturn(estadioSalvo);
        doNothing().when(estadioService).deleteEstadio(1L);

        mockMvc.perform(delete("/estadio/{idEstadio}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }


    @Test
    void testDeveRetornarClubesQuandoExistemClubes() throws Exception {

        EstadioModel estadio1 = montarEstadioModelParaTestes(1L, "Estadio 1");
        EstadioModel estadio2 = montarEstadioModelParaTestes(2L, "Estadio 2");

        List<EstadioModel> estadio = Arrays.asList(estadio1, estadio2);

        Page<EstadioModel> pageEstadios = new PageImpl<>(estadio);

        when(estadioService.listarTodosEstadios(any(Pageable.class))).thenReturn(pageEstadios);

        mockMvc.perform(get("/estadio")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "nome,asc")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].nomeEstadio").value("Estadio 1"))
                .andExpect(jsonPath("$.content[1].nomeEstadio").value("Estadio 2"));
    }

    public EstadioResponseDto montarEstadioModelParaTestesDto(String nome) {

      return new EstadioResponseDto( nome, "13208-600", "Rua Exemplo", "Apto 101", "Bairro Exemplo", "Cidade Exemplo", "UF", "12345678", "0000", "11", "0000", true);

    }

    public EstadioModel montarEstadioModelParaTestes(Long id, String nome) {

        EstadioModel estadioModel = new EstadioModel();
        estadioModel.setId(id);
        estadioModel.setNomeEstadio(nome);

        return estadioModel;
    }

}
