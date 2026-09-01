package conexao;

import ui.TelaInicial;

import javax.swing.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexao {
    String url = "jdbc:sqlserver://localhost:49681;databaseName=TecStore;encrypt=true;trustServerCertificate=true;";
    String user = "bancotec";
    String senha = "Teste123!";

    public Conexao(){
        try{
            Connection conn = DriverManager.getConnection(url, user, senha);
            if(conn != null){
                JOptionPane.showMessageDialog(null, "Conexão estabelecida com sucesso");
                TelaInicial tela = new TelaInicial();
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
    }
}
