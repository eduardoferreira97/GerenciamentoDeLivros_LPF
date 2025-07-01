package br.com.poli.biblioteca.repository;

import br.com.poli.biblioteca.model.Emprestimo;
import br.com.poli.biblioteca.model.StatusEmprestimo;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EmprestimoRepository {
    private Connection conectar() throws SQLException {
        return DatabaseSetup.conectar();
    }

    private Emprestimo criarEmprestimoDoResultSet(ResultSet rs) throws SQLException {
        return new Emprestimo(
                rs.getInt("id"),
                rs.getString("livro_isbn"),
                rs.getInt("usuario_id"),
                LocalDate.parse(rs.getString("data_emprestimo")),
                LocalDate.parse(rs.getString("data_prevista_devolucao")),
                rs.getString("data_devolucao_real") != null ? LocalDate.parse(rs.getString("data_devolucao_real")) : null,
                StatusEmprestimo.valueOf(rs.getString("status"))
        );
    }

    public void salvar(Emprestimo emprestimo) {
        String sql = "INSERT INTO emprestimos(livro_isbn, usuario_id, data_emprestimo, data_prevista_devolucao, status) VALUES(?,?,?,?,?)";
        try (Connection conn = this.conectar(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, emprestimo.getLivroIsbn());
            pstmt.setInt(2, emprestimo.getUsuarioId());
            pstmt.setString(3, emprestimo.getDataEmprestimo().toString());
            pstmt.setString(4, emprestimo.getDataPrevistaDevolucao().toString());
            pstmt.setString(5, emprestimo.getStatus().toString());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar empréstimo: " + e.getMessage());
        }
    }

    public void atualizar(Emprestimo emprestimo) {
        String sql = "UPDATE emprestimos SET data_devolucao_real = ?, status = ? WHERE id = ?";
        try (Connection conn = this.conectar(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, emprestimo.getDataDevolucaoReal() != null ? emprestimo.getDataDevolucaoReal().toString() : null);
            pstmt.setString(2, emprestimo.getStatus().toString());
            pstmt.setInt(3, emprestimo.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar empréstimo: " + e.getMessage());
        }
    }

    public Optional<Emprestimo> buscarPorId(int id) {
        String sql = "SELECT * FROM emprestimos WHERE id = ?";
        try (Connection conn = this.conectar(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return Optional.of(criarEmprestimoDoResultSet(rs));
        } catch (SQLException e) {
            System.err.println("Erro ao buscar empréstimo por ID: " + e.getMessage());
        }
        return Optional.empty();
    }

    public List<Emprestimo> buscarAtivosPorUsuarioId(int usuarioId) {
        String sql = "SELECT * FROM emprestimos WHERE usuario_id = ? AND status = 'ATIVO'";
        List<Emprestimo> emprestimos = new ArrayList<>();
        try (Connection conn = this.conectar(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, usuarioId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) emprestimos.add(criarEmprestimoDoResultSet(rs));
        } catch (SQLException e) {
            System.err.println("Erro ao buscar empréstimos ativos: " + e.getMessage());
        }
        return emprestimos;
    }

    public List<Emprestimo> buscarTodosPorUsuarioId(int usuarioId) {
        String sql = "SELECT * FROM emprestimos WHERE usuario_id = ?";
        List<Emprestimo> emprestimos = new ArrayList<>();
        try (Connection conn = this.conectar(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, usuarioId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) emprestimos.add(criarEmprestimoDoResultSet(rs));
        } catch (SQLException e) {
            System.err.println("Erro ao buscar histórico do usuário: " + e.getMessage());
        }
        return emprestimos;
    }

    public List<Emprestimo> buscarTodosAtrasados() {
        String sql = "SELECT * FROM emprestimos WHERE status = 'ATIVO' AND data_prevista_devolucao < ?";
        List<Emprestimo> emprestimos = new ArrayList<>();
        try (Connection conn = this.conectar(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, LocalDate.now().toString());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) emprestimos.add(criarEmprestimoDoResultSet(rs));
        } catch (SQLException e) {
            System.err.println("Erro ao buscar empréstimos atrasados: " + e.getMessage());
        }
        return emprestimos;
    }
}