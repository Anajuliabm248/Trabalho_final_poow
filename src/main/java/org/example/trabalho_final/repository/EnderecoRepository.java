package org.example.trabalho_final.repository;

import org.example.trabalho_final.model.Endereco;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class EnderecoRepository {

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Endereco> rowMapper = (rs, rowNum) -> {
        Endereco e = new Endereco();
        e.setId(rs.getInt("id"));
        e.setClienteId(rs.getInt("cliente_id"));
        e.setLogradouro(rs.getString("logradouro"));
        e.setNumero(rs.getInt("numero"));
        e.setComplemento(rs.getString("complemento"));
        e.setBairro(rs.getString("bairro"));
        e.setCidade(rs.getString("cidade"));
        e.setEstado(rs.getString("estado"));
        e.setCep(rs.getString("cep"));
        e.setPais(rs.getString("pais"));
        return e;
    };

    public EnderecoRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean inserir(Endereco endereco) {

        String sql = """
                INSERT INTO endereco
                    (cliente_id, logradouro, numero, complemento, bairro, cidade, estado, cep, pais)
                VALUES
                    (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        return jdbcTemplate.update(
                sql,
                endereco.getClienteId(),
                endereco.getLogradouro(),
                endereco.getNumero(),
                endereco.getComplemento(),
                endereco.getBairro(),
                endereco.getCidade(),
                endereco.getEstado(),
                endereco.getCep(),
                endereco.getPais()
        ) > 0;
    }

    public Endereco buscarPorClienteId(int clienteId) {

        String sql = """
                SELECT * FROM endereco WHERE cliente_id = ?
                """;

        return jdbcTemplate.query(
                sql,
                rowMapper,
                clienteId
        ).stream().findFirst().orElse(null);
    }

    public void atualizar(Endereco endereco) {
        String sql = """
                UPDATE endereco SET logradouro=?, numero=?, complemento=?, 
                bairro=?, cidade=?, estado=?, cep=?, pais=?
                WHERE cliente_id=?
                """;
        jdbcTemplate.update(
                sql,
                endereco.getLogradouro(),
                endereco.getNumero(),
                endereco.getComplemento(),
                endereco.getBairro(),
                endereco.getCidade(),
                endereco.getEstado(),
                endereco.getCep(),
                endereco.getPais(),
                endereco.getClienteId()
        );
    }
}