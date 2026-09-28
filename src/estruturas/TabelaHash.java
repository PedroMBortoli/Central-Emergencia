package estruturas;

public class TabelaHash<K, V> {
    private static final int CAPACIDADE_INICIAL = 16;

    private No<K, V>[] tabela;
    private int tamanho;

    // função hash, inserir, buscar, remover serão adicionados depois

    private static class No<K, V> {
        K chave;
        V valor;
        No<K, V> proximo;
    }
}
