package estruturas;

import java.util.*;

public class Trie {

    //Classe do nó
    private static class No {
        Map<Character, No> filhos = new HashMap<>();
        boolean fimDePalavra = false;
        List<Object> valores = new LinkedList<>();
    }

    private No raiz;

    public Trie(){
        raiz = new No();
    }

    // inserir, buscar e buscarPorPrefixo serão adicionados depois

    public void inserir(String chave, Object valor){
        String chaveNormalizada = chave.toUpperCase();
        No atual = raiz;

        for (int i = 0; i < chaveNormalizada.length(); i++){
            char c = chaveNormalizada.charAt(i);

            No proximo = atual.filhos.get(c);
            if (proximo == null){
                proximo = new No();
                atual.filhos.put(c, proximo);
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

        for(No filho : no.filhos.values()){
            coletarValores(filho, resultado);
        }

    }

    public List<Object> buscarPorPrefixo(String prefixo){
        String prefixoNormalizado = prefixo.toUpperCase();
        No atual = raiz;

        for(int i = 0; i < prefixoNormalizado.length(); i++){
            char c = prefixoNormalizado.charAt(i);
            No proximo = atual.filhos.get(c);

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

}
