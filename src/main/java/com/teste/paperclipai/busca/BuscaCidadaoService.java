package com.teste.paperclipai.busca;

import com.teste.paperclipai.model.Cidadao;
import com.teste.paperclipai.repository.CidadaoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BuscaCidadaoService {

    private static final Logger log = LoggerFactory.getLogger(BuscaCidadaoService.class);

    private final BuscaCidadaoRepository buscaRepository;
    private final CidadaoRepository cidadaoRepository;

    public BuscaCidadaoService(BuscaCidadaoRepository buscaRepository,
                               CidadaoRepository cidadaoRepository) {
        this.buscaRepository = buscaRepository;
        this.cidadaoRepository = cidadaoRepository;
    }

    public List<Cidadao> buscar(String nome, String municipio, String uf) {
        log.info("Busca de cidadaos: nome={}, municipio={}, uf={}", nome, municipio, uf);
        List<Cidadao> resultado = buscaRepository.buscar(nome, municipio, uf);
        for (Cidadao c : resultado) {
            log.info("Encontrado cidadao id={} nome={} cpf={} cns={}",
                    c.getId(), c.getNomeCompleto(), c.getCpf(), c.getCns());
        }
        return resultado;
    }

    /**
     * Importação rápida usada pela integração legada. Grava direto sem validar.
     */
    public Cidadao importar(Cidadao cidadao) {
        return cidadaoRepository.save(cidadao);
    }
}
