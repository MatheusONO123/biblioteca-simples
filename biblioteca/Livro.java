package biblioteca;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

public class Livro {
    private String titulo;
    private String autor;
    private int ano;

    Scanner scanner = new Scanner(System.in);
    ConexaoBD conexao = new ConexaoBD();

    public void cadastrarLivro() {
        System.out.print("Digite o título do livro: ");
        titulo = scanner.nextLine();

        System.out.print("Digite o autor do livro: ");
        autor = scanner.nextLine();

        System.out.print("Digite o ano de publicação: ");
        ano = scanner.nextInt();

        try (Connection conn = conexao.conectar()) {
            String sql = "INSERT INTO livro (titulo, autor, ano) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, titulo);
            stmt.setString(2, autor);
            stmt.setInt(3, ano);
            stmt.executeUpdate();
            System.out.println("Livro cadastrado com sucesso!");
        } catch (Exception e) {
            System.out.println("Erro ao cadastrar: " + e.getMessage());
        }

        scanner.nextLine(); // limpar buffer
    }

    public void listarLivros() {
        try (Connection conn = conexao.conectar()) {
            String sql = "SELECT * FROM livro";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id"));
                System.out.println("Título: " + rs.getString("titulo"));
                System.out.println("Autor: " + rs.getString("autor"));
                System.out.println("Ano: " + rs.getInt("ano"));
                System.out.println("-----------------------");
            }
        } catch (Exception e) {
            System.out.println("Erro ao listar: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Livro livro = new Livro();

        while (true) {
            System.out.println("\n1 - Cadastrar Livro");
            System.out.println("2 - Listar Livros");
            System.out.println("0 - Sair");
            System.out.print("Escolha: ");
            int op = livro.scanner.nextInt();
            livro.scanner.nextLine(); // limpar buffer

            if (op == 1) {
                livro.cadastrarLivro();
            } else if (op == 2) {
                livro.listarLivros();
            } else if (op == 0) {
                break;
            } else {
                System.out.println("Opção inválida.");
            }
        }
    }
}