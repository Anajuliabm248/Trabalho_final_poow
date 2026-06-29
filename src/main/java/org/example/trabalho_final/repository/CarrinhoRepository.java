package org.example.trabalho_final.repository;

import org.example.trabalho_final.model.Carrinho;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class CarrinhoRepository {

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Carrinho> rowMapper = (rs, rowNum) -> {
        Carrinho c = new Carrinho();
        c.setId(rs.getInt("id"));
        c.setClienteId(rs.getInt("cliente_id"));
        c.setValorTotal(rs.getDouble("valor_total"));
        return c;
    };

    public CarrinhoRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean inserir(Carrinho carrinho) {

        String sql = """
                INSERT INTO carrinho (cliente_id, valor_total)
                VALUES (?, 0.00)
                """;

        return jdbcTemplate.update(sql, carrinho.getClienteId()) > 0;
    }

    public Carrinho buscarPorClienteId(int clienteId) {

        String sql = """
                SELECT *
                FROM carrinho
                WHERE cliente_id = ?
                """;

        return jdbcTemplate.query(
                sql,
                rowMapper,
                clienteId
        ).stream().findFirst().orElse(null);
    }

    public void atualizarTotal(int carrinhoId, double total) {

        String sql = """ 
                UPDATE carrinho SET valor_total = ? WHERE id = ? 
                """;

        jdbcTemplate.update(sql, total, carrinhoId);
    }

}