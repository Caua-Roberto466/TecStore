package main.java.conexao;

import main.java.ui.TelaInicial;

import javax.swing.*;
import java.sql.*;

public class Conexao {
    public static String url = "jdbc:sqlserver://localhost:49681;databaseName=TecStore;encrypt=true;trustServerCertificate=true;";
    public static String user = "bancotec";
    public static String senha = "Teste123!";
    Connection conn;
    Statement stmt;
    ResultSet resultset;

    public Conexao(){
        try{
            conn = DriverManager.getConnection(url, user, senha);
            if(conn != null){
                JOptionPane.showMessageDialog(null, "Conexão estabelecida com sucesso");

            }
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    public Connection getConn() throws SQLException {
        return conn;
    }

    public void comandoSQL(String sql){
        try{
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            resultset = stmt.executeQuery(sql);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "ERRO INESPERADO!!! "+e);
        }
    }
}
