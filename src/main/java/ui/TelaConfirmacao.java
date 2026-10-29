package ui;

import logica.Carrinho;
import logica.ItemCarrinho;
import logica.VendaControle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TelaConfirmacao extends JFrame {
    private static final Color AZUL = new Color(41, 128, 185);
    private static final Color AZUL_ESCURO = new Color(31, 97, 141);
    private static final Color AZUL_CLARO = new Color(235, 245, 251);
    private static final Color VERDE = new Color(39, 174, 96);
    private static final Color CINZA_FUNDO = new Color(240, 242, 245);
    private static final Color CINZA_TEXTO = new Color(100, 100, 100);
    private static final Color BORDA_CLARA = new Color(220, 224, 230);
    private static final Color TEXTO_ESCURO = new Color(40, 40, 40);

    private static final int LARGURA = 540;
    private static final int ALTURA = 660;

    //Cabeçalho
    JLabel titulo, subtitulo;

    //Controle das duas "páginas" (pagamento / sucesso)
    CardLayout cards;
    JPanel conteudo;

    //Pagamento
    JToggleButton rdCartao, rdPix;
    JButton confirmar, voltarCarrinho;

    //Sucesso
    JLabel valVenda = new JLabel();
    JLabel valData = new JLabel();
    JLabel valForma = new JLabel();
    JLabel valTotal = new JLabel();
    JButton voltarLoja, irPerfil;

    public TelaConfirmacao(){
        componentes();
    }

    public void componentes(){
        //Configuração da tela
        setTitle("TecStore - Confirmação de pagamento");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(CINZA_FUNDO);
        getContentPane().setPreferredSize(new Dimension(LARGURA, ALTURA));
        setResizable(false);

        //Cabeçalho com degradê (mesmo das outras telas)
        JPanel header = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g){
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setPaint(new GradientPaint(0, 0, AZUL, getWidth(), getHeight(), AZUL_ESCURO));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        header.setPreferredSize(new Dimension(LARGURA, 100));

        titulo = new JLabel("Confirmação de pagamento", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 20));
        titulo.setForeground(Color.WHITE);
        titulo.setBounds(0, 22, LARGURA, 30);

        subtitulo = new JLabel("Revise seu pedido e escolha a forma de pagamento", SwingConstants.CENTER);
        subtitulo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitulo.setForeground(new Color(220, 236, 247));
        subtitulo.setBounds(0, 52, LARGURA, 20);

        header.add(titulo);
        header.add(subtitulo);

        //Corpo com as duas páginas
        cards = new CardLayout();
        conteudo = new JPanel(cards);
        conteudo.setOpaque(false);
        conteudo.add(criarPainelPagamento(), "pagamento");
        conteudo.add(criarPainelSucesso(), "sucesso");

        JPanel corpo = new JPanel(new BorderLayout());
        corpo.setOpaque(false);
        corpo.setBorder(BorderFactory.createEmptyBorder(20, 24, 24, 24));
        corpo.add(conteudo, BorderLayout.CENTER);

        add(header, BorderLayout.NORTH);
        add(corpo, BorderLayout.CENTER);

        /*
        ==============================================================
        Eventos
        ==============================================================
        */
        voltarCarrinho.addActionListener(e -> {
            TelaCarrinho carrinho = new TelaCarrinho();
            dispose();
        });

        confirmar.addActionListener(e -> iniciarPagamento());

        voltarLoja.addActionListener(e -> {
            TelaHome home = new TelaHome();
            dispose();
        });

        irPerfil.addActionListener(e -> {
            TelaPerfil perfil = new TelaPerfil();
            dispose();
        });

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /*
    ==============================================================
    Finalização da venda
    ==============================================================
    */
    //Abre a janela de acordo com a forma de pagamento escolhida
    private void iniciarPagamento(){
        if(Carrinho.estaVazio()){
            JOptionPane.showMessageDialog(this, "Seu carrinho está vazio");
            return;
        }

        double total = Carrinho.getTotal();

        if(rdPix.isSelected()){
            TelaPagamentoPix pix = new TelaPagamentoPix(this, total);
            pix.setVisible(true);
            if(pix.isConfirmado()){
                finalizarPagamento("Pix");
            }
        }else{
            TelaPagamentoCartao cartao = new TelaPagamentoCartao(this, total);
            cartao.setVisible(true);
            if(cartao.isConfirmado()){
                int parcelas = cartao.getParcelas();
                finalizarPagamento(parcelas > 1 ? "Cartão (" + parcelas + "x)" : "Cartão");
            }
        }
    }

    private void finalizarPagamento(String formaPagamento){
        if(Carrinho.estaVazio()){
            JOptionPane.showMessageDialog(this, "Seu carrinho está vazio");
            return;
        }

        //Guarda o total antes de esvaziar o carrinho
        final String totalTxt = String.format("R$ %.2f", Carrinho.getTotal());

        //Bloqueia os botões para evitar clique duplo (venda registrada duas vezes)
        confirmar.setEnabled(false);
        confirmar.setText("Processando...");
        voltarCarrinho.setEnabled(false);

        //A venda roda em segundo plano para a tela não travar
        new SwingWorker<Integer, Void>() {
            @Override
            protected Integer doInBackground(){
                VendaControle venda = new VendaControle();
                return venda.finalizarVenda(Carrinho.getItens(), formaPagamento);
            }

            @Override
            protected void done(){
                int idVenda = 0;
                try{
                    idVenda = get();
                }catch(Exception ex){
                    ex.printStackTrace();
                }

                if(idVenda > 0){
                    Carrinho.limpar();
                    mostrarSucesso(idVenda, formaPagamento, totalTxt);
                }else{
                    confirmar.setEnabled(true);
                    confirmar.setText(textoBotaoConfirmar());
                    confirmar.setBackground(AZUL);
                    voltarCarrinho.setEnabled(true);
                    JOptionPane.showMessageDialog(TelaConfirmacao.this,
                            "Não foi possível concluir o pagamento.\nVerifique sua conexão com o banco e tente novamente.");
                }
            }
        }.execute();
    }

    private void mostrarSucesso(int idVenda, String formaPagamento, String totalTxt){
        String data = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        valVenda.setText("#" + idVenda);
        valData.setText(data);
        valForma.setText(formaPagamento);
        valTotal.setText(totalTxt);

        titulo.setText("Compra finalizada");
        subtitulo.setText("Obrigado por comprar na TecStore");
        cards.show(conteudo, "sucesso");
    }

    /*
    ==============================================================
    Página de pagamento
    ==============================================================
    */
    private JPanel criarPainelPagamento(){
        Cartao card = new Cartao(new BorderLayout(0, 12));
        card.setBorder(BorderFactory.createEmptyBorder(18, 20, 20, 20));

        card.add(secao("RESUMO DO PEDIDO"), BorderLayout.NORTH);

        //Lista de itens (com rolagem, para carrinhos grandes)
        JPanel lista = new JPanel();
        lista.setLayout(new BoxLayout(lista, BoxLayout.Y_AXIS));
        lista.setOpaque(false);

        List<ItemCarrinho> itens = Carrinho.getItens();
        if(itens.isEmpty()){
            JLabel vazio = new JLabel("Nenhum item no carrinho");
            vazio.setFont(new Font("SansSerif", Font.ITALIC, 13));
            vazio.setForeground(CINZA_TEXTO);
            lista.add(vazio);
        }else{
            for(ItemCarrinho item : itens){
                lista.add(criarLinhaItem(item));
            }
        }

        JScrollPane scroll = new JScrollPane(lista);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(12);
        scroll.setPreferredSize(new Dimension(100, 150));
        card.add(scroll, BorderLayout.CENTER);

        //Parte de baixo: total, forma de pagamento e botões
        JPanel sul = new JPanel();
        sul.setLayout(new BoxLayout(sul, BoxLayout.Y_AXIS));
        sul.setOpaque(false);

        JPanel linhaTotal = new JPanel(new BorderLayout());
        linhaTotal.setOpaque(false);
        linhaTotal.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, BORDA_CLARA),
                BorderFactory.createEmptyBorder(12, 0, 12, 0)
        ));
        JLabel lblTotal = new JLabel("Total");
        lblTotal.setFont(new Font("SansSerif", Font.BOLD, 15));
        lblTotal.setForeground(TEXTO_ESCURO);
        JLabel lblTotalPagamento = new JLabel(String.format("R$ %.2f", Carrinho.getTotal()));
        lblTotalPagamento.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblTotalPagamento.setForeground(AZUL_ESCURO);
        linhaTotal.add(lblTotal, BorderLayout.WEST);
        linhaTotal.add(lblTotalPagamento, BorderLayout.EAST);
        linhaTotal.setMaximumSize(new Dimension(Integer.MAX_VALUE, linhaTotal.getPreferredSize().height));

        rdCartao = new JToggleButton("Cartão");
        rdPix = new JToggleButton("Pix");
        ButtonGroup grupoPagamento = new ButtonGroup();
        grupoPagamento.add(rdCartao);
        grupoPagamento.add(rdPix);
        rdCartao.setSelected(true);
        estilizarOpcao(rdCartao);
        estilizarOpcao(rdPix);

        JPanel opcoes = new JPanel(new GridLayout(1, 2, 10, 0));
        opcoes.setOpaque(false);
        opcoes.add(rdCartao);
        opcoes.add(rdPix);
        opcoes.setPreferredSize(new Dimension(100, 46));
        opcoes.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));

        confirmar = new JButton(textoBotaoConfirmar());
        voltarCarrinho = new JButton("Voltar ao carrinho");
        estilizarPrimario(confirmar);
        estilizarSecundario(voltarCarrinho);

        //O texto do botão acompanha a forma de pagamento escolhida
        rdCartao.addItemListener(e -> { if(confirmar.isEnabled()) confirmar.setText(textoBotaoConfirmar()); });
        rdPix.addItemListener(e -> { if(confirmar.isEnabled()) confirmar.setText(textoBotaoConfirmar()); });

        JPanel botoes = new JPanel(new GridLayout(1, 2, 10, 0));
        botoes.setOpaque(false);
        botoes.add(confirmar);
        botoes.add(voltarCarrinho);
        botoes.setPreferredSize(new Dimension(100, 44));
        botoes.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));

        JLabel secPagamento = secao("FORMA DE PAGAMENTO");

        linhaTotal.setAlignmentX(Component.LEFT_ALIGNMENT);
        opcoes.setAlignmentX(Component.LEFT_ALIGNMENT);
        botoes.setAlignmentX(Component.LEFT_ALIGNMENT);

        sul.add(linhaTotal);
        sul.add(Box.createVerticalStrut(10));
        sul.add(secPagamento);
        sul.add(Box.createVerticalStrut(8));
        sul.add(opcoes);
        sul.add(Box.createVerticalStrut(20));
        sul.add(botoes);

        card.add(sul, BorderLayout.SOUTH);
        return card;
    }

    private String textoBotaoConfirmar(){
        return rdPix != null && rdPix.isSelected() ? "Pagar com Pix" : "Pagar com cartão";
    }

    private JPanel criarLinhaItem(ItemCarrinho item){
        JPanel linha = new JPanel(new BorderLayout(10, 0));
        linha.setOpaque(false);
        linha.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDA_CLARA),
                BorderFactory.createEmptyBorder(8, 0, 8, 0)
        ));

        JLabel nome = new JLabel(item.getQuantidade() + "x " + item.getProduto().getNome());
        nome.setFont(new Font("SansSerif", Font.PLAIN, 13));
        nome.setForeground(TEXTO_ESCURO);

        JLabel subtotal = new JLabel(String.format("R$ %.2f", item.getSubtotal()));
        subtotal.setFont(new Font("SansSerif", Font.BOLD, 13));
        subtotal.setForeground(TEXTO_ESCURO);

        linha.add(nome, BorderLayout.CENTER);
        linha.add(subtotal, BorderLayout.EAST);
        linha.setMaximumSize(new Dimension(Integer.MAX_VALUE, linha.getPreferredSize().height));
        return linha;
    }

    /*
    ==============================================================
    Página de sucesso
    ==============================================================
    */
    private JPanel criarPainelSucesso(){
        Cartao card = new Cartao(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(24, 20, 24, 20));

        JPanel centro = new JPanel();
        centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));
        centro.setOpaque(false);

        SeloSucesso selo = new SeloSucesso();
        selo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSucesso = new JLabel("Pagamento confirmado!");
        lblSucesso.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblSucesso.setForeground(TEXTO_ESCURO);
        lblSucesso.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblInfo = new JLabel("Seu pedido foi registrado com sucesso");
        lblInfo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblInfo.setForeground(CINZA_TEXTO);
        lblInfo.setAlignmentX(Component.CENTER_ALIGNMENT);

        valTotal.setForeground(AZUL_ESCURO);
        JPanel detalhes = new JPanel();
        detalhes.setLayout(new BoxLayout(detalhes, BoxLayout.Y_AXIS));
        detalhes.setOpaque(false);
        detalhes.add(criarLinhaInfo("Nº da venda", valVenda));
        detalhes.add(criarLinhaInfo("Data", valData));
        detalhes.add(criarLinhaInfo("Pagamento", valForma));
        detalhes.add(criarLinhaInfo("Valor total", valTotal));
        detalhes.setAlignmentX(Component.CENTER_ALIGNMENT);
        detalhes.setMaximumSize(new Dimension(380, detalhes.getPreferredSize().height));

        voltarLoja = new JButton("Voltar à loja");
        irPerfil = new JButton("Minha conta");
        estilizarPrimario(voltarLoja);
        estilizarSecundario(irPerfil);

        JPanel botoes = new JPanel(new GridLayout(1, 2, 10, 0));
        botoes.setOpaque(false);
        botoes.add(voltarLoja);
        botoes.add(irPerfil);
        botoes.setPreferredSize(new Dimension(380, 44));
        botoes.setMaximumSize(new Dimension(380, 44));
        botoes.setAlignmentX(Component.CENTER_ALIGNMENT);

        centro.add(Box.createVerticalGlue());
        centro.add(selo);
        centro.add(Box.createVerticalStrut(16));
        centro.add(lblSucesso);
        centro.add(Box.createVerticalStrut(4));
        centro.add(lblInfo);
        centro.add(Box.createVerticalStrut(24));
        centro.add(detalhes);
        centro.add(Box.createVerticalStrut(28));
        centro.add(botoes);
        centro.add(Box.createVerticalGlue());

        card.add(centro, BorderLayout.CENTER);
        return card;
    }

    private JPanel criarLinhaInfo(String rotulo, JLabel valor){
        JPanel linha = new JPanel(new BorderLayout(10, 0));
        linha.setOpaque(false);
        linha.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDA_CLARA),
                BorderFactory.createEmptyBorder(9, 0, 9, 0)
        ));

        JLabel lbl = new JLabel(rotulo);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lbl.setForeground(CINZA_TEXTO);

        valor.setFont(new Font("SansSerif", Font.BOLD, 13));
        valor.setHorizontalAlignment(SwingConstants.RIGHT);
        if(valor.getForeground() == null || valor.getForeground().equals(UIManager.getColor("Label.foreground"))){
            valor.setForeground(TEXTO_ESCURO);
        }

        linha.add(lbl, BorderLayout.WEST);
        linha.add(valor, BorderLayout.CENTER);
        linha.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        return linha;
    }

    /*
    ==============================================================
    Estilos
    ==============================================================
    */
    private JLabel secao(String texto){
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 11));
        lbl.setForeground(CINZA_TEXTO);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private void estilizarOpcao(JToggleButton b){
        b.setFont(new Font("SansSerif", Font.BOLD, 14));
        b.setFocusPainted(false);
        b.setContentAreaFilled(false);   //garante a cor de fundo escolhida em qualquer visual do Java
        b.setOpaque(true);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        atualizarOpcao(b);
        b.addItemListener(e -> atualizarOpcao(b));
    }

    private void atualizarOpcao(JToggleButton b){
        if(b.isSelected()){
            b.setBackground(AZUL_CLARO);
            b.setForeground(AZUL_ESCURO);
            b.setBorder(BorderFactory.createLineBorder(AZUL, 2));
        }else{
            b.setBackground(Color.WHITE);
            b.setForeground(new Color(80, 80, 80));
            b.setBorder(BorderFactory.createLineBorder(BORDA_CLARA, 2));
        }
    }

    private void estilizarPrimario(JButton b){
        b.setFont(new Font("SansSerif", Font.BOLD, 14));
        b.setBackground(AZUL);
        b.setForeground(Color.WHITE);
        b.setOpaque(true);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder());
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e){ if(b.isEnabled()) b.setBackground(AZUL_ESCURO); }
            @Override
            public void mouseExited(MouseEvent e){ if(b.isEnabled()) b.setBackground(AZUL); }
        });
    }

    private void estilizarSecundario(JButton b){
        b.setFont(new Font("SansSerif", Font.BOLD, 14));
        b.setBackground(Color.WHITE);
        b.setForeground(AZUL);
        b.setOpaque(true);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createLineBorder(AZUL, 2));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e){ if(b.isEnabled()) b.setBackground(AZUL_CLARO); }
            @Override
            public void mouseExited(MouseEvent e){ if(b.isEnabled()) b.setBackground(Color.WHITE); }
        });
    }

    //Card branco arredondado com sombra suave (mesmo estilo da loja)
    private static class Cartao extends JPanel {
        Cartao(LayoutManager layout){
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

    //Círculo verde com um "check" desenhado (não depende de fonte)
    private static class SeloSucesso extends JPanel {
        SeloSucesso(){
            setOpaque(false);
            setPreferredSize(new Dimension(80, 80));
            setMaximumSize(new Dimension(80, 80));
        }

        @Override
        protected void paintComponent(Graphics g){
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();

            g2.setColor(new Color(39, 174, 96, 40));
            g2.fillOval(0, 0, w, h);
            g2.setColor(VERDE);
            g2.fillOval(8, 8, w - 16, h - 16);

            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawLine((int) (w * 0.31), (int) (h * 0.52), (int) (w * 0.44), (int) (h * 0.65));
            g2.drawLine((int) (w * 0.44), (int) (h * 0.65), (int) (w * 0.69), (int) (h * 0.36));
            g2.dispose();
        }
    }
}