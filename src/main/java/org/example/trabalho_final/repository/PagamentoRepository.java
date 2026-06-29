package org.example.trabalho_final.repository;

import org.example.trabalho_final.model.Pagamento;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class PagamentoRepository {

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Pagamento> rowMapper = (rs, rowNum) -> {
        Pagamento p = new Pagamento();
        p.setId(rs.getInt("id"));
        p.setVendaId(rs.getInt("venda_id"));
        p.setFormaPagamento(rs.getString("forma_pagamento"));
        p.setStatus(rs.getString("status"));
        p.setDtPagamento(rs.getDate("dt_pagamento"));
        p.setValor(rs.getDouble("valor"));
        return p;
    };

    public PagamentoRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean inserir(Pagamento pagamento) {
        String sql = """
                INSERT INTO pagamento (venda_id, forma_pagamento, status, valor)
                VALUES (?, ?, 'PENDENTE', ?)
                """;

        return jdbcTemplate.update(
                sql,
                pagamento.getVendaId(),
                pagamento.getFormaPagamento(),
                pagamento.getValor()
        ) > 0;
    }

    public Pagamento buscarPorVenda(int vendaId) {
        String sql = """
                SELECT * FROM pagamento WHERE venda_id = ?
                """;

        return jdbcTemplate.query(sql, rowMapper, vendaId)
                .stream()
                .findFirst()
                .orElse(null);
    }

    public void atualizarStatus(int vendaId, String status) {
        String sql = """
                UPDATE pagamento SET status=?, 
                dt_pagamento=CURRENT_DATE 
                WHERE venda_id=?
                """;

        jdbcTemplate.update(
                sql,
                status,
                vendaId
        );
    }
}
