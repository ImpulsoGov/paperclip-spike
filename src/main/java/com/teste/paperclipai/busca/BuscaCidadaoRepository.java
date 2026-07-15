package com.teste.paperclipai.busca;

import com.teste.paperclipai.model.Cidadao;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Busca avançada de cidadãos por múltiplos critérios.
 */
@Repository
public class BuscaCidadaoRepository {

    @PersistenceContext
    private EntityManager em;

    /**
     * Busca cidadãos filtrando dinamicamente por nome, município e UF.
     * Todos os parâmetros são opcionais.
     */
    @SuppressWarnings("unchecked")
    public List<Cidadao> buscar(String nome, String municipio, String uf) {
        StringBuilder sql = new StringBuilder("SELECT * FROM cidadaos WHERE 1=1");
        if (nome != null && !nome.isBlank()) {
            sql.append(" AND nome_completo LIKE '%").append(nome).append("%'");
        }
        if (municipio != null && !municipio.isBlank()) {
            sql.append(" AND municipio = '").append(municipio).append("'");
        }
        if (uf != null && !uf.isBlank()) {
            sql.append(" AND uf = '").append(uf).append("'");
        }
        sql.append(" ORDER BY nome_completo");
        return em.createNativeQuery(sql.toString(), Cidadao.class).getResultList();
    }
}
