package logica;

import conexao.Conexao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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

    public boolean adicionarProduto(String nome, String descricao, String categoria, double preco, int quantidade){
        try{
            String sqlEstoque = "INSERT INTO Estoque (quantidade_atual, estoque_minimo) VALUES (?, ?)";
            int idEstoque = -1;
            try(var stmt = con.getConn().prepareStatement(sqlEstoque, Statement.RETURN_GENERATED_KEYS)){
                stmt.setInt(1, quantidade);
                stmt.setInt(2, 0);
                stmt.executeUpdate();
                try(var rs = stmt.getGeneratedKeys()){
                    if(rs.next()){
                        idEstoque = rs.getInt(1);
                    }
                }
            }
            if(idEstoque <= 0){
                return false;
            }
            String sqlProduto = "INSERT INTO produto (nome_produto, descricao_produto, categoria_produto, preco_produto, id_estoque) VALUES (?, ?, ?, ?, ?)";
            try(var stmt = con.getConn().prepareStatement(sqlProduto)){
                stmt.setString(1, nome);
                stmt.setString(2, descricao);
                stmt.setString(3, categoria);
                stmt.setDouble(4, preco);
                stmt.setInt(5, idEstoque);
                stmt.executeUpdate();
            }
        }catch (SQLException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
