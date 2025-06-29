package br.com.poli.biblioteca.repository;

import br.com.poli.biblioteca.model.Livro;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LivroRepository {
    private Connection conectar() throws SQLException {
        return DatabaseSetup.conectar();
    }

    public List<Livro> buscarTodos() {
        String sql = "SELECT * FROM livros";
        List<Livro> livros = new ArrayList<>();

        try (Connection conn = this.conectar();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Livro livro = new Livro(
                        rs.getString("isbn"),
                        rs.getString("titulo"),
                        rs.getString("autor"),
                        rs.getInt("anoPublicacao"),
                        rs.getInt("quantidadeTotal"),
                        rs.getInt("quantidadeDisponivel")
                );
                livros.add(livro);
        }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return livros;
    }

   public Optional<Livro> buscaPorISBN(String isbn) {
       String sql = "SELECT * FROM livros WHERE isbn = ?";

       try (Connection conn = this.conectar();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, isbn);
            ResultSet rs = pstmt.executeQuery();

           if (rs.next()) {
               return Optional.of(new Livro(
                       rs.getString("isbn"),
                       rs.getString("titulo"),
                       rs.getString("autor"),
                       rs.getInt("anoPublicacao"),
                       rs.getInt("quantidadeTotal"),
                       rs.getInt("quantidadeDisponivel")
               ));
           }
       } catch (SQLException e) {
           System.out.println(e.getMessage());
       }
       return Optional.empty();
   }

    public void adicionarLivro(Livro livro) {
        String sql = "INSERT INTO livros(isbn, titulo, autor, anoPublicacao, quantidadeTotal, quantidadeDisponivel) VALUES(?,?,?,?,?,?)";
        try (Connection conn = this.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, livro.getIsbn());
            pstmt.setString(2, livro.getTitulo());
            pstmt.setString(3, livro.getAutor());
            pstmt.setInt(4, livro.getAnoPublicacao());
            pstmt.setInt(5, livro.getQuantidadeTotal());
            pstmt.setInt(6, livro.getQuantidadeDisponivel());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }
    public void atualizarLivro(Livro livro) {
        String sql = "UPDATE livros SET titulo = ?, autor = ?, anoPublicacao = ?, quantidadeTotal = ?, quantidadeDisponivel = ? WHERE isbn = ?";
        try (Connection conn = this.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, livro.getTitulo());
            pstmt.setString(2, livro.getAutor());
            pstmt.setInt(3, livro.getAnoPublicacao());
            pstmt.setInt(4, livro.getQuantidadeTotal());
            pstmt.setInt(5, livro.getQuantidadeDisponivel());
            pstmt.setString(6, livro.getIsbn());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void removerlivroPorIsbn(String isbn) {
        String sql = "DELETE FROM livros WHERE isbn = ?";
        try (Connection conn = this.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, isbn);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void atualizarEstoque(String isbn, int novaQuantidadeTotal, int novaQuantidadeDisponivel) {
        String sql = "UPDATE livros SET quantidadeTotal = ?, quantidadeDisponivel = ? WHERE isbn = ?";
        try (Connection conn = this.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, novaQuantidadeTotal);
            pstmt.setInt(2, novaQuantidadeDisponivel);
            pstmt.setString(3, isbn);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public List<Livro> buscarPorTermo(String termo) {
        String sql = "SELECT * FROM livros WHERE titulo LIKE ? OR autor LIKE ? OR isbn LIKE ?";
        List<Livro> livros = new ArrayList<>();

        final String termoDeBusca = "%" + termo + "%";

        try (Connection conn = this.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, termoDeBusca);
            pstmt.setString(2, termoDeBusca);
            pstmt.setString(3, termoDeBusca);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                livros.add(new Livro(
                        rs.getString("isbn"),
                        rs.getString("titulo"),
                        rs.getString("autor"),
                        rs.getInt("anoPublicacao"),
                        rs.getInt("quantidadeTotal"),
                        rs.getInt("quantidadeDisponivel")
                ));
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return livros;
    }

    public Optional<Livro> buscarPorTituloEAutor(String titulo, String autor) {
        String sql = "SELECT * FROM livros WHERE titulo = ? AND autor = ?";
        try (Connection conn = this.conectar(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, titulo);
            pstmt.setString(2, autor);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(new Livro(rs.getString("isbn"), rs.getString("titulo"), rs.getString("autor"), rs.getInt("anoPublicacao"), rs.getInt("quantidadeTotal"), rs.getInt("quantidadeDisponivel")));
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return Optional.empty();
    }
}
