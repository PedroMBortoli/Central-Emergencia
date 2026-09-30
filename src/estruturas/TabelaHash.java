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

}

