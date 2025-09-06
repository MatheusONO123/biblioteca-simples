package biblioteca;

import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class AppSwing {

    public static void main(String[] args) {
        while (true) {
            String[] opcoes = {
                    "Cadastrar Livro",
                    "Listar Livros",
                    "Registrar Empréstimo",
                    "Registrar Devolução",
                    "Ver Empréstimos",
                    "Remover Livro",
                    "Sair"
            };

            String escolha = (String) JOptionPane.showInputDialog(
                    null,
                    "Escolha uma opção:",
                    "Biblioteca",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    opcoes,
                    opcoes[0]
            );

            if (escolha == null || escolha.equals("Sair")) {
                break;
            }

            switch (escolha) {
                case "Cadastrar Livro":
                    cadastrarLivro();
                    break;
                case "Listar Livros":
                    listarLivros();
                    break;
                case "Registrar Empréstimo":
                    registrarEmprestimo();
                    break;
                case "Registrar Devolução":
                    registrarDevolucao();
                    break;
                case "Ver Empréstimos":
                    verEmprestimos();
                    break;
                case "Remover Livro":
                    removerLivro();
                    break;
            }
        }
    }

    private static void cadastrarLivro() {
        String titulo = JOptionPane.showInputDialog("Digite o título do livro:");
        if (titulo == null || titulo.trim().isEmpty()) return;

        try (Connection con = ConexaoBD.conectar();
             PreparedStatement ps = con.prepareStatement("INSERT INTO livro (titulo, disponivel) VALUES (?, true)")) {
            ps.setString(1, titulo);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(null, "Livro cadastrado com sucesso!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro: " + e.getMessage());
        }
    }

    private static void listarLivros() {
        try (Connection con = ConexaoBD.conectar();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM livro")) {

            String[] colunas = {"ID", "Título", "Disponível"};
            DefaultTableModel model = new DefaultTableModel(colunas, 0);

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("titulo"),
                        rs.getBoolean("disponivel") ? "Sim" : "Não"
                });
            }

            JTable tabela = new JTable(model);
            JOptionPane.showMessageDialog(null, new JScrollPane(tabela), "Lista de Livros", JOptionPane.PLAIN_MESSAGE);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro: " + e.getMessage());
        }
    }

    private static void registrarEmprestimo() {
        String usuario = JOptionPane.showInputDialog("Digite o nome do usuário:");
        String idLivroStr = JOptionPane.showInputDialog("Digite o ID do livro:");

        if (usuario == null || idLivroStr == null) return;

        try {
            int idLivro = Integer.parseInt(idLivroStr);

            try (Connection con = ConexaoBD.conectar();
                 PreparedStatement ps = con.prepareStatement(
                         "INSERT INTO emprestimo (usuario, livro_id, data_emprestimo, data_devolucao) VALUES (?, ?, NOW(), DATE_ADD(NOW(), INTERVAL 7 DAY))")) {

                ps.setString(1, usuario);
                ps.setInt(2, idLivro);
                ps.executeUpdate();

                try (PreparedStatement ps2 = con.prepareStatement("UPDATE livro SET disponivel = false WHERE id = ?")) {
                    ps2.setInt(1, idLivro);
                    ps2.executeUpdate();
                }

                JOptionPane.showMessageDialog(null, "Empréstimo registrado com sucesso!");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro: " + e.getMessage());
        }
    }

    private static void registrarDevolucao() {
        String idLivroStr = JOptionPane.showInputDialog("Digite o ID do livro devolvido:");
        if (idLivroStr == null) return;

        try {
            int idLivro = Integer.parseInt(idLivroStr);

            try (Connection con = ConexaoBD.conectar()) {
                try (PreparedStatement ps = con.prepareStatement("DELETE FROM emprestimo WHERE livro_id = ?")) {
                    ps.setInt(1, idLivro);
                    ps.executeUpdate();
                }

                try (PreparedStatement ps2 = con.prepareStatement("UPDATE livro SET disponivel = true WHERE id = ?")) {
                    ps2.setInt(1, idLivro);
                    ps2.executeUpdate();
                }

                JOptionPane.showMessageDialog(null, "Devolução registrada com sucesso!");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro: " + e.getMessage());
        }
    }

    private static void verEmprestimos() {
        try (Connection con = ConexaoBD.conectar();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(
                     "SELECT e.id, e.usuario, l.titulo, e.data_emprestimo, e.data_devolucao " +
                             "FROM emprestimo e JOIN livro l ON e.livro_id = l.id")) {

            String[] colunas = {"ID", "Usuário", "Livro", "Data Empréstimo", "Data Devolução"};
            DefaultTableModel model = new DefaultTableModel(colunas, 0);

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("usuario"),
                        rs.getString("titulo"),
                        rs.getDate("data_emprestimo"),
                        rs.getDate("data_devolucao")
                });
            }

            JTable tabela = new JTable(model);
            JOptionPane.showMessageDialog(null, new JScrollPane(tabela), "Empréstimos", JOptionPane.PLAIN_MESSAGE);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro: " + e.getMessage());
        }
    }

    private static void removerLivro() {
        String idStr = JOptionPane.showInputDialog("Digite o ID do livro que deseja remover:");
        if (idStr == null || idStr.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "ID inválido.");
            return;
        }

        try {
            int idLivro = Integer.parseInt(idStr);

            try (Connection con = ConexaoBD.conectar();
                 PreparedStatement ps = con.prepareStatement("DELETE FROM livro WHERE id = ?")) {

                ps.setInt(1, idLivro);
                int linhas = ps.executeUpdate();

                if (linhas > 0) {
                    JOptionPane.showMessageDialog(null, "Livro removido com sucesso!");
                } else {
                    JOptionPane.showMessageDialog(null, "Nenhum livro encontrado com esse ID.");
                }

            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro ao remover livro: " + e.getMessage());
        }
    }
}
