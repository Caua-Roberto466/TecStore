package ui;

import logica.Carrinho;
import logica.ItemCarrinho;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class TelaCarrinho extends JFrame {
    private static final Color AZUL = new Color(41, 128, 185);
    private static final Color AZUL_ESCURO = new Color(31, 97, 141);
    private static final Color VERMELHO = new Color(192, 57, 43);
    private static final Color CINZA_FUNDO = new Color(240, 242, 245);
    private static final Color CINZA_TEXTO = new Color(100, 100, 100);
    private static final Color BORDA_CLARA = new Color(220, 224, 230);

    JLabel lblTotal, lblVazio;
    JPanel painelItens;
    JButton voltar, irPagamento;
    JLabel lblQuantidadeItens;

    public TelaCarrinho(){
        componentes();
    }

    public void componentes(){
        //Configuração da tela
        setTitle("TecStore - Carrinho");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(620, 520);
        setLocationRelativeTo(null);
        getContentPane().setBackground(CINZA_FUNDO);
        getContentPane().setLayout(new BorderLayout());

        //Header azul
        JPanel header = new JPanel(new BorderLayout(10, 0));
        header.setBackground(AZUL);
        header.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));

        JLabel titulo = new JLabel("Meu carrinho");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 20));
        titulo.setForeground(Color.WHITE);

        voltar = new JButton("← Voltar à loja");
        voltar.setFont(new Font("SansSerif", Font.PLAIN, 13));
        voltar.setForeground(Color.WHITE);
        voltar.setBorderPainted(false);
        voltar.setContentAreaFilled(false);
        voltar.setFocusPainted(false);
        voltar.setOpaque(false);
        voltar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        voltar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e){
                voltar.setForeground(new Color(220, 235, 245));
            }
            @Override
            public void mouseExited(MouseEvent e){
                voltar.setForeground(Color.WHITE);
            }
        });

        header.add(titulo, BorderLayout.WEST);
        header.add(voltar, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        //Lista de itens
        painelItens = new JPanel();
        painelItens.setLayout(new BoxLayout(painelItens, BoxLayout.Y_AXIS));
        painelItens.setBackground(CINZA_FUNDO);
        painelItens.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JScrollPane scrollItens = new JScrollPane(painelItens);
        scrollItens.setBorder(null);
        scrollItens.getViewport().setBackground(CINZA_FUNDO);
        scrollItens.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(scrollItens, BorderLayout.CENTER);

        lblVazio = new JLabel("<html><div style='text-align:center'><font size='5'>🛒</font><br><br>Seu carrinho está vazio<br><font color='#888888' size='2'>Adicione produtos para continuar</font></div></html>");
        lblVazio.setFont(new Font("SansSerif", Font.ITALIC, 14));
        lblVazio.setForeground(CINZA_TEXTO);
        lblVazio.setHorizontalAlignment(SwingConstants.CENTER);
        lblVazio.setAlignmentX(Component.CENTER_ALIGNMENT);

        //Rodapé com total e pagamento
        JPanel rodape = new JPanel(new BorderLayout(10, 0));
        rodape.setBackground(Color.WHITE);
        rodape.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, BORDA_CLARA),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));

        JPanel painelTotal = new JPanel(new BorderLayout(0, 2));
        painelTotal.setOpaque(false);

        lblQuantidadeItens = new JLabel();
        lblQuantidadeItens.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblQuantidadeItens.setForeground(CINZA_TEXTO);

        lblTotal = new JLabel("Total: R$ 0,00");
        lblTotal.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblTotal.setForeground(AZUL_ESCURO);

        painelTotal.add(lblQuantidadeItens, BorderLayout.NORTH);
        painelTotal.add(lblTotal, BorderLayout.SOUTH);

        irPagamento = new JButton("Ir para pagamento");
        irPagamento.setFont(new Font("SansSerif", Font.BOLD, 14));
        irPagamento.setBackground(AZUL);
        irPagamento.setForeground(Color.WHITE);
        irPagamento.setFocusPainted(false);
        irPagamento.setCursor(new Cursor(Cursor.HAND_CURSOR));
        irPagamento.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        irPagamento.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e){
                if(irPagamento.isEnabled()) irPagamento.setBackground(AZUL_ESCURO);
            }
            @Override
            public void mouseExited(MouseEvent e){
                if(irPagamento.isEnabled()) irPagamento.setBackground(AZUL);
            }
        });

        rodape.add(painelTotal, BorderLayout.CENTER);
        rodape.add(irPagamento, BorderLayout.EAST);
        add(rodape, BorderLayout.SOUTH);

        atualizarTela();

        /*
        ==============================================================
        Eventos
        ==============================================================
        */
        voltar.addActionListener(e -> {
            TelaHome home = new TelaHome();
            dispose();
        });

        irPagamento.addActionListener(e -> {
            TelaConfirmacao confirmacao = new TelaConfirmacao();
            dispose();
        });

        setVisible(true);
    }

    public void atualizarTela(){
        painelItens.removeAll();
        List<ItemCarrinho> itens = Carrinho.getItens();

        if(itens.isEmpty()){
            painelItens.add(Box.createVerticalGlue());
            painelItens.add(lblVazio);
            painelItens.add(Box.createVerticalGlue());
            lblTotal.setText("Total: R$ 0,00");
            lblQuantidadeItens.setText("0 itens");
            irPagamento.setEnabled(false);
            irPagamento.setBackground(new Color(180, 190, 200));
            irPagamento.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }else{
            int totalItens = 0;
            for(ItemCarrinho item : itens){
                painelItens.add(criarCardItem(item));
                painelItens.add(Box.createVerticalStrut(10));
                totalItens += item.getQuantidade();
            }
            lblTotal.setText(String.format("Total: R$ %.2f", Carrinho.getTotal()));
            lblQuantidadeItens.setText(totalItens + (totalItens == 1 ? " item" : " itens"));
            irPagamento.setEnabled(true);
            irPagamento.setBackground(AZUL);
            irPagamento.setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        painelItens.revalidate();
        painelItens.repaint();
    }

    public JPanel criarCardItem(ItemCarrinho item){
        JPanel card = new JPanel(new BorderLayout(10, 0));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new TelaHome.BordaArredondada(12, BORDA_CLARA),
                BorderFactory.createEmptyBorder(12, 14, 12, 14)
        ));

        //Nome e preço unitário
        JPanel painelInfo = new JPanel(new BorderLayout(0, 4));
        painelInfo.setOpaque(false);

        JLabel nome = new JLabel("<html>" + item.getProduto().getNome() + "</html>");
        nome.setFont(new Font("SansSerif", Font.BOLD, 14));
        nome.setForeground(new Color(40, 40, 40));

        JLabel precoUnitario = new JLabel(String.format("R$ %.2f cada", item.getProduto().getPreco()));
        precoUnitario.setFont(new Font("SansSerif", Font.PLAIN, 12));
        precoUnitario.setForeground(CINZA_TEXTO);

        painelInfo.add(nome, BorderLayout.NORTH);
        painelInfo.add(precoUnitario, BorderLayout.SOUTH);

        //Controles de quantidade
        JPanel painelQuantidade = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 0));
        painelQuantidade.setOpaque(false);

        JButton diminuir = criarBotaoQuantidade("−");
        JLabel quantidade = new JLabel(String.valueOf(item.getQuantidade()));
        quantidade.setFont(new Font("SansSerif", Font.BOLD, 14));
        quantidade.setHorizontalAlignment(SwingConstants.CENTER);
        quantidade.setPreferredSize(new Dimension(28, 28));
        JButton aumentar = criarBotaoQuantidade("+");

        painelQuantidade.add(diminuir);
        painelQuantidade.add(quantidade);
        painelQuantidade.add(aumentar);

        //Subtotal e remover
        JPanel painelDireita = new JPanel(new BorderLayout(0, 6));
        painelDireita.setOpaque(false);

        JLabel subtotal = new JLabel(String.format("R$ %.2f", item.getSubtotal()));
        subtotal.setFont(new Font("SansSerif", Font.BOLD, 15));
        subtotal.setForeground(AZUL_ESCURO);
        subtotal.setHorizontalAlignment(SwingConstants.RIGHT);

        JButton remover = new JButton("Remover");
        remover.setFont(new Font("SansSerif", Font.PLAIN, 12));
        remover.setForeground(VERMELHO);
        remover.setBorderPainted(false);
        remover.setContentAreaFilled(false);
        remover.setFocusPainted(false);
        remover.setOpaque(false);
        remover.setCursor(new Cursor(Cursor.HAND_CURSOR));
        remover.setHorizontalAlignment(SwingConstants.RIGHT);
        remover.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e){
                remover.setText("<html><u>Remover</u></html>");
            }
            @Override
            public void mouseExited(MouseEvent e){
                remover.setText("Remover");
            }
        });

        painelDireita.add(subtotal, BorderLayout.NORTH);
        painelDireita.add(remover, BorderLayout.SOUTH);

        //Eventos
        diminuir.addActionListener(e -> {
            Carrinho.alterarQuantidade(item.getProduto(), item.getQuantidade() - 1);
            atualizarTela();
        });

        aumentar.addActionListener(e -> {
            Carrinho.alterarQuantidade(item.getProduto(), item.getQuantidade() + 1);
            atualizarTela();
        });

        remover.addActionListener(e -> {
            Carrinho.remover(item.getProduto());
            atualizarTela();
        });

        card.add(painelInfo, BorderLayout.WEST);
        card.add(painelQuantidade, BorderLayout.CENTER);
        card.add(painelDireita, BorderLayout.EAST);

        return card;
    }

    private JButton criarBotaoQuantidade(String texto){
        JButton botao = new JButton(texto);
        botao.setFont(new Font("SansSerif", Font.BOLD, 14));
        botao.setPreferredSize(new Dimension(28, 28));
        botao.setFocusPainted(false);
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botao.setBackground(new Color(236, 240, 241));
        botao.setForeground(AZUL_ESCURO);
        botao.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDA_CLARA),
                BorderFactory.createEmptyBorder(2, 6, 2, 6)
        ));
        botao.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e){
                botao.setBackground(new Color(218, 228, 235));
            }
            @Override
            public void mouseExited(MouseEvent e){
                botao.setBackground(new Color(236, 240, 241));
            }
        });
        return botao;
    }
}
