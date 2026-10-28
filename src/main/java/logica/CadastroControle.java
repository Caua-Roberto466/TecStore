package logica;

import conexao.Conexao;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;

public class CadastroControle {
    Conexao con = new Conexao();

    public boolean cadastrar(String nome, String email, String senha, String telefone) {
        String senhaHash = BCrypt.hashpw(senha, BCrypt.gensalt());
        String sql = "INSERT INTO usuario (nome, email, senha_hash, telefone) VALUES (?, ?, ?, ?)";

        try{
            String sqlVerifica = "SELECT id_usuario FROM usuario WHERE email = ?";
            try(var stmtVerifica = con.getConn().prepareStatement(sqlVerifica)){
                stmtVerifica.setString(1, email);
                try(var rsVerifica = stmtVerifica.executeQuery()){
                    if(rsVerifica.next()){
                        return false;
                    }
                }
            }

            try(var stmt = con.getConn().prepareStatement(sql)){
                stmt.setString(1, nome);
                stmt.setString(2, email);
                stmt.setString(3, senhaHash);
                stmt.setString(4, telefone);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
