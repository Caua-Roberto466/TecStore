package logica;

import main.java.conexao.Conexao;

public class AtualizarPerfil {
    Conexao con = new Conexao();
    public void atualizarNome(String nome){
        String sql = "UPDATE usuario SET nome = ? WHERE email = ?";

        
    }
}
