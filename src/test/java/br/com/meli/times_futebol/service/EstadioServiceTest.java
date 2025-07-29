package br.com.meli.times_futebol.service;

import br.com.meli.times_futebol.dto.EstadioRequestDto;
import br.com.meli.times_futebol.dto.EstadioResponseDto;
import br.com.meli.times_futebol.exception.EntidadeNaoEncontradaException;
import br.com.meli.times_futebol.model.EstadioModel;
import br.com.meli.times_futebol.repository.EstadioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EstadioServiceTest {

    @Mock
    private EstadioRepository estadioRepository;

    @InjectMocks
    private EstadioService estadioService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testeQuandoCriarEstadioComValidacoes() {

        EstadioRequestDto dto = new EstadioRequestDto("EstadioTeste", "13208-600");
        when(estadioRepository.existsByNomeEstadioIgnoreCase(any())).thenReturn(false);

        EstadioResponseDto estadioResponseDto =  estadioService.criarEstadio(dto);

        assertEquals("13208-600", estadioResponseDto.cep());
        verify(estadioRepository).save(any());

    }

    @Test
    public void testeQuandoAtualizarEstadioComValidacoes() {

        EstadioRequestDto dto = new EstadioRequestDto("EstadioAtualizado", "13208-500");
        EstadioModel estadioExistente = new EstadioModel();
        estadioExistente.setId(1L);
        estadioExistente.setNomeEstadio("EstadioAntigo");

        when(estadioRepository.findById(1L)).thenReturn(java.util.Optional.of(estadioExistente));
        when(estadioRepository.existsByNomeEstadioIgnoreCase(any())).thenReturn(false);

        EstadioResponseDto estadioAtualizado  = estadioService.atualizarEstadio(estadioExistente, dto);

        assertEquals("13208-500", estadioAtualizado.cep());
        verify(estadioRepository).save(any());

   }

    @Test
    public void testeQuandoListarEstadios() {

        Pageable pageable = PageRequest.of(0, 10);
        when(estadioRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of()));

        var estadios = estadioService.listarTodosEstadios(pageable);

        assertEquals(0, estadios.getContent().size());
        verify(estadioRepository).findAll(pageable);

    }

    @Test
    public void testeQuandoBuscarEstadioPorId() {

        EstadioModel estadio = new EstadioModel();
        estadio.setId(1L);
        estadio.setNomeEstadio("EstadioTeste");

        when(estadioRepository.findById(1L)).thenReturn(java.util.Optional.of(estadio));

        EstadioModel estadioEncontrado = estadioService.acharEstadio(1L);

        assertEquals("EstadioTeste", estadioEncontrado.getNomeEstadio());
        verify(estadioRepository).findById(1L);

    }

    @Test
    public void testeQuandoEstadioNaoEncontrado() {
        Long idInexistente = 999L;
        when(estadioRepository.findById(idInexistente)).thenReturn(java.util.Optional.empty());

        Exception ex = assertThrows(EntidadeNaoEncontradaException.class,
                () -> estadioService.acharEstadio(idInexistente));

        assertTrue(ex.getMessage().contains(" nao encontrado"));
        verify(estadioRepository).findById(idInexistente);
    }

    @Test
    public void testeQuandoDeletarEstadio() {
        estadioService.deleteEstadio(1L);
        verify(estadioRepository).deleteById(1L);
    }
}
