package com.teste.paperclipai.dto;

import com.teste.paperclipai.model.Cidadao;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Representação pública do cidadão. O CPF é sempre mascarado (LGPD).
 */
public record CidadaoResponse(
        Long id,
        String nomeCompleto,
        String cpfMascarado,
        String cns,
        LocalDate dataNascimento,
        String sexo,
        String municipio,
        String uf,
        String telefone,
        LocalDateTime criadoEm,
        LocalDateTime atualizadoEm
) {

    public static CidadaoResponse de(Cidadao c) {
        return new CidadaoResponse(
                c.getId(),
                c.getNomeCompleto(),
                mascararCpf(c.getCpf()),
                c.getCns(),
                c.getDataNascimento(),
                c.getSexo(),
                c.getMunicipio(),
                c.getUf(),
                c.getTelefone(),
                c.getCriadoEm(),
                c.getAtualizadoEm()
        );
    }

    /** 12345678901 → ***.456.789-** */
    private static String mascararCpf(String cpf) {
        if (cpf == null || cpf.length() != 11) {
            return "***";
        }
        return "***." + cpf.substring(3, 6) + "." + cpf.substring(6, 9) + "-**";
    }
}
