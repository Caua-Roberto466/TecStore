import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class TesteVenda {

    public static void main(String[] args) {
        VendaDAO dao = new VendaDAO();

        try {
            // Criar nova venda
            Venda novaVenda = new Venda(
                    LocalDateTime.now(),
                    new BigDecimal("199.90"),
                    "Cartão de Crédito",
                    1,  // id_usuario
                    2,  // id_vendedor
                    3   // id_item
            );

            int id = dao.inserir(novaVenda);
            System.out.println("Venda inserida com id: " + id);

            // Buscar por id
            Venda venda = dao.buscarPorId(id);
            System.out.println("Venda encontrada: " + venda);

            // Atualizar
            venda.setFormaPagamento("Pix");
            dao.atualizar(venda);
            System.out.println("Venda atualizada.");

            // Listar todas
            List<Venda> todas = dao.listarTodas();
            System.out.println("Total de vendas: " + todas.size());
            todas.forEach(System.out::println);

            // Deletar
            dao.deletar(id);
            System.out.println("Venda removida.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
