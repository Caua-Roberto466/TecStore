package ui;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.YearMonth;

/**
 * Janela de pagamento com cartão: pede número, nome, validade, CVV e parcelas.
 * Os dados são apenas validados aqui na tela; nada é gravado nem enviado para lugar nenhum.
 * Use isConfirmado() e getParcelas() depois que a janela fechar.
 */
public class TelaPagamentoCartao extends JDialog {
    private static final Color AZUL = new Color(41, 128, 185);
    private static final Color AZUL_ESCURO = new Color(31, 97, 141);
    private static final Color AZUL_CLARO = new Color(235, 245, 251);
    private static final Color VERMELHO = new Color(192, 57, 43);
    private static final Color CINZA_FUNDO = new Color(240, 242, 245);
    private static final Color CINZA_TEXTO = new Color(100, 100, 100);
    private static final Color BORDA_CLARA = new Color(220, 224, 230);
    private static final Color TEXTO_ESCURO = new Color(40, 40, 40);

    private static final int LARGURA = 460;

    private boolean confirmado = false;
    private int parcelas = 1;
    private final double total;

    private final JTextField txtNumero = new JTextField();
    private final JTextField txtNome = new JTextField();
    private final JTextField txtValidade = new JTextField();
    private final JPasswordField txtCvv = new JPasswordField();
    private JComboBox<String> cmbParcelas;
    private JLabel lblErro;
    private JButton btnPagar, btnCancelar;
    private PreviewCartao preview;

    public TelaPagamentoCartao(Frame dono, double total) {
        super(dono, "TecStore - Pagamento com cartão", true);
        this.total = total;
        componentes();
    }

    public boolean isConfirmado() {
        return confirmado;
    }

    public int getParcelas() {
        return parcelas;
    }

    private void componentes() {
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(CINZA_FUNDO);
        setResizable(false);

        //Cabeçalho com degradê
        JPanel header = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setPaint(new GradientPaint(0, 0, AZUL, getWidth(), getHeight(), AZUL_ESCURO));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        header.setPreferredSize(new Dimension(LARGURA, 84));

        JLabel titulo = new JLabel("Pagamento com cartão", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 20));
        titulo.setForeground(Color.WHITE);
        titulo.setBounds(0, 16, LARGURA, 30);

        JLabel subtitulo = new JLabel("Informe os dados do cartão para finalizar", SwingConstants.CENTER);
        subtitulo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitulo.setForeground(new Color(220, 236, 247));
        subtitulo.setBounds(0, 46, LARGURA, 20);

        header.add(titulo);
        header.add(subtitulo);

        //Pré-visualização do cartão (atualiza enquanto o cliente digita)
        preview = new PreviewCartao();
        preview.setAlignmentX(Component.CENTER_ALIGNMENT);

        //Máscaras de digitação
        ((AbstractDocument) txtNumero.getDocument()).setDocumentFilter(new FiltroDigitos(16, new int[]{4, 4, 4, 4}, ' '));
        ((AbstractDocument) txtValidade.getDocument()).setDocumentFilter(new FiltroDigitos(4, new int[]{2, 2}, '/'));
        ((AbstractDocument) txtCvv.getDocument()).setDocumentFilter(new FiltroDigitos(4, new int[]{4}, ' '));

        estilizarCampo(txtNumero);
        estilizarCampo(txtNome);
        estilizarCampo(txtValidade);
        estilizarCampo(txtCvv);

        DocumentListener atualizar = new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { preview.repaint(); }
            public void removeUpdate(DocumentEvent e) { preview.repaint(); }
            public void changedUpdate(DocumentEvent e) { preview.repaint(); }
        };
        txtNumero.getDocument().addDocumentListener(atualizar);
        txtNome.getDocument().addDocumentListener(atualizar);
        txtValidade.getDocument().addDocumentListener(atualizar);

        //Parcelas (sem juros)
        cmbParcelas = new JComboBox<>();
        int maxParcelas = (int) Math.max(1, Math.min(6, Math.floor(total / 20.0)));
        for (int i = 1; i <= maxParcelas; i++) {
            cmbParcelas.addItem(String.format("%dx de R$ %.2f sem juros", i, total / i));
        }
        cmbParcelas.setFont(new Font("SansSerif", Font.PLAIN, 14));
        cmbParcelas.setBackground(Color.WHITE);
        cmbParcelas.setCursor(new Cursor(Cursor.HAND_CURSOR));

        //Formulário
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setOpaque(false);

        JPanel linhaValCvv = new JPanel(new GridLayout(1, 2, 12, 0));
        linhaValCvv.setOpaque(false);
        linhaValCvv.add(criarCampo("VALIDADE (MM/AA)", txtValidade));
        linhaValCvv.add(criarCampo("CVV", txtCvv));

        JPanel pNumero = criarCampo("NÚMERO DO CARTÃO", txtNumero);
        JPanel pNome = criarCampo("NOME IMPRESSO NO CARTÃO", txtNome);
        JPanel pParcelas = criarCampo("PARCELAS", cmbParcelas);
        for (JComponent c : new JComponent[]{pNumero, pNome, linhaValCvv, pParcelas}) {
            c.setAlignmentX(Component.CENTER_ALIGNMENT);
            c.setMaximumSize(new Dimension(Integer.MAX_VALUE, c.getPreferredSize().height));
        }
        form.add(pNumero);
        form.add(Box.createVerticalStrut(8));
        form.add(pNome);
        form.add(Box.createVerticalStrut(8));
        form.add(linhaValCvv);
        form.add(Box.createVerticalStrut(8));
        form.add(pParcelas);

        lblErro = new JLabel(" ");
        lblErro.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblErro.setForeground(VERMELHO);
        lblErro.setAlignmentX(Component.CENTER_ALIGNMENT);

        btnPagar = new JButton(String.format("Pagar R$ %.2f", total));
        btnCancelar = new JButton("Cancelar");
        estilizarPrimario(btnPagar);
        estilizarSecundario(btnCancelar);

        JPanel botoes = new JPanel(new GridLayout(1, 2, 10, 0));
        botoes.setOpaque(false);
        botoes.add(btnPagar);
        botoes.add(btnCancelar);
        botoes.setPreferredSize(new Dimension(380, 44));
        botoes.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        botoes.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel aviso = new JLabel("Seus dados não são armazenados.");
        aviso.setFont(new Font("SansSerif", Font.PLAIN, 11));
        aviso.setForeground(CINZA_TEXTO);
        aviso.setAlignmentX(Component.CENTER_ALIGNMENT);

        Cartao card = new Cartao(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(16, 20, 14, 20));

        JPanel centro = new JPanel();
        centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));
        centro.setOpaque(false);
        centro.add(preview);
        centro.add(Box.createVerticalStrut(14));
        centro.add(form);
        centro.add(Box.createVerticalStrut(6));
        centro.add(lblErro);
        centro.add(Box.createVerticalStrut(6));
        centro.add(botoes);
        centro.add(Box.createVerticalStrut(8));
        centro.add(aviso);
        card.add(centro, BorderLayout.CENTER);

        JPanel corpo = new JPanel(new BorderLayout());
        corpo.setOpaque(false);
        corpo.setBorder(BorderFactory.createEmptyBorder(16, 24, 20, 24));
        corpo.add(card, BorderLayout.CENTER);

        add(header, BorderLayout.NORTH);
        add(corpo, BorderLayout.CENTER);

        //Eventos
        btnPagar.addActionListener(e -> validarEConfirmar());
        btnCancelar.addActionListener(e -> dispose());
        getRootPane().setDefaultButton(btnPagar);

        pack();
        setLocationRelativeTo(getOwner());
    }

    /*
    ==============================================================
    Validação
    ==============================================================
    */
    private void validarEConfirmar() {
        String numero = somenteDigitos(txtNumero.getText());
        String nome = txtNome.getText().trim();
        String validade = somenteDigitos(txtValidade.getText());
        String cvv = new String(txtCvv.getPassword());

        if (numero.length() < 13 || !passaLuhn(numero)) {
            erro("Número de cartão inválido.", txtNumero);
            return;
        }
        if (nome.length() < 3) {
            erro("Informe o nome impresso no cartão.", txtNome);
            return;
        }
        if (!validadeOk(validade)) {
            erro("Validade inválida ou cartão vencido.", txtValidade);
            return;
        }
        if (cvv.length() < 3) {
            erro("CVV inválido.", txtCvv);
            return;
        }

        parcelas = cmbParcelas.getSelectedIndex() + 1;
        confirmado = true;
        dispose();
    }

    private void erro(String mensagem, JComponent campo) {
        lblErro.setText(mensagem);
        campo.requestFocusInWindow();
    }

    private boolean validadeOk(String mmaa) {
        if (mmaa.length() != 4) return false;
        int mes = Integer.parseInt(mmaa.substring(0, 2));
        int ano = 2000 + Integer.parseInt(mmaa.substring(2, 4));
        if (mes < 1 || mes > 12) return false;
        return !YearMonth.of(ano, mes).isBefore(YearMonth.now());
    }

    //Algoritmo de Luhn: confere se o número de cartão é matematicamente válido
    private boolean passaLuhn(String numero) {
        int soma = 0;
        boolean dobrar = false;
        for (int i = numero.length() - 1; i >= 0; i--) {
            int d = numero.charAt(i) - '0';
            if (dobrar) {
                d *= 2;
                if (d > 9) d -= 9;
            }
            soma += d;
            dobrar = !dobrar;
        }
        return soma % 10 == 0;
    }

    private static String somenteDigitos(String s) {
        return s.replaceAll("\\D", "");
    }

    /*
    ==============================================================
    Estilos e componentes
    ==============================================================
    */
    private JPanel criarCampo(String rotulo, JComponent campo) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);

        JLabel lbl = new JLabel(rotulo);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 11));
        lbl.setForeground(CINZA_TEXTO);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        campo.setAlignmentX(Component.LEFT_ALIGNMENT);
        campo.setPreferredSize(new Dimension(100, 34));
        campo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));

        p.add(lbl);
        p.add(Box.createVerticalStrut(4));
        p.add(campo);
        return p;
    }

    private void estilizarCampo(JTextField campo) {
        campo.setFont(new Font("SansSerif", Font.PLAIN, 14));
        campo.setForeground(TEXTO_ESCURO);
        campo.setBorder(bordaCampo(BORDA_CLARA));
        campo.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) { campo.setBorder(bordaCampo(AZUL)); }
            @Override
            public void focusLost(FocusEvent e) { campo.setBorder(bordaCampo(BORDA_CLARA)); }
        });
    }

    private javax.swing.border.Border bordaCampo(Color cor) {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(cor, 1),
                BorderFactory.createEmptyBorder(0, 10, 0, 10)
        );
    }

    private void estilizarPrimario(JButton b) {
        b.setFont(new Font("SansSerif", Font.BOLD, 14));
        b.setBackground(AZUL);
        b.setForeground(Color.WHITE);
        b.setOpaque(true);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder());
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { if (b.isEnabled()) b.setBackground(AZUL_ESCURO); }
            @Override
            public void mouseExited(MouseEvent e) { if (b.isEnabled()) b.setBackground(AZUL); }
        });
    }

    private void estilizarSecundario(JButton b) {
        b.setFont(new Font("SansSerif", Font.BOLD, 14));
        b.setBackground(Color.WHITE);
        b.setForeground(AZUL);
        b.setOpaque(true);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createLineBorder(AZUL, 2));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { if (b.isEnabled()) b.setBackground(AZUL_CLARO); }
            @Override
            public void mouseExited(MouseEvent e) { if (b.isEnabled()) b.setBackground(Color.WHITE); }
        });
    }

    //Aceita só números, limita a quantidade e coloca os separadores (ex.: 1234 5678 ... ou 12/34)
    private static class FiltroDigitos extends DocumentFilter {
        private final int maxDigitos;
        private final int[] grupos;
        private final char separador;

        FiltroDigitos(int maxDigitos, int[] grupos, char separador) {
            this.maxDigitos = maxDigitos;
            this.grupos = grupos;
            this.separador = separador;
        }

        @Override
        public void insertString(FilterBypass fb, int offset, String texto, AttributeSet attr) throws BadLocationException {
            replace(fb, offset, 0, texto, attr);
        }

        @Override
        public void remove(FilterBypass fb, int offset, int tamanho) throws BadLocationException {
            replace(fb, offset, tamanho, "", null);
        }

        @Override
        public void replace(FilterBypass fb, int offset, int tamanho, String texto, AttributeSet attr) throws BadLocationException {
            String atual = fb.getDocument().getText(0, fb.getDocument().getLength());
            String novo = atual.substring(0, offset) + (texto == null ? "" : texto) + atual.substring(offset + tamanho);
            String digitos = somenteDigitos(novo);
            if (digitos.length() > maxDigitos) {
                digitos = digitos.substring(0, maxDigitos);
            }
            fb.replace(0, fb.getDocument().getLength(), formatar(digitos), attr);
        }

        private String formatar(String digitos) {
            StringBuilder sb = new StringBuilder();
            int limiteGrupo = grupos[0];
            int g = 0;
            for (int i = 0; i < digitos.length(); i++) {
                if (i == limiteGrupo && g + 1 < grupos.length) {
                    sb.append(separador);
                    g++;
                    limiteGrupo += grupos[g];
                }
                sb.append(digitos.charAt(i));
            }
            return sb.toString();
        }
    }

    //Card branco arredondado com sombra suave (mesmo estilo das outras telas)
    private static class Cartao extends JPanel {
        Cartao(LayoutManager layout) {
            super(layout);
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
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

    //Desenho do cartão que acompanha o que o cliente digita
    private class PreviewCartao extends JPanel {
        PreviewCartao() {
            setOpaque(false);
            Dimension d = new Dimension(300, 172);
            setPreferredSize(d);
            setMinimumSize(d);
            setMaximumSize(d);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();

            g2.setPaint(new GradientPaint(0, 0, new Color(44, 62, 80), w, h, AZUL));
            g2.fillRoundRect(0, 0, w, h, 18, 18);

            //Círculos decorativos
            g2.setClip(new java.awt.geom.RoundRectangle2D.Float(0, 0, w, h, 18, 18));
            g2.setColor(new Color(255, 255, 255, 22));
            g2.fillOval(w - 120, -60, 190, 190);
            g2.setColor(new Color(255, 255, 255, 14));
            g2.fillOval(w - 200, 60, 150, 150);
            g2.setClip(null);

            //Chip
            g2.setColor(new Color(241, 196, 15));
            g2.fillRoundRect(20, 22, 38, 28, 6, 6);
            g2.setColor(new Color(0, 0, 0, 50));
            g2.drawLine(20, 36, 58, 36);
            g2.drawLine(39, 22, 39, 50);

            //Bandeira
            String digitos = somenteDigitos(txtNumero.getText());
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("SansSerif", Font.BOLD | Font.ITALIC, 15));
            String bandeira = detectarBandeira(digitos);
            FontMetrics fmB = g2.getFontMetrics();
            g2.drawString(bandeira, w - 20 - fmB.stringWidth(bandeira), 40);

            //Número (com • nas posições que faltam)
            StringBuilder num = new StringBuilder();
            for (int i = 0; i < 16; i++) {
                if (i > 0 && i % 4 == 0) num.append(' ');
                num.append(i < digitos.length() ? digitos.charAt(i) : '\u2022');
            }
            g2.setFont(new Font("Monospaced", Font.BOLD, 19));
            g2.drawString(num.toString(), 20, 92);

            //Titular e validade
            g2.setFont(new Font("SansSerif", Font.PLAIN, 9));
            g2.setColor(new Color(255, 255, 255, 170));
            g2.drawString("TITULAR", 20, 122);
            g2.drawString("VALIDADE", w - 80, 122);

            g2.setColor(Color.WHITE);
            g2.setFont(new Font("SansSerif", Font.BOLD, 13));
            String nome = txtNome.getText().trim().isEmpty() ? "NOME DO TITULAR" : txtNome.getText().trim().toUpperCase();
            if (nome.length() > 24) nome = nome.substring(0, 24);
            g2.drawString(nome, 20, 142);

            String validade = txtValidade.getText().isEmpty() ? "MM/AA" : txtValidade.getText();
            g2.drawString(validade, w - 80, 142);
            g2.dispose();
        }

        private String detectarBandeira(String d) {
            if (d.startsWith("4")) return "VISA";
            if (d.length() >= 2) {
                int p2 = Integer.parseInt(d.substring(0, 2));
                if (p2 >= 51 && p2 <= 55) return "MASTERCARD";
                if (p2 == 34 || p2 == 37) return "AMEX";
            }
            if (d.length() >= 4) {
                int p4 = Integer.parseInt(d.substring(0, 4));
                if (p4 >= 2221 && p4 <= 2720) return "MASTERCARD";
            }
            return "CARTÃO";
        }
    }
}
