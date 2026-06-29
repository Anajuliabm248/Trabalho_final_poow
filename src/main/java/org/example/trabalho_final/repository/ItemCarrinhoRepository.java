package org.example.trabalho_final.repository;

import org.example.trabalho_final.model.ItemCarrinho;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ItemCarrinhoRepository {

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<ItemCarrinho> rowMapper = (rs, rowNum) -> {
        ItemCarrinho item = new ItemCarrinho();
        item.setId(rs.getInt("id"));
        item.setCarrinhoId(rs.getInt("carrinho_id"));
        item.setLivroId(rs.getInt("livro_id"));
        item.setQuantidade(rs.getInt("quantidade"));
        item.setSubtotal(rs.getDouble("subtotal"));
        return item;
    };

    public ItemCarrinhoRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean inserir(ItemCarrinho item) {
        String sql = """
                INSERT INTO item_carrinho (carrinho_id, livro_id, quantidade, subtotal) VALUES (?, ?, ?, ?)
                """;

        return jdbcTemplate.update(sql, item.getCarrinhoId(), item.getLivroId(), item.getQuantidade(), item.getSubtotal()) > 0;
    }

    public List<ItemCarrinho> listarPorCarrinho(int carrinhoId) {
        String sql = """
                SELECT * FROM item_carrinho WHERE carrinho_id = ?
                """;
        return jdbcTemplate.query(sql, rowMapper, carrinhoId);
    }

    public ItemCarrinho buscarPorCarrinhoELivro(int carrinhoId, int livroId) {
        String sql = """
                SELECT * FROM item_carrinho WHERE carrinho_id = ? AND livro_id = ?
                """;

        return jdbcTemplate.query(
                sql,
                rowMapper,
                carrinhoId,
                livroId
        ).stream().findFirst().orElse(null);
    }

    public void atualizar(ItemCarrinho item) {
        String sql = """
                UPDATE item_carrinho SET quantidade=?, subtotal=? WHERE id=?
                """;

        jdbcTemplate.update(sql, item.getQuantidade(), item.getSubtotal(), item.getId());
    }

    public void excluir(int id) {
        String sql = """
                DELETE FROM item_carrinho WHERE id = ?
                """;

        jdbcTemplate.update(sql, id);
    }

    public void excluirPorCarrinho(int carrinhoId) {
        String sql = """
                DELETE FROM item_carrinho WHERE carrinho_id = ?
                """;

        jdbcTemplate.update(sql, carrinhoId);
    }
}
