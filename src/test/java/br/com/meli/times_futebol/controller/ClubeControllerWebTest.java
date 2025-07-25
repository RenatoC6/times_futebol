package br.com.meli.times_futebol.controller;

import br.com.meli.times_futebol.dto.ClubeRequestDto;
import br.com.meli.times_futebol.dto.ClubeResponseRetrospectivaDto;
import br.com.meli.times_futebol.model.ClubeModel;
import br.com.meli.times_futebol.service.ClubeService;
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

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ClubeController.class)
public class ClubeControllerWebTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ClubeService clubeService;

    @Autowired
    private ObjectMapper objectMapper; // Para serializar/deserializar JSON


    @Test
    void testDeveBuscarClubePorId() throws Exception {

        ClubeModel clube = montarClubeModelParaTestes(1L, "Clube 1", "SP", LocalDate.of(2025, 1, 1), true);

        when(clubeService.acharTime(1L)).thenReturn(clube);

        mockMvc.perform(get("/clube/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nome").value("Clube 1"))
                .andExpect(jsonPath("$.estado").value("SP"))
                .andExpect(jsonPath("$.dataCriacao").value("2025-01-01"))
                .andExpect(jsonPath("$.status").value(true));
    }

    @Test
    void testDeveCriarClube() throws Exception {

        ClubeRequestDto novoClube = new ClubeRequestDto("Clube 1", "RJ", LocalDate.of(2025, 1, 1), true);
        ClubeModel clubeSalvo = montarClubeModelParaTestes(2L, "Novo Clube", "SP", LocalDate.of(2025, 1, 1), true);

        when(clubeService.criarTime(novoClube)).thenReturn(clubeSalvo);

        mockMvc.perform(post("/clube")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(novoClube)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.nome").value("Novo Clube"))
                .andExpect(jsonPath("$.estado").value("SP"))
                .andExpect(jsonPath("$.dataCriacao").value("2025-01-01"))
                .andExpect(jsonPath("$.status").value(true));
    }


    @Test
    void testDeveAtualizarClube() throws Exception {
        ClubeRequestDto novoClube = new ClubeRequestDto("Clube", "MG", LocalDate.of(2025, 1, 1), false);

        Long id = 2L;
        ClubeModel clubeAtualizado = montarClubeModelParaTestes(2L, "Clube Editado", "MG", LocalDate.of(2025, 1, 1), false);

        when(clubeService.atualizarTime(id, novoClube)).thenReturn(clubeAtualizado);

        mockMvc.perform(put("/clube/{id}", id)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(novoClube)))
                .andExpect(status().isOk());
    }

    @Test
    void testDeveInativarClube() throws Exception {
        ClubeModel clubeAtualizado = montarClubeModelParaTestes(1L, "Clube Editado", "MG", LocalDate.of(2025, 1, 1), false);

        when(clubeService.inativaTime(1L)).thenReturn(clubeAtualizado);

        mockMvc.perform(delete("/clube/{id}", 1L))
                .andExpect(status().isOk());
    }

    @Test
    void testDeveRetornarClubesQuandoExistemClubes() throws Exception {

        ClubeModel clube1 = montarClubeModelParaTestes(1L, "Flamengo", "RJ", LocalDate.of(2025, 1, 1), true);
        ClubeModel clube2 = montarClubeModelParaTestes(1L, "Santos", "SP", LocalDate.of(2025, 1, 1), true);

        List<ClubeModel> clubes = Arrays.asList(clube1, clube2);

        Page<ClubeModel> pageClubes = new PageImpl<>(clubes);

        when(clubeService.listarTodosTimes(any(Pageable.class))).thenReturn(pageClubes);

        mockMvc.perform(get("/clube")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "nome,asc")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].nome").value("Flamengo"))
                .andExpect(jsonPath("$.content[1].nome").value("Santos"));
    }

    @Test
    void testdeveRetornarMensagemQuandoNaoExistemClubes() throws Exception {
        Page<ClubeModel> emptyPage = new PageImpl<>(Collections.emptyList());
        when(clubeService.listarTodosTimes(any())).thenReturn(emptyPage);

        mockMvc.perform(get("/clube")
                .param("page", "0")
                .param("size", "10")
                .param("sort", "nome,asc")
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());

    }

    @Test
    void testdeveRetornarRetrospectivaDeClube() throws Exception {

        ClubeResponseRetrospectivaDto dto = new ClubeResponseRetrospectivaDto("Teste", "Palmeiras", "Santos", 1L, 1L, 1L, 1L, 1L);

        when(clubeService.buscaRetrospectivaClube(1L)).thenReturn(dto);

        // mocMvc simu
        mockMvc.perform(get("/clube/retrospectiva/{idClube}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)) //Diz que o tipo de conteúdo solicitado (header HTTP Content-Type) é application/json.
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Palmeiras"))
                .andExpect(jsonPath("$.vitorias").value(1));
    }


    public ClubeModel montarClubeModelParaTestes(Long id, String nome, String estado, LocalDate dataCriacao, boolean status) {

        ClubeModel clubeModel = new ClubeModel();
        clubeModel.setId(id);
        clubeModel.setNome(nome);
        clubeModel.setEstado(estado);
        clubeModel.setDataCriacao(dataCriacao);
        clubeModel.setStatus(status);

        return clubeModel;
    }
}