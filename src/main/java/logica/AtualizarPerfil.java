package logica;

import main.java.conexao.Conexao;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AtualizarPerfil {
    Conexao con = new Conexao();
    Usuario usuario = Sessao.getUsuarioLogado();

    public boolean atualizarNome(String nome){
        String sql = "UPDATE usuario SET nome = ? WHERE email = ?";
        String emailBanco = usuario.getEmail();

        try(PreparedStatement stmt = con.getConn().prepareStatement(sql)){
            stmt.setString(1, nome);
            stmt.setString(2, emailBanco);
            int afetados = stmt.executeUpdate();
            return afetados > 0;
        }catch(SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean atualizarEmail(String email){
        String sql = "UPDATE usuario SET email = ? WHERE email = ?";
        String emailBanco = usuario.getEmail();

        try(PreparedStatement stmt = con.getConn().prepareStatement(sql)){
            stmt.setString(1, email);
            stmt.setString(2, emailBanco);
            int afetados = stmt.executeUpdate();
            return afetados > 0;
        }catch(SQLException e){
            e.printStackTrace();
            return false;
        }
    }
}
