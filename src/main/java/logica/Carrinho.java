package logica;

import java.util.ArrayList;
import java.util.List;

public class Carrinho {
    private static final List<ItemCarrinho> itens = new ArrayList<>();

    public static void adicionar(Produto produto, int quantidade){
        for(ItemCarrinho item : itens){
            if(item.getProduto().getId() == produto.getId()){
                item.setQuantidade(item.getQuantidade() + quantidade);
                return;
            }
        }
        itens.add(new ItemCarrinho(produto, quantidade));
    }

    public static void alterarQuantidade(Produto produto, int quantidade){
        if(quantidade <= 0){
            remover(produto);
            return;
        }
        for(ItemCarrinho item : itens){
            if(item.getProduto().getId() == produto.getId()){
                item.setQuantidade(quantidade);
                return;
            }
        }
    }

    public static void remover(Produto produto){
        itens.removeIf(item -> item.getProduto().getId() == produto.getId());
    }

    public static List<ItemCarrinho> getItens(){
        return itens;
    }

    public static double getTotal(){
        double total = 0;
        for(ItemCarrinho item : itens){
            total += item.getSubtotal();
        }
        return total;
    }

    public static boolean estaVazio(){
        return itens.isEmpty();
    }

    public static void limpar(){
        itens.clear();
    }
}
