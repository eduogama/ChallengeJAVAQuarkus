package br.com.fiap.hc.dao;

import br.com.fiap.hc.exception.EntidadeNaoEncontradaException;
import br.com.fiap.hc.factory.ConnectionFactory;
import br.com.fiap.hc.model.Consulta;
import br.com.fiap.hc.model.Medico;
import br.com.fiap.hc.model.Paciente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ConsultaDao {

    private final Connection conexao;
    private final PacienteDao pacienteDao;
    private final MedicoDao medicoDao;

    public ConsultaDao() throws SQLException, ClassNotFoundException {
        this.conexao = ConnectionFactory.getConnection();
        this.pacienteDao = new PacienteDao();
        this.medicoDao = new MedicoDao();
    }


    public void cadastrar(Consulta consulta) throws SQLException {
        String sql = "INSERT INTO T_HC_CONSULTA (ID_CONSULTA, DT_HORA, ST_STATUS, DS_AREA_MEDICA, ID_PACIENTE, ID_MEDICO) VALUES (?, ?, ?, ?, ?, ?)";

        if (consulta.getPaciente() == null || consulta.getMedico() == null) {
            throw new IllegalArgumentException("Paciente ou Médico não podem ser nulos ao cadastrar.");
        }

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, consulta.getIdConsulta());
            stmt.setTimestamp(2, new Timestamp(consulta.getDataHora().getTime()));
            stmt.setString(3, consulta.getStatus());
            stmt.setString(4, consulta.getAreaMedica());
            stmt.setInt(5, consulta.getPaciente().getIdPaciente());
            stmt.setInt(6, consulta.getMedico().getIdMedico());

            int rowsAffected = stmt.executeUpdate();
            System.out.println("Consulta cadastrada com sucesso. Linhas afetadas: " + rowsAffected);
        } catch (SQLException e) {
            System.err.println("Erro ao cadastrar consulta: " + e.getMessage());
            throw e;
        }
    }


    public Consulta buscar(int id) throws SQLException, EntidadeNaoEncontradaException {
        Consulta consulta = null;
        String sql = "SELECT * FROM T_HC_CONSULTA WHERE ID_CONSULTA = ?";

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    consulta = parseConsulta(rs);
                }
            }
        }
        if (consulta == null) {
            throw new EntidadeNaoEncontradaException("Consulta com ID " + id + " não encontrada.");
        }
        return consulta;
    }


    public List<Consulta> listarConsulta() throws SQLException {
        List<Consulta> consultas = new ArrayList<>();
        String sql = "SELECT * FROM T_HC_CONSULTA";

        try (PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                try {
                    consultas.add(parseConsulta(rs));
                } catch (EntidadeNaoEncontradaException e) {
                    System.out.println("Aviso: Falha ao carregar FK para uma consulta: " + e.getMessage());
                }
            }
        }
        return consultas;
    }


    public void atualizar(Consulta consulta) throws SQLException {
        String sql = "UPDATE T_HC_CONSULTA SET DT_HORA = ?, ST_STATUS = ?, DS_AREA_MEDICA = ?, ID_PACIENTE = ?, ID_MEDICO = ? WHERE ID_CONSULTA = ?";

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {

            if (consulta.getPaciente() == null || consulta.getMedico() == null) {
                System.err.println("Erro Crítico: Paciente ou Médico nulo no objeto Consulta. Falha ao atualizar.");
                throw new SQLException("Objeto Paciente ou Médico não carregado (null), viola restrição de integridade.");
            }

            stmt.setTimestamp(1, new Timestamp(consulta.getDataHora().getTime()));
            stmt.setString(2, consulta.getStatus());
            stmt.setString(3, consulta.getAreaMedica());
            stmt.setInt(4, consulta.getPaciente().getIdPaciente());
            stmt.setInt(5, consulta.getMedico().getIdMedico());
            stmt.setInt(6, consulta.getIdConsulta());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                System.out.println("Aviso: Consulta com ID " + consulta.getIdConsulta() + " não foi encontrada para atualização.");
            } else {
                System.out.println("Consulta atualizada com sucesso. Linhas afetadas: " + rowsAffected);
            }
        }
    }


    public void remover(int idConsulta) throws SQLException {
        String sql = "DELETE FROM T_HC_CONSULTA WHERE ID_CONSULTA = ?";
        try(PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, idConsulta);
            stmt.executeUpdate();
        }
    }

    private Consulta parseConsulta(ResultSet rs) throws SQLException, EntidadeNaoEncontradaException {
        int idPaciente = rs.getInt("ID_PACIENTE");
        int idMedico = rs.getInt("ID_MEDICO");

        Paciente paciente = pacienteDao.buscar(idPaciente);
        Medico medico = medicoDao.buscar(idMedico);

        return new Consulta(
                rs.getInt("ID_CONSULTA"),
                rs.getTimestamp("DT_HORA"),
                rs.getString("ST_STATUS"),
                rs.getString("DS_AREA_MEDICA"),
                paciente,
                medico
        );
    }

    public void fecharConexao() {
        if (conexao != null) {
            try {
                conexao.close();
            } catch (SQLException e) {
                System.err.println("Erro ao fechar conexão do ConsultaDao: " + e.getMessage());
            }
        }
    }
}