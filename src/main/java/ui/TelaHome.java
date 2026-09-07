package ui;

import javax.swing.*;

public class TelaHome extends JFrame {
    public TelaHome(){
        componentes();
    }

    public void componentes(){
        //Configuração da tela
        setTitle("TecStore - Login");
        setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500,350);
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
