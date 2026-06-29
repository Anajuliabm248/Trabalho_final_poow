package org.example.trabalho_final.repository;

import org.example.trabalho_final.model.Cliente;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class ClienteRepository {

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Cliente> rowMapper = (rs, rowNum) -> {
        Cliente c = new Cliente();
        c.setId(rs.getInt("id"));
        c.setNome(rs.getString("nome"));
        c.setCpf(rs.getString("cpf"));
        c.setTelefone(rs.getString("telefone"));
        c.setEmail(rs.getString("email"));
        c.setSenha(rs.getString("senha"));
        c.setAtivo(rs.getBoolean("ativo"));
        return c;
    };

    public ClienteRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Insere em usuario e cliente dentro de uma transação
    public boolean inserir(Cliente cliente) {
        String sqlUsuario = """
                INSERT INTO usuario
                    (nome, cpf, telefone, email, senha, ativo, tipo)
                VALUES
                    (?, ?, ?, ?, ?, ?, ?)
                """;

        String sqlCliente = """
                INSERT INTO cliente (id)
                VALUES (?)
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        int linhas = jdbcTemplate.update(connection -> {

            PreparedStatement ps = connection.prepareStatement(
                    sqlUsuario,
                    Statement.RETURN_GENERATED_KEYS
            );

            ps.setString(1, cliente.getNome());
            ps.setString(2, cliente.getCpf());
            ps.setString(3, cliente.getTelefone());
            ps.setString(4, cliente.getEmail());
            ps.setString(5, cliente.getSenha());
            ps.setBoolean(6, cliente.isAtivo());
            ps.setString(7, cliente.getTipo());

            return ps;

        }, keyHolder);

        if (linhas == 0 || keyHolder.getKey() == null) {
            return false;
        }

        int idGerado = keyHolder.getKey().intValue();

        jdbcTemplate.update(sqlCliente, idGerado);

        cliente.setId(idGerado);

        return true;
    }

    public Cliente buscarPorId(int id) {
        String sql = """
                SELECT u.*
                FROM usuario u
                INNER JOIN cliente c ON c.id = u.id
                WHERE u.id = ?
                """;

        return jdbcTemplate.query(sql, rowMapper, id)
                .stream()
                .findFirst()
                .orElse(null);
    }

    public Cliente buscarPorEmail(String email) {
        String sql = """
                SELECT u.*
                FROM usuario u
                INNER JOIN cliente c ON c.id = u.id
                WHERE u.email = ?
                """;

        return jdbcTemplate.query(sql, rowMapper, email)
                .stream()
                .findFirst()
                .orElse(null);
    }

    public List<Cliente> listar() {
        String sql = """
                SELECT u.*
                FROM usuario u
                INNER JOIN cliente c ON c.id = u.id
                WHERE u.ativo = true
                """;

        return jdbcTemplate.query(sql, rowMapper);
    }

    public void atualizar(Cliente cliente) {
        String sql = """
                UPDATE usuario
                SET nome = ?,
                    cpf = ?,
                    telefone = ?,
                    email = ?
                WHERE id = ?
                """;

        jdbcTemplate.update(
                sql,
                cliente.getNome(),
                cliente.getCpf(),
                cliente.getTelefone(),
                cliente.getEmail(),
                cliente.getId()
        );
    }

    public void excluir(int id) {
        String sql = """
                DELETE FROM usuario
                WHERE id = ?
                """;

        jdbcTemplate.update(sql, id);
    }
}