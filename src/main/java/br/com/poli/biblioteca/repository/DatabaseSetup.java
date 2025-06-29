package br.com.poli.biblioteca.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseSetup {

    private static final String DATABASE_URL = "jdbc:sqlite:biblioteca.db";

    public static void criarTabelas() {
        String sql = "CREATE TABLE IF NOT EXISTS livros ("
                + " isbn TEXT PRIMARY KEY,"
                + " titulo TEXT NOT NULL,"
                + " autor TEXT,"
                + " editora TEXT,"
                + " anoPublicacao INTEGER,"
                + " quantidadeTotal INTEGER,"
                + " quantidadeDisponivel INTEGER"
                + ");";

        String sqlUsuarios = "CREATE TABLE IF NOT EXISTS usuarios ("
                + " id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + " nome TEXT NOT NULL,"
                + " email TEXT NOT NULL UNIQUE,"
                + " tipo TEXT NOT NULL CHECK(tipo IN ('Aluno', 'Funcionario')),"
                + " matricula TEXT UNIQUE," // Apenas para Alunos
                + " cargo TEXT"      // Apenas para Funcionários
                + ");";

        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Tabela 'livros' verificada/criada com sucesso.");
            stmt.execute(sqlUsuarios);
            System.out.println("Tabela 'usuarios' verificada/criada com sucesso.");
        } catch (SQLException e) {
            System.err.println("Erro ao criar tabela: " + e.getMessage());
        }
    }

    public static Connection conectar() throws SQLException {
        return DriverManager.getConnection(DATABASE_URL);
    }
}