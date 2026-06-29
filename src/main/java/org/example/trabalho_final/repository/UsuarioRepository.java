package org.example.trabalho_final.repository;

import org.example.trabalho_final.model.Usuario;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class UsuarioRepository {

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Usuario> rowMapper = (rs, rowNum) -> {
        Usuario u = new Usuario();
        u.setId(rs.getInt("id"));
        u.setNome(rs.getString("nome"));
        u.setCpf(rs.getString("cpf"));
        u.setTelefone(rs.getString("telefone"));
        u.setEmail(rs.getString("email"));
        u.setSenha(rs.getString("senha"));
        u.setAtivo(rs.getBoolean("ativo"));
        u.setTipo(rs.getString("tipo"));
        return u;
    };

    public UsuarioRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Usuario buscarPorEmail(String email) {
        String sql = """
                SELECT * FROM usuario WHERE email = ?
                """;

        return jdbcTemplate.query(
                sql,
                rowMapper,
                email
        ).stream().findFirst().orElse(null);
    }

    public boolean existeEmail(String email) {
        String sql = """
                SELECT COUNT(*)
                FROM usuario
                WHERE email = ?
                """;

        Integer quantidade = jdbcTemplate.queryForObject(
                sql,
                Integer.class,
                email
        );

        return quantidade != null && quantidade > 0;
    }

    public boolean existeCpf(String cpf) {
        String sql = """
                SELECT COUNT(*)
                FROM usuario
                WHERE cpf = ?
                """;

        Integer quantidade = jdbcTemplate.queryForObject(
                sql,
                Integer.class,
                cpf
        );

        return quantidade != null && quantidade > 0;
    }

    public void atualizarAtivo(int id, boolean ativo) {
        String sql = """
                UPDATE usuario SET ativo = ? WHERE id = ?
                """;

        jdbcTemplate.update(
                sql,
                ativo,
                id
        );
    }
}