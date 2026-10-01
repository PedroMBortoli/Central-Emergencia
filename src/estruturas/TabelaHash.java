package estruturas;

import modelo.Ocorrencia;

public class TabelaHash {
    private static final int CAPACIDADE_INICIAL = 16;
    private static final double FATOR_CARGA_MAX = 0.75;
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

    public Ocorrencia[] listarTodos() {
        Ocorrencia[] resultado = new Ocorrencia[tamanho];
        int pos = 0;

        for (int i = 0; i < tabela.length; i++) {
            No aux = tabela[i];
            while (aux != null) {
                resultado[pos] = aux.valor;
                pos++;
                aux = aux.prox;
            }
        }
        return resultado;
    }

    //redimensionar
    private void redimensionar()
    {
        No[] antigaTabela = tabela;

        tabela = new No[antigaTabela.length * 2];

        tamanho = 0;

        for(int i = 0; i < antigaTabela.length; i++){
            No aux = antigaTabela[i];
            while (aux != null) {
                inserir(aux.chave, aux.valor);
                aux = aux.prox;
            }
        }
    }

    //remover
    public boolean remover(String chave) {
        int indice = hash(chave);
        No aux = tabela[indice];
        No anterior = null;

        while (aux != null) {
            if (aux.chave.equals(chave)) {
                if (anterior == null) {
                    tabela[indice] = aux.prox;
                } else {
                    anterior.prox = aux.prox;
                }
                tamanho--;
                return true;
            }
            anterior = aux;
            aux = aux.prox;
        }
        return false;
    }

    //inserção
    public void inserir(String chave, Ocorrencia valor){
        int indice = hash(chave);

        No aux = tabela[indice];

        while(aux != null)
        {
            if(aux.chave.equals(chave)){
                aux.valor = valor;
                return;
            }
            aux = aux.prox;
        }

        No novo = new No(chave, valor);
        novo.prox = tabela[indice];
        tabela[indice] = novo;

        this.tamanho++;

        if ((double) tamanho / tabela.length > FATOR_CARGA_MAX) {
            redimensionar();
        }
    }

    //busca
    public Ocorrencia buscar(String chave)
    {
        int indice = hash(chave);

        No aux = tabela[indice];

        while(aux != null)
        {
            if(aux.chave.equals(chave)){
                return aux.valor;
            }
            aux = aux.prox;
        }

        return null;
    }

    //funcao hash
    private int hash(String chave) {
        int h = 0;
        for(int i = 0; i < chave.length(); i++){
            h = 31 * h + chave.charAt(i);
        }
        return (h & 0x7fffffff) % tabela.length;
    }

    public int getTamanho()
    {
        return this.tamanho;
    }
}
