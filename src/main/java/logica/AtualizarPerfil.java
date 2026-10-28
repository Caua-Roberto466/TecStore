package logica;

import conexao.Conexao;
import org.mindrot.jbcrypt.BCrypt;

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

    public boolean atualizarSenha(String senha){
        String senhaHash = BCrypt.hashpw(senha, BCrypt.gensalt());
        String sql = "UPDATE usuario SET senha_hash = ? WHERE email = ?";
        try(PreparedStatement stmt = con.getConn().prepareStatement(sql)){
            stmt.setString(1, senhaHash);
            stmt.setString(2, usuario.getEmail());
            int afetados = stmt.executeUpdate();
            return afetados > 0;
        }catch(SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean atualizarTelefone(String telefone){
        String sql = "UPDATE usuario SET telefone = ? WHERE email = ?";
        try(PreparedStatement stmt = con.getConn().prepareStatement(sql)){
            stmt.setString(1, telefone);
            stmt.setString(2, usuario.getEmail());
            int afetados = stmt.executeUpdate();
            return afetados > 0;
        }catch(SQLException e){
            e.printStackTrace();
            return false;
        }
    }
}
