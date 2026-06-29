package org.example.trabalho_final.repository;

import org.example.trabalho_final.model.Livro;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class LivroRepository {

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Livro> rowMapper = (rs, rowNum) -> {
        Livro l = new Livro();
        l.setId(rs.getInt("id"));
        l.setCategoriaId(rs.getInt("categoria_id"));
        l.setVendedorId(rs.getInt("vendedor_id")); // ← novo campo
        l.setNome(rs.getString("nome"));
        l.setAutor(rs.getString("autor"));
        l.setIsbn(rs.getString("isbn"));
        l.setDescricao(rs.getString("descricao"));
        l.setNumPagina(rs.getInt("num_pagina"));
        l.setAnoLancamento(rs.getInt("ano_lancamento"));
        l.setPreco(rs.getDouble("preco"));
        l.setQuantidade(rs.getInt("quantidade"));
        l.setImgCapa(rs.getString("img_capa"));
        return l;
    };

    public LivroRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Catálogo público: todos os livros (clientes podem ver tudo)
    public List<Livro> listar() {
        String sql = """
                SELECT * FROM livro ORDER BY nome
                """;

        return jdbcTemplate.query(sql, rowMapper);
    }

    // Estoque do vendedor: só os livros dele
    public List<Livro> listarPorVendedor(int vendedorId) {
        String sql = """
                SELECT * FROM livro WHERE vendedor_id = ? ORDER BY nome
                """;

        return jdbcTemplate.query(sql, rowMapper, vendedorId);
    }

    public boolean inserir(Livro livro) {
        String sql = """
                INSERT INTO livro
                (categoria_id, vendedor_id, nome, autor, isbn, descricao,
                num_pagina, ano_lancamento, preco, quantidade, img_capa)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        return jdbcTemplate.update(
                sql,
                livro.getCategoriaId(),
                livro.getVendedorId(),
                livro.getNome(),
                livro.getAutor(),
                livro.getIsbn(),
                livro.getDescricao(),
                livro.getNumPagina(),
                livro.getAnoLancamento(),
                livro.getPreco(),
                livro.getQuantidade(),
                livro.getImgCapa()
        ) > 0;
    }

    public Livro buscarPorId(int id) {
        String sql = """
                SELECT * FROM livro WHERE id = ?
                """;

        return jdbcTemplate.query(
                sql,
                rowMapper,
                id
        ).stream().findFirst().orElse(null);
    }

    public List<Livro> buscarPorNome(String nome) {
        String sql = "SELECT * FROM livro WHERE LOWER(nome) LIKE LOWER(?)";
        // Adiciona o % antes e depois do nome para o operador LIKE
        String termoBusca = "%" + nome + "%";
        return jdbcTemplate.query(sql, rowMapper, termoBusca);
    }

    public List<Livro> buscarPorCategoria(int categoriaId) {
        String sql = """
                SELECT * FROM livro WHERE categoria_id = ? 
                ORDER BY nome
                """;

        return jdbcTemplate.query(sql, rowMapper, categoriaId);
    }

    public void atualizar(Livro livro) {
        // vendedor_id não é alterado no update (dono não muda)
        String sql = """
                UPDATE livro SET categoria_id=?, nome=?, autor=?, isbn=?, descricao=?,
                num_pagina=?, ano_lancamento=?, preco=?, quantidade=?, img_capa=?
                WHERE id=? AND vendedor_id=?
                """;

        jdbcTemplate.update(
                sql,
                livro.getCategoriaId(),
                livro.getNome(),
                livro.getAutor(),
                livro.getIsbn(),
                livro.getDescricao(),
                livro.getNumPagina(),
                livro.getAnoLancamento(),
                livro.getPreco(),
                livro.getQuantidade(),
                livro.getImgCapa(),
                livro.getId(),
                livro.getVendedorId()
        );
    }

    public void atualizarEstoque(int livroId, int quantidade) {
        String sql = """
                UPDATE livro SET quantidade = ? WHERE id = ?
                """;

        jdbcTemplate.update(
                sql,
                quantidade,
                livroId
        );
    }

    public boolean excluir(int livroId, int vendedorId) {

        String sql = """
                DELETE FROM livro
                WHERE id = ? AND vendedor_id = ?
                """;

        return jdbcTemplate.update(sql, livroId, vendedorId) > 0;
    }
}
