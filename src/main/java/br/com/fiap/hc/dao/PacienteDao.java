package br.com.fiap.hc.dao;

import br.com.fiap.hc.exception.EntidadeNaoEncontradaException;
import br.com.fiap.hc.factory.ConnectionFactory;
import br.com.fiap.hc.model.Endereco;
import br.com.fiap.hc.model.Paciente;
import jakarta.enterprise.context.ApplicationScoped;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class PacienteDao {

    private final Connection conexao;

    public PacienteDao() throws SQLException, ClassNotFoundException {
        conexao = ConnectionFactory.getConnection();
    }

    public void cadastrar(Paciente paciente) throws SQLException {
        PreparedStatement stmt = conexao.prepareStatement(
                "INSERT INTO T_HC_PACIENTE (ID_PACIENTE, NM_NOME, CD_CPF, DT_NASCIMENTO, NR_TELEFONE, DS_EMAIL, ID_ENDERECO) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)"
        );
        stmt.setInt(1, paciente.getIdPaciente());
        stmt.setString(2, paciente.getNome());
        stmt.setString(3, paciente.getCpf());
        stmt.setDate(4, java.sql.Date.valueOf(paciente.getDataNascimeto()));
        stmt.setString(5, paciente.getTelefone());
        stmt.setString(6, paciente.getEmail());
        stmt.setInt(7, paciente.getEndereco().getIdEndereco());
        stmt.executeUpdate();
    }

    public void atualizar(Paciente paciente) throws SQLException {
        PreparedStatement stmt = conexao.prepareStatement(
                "UPDATE T_HC_PACIENTE SET NM_NOME = ?, CD_CPF = ?, DT_NASCIMENTO = ?, NR_TELEFONE = ?, DS_EMAIL = ?, ID_ENDERECO = ? " +
                        "WHERE ID_PACIENTE = ?"
        );
        stmt.setString(1, paciente.getNome());
        stmt.setString(2, paciente.getCpf());
        stmt.setDate(3, java.sql.Date.valueOf(paciente.getDataNascimeto()));
        stmt.setString(4, paciente.getTelefone());
        stmt.setString(5, paciente.getEmail());
        stmt.setInt(6, paciente.getEndereco().getIdEndereco());
        stmt.setInt(7, paciente.getIdPaciente());
        stmt.executeUpdate();
    }

    public void remover(int idPaciente) throws SQLException {
        PreparedStatement stmt = conexao.prepareStatement("DELETE FROM T_HC_PACIENTE WHERE ID_PACIENTE = ?");
        stmt.setInt(1, idPaciente);
        stmt.executeUpdate();
    }

    public Paciente buscar(int idPaciente) throws SQLException, EntidadeNaoEncontradaException {
        PreparedStatement stmt = conexao.prepareStatement("SELECT * FROM T_HC_PACIENTE WHERE ID_PACIENTE = ?");
        stmt.setInt(1, idPaciente);
        ResultSet rs = stmt.executeQuery();

        if (!rs.next()) {
            throw new EntidadeNaoEncontradaException("Paciente não encontrado");
        }

        return parsePaciente(rs);
    }

    private Paciente parsePaciente(ResultSet rs) throws SQLException {
        int id = rs.getInt("ID_PACIENTE");
        String nome = rs.getString("NM_NOME");
        String cpf = rs.getString("CD_CPF");
        String dataNascimento = rs.getDate("DT_NASCIMENTO").toString();
        String telefone = rs.getString("NR_TELEFONE");
        String email = rs.getString("DS_EMAIL");
        int idEndereco = rs.getInt("ID_ENDERECO");

        return new Paciente(id, nome, cpf, dataNascimento, telefone, email, new Endereco(idEndereco));
    }

    public List<Paciente> listar() throws SQLException {
        PreparedStatement stmt = conexao.prepareStatement("SELECT * FROM T_HC_PACIENTE");
        ResultSet rs = stmt.executeQuery();

        List<Paciente> pacientes = new ArrayList<>();
        while (rs.next()) {
            pacientes.add(parsePaciente(rs));
        }

        return pacientes;
    }

    public void fecharConexao() {
        if (conexao != null) {
            try {
                conexao.close();
            } catch (SQLException e) {
                System.err.println("Erro ao fechar conexão do PacienteDao: " + e.getMessage());
            }
        }
    }
}
