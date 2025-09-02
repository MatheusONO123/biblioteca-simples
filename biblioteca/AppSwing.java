package biblioteca;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class AppSwing {

    public static void main(String[] args) {
        String[] opcoes = {"Cadastrar Livro", "Listar Livros", "Sair"};
        while (true) {
            int escolha = JOptionPane.showOptionDialog(
                    null,
                    "Escolha uma opção:",
                    "Biblioteca",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    opcoes,
                    opcoes[0]
            );

            if (escolha == 0) {
                cadastrarLivro();
            } else if (escolha == 1) {
                listarLivros();
            } else {
                break;
            }
        }
    }

    private static void cadastrarLivro() {
        String titulo = JOptionPane.showInputDialog("Digite o título do livro:");
        String autor  = JOptionPane.showInputDialog("Digite o autor do livro:");
        String anoStr = JOptionPane.showInputDialog("Digite o ano de publicação:");

        if (titulo == null || autor == null || anoStr == null) return;

        try (Connection conn = new ConexaoBD().conectar()) {
            String sql = "INSERT INTO livro (titulo, autor, ano) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, titulo);
                stmt.setString(2, autor);
                stmt.setInt(3, Integer.parseInt(anoStr));
                stmt.executeUpdate();
            }
            JOptionPane.showMessageDialog(null, "📚 Livro cadastrado com sucesso!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "❌ Erro ao cadastrar: " + e.getMessage());
        }
    }

    private static void listarLivros() {
        try (Connection conn = new ConexaoBD().conectar()) {
            String sql = "SELECT id, titulo, autor, ano FROM livro";
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

                // Modelo da tabela
                DefaultTableModel model = new DefaultTableModel(
                        new Object[]{"ID", "Título", "Autor", "Ano"}, 0);

                while (rs.next()) {
                    model.addRow(new Object[]{
                            rs.getInt("id"),
                            rs.getString("titulo"),
                            rs.getString("autor"),
                            rs.getInt("ano")
                    });
                }

                JTable tabela = new JTable(model);
                JScrollPane scrollPane = new JScrollPane(tabela);
                scrollPane.setPreferredSize(new java.awt.Dimension(500, 300));

                JOptionPane.showMessageDialog(null, scrollPane, "📖 Livros Cadastrados", JOptionPane.PLAIN_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "❌ Erro ao listar: " + e.getMessage());
        }
    }
}
