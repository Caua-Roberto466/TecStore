package logica;

import main.java.conexao.Conexao;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ExcluirConta {
    Conexao con = new Conexao();

    public boolean excluirConta(String email){
        String sql = "DELETE FROM usuario WHERE email = ?";
        String emailBanco = Sessao.getUsuarioLogado().getEmail();

        try(PreparedStatement stmt = con.getConn().prepareStatement(sql)){
            stmt.setString(1, email);
            int exc = stmt.executeUpdate();
            return exc > 0;
        }catch(SQLException e){
            return false;
        }
    }
}
