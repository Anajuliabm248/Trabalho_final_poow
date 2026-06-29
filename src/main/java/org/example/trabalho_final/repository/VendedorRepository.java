package org.example.trabalho_final.repository;

import org.example.trabalho_final.model.Vendedor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class VendedorRepository {

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Vendedor> rowMapper = (rs, rowNum) -> {
        Vendedor v = new Vendedor();
        v.setId(rs.getInt("id"));
        v.setNome(rs.getString("nome"));
        v.setCpf(rs.getString("cpf"));
        v.setTelefone(rs.getString("telefone"));
        v.setEmail(rs.getString("email"));
        v.setSenha(rs.getString("senha"));
        v.setAtivo(rs.getBoolean("ativo"));
        return v;
    };

    public VendedorRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean inserir(Vendedor vendedor) {
        String sqlUsuario = """
                INSERT INTO usuario
                    (nome, cpf, telefone, email, senha, ativo, tipo)
                VALUES
                    (?, ?, ?, ?, ?, true, 'VENDEDOR')
                """;

        String sqlVendedor = """
                INSERT INTO vendedor (id)
                VALUES (?)
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        int linhas = jdbcTemplate.update(connection -> {

            PreparedStatement ps = connection.prepareStatement(
                    sqlUsuario,
                    Statement.RETURN_GENERATED_KEYS
            );

            ps.setString(1, vendedor.getNome());
            ps.setString(2, vendedor.getCpf());
            ps.setString(3, vendedor.getTelefone());
            ps.setString(4, vendedor.getEmail());
            ps.setString(5, vendedor.getSenha());

            return ps;

        }, keyHolder);

        if (linhas == 0 || keyHolder.getKey() == null) {
            return false;
        }

        int idGerado = keyHolder.getKey().intValue();

        vendedor.setId(idGerado);

        return jdbcTemplate.update(sqlVendedor, idGerado) > 0;
    }

    public Vendedor buscarPorEmail(String email) {
        String sql = """
                SELECT u.* FROM usuario u
                INNER JOIN vendedor v ON v.id = u.id
                WHERE u.email = ?
                """;

        return jdbcTemplate.query(
                sql,
                rowMapper,
                email
        ).stream().findFirst().orElse(null);
    }

    public List<Vendedor> listar() {
        String sql = """
                SELECT u.* FROM usuario u
                INNER JOIN vendedor v ON v.id = u.id
                WHERE u.ativo = true
                """;

        return jdbcTemplate.query(sql, rowMapper);
    }

    public void atualizar(Vendedor vendedor) {
        String sql = """
                UPDATE usuario SET nome=?, cpf=?, 
                telefone=?, email=? WHERE id=?
                """;

        jdbcTemplate.update(
                sql,
                vendedor.getNome(),
                vendedor.getCpf(),
                vendedor.getTelefone(),
                vendedor.getEmail(),
                vendedor.getId()
        );
    }
}