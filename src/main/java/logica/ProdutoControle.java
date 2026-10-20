package logica;

import conexao.Conexao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProdutoControle {
    Conexao con = new Conexao();

    public List<Produto> listarProdutos(){
        List<Produto> produtos = new ArrayList<>();
        String sql = "SELECT id_produto, nome_produto, descricao_produto, categoria_produto, preco_produto FROM produto";

        try(PreparedStatement stmt = con.getConn().prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()){
            while(rs.next()){
                produtos.add(new Produto(
                        rs.getInt("id_produto"),
                        rs.getString("nome_produto"),
                        rs.getString("descricao_produto"),
                        rs.getString("categoria_produto"),
                        rs.getDouble("preco_produto")
                ));
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return produtos;
    }
}
