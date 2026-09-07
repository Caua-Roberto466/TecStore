package ui;

import logica.Sessao;
import logica.Usuario;

import javax.swing.*;

public class TelaPerfil extends JFrame {
    JLabel prfNome, prfEmail;
    Usuario usuario = Sessao.getUsuarioLogado();
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

        
    }
}
