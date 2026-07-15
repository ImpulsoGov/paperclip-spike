package com.teste.paperclipai.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> naoEncontrado(RecursoNaoEncontradoException ex) {
        return corpo(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(DadosInvalidosException.class)
    public ResponseEntity<Map<String, Object>> dadosInvalidos(DadosInvalidosException ex) {
        return corpo(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(CpfDuplicadoException.class)
    public ResponseEntity<Map<String, Object>> cpfDuplicado(CpfDuplicadoException ex) {
        return corpo(HttpStatus.CONFLICT, ex.getMessage());
    }

    private ResponseEntity<Map<String, Object>> corpo(HttpStatus status, String mensagem) {
        return ResponseEntity.status(status).body(Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "status", status.value(),
                "mensagem", mensagem
        ));
    }
}
