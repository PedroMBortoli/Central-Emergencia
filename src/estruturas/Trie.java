package estruturas;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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

}
