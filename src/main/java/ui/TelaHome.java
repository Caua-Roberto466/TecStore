package ui;

import logica.Carrinho;
import logica.Produto;
import logica.ProdutoControle;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class TelaHome extends JFrame {
    private static final Color AZUL = new Color(41, 128, 185);
    private static final Color AZUL_ESCURO = new Color(31, 97, 141);
    private static final Color CINZA_TEXTO = new Color(100, 100, 100);
    private static final Color CINZA_FUNDO = new Color(240, 242, 245);
    private static final Color BORDA_CLARA = new Color(220, 224, 230);

    JButton minhaConta, verCarrinho;
    JLabel contadorCarrinho;
    JPanel painelProdutos;

    public TelaHome(){
        componentes();
    }

    public void componentes(){
        //Configuração da tela
        setTitle("TecStore - Loja");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(720, 540);
        setLocationRelativeTo(null);
        getContentPane().setBackground(CINZA_FUNDO);

        //Header azul
        JPanel header = new JPanel(new BorderLayout(10, 0));
        header.setBackground(AZUL);
        header.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));

        JLabel titulo = new JLabel("Nossos produtos");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 20));
        titulo.setForeground(Color.WHITE);

        JPanel painelAcoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        painelAcoes.setOpaque(false);

        minhaConta = new JButton("Minha conta");
        minhaConta.setFont(new Font("SansSerif", Font.PLAIN, 13));
        minhaConta.setForeground(Color.WHITE);
        minhaConta.setBorderPainted(false);
        minhaConta.setContentAreaFilled(false);
        minhaConta.setFocusPainted(false);
        minhaConta.setOpaque(false);
        minhaConta.setCursor(new Cursor(Cursor.HAND_CURSOR));
        hoverClaro(minhaConta);

        verCarrinho = new JButton("Carrinho");
        verCarrinho.setFont(new Font("SansSerif", Font.BOLD, 13));
        verCarrinho.setBackground(Color.WHITE);
        verCarrinho.setForeground(AZUL);
        verCarrinho.setFocusPainted(false);
        verCarrinho.setCursor(new Cursor(Cursor.HAND_CURSOR));
        verCarrinho.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));

        contadorCarrinho = new JLabel("0");
        contadorCarrinho.setFont(new Font("SansSerif", Font.BOLD, 12));
        contadorCarrinho.setForeground(Color.WHITE);
        contadorCarrinho.setBackground(new Color(231, 76, 60));
        contadorCarrinho.setOpaque(true);
        contadorCarrinho.setHorizontalAlignment(SwingConstants.CENTER);
        contadorCarrinho.setPreferredSize(new Dimension(22, 22));
        contadorCarrinho.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));

        //Botão carrinho + badge de contagem
        JPanel painelCarrinho = new JPanel(new BorderLayout(4, 0));
        painelCarrinho.setOpaque(false);
        painelCarrinho.add(verCarrinho, BorderLayout.CENTER);
        painelCarrinho.add(contadorCarrinho, BorderLayout.EAST);

        painelAcoes.add(minhaConta);
        painelAcoes.add(painelCarrinho);

        header.add(titulo, BorderLayout.WEST);
        painelAcoes.setPreferredSize(new Dimension(240, 32));
        header.add(painelAcoes, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        //Grade de produtos (2 colunas)
        painelProdutos = new JPanel(new GridLayout(0, 2, 15, 15));
        painelProdutos.setBackground(CINZA_FUNDO);
        painelProdutos.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JScrollPane scrollProdutos = new JScrollPane(painelProdutos);
        scrollProdutos.setBorder(null);
        scrollProdutos.getViewport().setBackground(CINZA_FUNDO);
        scrollProdutos.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(scrollProdutos, BorderLayout.CENTER);

        //Carregar produtos do banco
        carregarProdutos();
        atualizarContador();

        /*
        ==============================================================
        Eventos
        ==============================================================
        */
        minhaConta.addActionListener(e -> {
            TelaPerfil perfil = new TelaPerfil();
            dispose();
        });

        verCarrinho.addActionListener(e -> {
            TelaCarrinho carrinho = new TelaCarrinho();
            dispose();
        });

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowActivated(java.awt.event.WindowEvent e){
                atualizarContador();
            }
        });

        setVisible(true);
    }

    public void carregarProdutos(){
        painelProdutos.removeAll();

        ProdutoControle controle = new ProdutoControle();
        List<Produto> produtos = controle.listarProdutos();

        if(produtos.isEmpty()){
            JLabel vazio = new JLabel("<html><div style='text-align:center'>Nenhum produto disponível no momento<br><font color='#888888'>Tente novamente mais tarde</font></div></html>");
            vazio.setFont(new Font("SansSerif", Font.ITALIC, 14));
            vazio.setForeground(CINZA_TEXTO);
            vazio.setHorizontalAlignment(SwingConstants.CENTER);
            painelProdutos.add(vazio);
        }else{
            for(Produto produto : produtos){
                painelProdutos.add(criarCardProduto(produto));
            }
        }

        painelProdutos.revalidate();
        painelProdutos.repaint();
    }

    public JPanel criarCardProduto(Produto produto){
        JPanel card = new JPanel(new BorderLayout(8, 8));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new BordaArredondada(12, BORDA_CLARA),
                BorderFactory.createEmptyBorder(14, 14, 14, 14)
        ));
        card.setPreferredSize(new Dimension(310, 150));

        //Categoria como chip colorido + nome do produto
        JPanel painelTopo = new JPanel(new BorderLayout(8, 0));
        painelTopo.setOpaque(false);

        JLabel categoria = new JLabel(produto.getCategoria());
        categoria.setFont(new Font("SansSerif", Font.BOLD, 11));
        categoria.setForeground(AZUL);
        categoria.setOpaque(true);
        categoria.setBackground(new Color(232, 242, 250));
        categoria.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));

        JLabel nome = new JLabel("<html>" + produto.getNome() + "</html>");
        nome.setFont(new Font("SansSerif", Font.BOLD, 14));
        nome.setForeground(new Color(40, 40, 40));

        painelTopo.add(categoria, BorderLayout.WEST);
        painelTopo.add(nome, BorderLayout.CENTER);
        card.add(painelTopo, BorderLayout.NORTH);

        //Descrição
        JLabel descricao = new JLabel("<html><span style='color:#646464'>" + produto.getDescricao() + "</span></html>");
        descricao.setFont(new Font("SansSerif", Font.PLAIN, 12));
        card.add(descricao, BorderLayout.CENTER);

        //Preço + quantidade + botão
        JPanel painelBase = new JPanel(new BorderLayout(10, 0));
        painelBase.setOpaque(false);

        JLabel preco = new JLabel(String.format("R$ %.2f", produto.getPreco()));
        preco.setFont(new Font("SansSerif", Font.BOLD, 17));
        preco.setForeground(AZUL_ESCURO);

        JSpinner quantidade = new JSpinner(new SpinnerNumberModel(1, 1, 99, 1));
        quantidade.setFont(new Font("SansSerif", Font.PLAIN, 12));
        quantidade.setPreferredSize(new Dimension(50, 28));

        JButton adicionar = new JButton("Adicionar");
        adicionar.setFont(new Font("SansSerif", Font.BOLD, 12));
        adicionar.setBackground(AZUL);
        adicionar.setForeground(Color.WHITE);
        adicionar.setFocusPainted(false);
        adicionar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        adicionar.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
        hoverEscuro(adicionar);

        adicionar.addActionListener(e -> {
            int qtd = (int) quantidade.getValue();
            Carrinho.adicionar(produto, qtd);
            JOptionPane.showMessageDialog(this, qtd + "x " + produto.getNome() + " adicionado ao carrinho");
            atualizarContador();
        });

        JPanel painelAcao = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        painelAcao.setOpaque(false);
        painelAcao.add(quantidade);
        painelAcao.add(adicionar);

        painelBase.add(preco, BorderLayout.WEST);
        painelBase.add(painelAcao, BorderLayout.EAST);
        card.add(painelBase, BorderLayout.SOUTH);

        return card;
    }

    public void atualizarContador(){
        int total = 0;
        for(logica.ItemCarrinho item : Carrinho.getItens()){
            total += item.getQuantidade();
        }
        contadorCarrinho.setText(String.valueOf(total));
        contadorCarrinho.setVisible(total > 0);
    }

    //Efeitos de hover simples nos botões
    private void hoverEscuro(JButton botao){
        Color base = botao.getBackground();
        botao.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e){
                botao.setBackground(base.darker());
            }
            @Override
            public void mouseExited(MouseEvent e){
                botao.setBackground(base);
            }
        });
    }

    private void hoverClaro(JButton botao){
        botao.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e){
                botao.setForeground(new Color(220, 235, 245));
            }
            @Override
            public void mouseExited(MouseEvent e){
                botao.setForeground(Color.WHITE);
            }
        });
    }

    //Borda com cantos arredondados
    static class BordaArredondada extends AbstractBorder {
        private final int raio;
        private final Color cor;

        BordaArredondada(int raio, Color cor){
            this.raio = raio;
            this.cor = cor;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height){
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(cor);
            g2.drawRoundRect(x, y, width - 1, height - 1, raio, raio);
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c){
            return new Insets(raio / 2, raio / 2, raio / 2, raio / 2);
        }
    }
}
