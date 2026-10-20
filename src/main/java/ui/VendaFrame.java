import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class VendaFrame extends JFrame {

    private final VendaDAO dao = new VendaDAO();
    private final DateTimeFormatter formatoData = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // Campos do formulário
    private JTextField txtIdVenda;
    private JTextField txtDataVenda;
    private JTextField txtValorTotal;
    private JTextField txtFormaPagamento;
    private JTextField txtIdUsuario;
    private JTextField txtIdVendedor;
    private JTextField txtIdItem;

    // Tabela
    private JTable tabela;
    private DefaultTableModel modeloTabela;

    public VendaFrame() {
        super("Cadastro de Vendas");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 550);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        add(criarPainelFormulario(), BorderLayout.NORTH);
        add(criarPainelTabela(), BorderLayout.CENTER);
        add(criarPainelBotoes(), BorderLayout.SOUTH);

        carregarVendas();
    }

    private JPanel criarPainelFormulario() {
        JPanel painel = new JPanel(new GridLayout(4, 4, 8, 8));
        painel.setBorder(BorderFactory.createTitledBorder("Dados da Venda"));

        txtIdVenda = new JTextField();
        txtIdVenda.setEditable(false);
        txtDataVenda = new JTextField("dd/MM/yyyy HH:mm");
        txtValorTotal = new JTextField();
        txtFormaPagamento = new JTextField();
        txtIdUsuario = new JTextField();
        txtIdVendedor = new JTextField();
        txtIdItem = new JTextField();

        painel.add(new JLabel("ID Venda:"));
        painel.add(txtIdVenda);
        painel.add(new JLabel("Data Venda:"));
        painel.add(txtDataVenda);

        painel.add(new JLabel("Valor Total:"));
        painel.add(txtValorTotal);
        painel.add(new JLabel("Forma Pagamento:"));
        painel.add(txtFormaPagamento);

        painel.add(new JLabel("ID Usuário:"));
        painel.add(txtIdUsuario);
        painel.add(new JLabel("ID Vendedor:"));
        painel.add(txtIdVendedor);

        painel.add(new JLabel("ID Item:"));
        painel.add(txtIdItem);
        painel.add(new JLabel(""));

        return painel;
    }

    private JScrollPane criarPainelTabela() {
        String[] colunas = {"ID", "Data Venda", "Valor Total", "Forma Pagamento", "ID Usuário", "ID Vendedor", "ID Item"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabela = new JTable(modeloTabela);
        tabela.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                preencherFormularioComLinhaSelecionada();
            }
        });
        return new JScrollPane(tabela);
    }

    private JPanel criarPainelBotoes() {
        JPanel painel = new JPanel(new FlowLayout());

        JButton btnNovo = new JButton("Novo");
        JButton btnSalvar = new JButton("Salvar");
        JButton btnAtualizar = new JButton("Atualizar");
        JButton btnExcluir = new JButton("Excluir");
        JButton btnLimpar = new JButton("Limpar");

        btnNovo.addActionListener(this::acaoInserir);
        btnAtualizar.addActionListener(this::acaoAtualizar);
        btnExcluir.addActionListener(this::acaoExcluir);
        btnLimpar.addActionListener(e -> limparFormulario());

        // "Salvar" e "Novo" fazem a mesma coisa aqui: inserir novo registro
        btnSalvar.addActionListener(this::acaoInserir);

        painel.add(btnSalvar);
        painel.add(btnAtualizar);
        painel.add(btnExcluir);
        painel.add(btnLimpar);

        return painel;
    }

    private void acaoInserir(ActionEvent e) {
        try {
            Venda venda = montarVendaDoFormulario(false);
            dao.inserir(venda);
            JOptionPane.showMessageDialog(this, "Venda cadastrada com sucesso! ID: " + venda.getIdVenda());
            limparFormulario();
            carregarVendas();
        } catch (Exception ex) {
            mostrarErro(ex);
        }
    }

    private void acaoAtualizar(ActionEvent e) {
        if (txtIdVenda.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "Selecione uma venda na tabela para atualizar.",
                    "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            Venda venda = montarVendaDoFormulario(true);
            boolean ok = dao.atualizar(venda);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Venda atualizada com sucesso!");
                limparFormulario();
                carregarVendas();
            } else {
                JOptionPane.showMessageDialog(this, "Nenhuma venda foi atualizada.",
                        "Atenção", JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception ex) {
            mostrarErro(ex);
        }
    }

    private void acaoExcluir(ActionEvent e) {
        if (txtIdVenda.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "Selecione uma venda na tabela para excluir.",
                    "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirmacao = JOptionPane.showConfirmDialog(this,
                "Tem certeza que deseja excluir a venda #" + txtIdVenda.getText() + "?",
                "Confirmar exclusão", JOptionPane.YES_NO_OPTION);

        if (confirmacao == JOptionPane.YES_OPTION) {
            try {
                int id = Integer.parseInt(txtIdVenda.getText());
                boolean ok = dao.deletar(id);
                if (ok) {
                    JOptionPane.showMessageDialog(this, "Venda excluída com sucesso!");
                    limparFormulario();
                    carregarVendas();
                }
            } catch (Exception ex) {
                mostrarErro(ex);
            }
        }
    }

    private Venda montarVendaDoFormulario(boolean comId) {
        LocalDateTime data = LocalDateTime.parse(txtDataVenda.getText(), formatoData);
        BigDecimal valor = new BigDecimal(txtValorTotal.getText().replace(",", "."));
        String formaPagamento = txtFormaPagamento.getText();
        int idUsuario = Integer.parseInt(txtIdUsuario.getText());
        int idVendedor = Integer.parseInt(txtIdVendedor.getText());
        int idItem = Integer.parseInt(txtIdItem.getText());

        if (comId) {
            int id = Integer.parseInt(txtIdVenda.getText());
            return new Venda(id, data, valor, formaPagamento, idUsuario, idVendedor, idItem);
        }
        return new Venda(data, valor, formaPagamento, idUsuario, idVendedor, idItem);
    }

    private void preencherFormularioComLinhaSelecionada() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) return;

        txtIdVenda.setText(modeloTabela.getValueAt(linha, 0).toString());
        txtDataVenda.setText(modeloTabela.getValueAt(linha, 1).toString());
        txtValorTotal.setText(modeloTabela.getValueAt(linha, 2).toString());
        txtFormaPagamento.setText(modeloTabela.getValueAt(linha, 3).toString());
        txtIdUsuario.setText(modeloTabela.getValueAt(linha, 4).toString());
        txtIdVendedor.setText(modeloTabela.getValueAt(linha, 5).toString());
        txtIdItem.setText(modeloTabela.getValueAt(linha, 6).toString());
    }

    private void limparFormulario() {
        txtIdVenda.setText("");
        txtDataVenda.setText("dd/MM/yyyy HH:mm");
        txtValorTotal.setText("");
        txtFormaPagamento.setText("");
        txtIdUsuario.setText("");
        txtIdVendedor.setText("");
        txtIdItem.setText("");
        tabela.clearSelection();
    }

    private void carregarVendas() {
        try {
            modeloTabela.setRowCount(0);
            List<Venda> vendas = dao.listarTodas();
            for (Venda v : vendas) {
                modeloTabela.addRow(new Object[]{
                        v.getIdVenda(),
                        v.getDataVenda().format(formatoData),
                        v.getValorTotal(),
                        v.getFormaPagamento(),
                        v.getIdUsuario(),
                        v.getIdVendedor(),
                        v.getIdItem()
                });
            }
        } catch (Exception ex) {
            mostrarErro(ex);
        }
    }

    private void mostrarErro(Exception ex) {
        JOptionPane.showMessageDialog(this,
                "Erro: " + ex.getMessage(),
                "Erro", JOptionPane.ERROR_MESSAGE);
        ex.printStackTrace();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }
            new VendaFrame().setVisible(true);
        });
    }
}
