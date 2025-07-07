package br.com.meli.times_futebol.exception;

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

}
