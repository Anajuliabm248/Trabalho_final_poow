package org.example.trabalho_final.repository;

import org.example.trabalho_final.model.Venda;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class VendaRepository {

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Venda> rowMapper = (rs, rowNum) -> {
        Venda v = new Venda();
        v.setId(rs.getInt("id"));
        v.setClienteId(rs.getInt("cliente_id"));
        v.setDtVenda(rs.getDate("dt_venda"));
        v.setValorTotal(rs.getDouble("valor_total"));
        v.setStatus(rs.getString("status"));
        return v;
    };

    public VendaRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int inserir(Venda venda) {

        String sql = """
                INSERT INTO venda
                    (cliente_id, dt_venda, valor_total, status)
                VALUES
                    (?, CURRENT_DATE, ?, 'PENDENTE')
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {

            PreparedStatement ps = connection.prepareStatement(
                    sql,
                    Statement.RETURN_GENERATED_KEYS
            );

            ps.setInt(1, venda.getClienteId());
            ps.setDouble(2, venda.getValorTotal());

            return ps;

        }, keyHolder);

        if (keyHolder.getKey() == null) {
            throw new RuntimeException("Não foi possível obter o ID da venda.");
        }

        int idGerado = keyHolder.getKey().intValue();

        venda.setId(idGerado);

        return idGerado;
    }

    public Venda buscarPorId(int id) {
        String sql = """
                SELECT * FROM venda WHERE id = ?
                """;

        return jdbcTemplate.query(
                sql,
                rowMapper,
                id
        ).stream().findFirst().orElse(null);
    }

    public List<Venda> listarPorCliente(int clienteId) {
        String sql = """
                SELECT * FROM venda WHERE cliente_id = ?
                ORDER BY dt_venda DESC
                """;

        return jdbcTemplate.query(sql, rowMapper, clienteId);
    }

    /**
     * Retorna as vendas que contêm pelo menos um livro deste vendedor.
     * Usa DISTINCT para não duplicar a venda quando ela tiver vários itens do mesmo vendedor.
     */
    public List<Venda> listarPorVendedor(int vendedorId) {
        String sql = """
                SELECT DISTINCT v.* FROM venda v
                INNER JOIN item_venda iv ON iv.venda_id = v.id
                INNER JOIN livro l ON l.id = iv.livro_id
                WHERE l.vendedor_id = ?
                ORDER BY v.dt_venda DESC
                """;

        return jdbcTemplate.query(sql, rowMapper, vendedorId);
    }

    public List<Venda> listarTodas() {
        String sql = """
                SELECT * FROM venda ORDER BY dt_venda DESC
                """;

        return jdbcTemplate.query(sql, rowMapper);
    }

    public void atualizarStatus(int vendaId, String status) {
        String sql = """
                UPDATE venda SET status = ? WHERE id = ?
                """;

        jdbcTemplate.update(
                sql,
                status,
                vendaId
        );
    }
}
