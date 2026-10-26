import logica.Venda;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class VendaDAO {

    private static final String SQL_INSERT =
            "INSERT INTO Venda (data_venda, valor_total, forma_pagamento, id_usuario, id_vendedor, id_item) " +
            "VALUES (?, ?, ?, ?, ?, ?)";

    private static final String SQL_UPDATE =
            "UPDATE Venda SET data_venda = ?, valor_total = ?, forma_pagamento = ?, " +
            "id_usuario = ?, id_vendedor = ?, id_item = ? WHERE id_venda = ?";

    private static final String SQL_DELETE =
            "DELETE FROM Venda WHERE id_venda = ?";

    private static final String SQL_SELECT_BY_ID =
            "SELECT id_venda, data_venda, valor_total, forma_pagamento, id_usuario, id_vendedor, id_item " +
            "FROM Venda WHERE id_venda = ?";

    private static final String SQL_SELECT_ALL =
            "SELECT id_venda, data_venda, valor_total, forma_pagamento, id_usuario, id_vendedor, id_item " +
            "FROM Venda ORDER BY id_venda";

    public int inserir(Venda venda) throws SQLException {
        try (Connection conn = ConexaoSQLServer.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setTimestamp(1, Timestamp.valueOf(venda.getDataVenda()));
            stmt.setBigDecimal(2, venda.getValorTotal());
            stmt.setString(3, venda.getFormaPagamento());
            stmt.setInt(5, venda.getIdVendedor());
            stmt.setInt(6, venda.getIdItem());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int idGerado = rs.getInt(1);
                    venda.setIdVenda(idGerado);
                    return idGerado;
                }
            }
        }
        return -1;
    }

    public boolean atualizar(Venda venda) throws SQLException {
        try (Connection conn = ConexaoSQLServer.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_UPDATE)) {

            stmt.setTimestamp(1, Timestamp.valueOf(venda.getDataVenda()));
            stmt.setBigDecimal(2, venda.getValorTotal());
            stmt.setString(3, venda.getFormaPagamento());
            stmt.setInt(4, venda.getIdUsuario());
            stmt.setInt(5, venda.getIdVendedor());
            stmt.setInt(6, venda.getIdItem());
            stmt.setInt(7, venda.getIdVenda());

            return stmt.executeUpdate() > 0;
        }
    }

    public boolean deletar(int idVenda) throws SQLException {
        try (Connection conn = ConexaoSQLServer.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_DELETE)) {

            stmt.setInt(1, idVenda);
            return stmt.executeUpdate() > 0;
        }
    }

    public Venda buscarPorId(int idVenda) throws SQLException {
        try (Connection conn = ConexaoSQLServer.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_SELECT_BY_ID)) {

            stmt.setInt(1, idVenda);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearVenda(rs);
                }
            }
        }
        return null;
    }


}
