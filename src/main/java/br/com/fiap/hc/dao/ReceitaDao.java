package br.com.fiap.hc.dao;

import br.com.fiap.hc.exception.EntidadeNaoEncontradaException;
import br.com.fiap.hc.factory.ConnectionFactory;
import br.com.fiap.hc.model.Consulta;
import br.com.fiap.hc.model.Receita;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReceitaDao {

    private final Connection conexao;

    public ReceitaDao() throws SQLException, ClassNotFoundException {
        conexao = ConnectionFactory.getConnection();
    }

    public void fecharConexao() {
        if (conexao != null) {
            try {
                conexao.close();
            } catch (SQLException e) {
                System.err.println("Erro ao fechar conexão do ReceitaDao: " + e.getMessage());
            }
        }
    }

    public void cadastrar(Receita receita) throws SQLException {
        try (PreparedStatement stmt = conexao.prepareStatement(
                "INSERT INTO T_HC_RECEITA (ID_RECEITA, DS_MEDICAMENTO, DS_DOSAGEM, DT_EMISSAO, ID_CONSULTA) " +
                        "VALUES (?, ?, ?, ?, ?)"
        )) {
            stmt.setInt(1, receita.getIdReceita());
            stmt.setString(2, receita.getMedicamento());
            stmt.setString(3, receita.getDosagem());

            stmt.setDate(4, new java.sql.Date(receita.getDataEmissao().getTime()));

            stmt.setInt(5, receita.getConsulta().getIdConsulta());
            stmt.executeUpdate();
            System.out.println("Receita ID " + receita.getIdReceita() + " cadastrada com sucesso.");
        }
    }

    public void atualizar(Receita receita) throws SQLException {
        try (PreparedStatement stmt = conexao.prepareStatement(
                "UPDATE T_HC_RECEITA SET DS_MEDICAMENTO = ?, DS_DOSAGEM = ?, DT_EMISSAO = ?, ID_CONSULTA = ? " +
                        "WHERE ID_RECEITA = ?"
        )) {
            stmt.setString(1, receita.getMedicamento());
            stmt.setString(2, receita.getDosagem());

            stmt.setDate(3, new java.sql.Date(receita.getDataEmissao().getTime()));

            stmt.setInt(4, receita.getConsulta().getIdConsulta());
            stmt.setInt(5, receita.getIdReceita());
            stmt.executeUpdate();
            System.out.println("Receita ID " + receita.getIdReceita() + " atualizada com sucesso.");
        }
    }

    public void remover(int idReceita) throws SQLException {
        try (PreparedStatement stmt = conexao.prepareStatement("DELETE FROM T_HC_RECEITA WHERE ID_RECEITA = ?")) {
            stmt.setInt(1, idReceita);
            int rows = stmt.executeUpdate();
            System.out.println("Receita ID " + idReceita + " removida. Linhas afetadas: " + rows);
        }
    }

    public Receita buscar(int idReceita) throws SQLException, EntidadeNaoEncontradaException {
        try (PreparedStatement stmt = conexao.prepareStatement("SELECT * FROM T_HC_RECEITA WHERE ID_RECEITA = ?")) {
            stmt.setInt(1, idReceita);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    throw new EntidadeNaoEncontradaException("Receita com ID " + idReceita + " não encontrada.");
                }
                return parseReceita(rs);
            }
        }
    }

    private Receita parseReceita(ResultSet rs) throws SQLException {
        int id = rs.getInt("ID_RECEITA");
        String medicamento = rs.getString("DS_MEDICAMENTO");
        String dosagem = rs.getString("DS_DOSAGEM");
        Date dataEmissao = rs.getDate("DT_EMISSAO");
        int idConsulta = rs.getInt("ID_CONSULTA");

        return new Receita(id, medicamento, dosagem, dataEmissao, new Consulta(idConsulta));
    }

    public List<Receita> listar() throws SQLException {
        List<Receita> receitas = new ArrayList<>();
        try (PreparedStatement stmt = conexao.prepareStatement("SELECT * FROM T_HC_RECEITA");
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                receitas.add(parseReceita(rs));
            }
        }
        return receitas;
    }
}