package com.teste.paperclipai.config;

import com.teste.paperclipai.model.Cidadao;
import com.teste.paperclipai.repository.CidadaoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;

/**
 * Popula o banco em memória com dados de exemplo (CPFs fictícios porém válidos).
 */
@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seed(CidadaoRepository repository) {
        return args -> {
            if (repository.count() > 0) {
                return;
            }
            repository.save(criar("Maria da Silva Santos", "52998224725", "898001160660000",
                    LocalDate.of(1985, 3, 12), "FEMININO", "Recife", "PE", "81988887777"));
            repository.save(criar("João Pereira de Souza", "11144477735", "898001160660011",
                    LocalDate.of(1990, 7, 4), "MASCULINO", "Recife", "PE", "81977776666"));
            repository.save(criar("Ana Carolina Oliveira", "93541134780", null,
                    LocalDate.of(2001, 11, 23), "FEMININO", "São Paulo", "SP", null));
        };
    }

    private Cidadao criar(String nome, String cpf, String cns, LocalDate nascimento,
                          String sexo, String municipio, String uf, String telefone) {
        Cidadao c = new Cidadao();
        c.setNomeCompleto(nome);
        c.setCpf(cpf);
        c.setCns(cns);
        c.setDataNascimento(nascimento);
        c.setSexo(sexo);
        c.setMunicipio(municipio);
        c.setUf(uf);
        c.setTelefone(telefone);
        return c;
    }
}
