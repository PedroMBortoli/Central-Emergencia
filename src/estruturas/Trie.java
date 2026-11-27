package estruturas;

import java.util.LinkedList;
import java.util.List;

public class Trie {

    //Classe do nó
    private static class No {
        TabelaHash<Character, No> filhos = new TabelaHash<>(16);
        boolean fimDePalavra = false;
        List<Object> valores = new LinkedList<>();
    }

    private No raiz;

    public Trie(){
        raiz = new No();
    }

    public void inserir(String chave, Object valor){
        String chaveNormalizada = chave.toUpperCase();
        No atual = raiz;

        for (int i = 0; i < chaveNormalizada.length(); i++){
            char c = chaveNormalizada.charAt(i);

            No proximo = atual.filhos.buscar(c);
            if (proximo == null){
                proximo = new No();
                atual.filhos.inserir(c, proximo);
            }

            atual = proximo;
        }

        atual.fimDePalavra = true;
        atual.valores.add(valor);
    }

    private  void coletarValores (No no, List<Object> resultado){
        if(no.fimDePalavra){
            resultado.addAll(no.valores);
        }

        for(No filho : no.filhos.listarTodos()){
            coletarValores(filho, resultado);
        }

    }

    public List<Object> buscarPorPrefixo(String prefixo){
        String prefixoNormalizado = prefixo.toUpperCase();
        No atual = raiz;

        for(int i = 0; i < prefixoNormalizado.length(); i++){
            char c = prefixoNormalizado.charAt(i);
            No proximo = atual.filhos.buscar(c);

            //caso do prefixo nao existir na arvore
            if(proximo == null){
                return new LinkedList<>();

            }
            atual = proximo;

        }

        List<Object> resultado = new LinkedList<>();
        coletarValores (atual, resultado);
        return  resultado;

    }

    // remove so esse valor da chave (duas ocorrencias podem ter a mesma descricao)
    public boolean remover(String chave, Object valor){
        // removido[0] guarda se o valor foi removido (a recursao ja devolve outra coisa)
        boolean[] removido = new boolean[1];
        removerRecursivo(raiz, chave.toUpperCase(), 0, valor, removido);
        return removido[0];
    }

    // desce ate o fim da palavra e, na volta, apaga os nos que ficaram vazios.
    // retorna true se o no atual pode ser removido do pai
    private boolean removerRecursivo(No no, String chaveNormalizada, int indice, Object valor, boolean[] removido){

        if (indice == chaveNormalizada.length()){
            //fim da palavra: so remove se ela existe
            if (!no.fimDePalavra){
                return false; //chave nao existe
            }

            removido[0] = no.valores.remove(valor); //remove so essa ocorrencia

            if (no.valores.isEmpty()){
                no.fimDePalavra = false; //nao sobrou nenhuma ocorrencia para essa chave
            }

            //so apaga o no se ele nao marca palavra e nao tem filhos
            return !no.fimDePalavra && no.filhos.getTamanho() == 0;
        }

        char c = chaveNormalizada.charAt(indice);
        No proximo = no.filhos.buscar(c);

        if (proximo == null){
            return false; //chave nao existe
        }

        boolean filhoFicouVazio = removerRecursivo(proximo, chaveNormalizada, indice + 1, valor, removido);

        if (filhoFicouVazio){
            no.filhos.remover(c);
        }

        //ve se este no tambem ficou vazio
        return !no.fimDePalavra && no.filhos.getTamanho() == 0;
    }

}