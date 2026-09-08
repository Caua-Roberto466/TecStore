package ui;

import logica.Sessao;
import logica.Usuario;

import javax.swing.*;
import java.awt.*;

public class TelaPerfil extends JFrame {
    JLabel prfNome, prfEmail;
    Usuario usuario = Sessao.getUsuarioLogado();
    ImageIcon img = new ImageIcon("editar.png");
    Image imgR = img.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
    ImageIcon icEditar = new ImageIcon(imgR);
    JButton editar = new JButton(icEditar);

    public TelaPerfil(){
        componentes();
    }

    public void componentes(){
        //Configuração da tela
        setTitle("TecStore - Login");
        setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500,500);
        setLocationRelativeTo(null);
        setVisible(true);

        prfNome = new JLabel();
        prfEmail = new JLabel();

        prfNome.setText(usuario.getNome());
        prfEmail.setText(usuario.getEmail());

        editar.setBounds(20,20,20,20);

        add(editar);
    }
}
