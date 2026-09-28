package estruturas;

public class Trie {
    private No raiz;

    // inserir, buscar e buscarPorPrefixo serão adicionados depois

    private static class No {
        No[] filhos;
        boolean fimDePalavra;
    }
}
