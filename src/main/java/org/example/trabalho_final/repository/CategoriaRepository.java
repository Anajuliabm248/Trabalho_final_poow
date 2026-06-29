package org.example.trabalho_final.repository;

import org.example.trabalho_final.model.Categoria;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CategoriaRepository {

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Categoria> rowMapper = (rs, rowNum) -> {
        Categoria c = new Categoria();
        c.setId(rs.getInt("id"));
        c.setNome(rs.getString("nome"));
        return c;
    };

    public CategoriaRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean inserir(Categoria categoria) {

        String sql = """
                INSERT INTO categoria (nome)
                VALUES (?)
                """;

        return jdbcTemplate.update(sql, categoria.getNome()) > 0;
    }

    public List<Categoria> listar() {

        String sql = """
                SELECT *
                FROM categoria
                ORDER BY nome
                """;

        return jdbcTemplate.query(sql, rowMapper);
    }

    public Categoria buscarPorId(int id) {

        String sql = """
                SELECT *
                FROM categoria
                WHERE id = ?
                """;

        return jdbcTemplate.query(sql, rowMapper, id)
                .stream()
                .findFirst()
                .orElse(null);
    }
}
