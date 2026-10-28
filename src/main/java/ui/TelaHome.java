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
    private static final Color VERDE = new Color(39, 174, 96);
    private static final Color CINZA_TEXTO = new Color(100, 100, 100);
    private static final Color CINZA_FUNDO = new Color(240, 242, 245);
    private static final Color BORDA_CLARA = new Color(220, 224, 230);

    // Paletas (início, fim do degradê) usadas nas miniaturas, uma por categoria
    private static final Color[][] PALETAS = {
            {new Color(52, 152, 219), new Color(31, 97, 141)},
            {new Color(155, 89, 182), new Color(108, 52, 131)},
            {new Color(26, 188, 156), new Color(14, 122, 102)},
            {new Color(230, 126, 34), new Color(175, 96, 26)},
            {new Color(231, 76, 60), new Color(169, 50, 38)},
            {new Color(52, 73, 94), new Color(33, 47, 61)}
    };

    JButton minhaConta, verCarrinho, gerenciar;
    JLabel contadorCarrinho;
    JPanel painelProdutos;

    public TelaHome(){
        componentes();
    }

    public void componentes(){
        //Configuração da tela
        setTitle("TecStore - Loja");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(780, 640);
        setLocationRelativeTo(null);
        getContentPane().setBackground(CINZA_FUNDO);

        //Header com degradê azul (mesmo estilo das telas de perfil e configuração)
        JPanel header = new JPanel(new BorderLayout(10, 0)) {
            @Override
            protected void paintComponent(Graphics g){
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setPaint(new GradientPaint(0, 0, AZUL, getWidth(), getHeight(), AZUL_ESCURO));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        header.setBorder(BorderFactory.createEmptyBorder(14, 24, 14, 24));

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

        gerenciar = new JButton("Gerenciar");
        gerenciar.setFont(new Font("SansSerif", Font.PLAIN, 13));
        gerenciar.setForeground(Color.WHITE);
        gerenciar.setBorderPainted(false);
        gerenciar.setContentAreaFilled(false);
        gerenciar.setFocusPainted(false);
        gerenciar.setOpaque(false);
        gerenciar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        hoverClaro(gerenciar);

        painelAcoes.add(minhaConta);
        painelAcoes.add(painelCarrinho);
        painelAcoes.add(gerenciar);

        header.add(titulo, BorderLayout.WEST);
        painelAcoes.setPreferredSize(new Dimension(340, 32));
        header.add(painelAcoes, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        //Grade de produtos (2 colunas)
        painelProdutos = new JPanel(new GridLayout(0, 2, 18, 18));
        painelProdutos.setBackground(CINZA_FUNDO);

        //Wrapper evita que os cards sejam esticados quando há poucos produtos
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(CINZA_FUNDO);
        wrapper.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        wrapper.add(painelProdutos, BorderLayout.NORTH);

        JScrollPane scrollProdutos = new JScrollPane(wrapper);
        scrollProdutos.setBorder(null);
        scrollProdutos.getViewport().setBackground(CINZA_FUNDO);
        scrollProdutos.getVerticalScrollBar().setUnitIncrement(16);
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

        gerenciar.addActionListener(e -> {
            TelaAdicionarProduto produto = new TelaAdicionarProduto();
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
        String categoriaTxt = produto.getCategoria();
        if(categoriaTxt == null || categoriaTxt.trim().isEmpty()){
            categoriaTxt = "Geral";
        }
        Color[] paleta = PALETAS[Math.abs(categoriaTxt.hashCode()) % PALETAS.length];

        //Card com cantos arredondados e sombra suave
        CardProduto card = new CardProduto(new BorderLayout(0, 10));
        card.setBorder(BorderFactory.createEmptyBorder(12, 12, 16, 12));
        card.setPreferredSize(new Dimension(330, 275));

        //Miniatura colorida com a inicial do produto
        String nomeProduto = produto.getNome() == null ? "" : produto.getNome();
        String letra = nomeProduto.isEmpty() ? "?" : nomeProduto.substring(0, 1).toUpperCase();
        card.add(new Miniatura(paleta[0], paleta[1], letra), BorderLayout.NORTH);

        //Informações: categoria (chip), nome e descrição
        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));

        JLabel categoria = new JLabel(categoriaTxt.toUpperCase());
        categoria.setFont(new Font("SansSerif", Font.BOLD, 10));
        categoria.setForeground(paleta[1]);
        categoria.setOpaque(true);
        categoria.setBackground(clarear(paleta[0], 0.85f));
        categoria.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));

        JPanel linhaChip = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        linhaChip.setOpaque(false);
        linhaChip.setAlignmentX(Component.LEFT_ALIGNMENT);
        linhaChip.add(categoria);

        JLabel nome = new JLabel("<html><div style='width:270px'>" + escapar(nomeProduto) + "</div></html>");
        nome.setFont(new Font("SansSerif", Font.BOLD, 15));
        nome.setForeground(new Color(40, 40, 40));
        nome.setAlignmentX(Component.LEFT_ALIGNMENT);

        String desc = produto.getDescricao() == null ? "" : produto.getDescricao();
        if(desc.length() > 80){
            desc = desc.substring(0, 77) + "...";
        }
        JLabel descricao = new JLabel("<html><div style='width:270px;color:#646464'>" + escapar(desc) + "</div></html>");
        descricao.setFont(new Font("SansSerif", Font.PLAIN, 12));
        descricao.setAlignmentX(Component.LEFT_ALIGNMENT);

        info.add(linhaChip);
        info.add(Box.createVerticalStrut(8));
        info.add(nome);
        info.add(Box.createVerticalStrut(4));
        info.add(descricao);
        card.add(info, BorderLayout.CENTER);

        //Preço + quantidade + botão
        JPanel painelBase = new JPanel(new BorderLayout(10, 0));
        painelBase.setOpaque(false);
        painelBase.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, BORDA_CLARA),
                BorderFactory.createEmptyBorder(10, 0, 0, 0)
        ));

        JLabel preco = new JLabel("<html><span style='font-size:10px;color:#646464'>R$</span> "
                + String.format("%.2f", produto.getPreco()) + "</html>");
        preco.setFont(new Font("SansSerif", Font.BOLD, 18));
        preco.setForeground(AZUL_ESCURO);

        JSpinner quantidade = new JSpinner(new SpinnerNumberModel(1, 1, 99, 1));
        quantidade.setFont(new Font("SansSerif", Font.PLAIN, 12));
        quantidade.setPreferredSize(new Dimension(52, 30));

        JButton adicionar = new JButton("Adicionar");
        adicionar.setFont(new Font("SansSerif", Font.BOLD, 12));
        adicionar.setBackground(AZUL);
        adicionar.setForeground(Color.WHITE);
        adicionar.setFocusPainted(false);
        adicionar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        adicionar.setBorder(BorderFactory.createEmptyBorder(7, 14, 7, 14));
        hoverEscuro(adicionar);

        adicionar.addActionListener(e -> {
            int qtd = (int) quantidade.getValue();
            Carrinho.adicionar(produto, qtd);
            atualizarContador();

            //Feedback no próprio botão (no lugar do pop-up)
            adicionar.setText("Adicionado!");
            adicionar.setBackground(VERDE);
            Timer volta = new Timer(1200, ev -> {
                adicionar.setText("Adicionar");
                adicionar.setBackground(AZUL);
            });
            volta.setRepeats(false);
            volta.start();
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

    //Mistura a cor com branco (fator 0 = cor original, 1 = branco)
    private static Color clarear(Color c, float fator){
        int r = (int) (c.getRed() + (255 - c.getRed()) * fator);
        int g = (int) (c.getGreen() + (255 - c.getGreen()) * fator);
        int b = (int) (c.getBlue() + (255 - c.getBlue()) * fator);
        return new Color(r, g, b);
    }

    //Evita que caracteres como < e > quebrem o texto em HTML
    private static String escapar(String s){
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    //Card branco arredondado com sombra suave
    private static class CardProduto extends JPanel {
        CardProduto(LayoutManager layout){
            super(layout);
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g){
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();

            g2.setColor(new Color(0, 0, 0, 14));
            g2.fillRoundRect(1, 3, w - 3, h - 4, 18, 18);

            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, w - 2, h - 4, 18, 18);

            g2.setColor(BORDA_CLARA);
            g2.drawRoundRect(0, 0, w - 3, h - 5, 18, 18);
            g2.dispose();
        }
    }

    //Miniatura com degradê e a inicial do produto
    private static class Miniatura extends JPanel {
        private final Color cor1, cor2;
        private final String letra;

        Miniatura(Color cor1, Color cor2, String letra){
            this.cor1 = cor1;
            this.cor2 = cor2;
            this.letra = letra;
            setOpaque(false);
            setPreferredSize(new Dimension(100, 78));
        }

        @Override
        protected void paintComponent(Graphics g){
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();

            g2.setPaint(new GradientPaint(0, 0, cor1, w, h, cor2));
            g2.fillRoundRect(0, 0, w, h, 12, 12);

            //Círculos decorativos
            g2.setClip(new java.awt.geom.RoundRectangle2D.Float(0, 0, w, h, 12, 12));
            g2.setColor(new Color(255, 255, 255, 35));
            g2.fillOval(w - 70, -30, 110, 110);
            g2.setColor(new Color(255, 255, 255, 25));
            g2.fillOval(w - 130, 30, 80, 80);
            g2.setClip(null);

            //Inicial do produto
            g2.setColor(new Color(255, 255, 255, 235));
            g2.setFont(new Font("SansSerif", Font.BOLD, 40));
            FontMetrics fm = g2.getFontMetrics();
            int x = (w - fm.stringWidth(letra)) / 2;
            int y = (h - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(letra, x, y);
            g2.dispose();
        }
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