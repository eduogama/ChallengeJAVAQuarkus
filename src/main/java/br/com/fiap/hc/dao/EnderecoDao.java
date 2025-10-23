package br.com.fiap.hc.dao;

import br.com.fiap.hc.exception.EntidadeNaoEncontradaException;
import br.com.fiap.hc.factory.ConnectionFactory;
import br.com.fiap.hc.model.Endereco;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EnderecoDao {

    private final Connection conexao;

    public EnderecoDao() throws SQLException, ClassNotFoundException {
        conexao = ConnectionFactory.getConnection();
    }

    public void cadastrar(Endereco endereco) throws SQLException {
        if (endereco.getLogradouro() == null || endereco.getLogradouro().isEmpty()) {
            throw new IllegalArgumentException("Logradouro não pode ser nulo ou vazio.");
        }

        PreparedStatement stmt = conexao.prepareStatement(
                "INSERT INTO T_HC_ENDERECO (ID_ENDERECO, DS_LOGRADOURO, NR_NUMERO, DS_COMPLEMENTO, NM_BAIRRO, NM_CIDADE, SG_ESTADO, NR_CEP) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?)"
        );
        stmt.setInt(1, endereco.getIdEndereco());
        stmt.setString(2, endereco.getLogradouro());
        stmt.setInt(3, endereco.getNumero());
        stmt.setString(4, endereco.getComplemento());
        stmt.setString(5, endereco.getBairro());
        stmt.setString(6, endereco.getCidade());
        stmt.setString(7, endereco.getEstado());
        stmt.setString(8, endereco.getCep());
        stmt.executeUpdate();
    }

    public void atualizar(Endereco endereco) throws SQLException {
        PreparedStatement stmt = conexao.prepareStatement(
                "UPDATE T_HC_ENDERECO SET DS_LOGRADOURO = ?, NR_NUMERO = ?, DS_COMPLEMENTO = ?, NM_BAIRRO = ?, NM_CIDADE = ?, SG_ESTADO = ?, NR_CEP = ? " +
                        "WHERE ID_ENDERECO = ?"
        );
        stmt.setString(1, endereco.getLogradouro());
        stmt.setInt(2, endereco.getNumero());
        stmt.setString(3, endereco.getComplemento());
        stmt.setString(4, endereco.getBairro());
        stmt.setString(5, endereco.getCidade());
        stmt.setString(6, endereco.getEstado());
        stmt.setString(7, endereco.getCep());
        stmt.setInt(8, endereco.getIdEndereco());
        stmt.executeUpdate();
    }

    public void remover(int idEndereco) throws SQLException {
        PreparedStatement stmt = conexao.prepareStatement("DELETE FROM T_HC_ENDERECO WHERE ID_ENDERECO = ?");
        stmt.setInt(1, idEndereco);
        stmt.executeUpdate();
    }

    public Endereco buscar(int idEndereco) throws SQLException, EntidadeNaoEncontradaException {
        PreparedStatement stmt = conexao.prepareStatement("SELECT * FROM T_HC_ENDERECO WHERE ID_ENDERECO = ?");
        stmt.setInt(1, idEndereco);
        ResultSet rs = stmt.executeQuery();

        if (!rs.next()) {
            throw new EntidadeNaoEncontradaException("Endereço não encontrado");
        }

        return parseEndereco(rs);
    }

    private Endereco parseEndereco(ResultSet rs) throws SQLException {
        int id = rs.getInt("ID_ENDERECO");
        String logradouro = rs.getString("DS_LOGRADOURO");
        int numero = rs.getInt("NR_NUMERO");
        String complemento = rs.getString("DS_COMPLEMENTO");
        String bairro = rs.getString("NM_BAIRRO");
        String cidade = rs.getString("NM_CIDADE");
        String estado = rs.getString("SG_ESTADO");
        String cep = rs.getString("NR_CEP");

        return new Endereco(id, logradouro, numero, complemento, bairro, cidade, estado, cep);
    }

    public List<Endereco> listar() throws SQLException {
        PreparedStatement stmt = conexao.prepareStatement("SELECT * FROM T_HC_ENDERECO");
        ResultSet rs = stmt.executeQuery();

        List<Endereco> enderecos = new ArrayList<>();
        while (rs.next()) {
            enderecos.add(parseEndereco(rs));
        }

        return enderecos;
    }

    public void fecharConexao() {
        if (conexao != null) {
            try {
                conexao.close();
                // Opcional: System.out.println("Conexão do EnderecoDao fechada.");
            } catch (SQLException e) {
                System.err.println("Erro ao fechar conexão do EnderecoDao: " + e.getMessage());
            }
        }
    }
}
