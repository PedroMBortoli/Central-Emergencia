package estruturas;

import modelo.Ocorrencia;

public class TabelaHash {
    private static final int CAPACIDADE_INICIAL = 16;
    private No[] tabela;
    private int tamanho;

    //NÓ para a lista encadeada da tabela
    private static class No {
        String chave;
        Ocorrencia valor;
        No prox;

        //construtor
        public No(String chave, Ocorrencia valor)
        {
            this.chave = chave;
            this.valor = valor;
            this.prox = null;
        }
    }

    //construtor
    public TabelaHash() {
        this.tamanho = 0;
        this.tabela = new No[CAPACIDADE_INICIAL];
    }




}
