package ui;

import logica.ProdutoControle;

import javax.swing.*;
import java.awt.*;

public class TelaAdicionarProduto extends JFrame {
    private static final Color AZUL = new Color(41, 128, 185);
    private static final Color AZUL_ESCURO = new Color(31, 97, 141);
    private static final Color CINZA_FUNDO = new Color(240, 242, 245);
    private static final Color CINZA_TEXTO = new Color(100, 100, 100);
    private static final Color BORDA_CLARA = new Color(220, 224, 230);

    private JTextField txtNome;
    private JTextField txtDescricao;
    private JTextField txtCategoria;
    private JTextField txtPreco;
    private JTextField txtQuantidade;
    private JButton cadastrar;
    private JButton voltar;

    public TelaAdicionarProduto(){
        componentes();
    }

    public void componentes(){
        setTitle("TecStore - Adicionar Produto");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(560, 460);
        setLocationRelativeTo(null);
        getContentPane().setBackground(CINZA_FUNDO);

        JPanel formulario = new JPanel(null);
        formulario.setBackground(Color.WHITE);
        formulario.setBorder(BorderFactory.createCompoundBorder(
                new TelaHome.BordaArredondada(12, BORDA_CLARA),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel titulo = new JLabel("Adicionar produto");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 20));
        titulo.setBounds(20, 20, 520, 30);

        JLabel lblNome = new JLabel("Nome:");
        lblNome.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblNome.setBounds(20, 60, 90, 25);
        txtNome = new JTextField(20);
        txtNome.setBounds(120, 60, 420, 25);

        JLabel lblDescricao = new JLabel("Descricao:");
        lblDescricao.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblDescricao.setBounds(20, 100, 90, 25);
        txtDescricao = new JTextField(20);
        txtDescricao.setBounds(120, 100, 420, 25);

        JLabel lblCategoria = new JLabel("Categoria:");
        lblCategoria.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblCategoria.setBounds(20, 140, 90, 25);
        txtCategoria = new JTextField(20);
        txtCategoria.setBounds(120, 140, 420, 25);

        JLabel lblPreco = new JLabel("Preco (R$):");
        lblPreco.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblPreco.setBounds(20, 180, 90, 25);
        txtPreco = new JTextField(20);
        txtPreco.setBounds(120, 180, 200, 25);

        JLabel lblQuantidade = new JLabel("Quantidade:");
        lblQuantidade.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblQuantidade.setBounds(20, 220, 90, 25);
        txtQuantidade = new JTextField(20);
        txtQuantidade.setBounds(120, 220, 150, 25);

        cadastrar = new JButton("Adicionar");
        cadastrar.setFont(new Font("SansSerif", Font.BOLD, 14));
        cadastrar.setBackground(AZUL);
        cadastrar.setForeground(Color.WHITE);
        cadastrar.setFocusPainted(false);
        cadastrar.setBounds(120, 280, 180, 40);
        cadastrar.addActionListener(e -> adicionarProduto());

        voltar = new JButton("Voltar");
        voltar.setFont(new Font("SansSerif", Font.PLAIN, 13));
        voltar.setBackground(Color.WHITE);
        voltar.setForeground(AZUL);
        voltar.setFocusPainted(false);
        voltar.setBounds(320, 280, 180, 40);
        voltar.addActionListener(e -> {
            TelaHome home = new TelaHome();
            dispose();
        });

        formulario.add(titulo);
        formulario.add(lblNome);
        formulario.add(txtNome);
        formulario.add(lblDescricao);
        formulario.add(txtDescricao);
        formulario.add(lblCategoria);
        formulario.add(txtCategoria);
        formulario.add(lblPreco);
        formulario.add(txtPreco);
        formulario.add(lblQuantidade);
        formulario.add(txtQuantidade);
        formulario.add(cadastrar);
        formulario.add(voltar);

        add(formulario);

        setVisible(true);
    }

    private void adicionarProduto(){
        String nome = txtNome.getText();
        String descricao = txtDescricao.getText();
        String categoria = txtCategoria.getText();
        String preco = txtPreco.getText();
        String quantidade = txtQuantidade.getText();

        if(nome.isEmpty() || categoria.isEmpty() || preco.isEmpty() || quantidade.isEmpty()){
            JOptionPane.showMessageDialog(this, "Preencha os campos obrigatorios (Nome, Categoria, Preco e Quantidade).");
            return;
        }

        try{
            double valor = Double.parseDouble(preco.replace(",", "."));
            int qtd = Integer.parseInt(quantidade);
            ProdutoControle controle = new ProdutoControle();
            boolean ok = controle.adicionarProduto(nome, descricao, categoria, valor, qtd);
            if(ok){
                JOptionPane.showMessageDialog(this, "Produto adicionado ao estoque com sucesso!");
                limparCampos();
            }else{
                JOptionPane.showMessageDialog(this, "Nao foi possivel adicionar o produto. Verifique a conexao com o banco.");
            }
        }catch(NumberFormatException ex){
            JOptionPane.showMessageDialog(this, "Preco e quantidade devem ser valores numericos validos.");
        }
    }

    private void limparCampos(){
        txtNome.setText("");
        txtDescricao.setText("");
        txtCategoria.setText("");
        txtPreco.setText("");
        txtQuantidade.setText("");
    }
}
