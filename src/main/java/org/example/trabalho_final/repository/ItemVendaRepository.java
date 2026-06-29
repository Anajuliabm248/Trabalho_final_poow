package org.example.trabalho_final.repository;

import org.example.trabalho_final.model.ItemVenda;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ItemVendaRepository {

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<ItemVenda> rowMapper = (rs, rowNum) -> {
        ItemVenda item = new ItemVenda();
        item.setId(rs.getInt("id"));
        item.setVendaId(rs.getInt("venda_id"));
        item.setLivroId(rs.getInt("livro_id"));
        item.setQuantidade(rs.getInt("quantidade"));
        item.setPrecoUni(rs.getDouble("preco_uni"));
        item.setSubtotal(rs.getDouble("subtotal"));
        return item;
    };

    public ItemVendaRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean inserir(ItemVenda item) {
        String sql = """
                INSERT INTO item_venda (venda_id, livro_id, quantidade, preco_uni, subtotal)
                VALUES (?, ?, ?, ?, ?)
                """;

        return jdbcTemplate.update(sql, item.getVendaId(), item.getLivroId(), item.getQuantidade(), item.getPrecoUni(), item.getSubtotal()) > 0;
    }

    public List<ItemVenda> listarPorVenda(int vendaId) {
        String sql = """
                SELECT * FROM item_venda WHERE venda_id = ?
                """;

        return jdbcTemplate.query(sql, rowMapper, vendaId);
    }
}
