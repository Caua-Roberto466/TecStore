package ui;

import logica.ExcluirConta;
import logica.Sessao;
import logica.Usuario;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class TelaPerfil extends JFrame {
    private static final Color AZUL = new Color(41, 128, 185);
    private static final Color AZUL_ESCURO = new Color(31, 97, 141);
    private static final Color AZUL_CLARO = new Color(235, 245, 251);
    private static final Color CINZA_FUNDO = new Color(240, 242, 245);
    private static final Color CINZA_TEXTO = new Color(100, 100, 100);
    private static final Color BORDA_CLARA = new Color(220, 224, 230);
    private static final Color VERMELHO = new Color(192, 57, 43);
    private static final Color VERDE = new Color(39, 174, 96);

    private static final int LARGURA = 480;
    private static final int ALTURA = 520;

    private JLabel prfNome, prfEmail;
    private Usuario usuario = Sessao.getUsuarioLogado();

    private JButton excluirConta = new JButton("Excluir conta");
    private JButton irLoja = new JButton("Ir para a loja");
    private JButton editarConta = new JButton("Editar conta");

    public TelaPerfil() {
        componentes();
    }

    public void componentes() {
        setTitle("TecStore - Minha Conta");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
        getContentPane().setBackground(CINZA_FUNDO);
        getContentPane().setPreferredSize(new Dimension(LARGURA, ALTURA));
        setResizable(false);

        // ===== Avatar (adicionado primeiro para ficar por cima do cabeçalho) =====
        AvatarPanel avatar = new AvatarPanel(iniciais(usuario.getNome()));
        avatar.setBounds((LARGURA - 100) / 2, 80, 100, 100);

        // ===== Cabeçalho com degradê =====
        JPanel cabecalho = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setPaint(new GradientPaint(0, 0, AZUL, getWidth(), getHeight(), AZUL_ESCURO));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        cabecalho.setBounds(0, 0, LARGURA, 130);

        JLabel titulo = new JLabel("Minha conta", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 20));
        titulo.setForeground(Color.WHITE);
        titulo.setBounds(0, 20, LARGURA, 30);
        cabecalho.add(titulo);

        JLabel subtitulo = new JLabel("Gerencie as informações do seu perfil", SwingConstants.CENTER);
        subtitulo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitulo.setForeground(new Color(220, 236, 247));
        subtitulo.setBounds(0, 50, LARGURA, 20);
        cabecalho.add(subtitulo);

        // ===== Nome e e-mail abaixo do avatar =====
        prfNome = new JLabel(usuario.getNome(), SwingConstants.CENTER);
        prfNome.setFont(new Font("SansSerif", Font.BOLD, 18));
        prfNome.setForeground(new Color(40, 40, 40));
        prfNome.setBounds(0, 188, LARGURA, 26);

        prfEmail = new JLabel(usuario.getEmail(), SwingConstants.CENTER);
        prfEmail.setFont(new Font("SansSerif", Font.PLAIN, 14));
        prfEmail.setForeground(CINZA_TEXTO);
        prfEmail.setBounds(0, 214, LARGURA, 20);

        // ===== Cartão com as informações =====
        JPanel card = new JPanel(null);
        card.setBackground(Color.WHITE);
        card.setBorder(new TelaHome.BordaArredondada(12, BORDA_CLARA));
        card.setBounds(30, 255, LARGURA - 60, 125);

        JLabel secao = new JLabel("INFORMAÇÕES DA CONTA");
        secao.setFont(new Font("SansSerif", Font.BOLD, 11));
        secao.setForeground(CINZA_TEXTO);
        secao.setBounds(20, 10, 300, 20);
        card.add(secao);

        adicionarLinha(card, "Nome", usuario.getNome(), 35, false);
        adicionarLinha(card, "E-mail", usuario.getEmail(), 62, false);
        // Se a sua classe Usuario tiver telefone, descomente a linha abaixo:
        // adicionarLinha(card, "Telefone", usuario.getTelefone(), 89, false);
        adicionarLinha(card, "Status", "Ativa", 89, true);

        // ===== Botões =====
        editarConta.setBounds(30, 405, 200, 42);
        irLoja.setBounds(250, 405, 200, 42);
        excluirConta.setBounds(140, 462, 200, 30);

        estilizarPrimario(editarConta);
        estilizarSecundario(irLoja);
        estilizarPerigo(excluirConta);

        add(avatar);
        add(cabecalho);
        add(prfNome);
        add(prfEmail);
        add(card);
        add(editarConta);
        add(irLoja);
        add(excluirConta);

        // ===== Ações =====
        editarConta.addActionListener(e -> {
            TelaConfiguracao config = new TelaConfiguracao();
            dispose();
        });

        irLoja.addActionListener(e -> {
            TelaHome home = new TelaHome();
            dispose();
        });

        excluirConta.addActionListener(e -> {
            int op = JOptionPane.showConfirmDialog(this, "Deseja realmente excluir sua conta?", "Confirmacao", JOptionPane.YES_NO_OPTION);

            if (op == JOptionPane.YES_OPTION) {
                ExcluirConta exc = new ExcluirConta();
                if (exc.excluirConta(prfEmail.getText())) {
                    JOptionPane.showMessageDialog(this, "Conta excluida com sucesso");
                    TelaLogin login = new TelaLogin();
                    dispose();
                }
            } else if (op == JOptionPane.NO_OPTION) {
                JOptionPane.showMessageDialog(this, "Acao interrompida, conta a salvo");
            }
        });

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // ---------- Linha de informação (rótulo à esquerda, valor à direita) ----------
    private void adicionarLinha(JPanel painel, String rotulo, String valor, int y, boolean destaque) {
        JLabel lblRotulo = new JLabel(rotulo);
        lblRotulo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblRotulo.setForeground(CINZA_TEXTO);
        lblRotulo.setBounds(20, y, 100, 22);

        JLabel lblValor = new JLabel(valor, SwingConstants.RIGHT);
        lblValor.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblValor.setForeground(destaque ? VERDE : new Color(40, 40, 40));
        lblValor.setBounds(120, y, painel.getWidth() - 140, 22);

        painel.add(lblRotulo);
        painel.add(lblValor);
    }

    // ---------- Estilos dos botões ----------
    private void estilizarPrimario(JButton b) {
        b.setFont(new Font("SansSerif", Font.BOLD, 14));
        b.setBackground(AZUL);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder());
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { b.setBackground(AZUL_ESCURO); }
            @Override
            public void mouseExited(MouseEvent e) { b.setBackground(AZUL); }
        });
    }

    private void estilizarSecundario(JButton b) {
        b.setFont(new Font("SansSerif", Font.BOLD, 14));
        b.setBackground(Color.WHITE);
        b.setForeground(AZUL);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createLineBorder(AZUL, 2));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { b.setBackground(AZUL_CLARO); }
            @Override
            public void mouseExited(MouseEvent e) { b.setBackground(Color.WHITE); }
        });
    }

    private void estilizarPerigo(JButton b) {
        b.setFont(new Font("SansSerif", Font.PLAIN, 13));
        b.setForeground(VERMELHO);
        b.setContentAreaFilled(false);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setOpaque(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    // ---------- Iniciais do nome (ex.: "Maria Silva" -> "MS") ----------
    private String iniciais(String nome) {
        if (nome == null || nome.trim().isEmpty()) return "?";
        String[] partes = nome.trim().split("\\s+");
        String ini = partes[0].substring(0, 1);
        if (partes.length > 1) {
            ini += partes[partes.length - 1].substring(0, 1);
        }
        return ini.toUpperCase();
    }

    // ---------- Avatar circular com as iniciais ----------
    private static class AvatarPanel extends JPanel {
        private final String texto;

        AvatarPanel(String texto) {
            this.texto = texto;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int d = Math.min(getWidth(), getHeight());

            // anel branco
            g2.setColor(Color.WHITE);
            g2.fillOval(0, 0, d, d);

            // círculo azul interno
            g2.setPaint(new GradientPaint(0, 0, new Color(52, 152, 219), 0, d, AZUL_ESCURO));
            g2.fillOval(5, 5, d - 10, d - 10);

            // iniciais
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("SansSerif", Font.BOLD, 34));
            FontMetrics fm = g2.getFontMetrics();
            int x = (d - fm.stringWidth(texto)) / 2;
            int y = (d - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(texto, x, y);

            g2.dispose();
        }
    }
}