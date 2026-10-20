package ui;

import logica.Carrinho;
import logica.ItemCarrinho;
import logica.VendaControle;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TelaConfirmacao extends JFrame {
    JLabel titulo;

    //Painel de pagamento
    JPanel painelPagamento;
    JLabel lblResumo, lblTotalPagamento;
    JRadioButton rdCartao, rdPix;
    JButton confirmar, voltarCarrinho;

    //Painel de sucesso
    JPanel painelSucesso;
    JLabel lblCheck, lblSucesso, lblDetalhes;
    JButton voltarLoja, irPerfil;

    public TelaConfirmacao(){
        componentes();
    }

    public void componentes(){
        //Configuração da tela
        setTitle("TecStore - Confirmação de pagamento");
        setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(550,500);
        setLocationRelativeTo(null);

        //Instâncias
        titulo = new JLabel("Confirmação de pagamento");

        painelPagamento = new JPanel(null);
        lblResumo = new JLabel();
        lblTotalPagamento = new JLabel();
        rdCartao = new JRadioButton("Cartão");
        rdPix = new JRadioButton("Pix");
        confirmar = new JButton("Confirmar pagamento");
        voltarCarrinho = new JButton("Voltar ao carrinho");

        painelSucesso = new JPanel(null);
        lblCheck = new JLabel("✓");
        lblSucesso = new JLabel("Pagamento confirmado!");
        lblDetalhes = new JLabel();
        voltarLoja = new JButton("Voltar à loja");
        irPerfil = new JButton("Minha conta");

        //Set Bounds
        titulo.setBounds(20, 20, 400, 30);
        painelPagamento.setBounds(20, 60, 510, 390);
        painelSucesso.setBounds(20, 60, 510, 390);

        lblResumo.setBounds(0, 0, 510, 200);
        lblTotalPagamento.setBounds(0, 210, 510, 30);
        rdCartao.setBounds(0, 260, 100, 25);
        rdPix.setBounds(110, 260, 100, 25);
        confirmar.setBounds(0, 320, 200, 40);
        voltarCarrinho.setBounds(220, 320, 160, 40);

        lblCheck.setBounds(0, 20, 510, 60);
        lblSucesso.setBounds(0, 90, 510, 40);
        lblDetalhes.setBounds(0, 150, 510, 120);
        voltarLoja.setBounds(40, 300, 160, 40);
        irPerfil.setBounds(220, 300, 160, 40);

        //Estilização
        titulo.setFont(new Font("SansSerif", Font.BOLD, 20));

        lblResumo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblResumo.setVerticalAlignment(SwingConstants.TOP);

        lblTotalPagamento.setFont(new Font("SansSerif", Font.BOLD, 16));

        ButtonGroup grupoPagamento = new ButtonGroup();
        grupoPagamento.add(rdCartao);
        grupoPagamento.add(rdPix);
        rdCartao.setSelected(true);
        rdCartao.setFont(new Font("SansSerif", Font.PLAIN, 14));
        rdPix.setFont(new Font("SansSerif", Font.PLAIN, 14));

        confirmar.setFont(new Font("SansSerif", Font.BOLD, 14));
        confirmar.setBackground(new Color(41, 128, 185));
        confirmar.setForeground(Color.WHITE);
        confirmar.setFocusPainted(false);
        confirmar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        voltarCarrinho.setFont(new Font("SansSerif", Font.PLAIN, 13));
        voltarCarrinho.setForeground(new Color(41, 128, 185));
        voltarCarrinho.setBorderPainted(false);
        voltarCarrinho.setContentAreaFilled(false);
        voltarCarrinho.setFocusPainted(false);
        voltarCarrinho.setOpaque(false);
        voltarCarrinho.setCursor(new Cursor(Cursor.HAND_CURSOR));

        lblCheck.setFont(new Font("SansSerif", Font.BOLD, 50));
        lblCheck.setForeground(new Color(46, 160, 67));
        lblCheck.setHorizontalAlignment(SwingConstants.CENTER);

        lblSucesso.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblSucesso.setHorizontalAlignment(SwingConstants.CENTER);

        lblDetalhes.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblDetalhes.setHorizontalAlignment(SwingConstants.CENTER);

        voltarLoja.setFont(new Font("SansSerif", Font.BOLD, 14));
        voltarLoja.setBackground(new Color(41, 128, 185));
        voltarLoja.setForeground(Color.WHITE);
        voltarLoja.setFocusPainted(false);
        voltarLoja.setCursor(new Cursor(Cursor.HAND_CURSOR));

        irPerfil.setFont(new Font("SansSerif", Font.PLAIN, 13));
        irPerfil.setForeground(new Color(41, 128, 185));
        irPerfil.setBorderPainted(false);
        irPerfil.setContentAreaFilled(false);
        irPerfil.setFocusPainted(false);
        irPerfil.setOpaque(false);
        irPerfil.setCursor(new Cursor(Cursor.HAND_CURSOR));

        //Montar resumo do pedido
        StringBuilder resumo = new StringBuilder("<html>");
        List<ItemCarrinho> itens = Carrinho.getItens();
        for(ItemCarrinho item : itens){
            resumo.append(item.getQuantidade())
                  .append("x ")
                  .append(item.getProduto().getNome())
                  .append(" - R$ ")
                  .append(String.format("%.2f", item.getSubtotal()))
                  .append("<br>");
        }
        resumo.append("</html>");
        lblResumo.setText(resumo.toString());
        lblTotalPagamento.setText(String.format("Total: R$ %.2f", Carrinho.getTotal()));

        //Adicionando na tela
        painelPagamento.add(lblResumo);
        painelPagamento.add(lblTotalPagamento);
        painelPagamento.add(rdCartao);
        painelPagamento.add(rdPix);
        painelPagamento.add(confirmar);
        painelPagamento.add(voltarCarrinho);

        painelSucesso.add(lblCheck);
        painelSucesso.add(lblSucesso);
        painelSucesso.add(lblDetalhes);
        painelSucesso.add(voltarLoja);
        painelSucesso.add(irPerfil);
        painelSucesso.setVisible(false);

        add(titulo);
        add(painelPagamento);
        add(painelSucesso);

        /*
        ==============================================================
        Eventos
        ==============================================================
        */
        voltarCarrinho.addActionListener(e -> {
            TelaCarrinho carrinho = new TelaCarrinho();
            dispose();
        });

        confirmar.addActionListener(e -> {
            if(Carrinho.estaVazio()){
                JOptionPane.showMessageDialog(null, "Seu carrinho está vazio");
                return;
            }

            String formaPagamento = rdCartao.isSelected() ? "Cartão" : "Pix";

            VendaControle venda = new VendaControle();
            int idVenda = venda.finalizarVenda(Carrinho.getItens(), formaPagamento);

            if(idVenda > 0){
                String data = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
                lblDetalhes.setText("<html>Nº da venda: " + idVenda
                        + "<br>Data: " + data
                        + "<br>Forma de pagamento: " + formaPagamento
                        + "<br>Valor total: " + String.format("R$ %.2f", Carrinho.getTotal())
                        + "</html>");

                Carrinho.limpar();

                painelPagamento.setVisible(false);
                painelSucesso.setVisible(true);
                revalidate();
                repaint();
            }else{
                JOptionPane.showMessageDialog(null, "Não foi possível concluir o pagamento.\nVerifique sua conexão com o banco e tente novamente.");
            }
        });

        voltarLoja.addActionListener(e -> {
            TelaHome home = new TelaHome();
            dispose();
        });

        irPerfil.addActionListener(e -> {
            TelaPerfil perfil = new TelaPerfil();
            dispose();
        });

        setVisible(true);
    }
}
