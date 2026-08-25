package org.example.ui;

import javax.swing.*;

public class TelaCadastro extends JFrame {
    JLabel rtlNome, rtlEmail, rtlSenha, titulo, login;
    JTextField txtNome, txtEmail, txtSenha;
    JButton cadastrar;

    public TelaCadastro(){
        componentes();
    }

    public void componentes(){
        //Configuração da tela
        setTitle("TecStore - Cadastro");
        setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500,450);
        setLocationRelativeTo(null);
        setVisible(true);

        //Instâncias
        rtlNome = new JLabel("Nome:");
        rtlEmail = new JLabel("Email:");
        rtlSenha = new JLabel("Senha:");
        titulo = new JLabel("Cadastre-se para acessar a sua conta");
        login = new JLabel("Já tem uma conta? Clique aqui para acessá-la");

        txtNome = new JTextField(100);
        txtEmail = new JTextField(150);
        txtSenha = new JTextField(30);
    }
}
