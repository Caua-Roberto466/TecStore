package conexao;

import javax.swing.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexao {
    String url = "jdbc:sqlserver://localhost\\SQLEXPRESS;databaseName=TecStore;encrypt=true;trustServerCertificate=true;";
    String user = "root";
    String senha = "";

    public Conexao(){
        try{
            Connection conn = DriverManager.getConnection(url, user, senha);
            if(conn != null){
                JOptionPane.showMessageDialog(null, "Conexão estabelecida com sucesso");
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
    }
}
