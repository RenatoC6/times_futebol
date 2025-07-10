package br.com.meli.times_futebol.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(EntidadeNaoEncontradaException.class)
    public ResponseEntity<ApiError> handleEntidadeNaoEncontradaException(EntidadeNaoEncontradaException ex) {
        ApiError error = new ApiError(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(GenericException.class)
    public ResponseEntity<ApiError> handleGenericException(GenericException ex) {
        ApiError error = new ApiError(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(GenericExceptionConflict.class)
    public ResponseEntity<ApiError> handleGenericExceptionConflict(GenericExceptionConflict ex) {
        ApiError error = new ApiError(ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    // validacoes de integridade de dados
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrity(DataIntegrityViolationException ex) {
        Throwable rootCause = ex.getRootCause();
        String mensagemErro = "Erro de integridade dos dados.";
        if (rootCause != null && rootCause.getMessage() != null) {
            mensagemErro += " Detalhe: " + rootCause.getMessage();
        }
        ApiError apiError = new ApiError(mensagemErro);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
    }

    // erro inesperado no acesso ao banco de dados
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeral(Exception ex) {
        String mensagemErro = "Erro inesperado ao salvar dados:";
        if (ex.getMessage() != null) {
            mensagemErro += " " + ex.getMessage();
        }
        ApiError apiError = new ApiError(mensagemErro);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiError);
    }

}
