package ui;

import logica.AtualizarPerfil;
import logica.ExcluirConta;
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
    JButton editarEmail = new JButton(icEditar);

    JTextField nomeEditado = new JTextField(100);
    JButton salvarNome = new JButton("Salvar");
    JTextField emailEditado = new JTextField(100);
    JButton salvarEmail = new JButton("Salvar");

    JButton excluirConta = new JButton("Excluir");

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

        // Instâncias
        prfNome = new JLabel();
        prfEmail = new JLabel();

        //Configuração
        prfNome.setText(usuario.getNome());
        prfEmail.setText(usuario.getEmail());

        //Estilização
        prfNome.setBounds(10, 20, 140, 22);
        prfEmail.setBounds(10, 60, 140, 20);
        editarNome.setBounds(155,20,20,20);
        editarEmail.setBounds(155,60,20,20);
        excluirConta.setBounds(10, 140, 80, 20);

        prfNome.setFont(new Font("SansSerif", Font.PLAIN, 20));

        //Adicionando na tela
        add(editarNome);
        add(editarEmail);
        add(prfNome);
        add(prfEmail);
        add(excluirConta);

        /*
        ==============================================================
        Eventos
        ==============================================================
        */
        editarNome.addActionListener(e ->{
            nomeEditado.setText(prfNome.getText());
            nomeEditado.setBounds(10, 40, 100, 20);
            salvarNome.setBounds(130, 40, 80, 20);
            add(salvarNome);
            add(nomeEditado);
            revalidate();
            repaint();
            nomeEditado.requestFocus();
        });

        salvarNome.addActionListener(e -> {
            AtualizarPerfil att = new AtualizarPerfil();
            boolean alterou = att.atualizarNome(nomeEditado.getText());
            if(alterou){
                prfNome.setText(nomeEditado.getText());
            }
            remove(salvarNome);
            remove(nomeEditado);
            revalidate();
            repaint();
        });

        editarEmail.addActionListener(e ->{
            emailEditado.setText(prfEmail.getText());
            emailEditado.setBounds(10, 82, 100, 20);
            salvarEmail.setBounds(130, 82, 80, 20);
            add(salvarEmail);
            add(emailEditado);
            revalidate();
            repaint();
            emailEditado.requestFocus();
        });

        salvarEmail.addActionListener(e -> {
            AtualizarPerfil att = new AtualizarPerfil();
            boolean alterou = att.atualizarEmail(emailEditado.getText());
            if(alterou){
                prfEmail.setText(emailEditado.getText());
            }
            remove(salvarEmail);
            remove(emailEditado);
            revalidate();
            repaint();
        });

        excluirConta.addActionListener(e -> {
            int op = JOptionPane.showConfirmDialog(null,"Deseja realmente excluir sua conta? [Ação irreversível]", "Confirmação", JOptionPane.YES_NO_OPTION);

            if(op == JOptionPane.YES_OPTION){
                ExcluirConta exc = new ExcluirConta();
                if(exc.excluirConta(prfEmail.getText())){
                    JOptionPane.showMessageDialog(null, "Conta excluída cm exito");
                    TelaCadastro tela = new TelaCadastro();
                    setVisible(false);
                }
            } else if(op == JOptionPane.NO_OPTION) {
                JOptionPane.showMessageDialog(null, "Ação interrompida, conta a salvo");
            }
        });
    }
}
