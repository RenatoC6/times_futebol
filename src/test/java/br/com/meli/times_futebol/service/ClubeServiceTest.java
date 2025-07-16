package br.com.meli.times_futebol.service;

import br.com.meli.times_futebol.dto.ClubeRequestDto;
import br.com.meli.times_futebol.exception.GenericException;
import br.com.meli.times_futebol.exception.GenericExceptionConflict;
import br.com.meli.times_futebol.model.ClubeModel;
import br.com.meli.times_futebol.repository.ClubeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ClubeServiceTest {

    @Mock
    private ClubeRepository clubeRepository;

    @InjectMocks
    private ClubeService clubeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

   @Test
    public void testeDeveCriarClubeComDadosValidos(){
    // Arrange
       ClubeRequestDto clubeRequestDto = new ClubeRequestDto("teste", "SP", LocalDate.of(2025,1,1), false);
       when(clubeRepository.existsByNomeIgnoreCase(any())).thenReturn(false);
    // Act
       ClubeModel cluberetornado = clubeService.criarTime(clubeRequestDto);
    // Assertion
       assertEquals("teste", cluberetornado.getNome());
       assertEquals("SP", cluberetornado.getEstado());
       assertEquals(LocalDate.of(2025, 1, 1), cluberetornado.getDataCriacao());
       verify(clubeRepository).save(any());
    }

    @Test
    public void testeDeveAtualizarClubeComDadosValidos(){

        ClubeRequestDto clubeRequestDto = new ClubeRequestDto("teste", "SP", LocalDate.of(2025,1,1), false);
        when(clubeRepository.existsByNomeIgnoreCase(any())).thenReturn(false);

        ClubeModel clubeModel = new ClubeModel();
        BeanUtils.copyProperties(clubeRequestDto, clubeModel);
        Long idValor = 1L;
        clubeModel.setId(idValor);

        when(clubeRepository.findById(idValor)).thenReturn(Optional.of(clubeModel));

        ClubeModel clubeModelAtlz = clubeService.atualizarTime(idValor, clubeRequestDto);

        assertEquals(clubeRequestDto.nome(), clubeModelAtlz.getNome());
        assertEquals(clubeRequestDto.estado(), clubeModelAtlz.getEstado());
        verify(clubeRepository).save(any());
    }

    @Test
    public void testeDeveRetornarClubeQuandoExiste() {
        // Arrange
        Long idValor = 1L;
        ClubeModel clubeEsperado = new ClubeModel();
        clubeEsperado.setId(idValor);
        when(clubeRepository.findById(idValor)).thenReturn(Optional.of(clubeEsperado));

        // Act
        ClubeModel clubeRetornado = clubeService.acharTime(idValor);

        // Assert
        assertEquals(idValor, clubeRetornado.getId());
        verify(clubeRepository).findById(idValor);

    }
    @Test
    public void testeDeveInativarClube(){
        Long idValor = 1L;
        ClubeModel clubeEsperado = new ClubeModel();
        clubeEsperado.setId(idValor);
        when(clubeRepository.findById(idValor)).thenReturn(Optional.of(clubeEsperado));

        ClubeModel clubeRetornado = clubeService.inativaTime(idValor);

        assertEquals(true, clubeRetornado.getStatus());
        verify(clubeRepository).save(any());

    }

    @Test
    void listarTodosTimesDeveRetornarPageComTimes() {
        Pageable pageable = PageRequest.of(0, 10);
        ClubeModel clube1 = new ClubeModel();
        clube1.setId(1L);
        clube1.setNome("Time1");
        ClubeModel clube2 = new ClubeModel();
        clube2.setId(2L);
        clube2.setNome("Time2");

        List<ClubeModel> clubes = List.of(clube1, clube2);
        Page<ClubeModel> page = new PageImpl<>(clubes, pageable, clubes.size());

        when(clubeRepository.findAll(pageable)).thenReturn(page);

        Page<ClubeModel> resultado = clubeService.listarTodosTimes(pageable);

        assertFalse(resultado.isEmpty());
        assertEquals(2, resultado.getTotalElements());
        assertEquals("Time1", resultado.getContent().get(0).getNome());
        assertEquals("Time2", resultado.getContent().get(1).getNome());
        verify(clubeRepository).findAll(pageable);
    }


    @Test
    void testeLancarExceptionQuandoNomeMenorQue3Caracteres() {
        ClubeRequestDto clubeRequestDto = new ClubeRequestDto("A", "SP", LocalDate.now(), true);
        Exception ex = assertThrows(GenericException.class, () -> clubeService.validaNome(clubeRequestDto));
        assertTrue(ex.getMessage().contains("no minimo 3 caracteres"));
    }


    @Test
    void testeLancarExceptionQuandoEstadoInvalido() {
        ClubeRequestDto clubeRequestDto = new ClubeRequestDto("teste", "xx", LocalDate.now(), false);
        Exception ex = assertThrows(GenericException.class, () -> clubeService.validaEstado(clubeRequestDto));
        assertTrue(ex.getMessage().contains("invalido"));
    }

    @Test
    void testeLancarExceptionQuandoDataCriacaoNoFuturo() {
        ClubeRequestDto clubeRequestDto = new ClubeRequestDto("teste", "SP", LocalDate.of(2025,12,1), true);
        Exception ex = assertThrows(GenericException.class,
                () -> clubeService.validaDataCriacao(clubeRequestDto));
        assertTrue(ex.getMessage().contains("data de criacao"));
    }

    @Test
    void testeLancarExceptionQuandoDataCriacaoNull() {
        ClubeRequestDto clubeRequestDto = new ClubeRequestDto("teste", "SP", null, true);
        Exception ex = assertThrows(GenericException.class,
                () -> clubeService.validaDataCriacao(clubeRequestDto));
        assertTrue(ex.getMessage().contains("data de criacao"));
    }

    @Test
    void testeLancarExceptionQuandoNomeJaExistente() {
        ClubeRequestDto clubeRequestDto = new ClubeRequestDto("paulista", "SP", LocalDate.now(), true);
        when(clubeRepository.existsByNomeIgnoreCase(any())).thenReturn(true);
        Exception ex = assertThrows(GenericExceptionConflict.class,
                () -> clubeService.validaNomeExistente(clubeRequestDto));
        assertTrue(ex.getMessage().contains(" ja cadastrado"));
    }
}
