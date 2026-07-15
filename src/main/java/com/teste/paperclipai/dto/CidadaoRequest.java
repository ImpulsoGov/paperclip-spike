package com.teste.paperclipai.dto;

import java.time.LocalDate;

/**
 * Payload de criação/atualização de cidadão.
 */
public record CidadaoRequest(
        String nomeCompleto,
        String cpf,
        String cns,
        LocalDate dataNascimento,
        String sexo,
        String municipio,
        String uf,
        String telefone
) {
}
