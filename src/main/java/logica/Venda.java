import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Venda {

    private int idVenda;
    private LocalDateTime dataVenda;
    private BigDecimal valorTotal;
    private String formaPagamento;
    private int idUsuario;
    private int idVendedor;
    private int idItem;

    public Venda() {
    }

    public Venda(LocalDateTime dataVenda, BigDecimal valorTotal, String formaPagamento,
                 int idUsuario, int idVendedor, int idItem) {
        this.dataVenda = dataVenda;
        this.valorTotal = valorTotal;
        this.formaPagamento = formaPagamento;
        this.idUsuario = idUsuario;
        this.idVendedor = idVendedor;
        this.idItem = idItem;
    }

    public Venda(int idVenda, LocalDateTime dataVenda, BigDecimal valorTotal, String formaPagamento,
                 int idUsuario, int idVendedor, int idItem) {
        this.idVenda = idVenda;
        this.dataVenda = dataVenda;
        this.valorTotal = valorTotal;
        this.formaPagamento = formaPagamento;
        this.idUsuario = idUsuario;
        this.idVendedor = idVendedor;
        this.idItem = idItem;
    }

    public int getIdVenda() {
        return idVenda;
    }

    public void setIdVenda(int idVenda) {
        this.idVenda = idVenda;
    }

    public LocalDateTime getDataVenda() {
        return dataVenda;
    }

    public void setDataVenda(LocalDateTime dataVenda) {
        this.dataVenda = dataVenda;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public String getFormaPagamento() {
        return formaPagamento;
    }

    public void setFormaPagamento(String formaPagamento) {
        this.formaPagamento = formaPagamento;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public int getIdVendedor() {
        return idVendedor;
    }

    public void setIdVendedor(int idVendedor) {
        this.idVendedor = idVendedor;
    }

    public int getIdItem() {
        return idItem;
    }

    public void setIdItem(int idItem) {
        this.idItem = idItem;
    }

    @Override
    public String toString() {
        return "Venda{" +
                "idVenda=" + idVenda +
                ", dataVenda=" + dataVenda +
                ", valorTotal=" + valorTotal +
                ", formaPagamento='" + formaPagamento + '\'' +
                ", idUsuario=" + idUsuario +
                ", idVendedor=" + idVendedor +
                ", idItem=" + idItem +
                '}';
    }
}
