package ui;

import javax.swing.*;
import java.awt.*;

public class TelaInicial extends JFrame {
    JLabel titulo, explicacao, callToAction;
    JButton entrar;
    public TelaInicial(){
        componentes();
    }
    public void componentes(){
        //Criar o layout da Janela
        setTitle("TecStore");
        setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500,350);
        setLocationRelativeTo(null);
        setVisible(true);

        //Instâncias
        titulo = new JLabel("Bem-Vindo á minha loja");
        explicacao = new JLabel("Uma loja de eletrônicos para melhorar seu dia a dia e ");
        callToAction = new JLabel("Acesse sua conta e veja tudo que você fazer");

        entrar = new JButton("Entrar");

        //Estilização
        titulo.setBounds(50, 40, 400, 40);
        explicacao.setBounds(50, 100, 400, 30);
        callToAction.setBounds(50, 150, 400, 30);
        entrar.setBounds(180, 220, 140, 40);

        titulo.setFont(new Font("SansSerif", Font.BOLD, 20));
        titulo.setHorizontalAlignment(SwingConstants.CENTER);

        explicacao.setFont(new Font("SansSerif", Font.PLAIN, 14));
        explicacao.setHorizontalAlignment(SwingConstants.CENTER);

        callToAction.setFont(new Font("SansSerif", Font.ITALIC, 13));
        callToAction.setHorizontalAlignment(SwingConstants.CENTER);

        entrar.setFont(new Font("SansSerif", Font.BOLD, 14));
        entrar.setBackground(new Color(41, 128, 185));
        entrar.setForeground(Color.WHITE);
        entrar.setFocusPainted(false);

        add(titulo);
        add(explicacao);
        add(callToAction);
        add(entrar);
        //Lógica

    }
}
