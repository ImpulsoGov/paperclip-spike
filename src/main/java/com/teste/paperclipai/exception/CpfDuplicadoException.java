package com.teste.paperclipai.exception;

public class CpfDuplicadoException extends RuntimeException {

    public CpfDuplicadoException() {
        super("Já existe um cidadão cadastrado com este CPF.");
    }
}
