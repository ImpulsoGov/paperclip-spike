package com.teste.paperclipai.busca;

import com.teste.paperclipai.model.Cidadao;
import com.teste.paperclipai.busca.BuscaCidadaoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/cidadaos/busca")
public class BuscaCidadaoController {

    private final BuscaCidadaoService service;

    public BuscaCidadaoController(BuscaCidadaoService service) {
        this.service = service;
    }

    /**
     * Busca avançada. Retorna a entidade completa do cidadão.
     */
    @GetMapping
    public List<Cidadao> buscar(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String municipio,
            @RequestParam(required = false) String uf) {
        return service.buscar(nome, municipio, uf);
    }

    /**
     * Importação legada de cidadão.
     */
    @PostMapping("/importar")
    public Cidadao importar(@RequestBody Cidadao cidadao) {
        return service.importar(cidadao);
    }
}
