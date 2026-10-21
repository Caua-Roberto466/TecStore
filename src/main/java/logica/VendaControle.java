package logica;

import conexao.Conexao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class VendaControle {
    Conexao con = new Conexao();

    public int finalizarVenda(List<ItemCarrinho> itens, String formaPagamento){
        int idVenda = -1;

        if(itens == null || itens.isEmpty()){
            return -1;
        }

        Usuario usuario = Sessao.getUsuarioLogado();
        if(usuario == null){
            return -1;
        }

        try{
            con.getConn().setAutoCommit(false);

            int idUsuario = buscarIdUsuario(con, usuario.getEmail());
            if(idUsuario <= 0){
                con.getConn().rollback();
                return -1;
            }

            int idVendedor = buscarPrimeiroVendedor(con);
            if(idVendedor <= 0){
                con.getConn().rollback();
                return -1;
            }

            double valorTotal = 0;
            for(ItemCarrinho item : itens){
                valorTotal += item.getSubtotal();
            }

            String sqlVenda = "INSERT INTO venda (valor_total, forma_pagamento, id_usuario, id_vendedor) VALUES (?, ?, ?, ?)";
            try(PreparedStatement stmt = con.getConn().prepareStatement(sqlVenda, Statement.RETURN_GENERATED_KEYS)){
                stmt.setDouble(1, valorTotal);
                stmt.setString(2, formaPagamento);
                stmt.setInt(3, idUsuario);
                stmt.setInt(4, idVendedor);
                stmt.executeUpdate();

                try(ResultSet rs = stmt.getGeneratedKeys()){
                    if(rs.next()){
                        idVenda = rs.getInt(1);
                    }
                }
            }

            if(idVenda <= 0){
                con.getConn().rollback();
                return -1;
            }

            String sqlItem = "INSERT INTO item_venda (quantidade, preco_unitario, subtotal, id_produto) VALUES (?, ?, ?, ?)";
            String sqlVendaItem = "INSERT INTO venda_item (id_venda, id_item) VALUES (?, ?)";

            for(ItemCarrinho item : itens){
                int idItem;
                try(PreparedStatement stmt = con.getConn().prepareStatement(sqlItem, Statement.RETURN_GENERATED_KEYS)){
                    stmt.setInt(1, item.getQuantidade());
                    stmt.setDouble(2, item.getProduto().getPreco());
                    stmt.setDouble(3, item.getSubtotal());
                    stmt.setInt(4, item.getProduto().getId());
                    stmt.executeUpdate();

                    try(ResultSet rs = stmt.getGeneratedKeys()){
                        if(rs.next()){
                            idItem = rs.getInt(1);
                        }else{
                            con.getConn().rollback();
                            return -1;
                        }
                    }
                }

                try(PreparedStatement stmt = con.getConn().prepareStatement(sqlVendaItem)){
                    stmt.setInt(1, idVenda);
                    stmt.setInt(2, idItem);
                    stmt.executeUpdate();
                }
            }

            con.getConn().commit();
        }catch (SQLException e){
            e.printStackTrace();
            try{
                con.getConn().rollback();
            }catch (SQLException ex){
                ex.printStackTrace();
            }
            return -1;
        }finally {
            try{
                con.getConn().setAutoCommit(true);
            }catch (SQLException e){
                e.printStackTrace();
            }
        }
        return idVenda;
    }

    private int buscarIdUsuario(Conexao con, String email) throws SQLException {
        String sql = "SELECT id_usuario FROM usuario WHERE email = ?";
        try(PreparedStatement stmt = con.getConn().prepareStatement(sql)){
            stmt.setString(1, email);
            try(ResultSet rs = stmt.executeQuery()){
                if(rs.next()){
                    return rs.getInt("id_usuario");
                }
            }
        }
        return -1;
    }

    private int buscarPrimeiroVendedor(Conexao con) throws SQLException {
        String sql = "SELECT TOP 1 id_vendedor FROM vendedor ORDER BY id_vendedor";
        try(PreparedStatement stmt = con.getConn().prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()){
            if(rs.next()){
                return rs.getInt("id_vendedor");
            }
        }
        return -1;
    }
}
