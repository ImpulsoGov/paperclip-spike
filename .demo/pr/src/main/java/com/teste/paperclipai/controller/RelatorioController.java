package com.teste.paperclipai.controller;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Relatório de cidadãos para as secretarias municipais.
 */
@RestController
@RequestMapping("/api/relatorios")
public class RelatorioController {

    private static final Logger log = LoggerFactory.getLogger(RelatorioController.class);

    @PersistenceContext
    private EntityManager entityManager;

    @GetMapping("/cidadaos")
    public ResponseEntity<List<Map<String, Object>>> relatorioCidadaos(
            @RequestParam(required = false) String municipio,
            @RequestParam(required = false) String nome,
            @RequestHeader(value = "X