package com.teste.paperclipai.service;

import com.teste.paperclipai.dto.CidadaoRequest;
import com.teste.paperclipai.dto.CidadaoResponse;
import com.teste.paperclipai.exception.CpfDuplicadoException;
import com.teste.paperclipai.exception.DadosInvalidosException;
import com.teste.paperclipai.exception.RecursoNaoEncontradoException;
import com.teste.paperclipai.model.Cidadao;
import com.teste.paperclipai.repository.CidadaoRepository;
import com.teste.paperclipai.util.CpfValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Set;

@Service
public class CidadaoService {

    private static final Set<String> SEXOS_VALIDOS = Set.of("MASCULINO", "FEMININO", "OUTRO", "NAO_INFORMADO");
    private static final Set<String> UFS_VALIDAS = Set.of(
            "AC", "AL", "AP", "AM", "BA", "CE", "DF", "ES", "GO", "MA", "MT", "MS", "MG",
            "PA", "PB", "PR", "PE", "PI", "RJ", "RN", "RS", "RO", "RR", "SC", "SP", "SE", "TO");

    private final CidadaoRepository repository;

    public CidadaoService(CidadaoRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public CidadaoResponse cadastrar(CidadaoRequest request) {
        validar(request);
        String cpf = CpfValidator.normalizar(request.cpf());
        if (repository.existsByCpf(cpf)) {
            throw new CpfDuplicadoException();
        }
        Cidadao cidadao = new Cidadao();
        aplicar(cidadao, request, cpf);
        return CidadaoResponse.de(repository.save(cidadao));
    }

    @Transactional(readOnly = true)
    public CidadaoResponse buscarPorId(Long id) {
        return CidadaoResponse.de(obter(id));
    }

    @Transactional(readOnly = true)
    public Page<CidadaoResponse> listar(String municipio, String nome, Pageable pageable) {
        Page<Cidadao> pagina;
        if (municipio != null && !municipio.isBlank()) {
            pagina = repository.findByMunicipioIgnoreCase(municipio.trim(), pageable);
        } else if (nome != null && !nome.isBlank()) {
            pagina = repository.findByNomeCompletoContainingIgnoreCase(nome.trim(), pageable);
        } else {
            pagina = repository.findAll(pageable);
        }
        return pagina.map(CidadaoResponse::de);
    }

    @Transactional
    public CidadaoResponse atualizar(Long id, CidadaoRequest request) {
        validar(request);
        Cidadao cidadao = obter(id);
        String cpf = CpfValidator.normalizar(request.cpf());
        repository.findByCpf(cpf)
                .filter(existente -> !existente.getId().equals(id))
                .ifPresent(existente -> {
                    throw new CpfDuplicadoException();
                });
        aplicar(cidadao, request, cpf);
        return CidadaoResponse.de(repository.save(cidadao));
    }

    @Transactional
    public void remover(Long id) {
        repository.delete(obter(id));
    }

    private Cidadao obter(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cidadão não encontrado: id=" + id));
    }

    private void aplicar(Cidadao cidadao, CidadaoRequest request, String cpfNormalizado) {
        cidadao.setNomeCompleto(request.nomeCompleto().trim());
        cidadao.setCpf(cpfNormalizado);
        cidadao.setCns(request.cns() == null ? null : request.cns().trim());
        cidadao.setDataNascimento(request.dataNascimento());
        cidadao.setSexo(request.sexo().trim().toUpperCase());
        cidadao.setMunicipio(request.municipio().trim());
        cidadao.setUf(request.uf().trim().toUpperCase());
        cidadao.setTelefone(request.telefone());
    }

    private void validar(CidadaoRequest request) {
        if (request.nomeCompleto() == null || request.nomeCompleto().trim().length() < 3) {
            throw new DadosInvalidosException("Nome completo é obrigatório (mínimo 3 caracteres).");
        }
        if (!CpfValidator.isValido(request.cpf())) {
            throw new DadosInvalidosException("CPF inválido.");
        }
        if (request.cns() != null && !request.cns().isBlank()
                && !request.cns().trim().matches("\\d{15}")) {
            throw new DadosInvalidosException("CNS deve conter exatamente 15 dígitos.");
        }
        if (request.dataNascimento() == null || request.dataNascimento().isAfter(LocalDate.now())) {
            throw new DadosInvalidosException("Data de nascimento é obrigatória e não pode ser futura.");
        }
        if (request.sexo() == null || !SEXOS_VALIDOS.contains(request.sexo().trim().toUpperCase())) {
            throw new DadosInvalidosException("Sexo deve ser um de: " + SEXOS_VALIDOS + ".");
        }
        if (request.municipio() == null || request.municipio().isBlank()) {
            throw new DadosInvalidosException("Município é obrigatório.");
        }
        if (request.uf() == null || !UFS_VALIDAS.contains(request.uf().trim().toUpperCase())) {
            throw new DadosInvalidosException("UF inválida.");
        }
    }
}
