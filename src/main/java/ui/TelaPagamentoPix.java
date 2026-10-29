package ui;

import logica.PixCopiaECola;
import logica.QrCode;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Janela de pagamento via Pix: mostra o QR code e o código "copia e cola".
 * Use isConfirmado() depois que a janela fechar para saber se o cliente confirmou o pagamento.
 */
public class TelaPagamentoPix extends JDialog {
    // Dados do recebedor. Troque pela chave Pix real da loja quando for usar de verdade.
    private static final String CHAVE_PIX = "tecstore@example.com";
    private static final String NOME_RECEBEDOR = "TecStore";
    private static final String CIDADE = "Sao Paulo";
    private static final int VALIDADE_SEGUNDOS = 10 * 60;

    private static final Color AZUL = new Color(41, 128, 185);
    private static final Color AZUL_ESCURO = new Color(31, 97, 141);
    private static final Color AZUL_CLARO = new Color(235, 245, 251);
    private static final Color VERDE = new Color(39, 174, 96);
    private static final Color VERMELHO = new Color(192, 57, 43);
    private static final Color CINZA_FUNDO = new Color(240, 242, 245);
    private static final Color CINZA_TEXTO = new Color(100, 100, 100);
    private static final Color BORDA_CLARA = new Color(220, 224, 230);
    private static final Color TEXTO_ESCURO = new Color(40, 40, 40);

    private static final int LARGURA = 460;

    private boolean confirmado = false;
    private final String codigoPix;
    private final QrCode qr;
    private int restante = VALIDADE_SEGUNDOS;
    private Timer relogio;

    private JLabel lblTempo;
    private JButton btnCopiar, btnConfirmar, btnCancelar;

    public TelaPagamentoPix(Frame dono, double total) {
        super(dono, "TecStore - Pagamento via Pix", true);
        String txid = "TEC" + System.currentTimeMillis();
        this.codigoPix = PixCopiaECola.gerar(CHAVE_PIX, NOME_RECEBEDOR, CIDADE, total, txid);
        this.qr = new QrCode(codigoPix);
        componentes(total);
    }

    public boolean isConfirmado() {
        return confirmado;
    }

    private void componentes(double total) {
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

        JLabel titulo = new JLabel("Pagamento via Pix", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 20));
        titulo.setForeground(Color.WHITE);
        titulo.setBounds(0, 16, LARGURA, 30);

        JLabel subtitulo = new JLabel("Escaneie o QR code ou use o Pix copia e cola", SwingConstants.CENTER);
        subtitulo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitulo.setForeground(new Color(220, 236, 247));
        subtitulo.setBounds(0, 46, LARGURA, 20);

        header.add(titulo);
        header.add(subtitulo);

        //Conteúdo
        Cartao card = new Cartao(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(16, 20, 18, 20));

        JPanel centro = new JPanel();
        centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));
        centro.setOpaque(false);

        JLabel lblValorTxt = new JLabel("VALOR A PAGAR");
        lblValorTxt.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblValorTxt.setForeground(CINZA_TEXTO);
        lblValorTxt.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblValor = new JLabel(String.format("R$ %.2f", total));
        lblValor.setFont(new Font("SansSerif", Font.BOLD, 26));
        lblValor.setForeground(AZUL_ESCURO);
        lblValor.setAlignmentX(Component.CENTER_ALIGNMENT);

        QrPanel painelQr = new QrPanel(qr);
        painelQr.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblCodigo = new JLabel("PIX COPIA E COLA");
        lblCodigo.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblCodigo.setForeground(CINZA_TEXTO);
        lblCodigo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextArea areaCodigo = new JTextArea(codigoPix, 3, 30);
        areaCodigo.setEditable(false);
        areaCodigo.setLineWrap(true);
        areaCodigo.setWrapStyleWord(false);
        areaCodigo.setFont(new Font("Monospaced", Font.PLAIN, 11));
        areaCodigo.setForeground(TEXTO_ESCURO);
        areaCodigo.setBackground(new Color(248, 249, 251));
        areaCodigo.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));

        JScrollPane scrollCodigo = new JScrollPane(areaCodigo);
        scrollCodigo.setBorder(BorderFactory.createLineBorder(BORDA_CLARA, 1));
        scrollCodigo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        scrollCodigo.setPreferredSize(new Dimension(380, 70));
        scrollCodigo.setAlignmentX(Component.CENTER_ALIGNMENT);

        btnCopiar = new JButton("Copiar código");
        estilizarSecundario(btnCopiar);
        btnCopiar.setPreferredSize(new Dimension(380, 38));
        btnCopiar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        btnCopiar.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblTempo = new JLabel(textoTempo());
        lblTempo.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblTempo.setForeground(CINZA_TEXTO);
        lblTempo.setAlignmentX(Component.CENTER_ALIGNMENT);

        btnConfirmar = new JButton("Já fiz o pagamento");
        btnCancelar = new JButton("Cancelar");
        estilizarPrimario(btnConfirmar);
        estilizarSecundario(btnCancelar);

        JPanel botoes = new JPanel(new GridLayout(1, 2, 10, 0));
        botoes.setOpaque(false);
        botoes.add(btnConfirmar);
        botoes.add(btnCancelar);
        botoes.setPreferredSize(new Dimension(380, 44));
        botoes.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        botoes.setAlignmentX(Component.CENTER_ALIGNMENT);

        centro.add(lblValorTxt);
        centro.add(lblValor);
        centro.add(Box.createVerticalStrut(10));
        centro.add(painelQr);
        centro.add(Box.createVerticalStrut(12));
        centro.add(lblCodigo);
        centro.add(Box.createVerticalStrut(6));
        centro.add(scrollCodigo);
        centro.add(Box.createVerticalStrut(8));
        centro.add(btnCopiar);
        centro.add(Box.createVerticalStrut(8));
        centro.add(lblTempo);
        centro.add(Box.createVerticalStrut(14));
        centro.add(botoes);

        card.add(centro, BorderLayout.CENTER);

        JPanel corpo = new JPanel(new BorderLayout());
        corpo.setOpaque(false);
        corpo.setBorder(BorderFactory.createEmptyBorder(18, 24, 22, 24));
        corpo.add(card, BorderLayout.CENTER);

        add(header, BorderLayout.NORTH);
        add(corpo, BorderLayout.CENTER);

        //Eventos
        btnCopiar.addActionListener(e -> copiarCodigo());

        btnConfirmar.addActionListener(e -> {
            confirmado = true;
            dispose();
        });

        btnCancelar.addActionListener(e -> dispose());

        //Contagem regressiva da validade do código
        relogio = new Timer(1000, e -> {
            restante--;
            if (restante <= 0) {
                relogio.stop();
                lblTempo.setText("Código expirado. Feche esta janela e tente novamente.");
                lblTempo.setForeground(VERMELHO);
                btnConfirmar.setEnabled(false);
                btnCopiar.setEnabled(false);
            } else {
                lblTempo.setText(textoTempo());
            }
        });
        relogio.start();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                relogio.stop();
            }
        });

        pack();
        setLocationRelativeTo(getOwner());
    }

    private String textoTempo() {
        return String.format("Código válido por %02d:%02d", restante / 60, restante % 60);
    }

    private void copiarCodigo() {
        try {
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(codigoPix), null);
            btnCopiar.setText("Código copiado!");
        } catch (Exception ex) {
            btnCopiar.setText("Não foi possível copiar");
        }
        Timer volta = new Timer(1500, ev -> btnCopiar.setText("Copiar código"));
        volta.setRepeats(false);
        volta.start();
    }

    /*
    ==============================================================
    Estilos
    ==============================================================
    */
    private void estilizarPrimario(JButton b) {
        b.setFont(new Font("SansSerif", Font.BOLD, 14));
        b.setBackground(VERDE);
        b.setForeground(Color.WHITE);
        b.setOpaque(true);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder());
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { if (b.isEnabled()) b.setBackground(VERDE.darker()); }
            @Override
            public void mouseExited(MouseEvent e) { if (b.isEnabled()) b.setBackground(VERDE); }
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

    //Desenha o QR code (fundo branco com margem de 4 módulos, como manda o padrão)
    private static class QrPanel extends JPanel {
        private final QrCode qr;

        QrPanel(QrCode qr) {
            this.qr = qr;
            setOpaque(false);
            Dimension d = new Dimension(236, 236);
            setPreferredSize(d);
            setMinimumSize(d);
            setMaximumSize(d);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();

            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, w - 1, h - 1, 14, 14);
            g2.setColor(BORDA_CLARA);
            g2.drawRoundRect(0, 0, w - 1, h - 1, 14, 14);

            int n = qr.getTamanho();
            int margem = 4;
            int esc = Math.min(w, h) / (n + margem * 2);
            int total = esc * n;
            int x0 = (w - total) / 2;
            int y0 = (h - total) / 2;

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
            g2.setColor(Color.BLACK);
            for (int y = 0; y < n; y++) {
                for (int x = 0; x < n; x++) {
                    if (qr.getModulo(x, y)) {
                        g2.fillRect(x0 + x * esc, y0 + y * esc, esc, esc);
                    }
                }
            }
            g2.dispose();
        }
    }
}
