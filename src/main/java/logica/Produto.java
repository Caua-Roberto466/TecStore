package logica;

public class Produto {
    private int id;
    private String nome;
    private String descricao;
    private String categoria;
    private double preco;

    public Produto(int id, String nome, String descricao, String categoria, double preco){
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.categoria = categoria;
        this.preco = preco;
    }

    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getDescricao() { return descricao; }
    public String getCategoria() { return categoria; }
    public double getPreco() { return preco; }
}
