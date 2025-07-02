package br.com.poli.biblioteca.repository;

import br.com.poli.biblioteca.model.Aluno;
import br.com.poli.biblioteca.model.Funcionario;
import br.com.poli.biblioteca.model.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UsuarioRepository {

    private Connection conectar() throws SQLException {
        return DatabaseSetup.conectar();
    }

    private Usuario criarUsuario(ResultSet rs) throws SQLException {
        String tipo = rs.getString("tipo");
        int id = rs.getInt("id");
        String nome = rs.getString("nome");
        String email = rs.getString("email");
        String cpf = rs.getString("cpf");
        
        if ("Aluno".equals(tipo)) {
            return new Aluno(id, nome, email, rs.getString("matricula"), cpf);
        } else {
            return new Funcionario(id, nome, email, cpf, rs.getString("cargo"));
        }
    }

    public List<Usuario> buscarTodos() {
        String sql = "SELECT * FROM usuarios";
        List<Usuario> usuarios = new ArrayList<>();
        try (Connection conn = this.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                usuarios.add(criarUsuario(rs));
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return usuarios;
    }

    public Optional<Usuario> buscarPorId(int id) {
        String sql = "SELECT * FROM usuarios WHERE id = ?";
        try (Connection conn = this.conectar(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(criarUsuario(rs));
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return Optional.empty();
    }
    
    public Optional<Usuario> buscarPorCPF(String cpf) {
        String sql = "SELECT * FROM usuarios WHERE cpf = ?";
        try (Connection conn = this.conectar(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, cpf);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(criarUsuario(rs));
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return Optional.empty();
    }

    public Optional<Usuario> buscarPorEmail(String email) {
        String sql = "SELECT * FROM usuarios WHERE email = ?";
        try (Connection conn = this.conectar(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(criarUsuario(rs));
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return Optional.empty();
    }

    public Optional<Usuario> buscarPorMatricula(String matricula) {
        String sql = "SELECT * FROM usuarios WHERE matricula = ?";
        try (Connection conn = this.conectar(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, matricula);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(criarUsuario(rs));
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return Optional.empty();
    }

    public List<Usuario> buscarPorTermo(String termo, String tipoUsuario) {
        StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM usuarios WHERE (nome LIKE ? OR email LIKE ? OR matricula LIKE ? OR cpf LIKE ?)");
        if (tipoUsuario != null && !tipoUsuario.isBlank()) {
            sqlBuilder.append(" AND tipo = ?");
        }
        List<Usuario> usuarios = new ArrayList<>();
        final String termoDeBusca = "%" + termo + "%";
        try (Connection conn = this.conectar(); PreparedStatement pstmt = conn.prepareStatement(sqlBuilder.toString())) {
            pstmt.setString(1, termoDeBusca);
            pstmt.setString(2, termoDeBusca);
            pstmt.setString(3, termoDeBusca);
            if (tipoUsuario != null && !tipoUsuario.isBlank()) {
                pstmt.setString(4, tipoUsuario);
            }
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                usuarios.add(criarUsuario(rs));
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return usuarios;
    }

    public void salvarUsuario(Usuario usuario) {
        String sql = "INSERT INTO usuarios(nome, email, tipo, cpf, matricula, cargo) VALUES(?,?,?,?,?,?)";
        try (Connection conn = this.conectar(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, usuario.getNome());
            pstmt.setString(2, usuario.getEmail());
            pstmt.setString(3, usuario.getTipo());
            pstmt.setString(4, usuario.getCPF());

            if (usuario instanceof Aluno) {
                pstmt.setString(5, ((Aluno) usuario).getMatricula());
                pstmt.setNull(6, Types.VARCHAR);                     
            } else if (usuario instanceof Funcionario) {
                pstmt.setNull(5, Types.VARCHAR);                      
                pstmt.setString(6, ((Funcionario) usuario).getCargo());
            }
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void removerPorCPF(String cpf) {
        String sql = "DELETE FROM usuarios WHERE cpf = ?";
        try (Connection conn = this.conectar(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, cpf);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao remover usuário por CPF: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}