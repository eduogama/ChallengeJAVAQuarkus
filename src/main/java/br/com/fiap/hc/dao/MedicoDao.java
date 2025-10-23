package br.com.fiap.hc.dao;

import br.com.fiap.hc.exception.EntidadeNaoEncontradaException;
import br.com.fiap.hc.factory.ConnectionFactory;
import br.com.fiap.hc.model.Medico;
import jakarta.enterprise.context.ApplicationScoped;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class MedicoDao {

    private final Connection conexao;

    public MedicoDao() throws SQLException, ClassNotFoundException {
        conexao = ConnectionFactory.getConnection();
    }

    public void fecharConexao() {
        if (conexao != null) {
            try {
                conexao.close();
                System.out.println("Conexão do MedicoDao fechada com sucesso.");
            } catch (SQLException e) {
                System.out.println("Erro ao fechar conexão do MedicoDao: " + e.getMessage());
            }
        }
    }

    public List<Medico> listarMedicos() throws SQLException {
        List<Medico> medicos = new ArrayList<>();
        try (PreparedStatement stmt = conexao.prepareStatement("SELECT * FROM T_HC_MEDICO");
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                medicos.add(parseMedico(rs));
            }
            System.out.println("Total de médicos mapeados: " + medicos.size());
        }

        return medicos;
    }


    public Medico buscar(int id) throws SQLException, EntidadeNaoEncontradaException {
        Medico medico = null;
        String sql = "SELECT ID_MEDICO, NM_MEDICO, NR_CRM, DS_ESPECIALIDADE, DS_EMAIL, NR_TELEFONE FROM T_HC_MEDICO WHERE ID_MEDICO = ?";

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    medico = parseMedico(rs);
                }
            }
        }

        if (medico == null) {
            throw new EntidadeNaoEncontradaException("Médico com ID " + id + " não encontrado.");
        }
        return medico;
    }

    public void cadastrarMedico(Medico medico) throws SQLException {
        if (medico == null) {
            throw new SQLException("Objeto Medico nulo");
        }
        String sql = "INSERT INTO T_HC_MEDICO (ID_MEDICO, NM_MEDICO, NR_CRM, DS_ESPECIALIDADE, DS_EMAIL, NR_TELEFONE) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, medico.getIdMedico());
            stmt.setString(2, medico.getNomeM());
            stmt.setString(3, medico.getCrm());
            stmt.setString(4, medico.getEspecialidade());
            stmt.setString(5, medico.getEmail());
            stmt.setString(6, medico.getTelefone());

            int rowsAffected = stmt.executeUpdate();
            System.out.println("Médico cadastrado com sucesso. Linhas afetadas: " + rowsAffected);
        } catch (SQLException e) {
            System.out.println("Erro ao cadastrar médico: " + e.getMessage());
            throw e;
        }
    }

    public void atualizarMedico(Medico medico) throws SQLException {
        String sql = "UPDATE T_HC_MEDICO SET NM_MEDICO = ?, NR_CRM = ?, DS_ESPECIALIDADE = ?, DS_EMAIL = ?, NR_TELEFONE = ? WHERE ID_MEDICO = ?";

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, medico.getNomeM());
            stmt.setString(2, medico.getCrm());
            stmt.setString(3, medico.getEspecialidade());
            stmt.setString(4, medico.getEmail());
            stmt.setString(5, medico.getTelefone());
            stmt.setInt(6, medico.getIdMedico());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                System.out.println("Aviso: Nenhum médico com ID " + medico.getIdMedico() + " foi encontrado para atualização.");
            } else {
                System.out.println("Médico atualizado com sucesso. Linhas afetadas: " + rowsAffected);
            }
        }
    }

    public void removerMedico(int idMedico) throws SQLException {
        try (PreparedStatement stmt = conexao.prepareStatement("DELETE FROM T_HC_MEDICO WHERE ID_MEDICO = ?")) {
            stmt.setInt(1, idMedico);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                System.out.println("Aviso: Nenhum médico com ID " + idMedico + " foi encontrado para exclusão.");
            } else {
                System.out.println("Médico removido com sucesso. Linhas afetadas: " + rowsAffected);
            }
        }
    }

    private Medico parseMedico(ResultSet rs) throws SQLException {
        int idMedico = rs.getInt("ID_MEDICO");
        String nomeM = rs.getString("NM_MEDICO");
        String crm = rs.getString("NR_CRM");
        String especialidade = rs.getString("DS_ESPECIALIDADE");
        String email = rs.getString("DS_EMAIL");
        String telefone = rs.getString("NR_TELEFONE");

        return new Medico(idMedico, nomeM, crm, especialidade, email, telefone);
    }
}