package com.teste.paperclipai.repository;

import com.teste.paperclipai.model.Cidadao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CidadaoRepository extends JpaRepository<Cidadao, Long> {

    Optional<Cidadao> findByCpf(String cpf);

    boolean existsByCpf(String cpf);

    Page<Cidadao> findByMunicipioIgnoreCase(String municipio, Pageable pageable);

    Page<Cidadao> findByNomeCompletoContainingIgnoreCase(String nome, Pageable pageable);
}
