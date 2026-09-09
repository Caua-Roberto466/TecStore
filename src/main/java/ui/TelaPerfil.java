package ui;

import logica.AtualizarPerfil;
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
    JButton editarNome = new JButton(icEditar);
    JTextField nomeEditado = new JTextField(100);
    JButton salvar = new JButton("Salvar");

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

        prfNome.setBounds(10, 20, 100, 20);
        editarNome.setBounds(130,18,20,20);

        add(editarNome);
        add(prfNome);

        editarNome.addActionListener(e ->{
            nomeEditado.setText(prfNome.getText());
            nomeEditado.setBounds(10, 40, 100, 20);
            salvar.setBounds(130, 40, 80, 20);
            add(salvar);
            add(nomeEditado);
            revalidate();
            repaint();
            nomeEditado.requestFocus();
        });
        salvar.addActionListener(e -> {
            AtualizarPerfil att = new AtualizarPerfil();
            boolean alterou = att.atualizarNome(nomeEditado.getText());
            if(alterou){
                prfNome.setText(nomeEditado.getText());
            }
            remove(salvar);
            remove(nomeEditado);
            revalidate();
            repaint();
        });
    }
}
