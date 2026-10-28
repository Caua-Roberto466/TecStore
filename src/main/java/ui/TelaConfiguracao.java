package ui;

import logica.AtualizarPerfil;
import logica.Sessao;
import logica.Usuario;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class TelaConfiguracao extends JFrame {
    private static final Color AZUL = new Color(41, 128, 185);
    private static final Color AZUL_ESCURO = new Color(31, 97, 141);
    private static final Color AZUL_CLARO = new Color(235, 245, 251);
    private static final Color CINZA_FUNDO = new Color(240, 242, 245);
    private static final Color CINZA_TEXTO = new Color(100, 100, 100);
    private static final Color BORDA_CLARA = new Color(220, 224, 230);

    private static final int LARGURA = 480;
    private static final int ALTURA = 500;

    private Usuario usuario = Sessao.getUsuarioLogado();

    private JTextField nomeEditado = new JTextField();
    private JTextField emailEditado = new JTextField();
    private JPasswordField senhaEditada = new JPasswordField();

    private JButton salvarNome = new JButton("Salvar");
    private JButton salvarEmail = new JButton("Salvar");
    private JButton salvarSenha = new JButton("Salvar");
    private JButton voltar = new JButton("Voltar ao perfil");

    public TelaConfiguracao() {
        componentes();
    }

    public void componentes() {
        setTitle("TecStore - Configuracao");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
        getContentPane().setBackground(CINZA_FUNDO);
        getContentPane().setPreferredSize(new Dimension(LARGURA, ALTURA));
        setResizable(false);

        // ===== Cabeçalho com degradê (mesmo da TelaPerfil) =====
        JPanel cabecalho = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setPaint(new GradientPaint(0, 0, AZUL, getWidth(), getHeight(), AZUL_ESCURO));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        cabecalho.setBounds(0, 0, LARGURA, 110);

        JLabel titulo = new JLabel("Configurações", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 20));
        titulo.setForeground(Color.WHITE);
        titulo.setBounds(0, 25, LARGURA, 30);
        cabecalho.add(titulo);

        JLabel subtitulo = new JLabel("Atualize seus dados de acesso", SwingConstants.CENTER);
        subtitulo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitulo.setForeground(new Color(220, 236, 247));
        subtitulo.setBounds(0, 55, LARGURA, 20);
        cabecalho.add(subtitulo);

        // ===== Cartão com os campos =====
        JPanel card = new JPanel(null);
        card.setBackground(Color.WHITE);
        card.setBorder(new TelaHome.BordaArredondada(12, BORDA_CLARA));
        card.setBounds(30, 135, LARGURA - 60, 255);

        nomeEditado.setText(usuario.getNome());
        emailEditado.setText(usuario.getEmail());

        adicionarSecao(card, "NOME", nomeEditado, salvarNome, 15);
        adicionarSecao(card, "E-MAIL", emailEditado, salvarEmail, 90);
        adicionarSecao(card, "NOVA SENHA", senhaEditada, salvarSenha, 165);

        // ===== Botão voltar =====
        voltar.setBounds(140, 410, 200, 42);
        estilizarSecundario(voltar);

        add(cabecalho);
        add(card);
        add(voltar);

        // ===== Ações =====
        salvarNome.addActionListener(e -> salvarNome());
        salvarEmail.addActionListener(e -> salvarEmail());
        salvarSenha.addActionListener(e -> salvarSenha());

        voltar.addActionListener(e -> {
            TelaPerfil perfil = new TelaPerfil();
            dispose();
        });

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // ---------- Ações de salvar ----------
    private void salvarNome() {
        String novoNome = nomeEditado.getText().trim();
        if (novoNome.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Digite o novo nome.");
            return;
        }
        AtualizarPerfil att = new AtualizarPerfil();
        if (att.atualizarNome(novoNome)) {
            JOptionPane.showMessageDialog(this, "Nome atualizado com sucesso");
        } else {
            JOptionPane.showMessageDialog(this, "Nao foi possivel atualizar o nome.");
        }
    }

    private void salvarEmail() {
        String novoEmail = emailEditado.getText().trim();
        if (novoEmail.isEmpty() || !novoEmail.contains("@")) {
            JOptionPane.showMessageDialog(this, "Digite um e-mail valido.");
            return;
        }
        AtualizarPerfil att = new AtualizarPerfil();
        if (att.atualizarEmail(novoEmail)) {
            JOptionPane.showMessageDialog(this, "E-mail atualizado com sucesso");
        } else {
            JOptionPane.showMessageDialog(this, "Nao foi possivel atualizar o e-mail.");
        }
    }

    private void salvarSenha() {
        String novaSenha = new String(senhaEditada.getPassword());
        if (novaSenha.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Digite a nova senha.");
            return;
        }
        AtualizarPerfil att = new AtualizarPerfil();
        if (att.atualizarSenha(novaSenha)) {
            JOptionPane.showMessageDialog(this, "Senha atualizada com sucesso");
            senhaEditada.setText("");
        } else {
            JOptionPane.showMessageDialog(this, "Nao foi possivel atualizar a senha.");
        }
    }

    // ---------- Seção: rótulo + campo + botão salvar ----------
    private void adicionarSecao(JPanel painel, String rotulo, JTextField campo, JButton botao, int y) {
        JLabel lbl = new JLabel(rotulo);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 11));
        lbl.setForeground(CINZA_TEXTO);
        lbl.setBounds(20, y, 300, 18);

        campo.setBounds(20, y + 22, 290, 34);
        estilizarCampo(campo);

        botao.setBounds(320, y + 22, 80, 34);
        estilizarPrimario(botao);

        painel.add(lbl);
        painel.add(campo);
        painel.add(botao);
    }

    // ---------- Estilos ----------
    private void estilizarCampo(JTextField campo) {
        campo.setFont(new Font("SansSerif", Font.PLAIN, 14));
        campo.setForeground(new Color(40, 40, 40));
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
        b.setFont(new Font("SansSerif", Font.BOLD, 13));
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
}