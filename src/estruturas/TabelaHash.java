package estruturas;

import java.util.LinkedList;

public class TabelaHash <K, V> {

    private static class Par<K, V> {

        K chave;
        V valor;

        Par(K chave, V valor){
            this.chave = chave;
            this.valor = valor;
        }
    }

    private LinkedList<Par<K,V>>[] buckets;
    private int capacidade;

    public TabelaHash(int capacidadeInicial){
        this.capacidade = capacidadeInicial;
        this.buckets = new LinkedList[capacidade];

    }

    private int hashCode (K chave){
        String s = chave.toString();
        int hash = 0;
            for (int i = 0; i < s.length(); i++){

                //31 pois é primo e gerando resutltados bastante aleatório mesmo para palavras parecidas
                //chatAt(i) retorna o caracter da String que ocupa a posição i;
                hash = 31 * hash + s.charAt(i);
            }

        return hash;
    }

    private int indice (K chave){
        int hash = hashCode(chave);

        //Math.abs(hash) retorna o valor em modulo de hash - nunca tera codigo negativo
        return Math.abs(hash) % capacidade;
    }

}

