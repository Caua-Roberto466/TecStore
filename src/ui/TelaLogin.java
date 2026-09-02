package ui;

import javax.swing.*;
import java.awt.*;

public class TelaLogin extends JFrame {
    JLabel rtlEmail, rtlSenha, titulo;
    JTextField txtEmail;
    JPasswordField txtSenha;
    JButton entrar, cadastro;
    public TelaLogin(){
        //Configuração da tela
        setTitle("TecStore - Login");
        setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500,350);
        setLocationRelativeTo(null);
        setVisible(true);

        //Isntâncias
        rtlEmail = new JLabel("Email:");
        rtlSenha = new JLabel("Senha:");
        titulo = new JLabel("Acesse a sua conta para continuar");

        txtEmail = new JTextField(100);

        txtSenha = new JPasswordField(30);

        entrar = new JButton("Entrar");
        cadastro = new JButton("Não tem uma conta? CLique aqui e crie uma");

        //Set Bounds
        titulo.setBounds(50, 30, 400, 30);

        rtlEmail.setBounds(50, 90, 100, 25);
        txtEmail.setBounds(150, 90, 250, 25);

        rtlSenha.setBounds(50, 130, 100, 25);
        txtSenha.setBounds(150, 130, 250, 25);

        entrar.setBounds(150, 180, 200, 40);
        cadastro.setBounds(70, 240, 360, 25);

        //Adicionando na tela
        add(rtlEmail);
        add(rtlSenha);
        add(titulo);
        add(txtEmail);
        add(txtSenha);
        add(entrar);
        add(cadastro);

        //Estilização
        titulo.setFont(new Font("SansSerif", Font.BOLD, 16));
        titulo.setHorizontalAlignment(SwingConstants.CENTER);

        rtlEmail.setFont(new Font("SansSerif", Font.PLAIN, 13));
        rtlSenha.setFont(new Font("SansSerif", Font.PLAIN, 13));

        entrar.setFont(new Font("SansSerif", Font.BOLD, 14));
        entrar.setBackground(new Color(41, 128, 185));
        entrar.setForeground(Color.WHITE);
        entrar.setFocusPainted(false);

        //Deixar o botão "cadastro" parecido com um texto clicável
        cadastro.setFont(new Font("SansSerif", Font.PLAIN, 13));
        cadastro.setForeground(new Color(41, 128, 185));
        cadastro.setHorizontalAlignment(SwingConstants.CENTER);
        cadastro.setBorderPainted(false);
        cadastro.setContentAreaFilled(false);
        cadastro.setFocusPainted(false);
        cadastro.setOpaque(false);
        cadastro.setCursor(new Cursor(Cursor.HAND_CURSOR));

        cadastro.addActionListener(e -> {
            TelaCadastro tela = new TelaCadastro();
            setVisible(false);
        });
    }
}
