package logica;

import main.java.conexao.Conexao;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginControle {
    Conexao con = new Conexao();

    String nomeUser, emailUser, telefoneUser, senhaUser;

    public boolean logar(String email, String senha){
        boolean res = false;
        String sql = "SELECT email, senha_hash FROM usuario WHERE email = ?";

        try(PreparedStatement stmt = con.getConn().prepareStatement(sql)){
            stmt.setString(1, email);

            try(ResultSet rs = stmt.executeQuery()){
                if (rs.next()){
                    String senhaBanco = rs.getString("senha_hash");
                    res = BCrypt.checkpw(senha, senhaBanco);
                }else{
                    res = false;
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
            res = false;
        }
        return res;
    }
}
