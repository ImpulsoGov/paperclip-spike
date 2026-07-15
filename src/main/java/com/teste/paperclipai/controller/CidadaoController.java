package com.teste.paperclipai.controller;

import com.teste.paperclipai.dto.CidadaoRequest;
import com.teste.paperclipai.dto.CidadaoResponse;
import com.teste.paperclipai.service.CidadaoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/cidadaos")
public class CidadaoController {

    private final CidadaoService service;

    public CidadaoController(CidadaoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<CidadaoResponse> cadastrar(@RequestBody CidadaoRequest request) {
        CidadaoResponse criado = service.cadastrar(request);
        return ResponseEntity
                .created(URI.create("/api/cidadaos/" + criado.id()))
                .body(criado);
    }

    @GetMapping
    public Page<CidadaoResponse> listar(
            @RequestParam(required = false) String municipio,
            @RequestParam(required = false) String nome,
            @PageableDefault(size = 20) Pageable pageable) {
        return service.listar(municipio, nome, pageable);
    }

    @GetMapping("/{id}")
    public CidadaoResponse buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public CidadaoResponse atualizar(@PathVariable Long id, @RequestBody CidadaoRequest request) {
        return service.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable Long id) {
        service.remover(id);
    }
}
