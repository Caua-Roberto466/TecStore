package ui;

import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.text.ParseException;

public class TelaCadastro extends JFrame {
    JLabel rtlNome, rtlEmail, rtlSenha, rtlTelefone, titulo;
    JFormattedTextField campo;
    JTextField txtNome, txtEmail;
    JFormattedTextField txtTelefone;
    JPasswordField txtSenha;
    JButton cadastrar, login;

    public TelaCadastro(){
        componentes();
    }

    public void componentes(){
        //Configuração da tela
        setTitle("TecStore - Cadastro");
        setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500,440);
        setLocationRelativeTo(null);
        setVisible(true);

        //Instâncias
        rtlNome = new JLabel("Nome:");
        rtlEmail = new JLabel("Email:");
        rtlSenha = new JLabel("Senha:");
        rtlTelefone = new JLabel("Telefone:");
        titulo = new JLabel("Cadastre-se para acessar a sua conta");

        txtNome = new JTextField(100);
        txtEmail = new JTextField(150);

        txtSenha = new JPasswordField(30);

        txtTelefone = new JFormattedTextField();

        cadastrar = new JButton("Cadastrar");
        login = new JButton("Já tem uma conta? Clique aqui para acessá-la");

        titulo.setBounds(50, 30, 400, 30);

        rtlNome.setBounds(50, 90, 100, 25);
        txtNome.setBounds(150, 90, 250, 25);

        rtlEmail.setBounds(50, 130, 100, 25);
        txtEmail.setBounds(150, 130, 250, 25);

        rtlTelefone.setBounds(50, 170, 100, 25);
        txtTelefone.setBounds(150, 170, 250, 25);

        rtlSenha.setBounds(50, 210, 100, 25);
        txtSenha.setBounds(150, 210, 250, 25);

        try {
            MaskFormatter mascara = new MaskFormatter("(##) #####-####");
            mascara.setPlaceholderCharacter('_');
            campo = new JFormattedTextField(mascara);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        cadastrar.setBounds(150, 260, 200, 40);
        login.setBounds(90, 320, 320, 25);

        titulo.setFont(new Font("SansSerif", Font.BOLD, 16));
        titulo.setHorizontalAlignment(SwingConstants.CENTER);

        rtlNome.setFont(new Font("SansSerif", Font.PLAIN, 13));
        rtlEmail.setFont(new Font("SansSerif", Font.PLAIN, 13));
        rtlSenha.setFont(new Font("SansSerif", Font.PLAIN, 13));
        rtlTelefone.setFont(new Font("SansSerif", Font.PLAIN, 13));

        cadastrar.setFont(new Font("SansSerif", Font.BOLD, 14));
        cadastrar.setBackground(new Color(41, 128, 185));
        cadastrar.setForeground(Color.WHITE);
        cadastrar.setFocusPainted(false);

        //Deixar o botão "login" parecido com um texto clicável
        login.setFont(new Font("SansSerif", Font.PLAIN, 13));
        login.setForeground(new Color(41, 128, 185));
        login.setHorizontalAlignment(SwingConstants.CENTER);
        login.setBorderPainted(false);
        login.setContentAreaFilled(false);
        login.setFocusPainted(false);
        login.setOpaque(false);
        login.setCursor(new Cursor(Cursor.HAND_CURSOR));

        add(titulo);
        add(rtlNome);
        add(txtNome);
        add(rtlEmail);
        add(txtEmail);
        add(rtlSenha);
        add(txtSenha);
        add(rtlTelefone);
        add(txtTelefone);
        add(cadastrar);
        add(login);

        login.addActionListener(e -> {
            TelaLogin login = new TelaLogin();
            setVisible(false);
        });
    }
}