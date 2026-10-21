package logica;

public class ItemCarrinho {
    private Produto produto;
    private int quantidade;

    public ItemCarrinho(Produto produto, int quantidade){
        this.produto = produto;
        this.quantidade = quantidade;
    }

    public Produto getProduto() { return produto; }
    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) {
        if (quantidade >= 0){
            this.quantidade = quantidade;
        }else{
            this.quantidade = 0;
        }
    }
    public double getSubtotal() { return produto.getPreco() * quantidade; }
}
