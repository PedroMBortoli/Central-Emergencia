package estruturas;

// Union-Find: diz se dois vertices ja estao conectados (usado no Kruskal para evitar ciclo).
public class UnionFind {

    private final int[] pai;
    private final int[] tamanho;

    public UnionFind(int quantidadeVertices) {
        pai = new int[quantidadeVertices];
        tamanho = new int[quantidadeVertices];
        for (int i = 0; i < quantidadeVertices; i++) {
            pai[i] = i;
            tamanho[i] = 1;
        }
    }

    // acha a raiz de x; no caminho, aponta todo mundo direto para a raiz
    public int find(int x) {
        if (pai[x] != x) {
            pai[x] = find(pai[x]);
        }
        return pai[x];
    }

    // junta os conjuntos de x e y (o menor fica embaixo do maior).
    // devolve false se ja estavam juntos (a aresta fecharia ciclo)
    public boolean union(int x, int y) {
        int raizX = find(x);
        int raizY = find(y);
        if (raizX == raizY) {
            return false;
        }
        if (tamanho[raizX] < tamanho[raizY]) {
            int troca = raizX;
            raizX = raizY;
            raizY = troca;
        }
        pai[raizY] = raizX;
        tamanho[raizX] += tamanho[raizY];
        return true;
    }
}
