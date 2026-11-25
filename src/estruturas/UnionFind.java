package estruturas;

// Estrutura de conjuntos disjuntos (Union-Find / Disjoint Set) implementada do
// zero com vetor pai[], usada pelo Kruskal do modulo 8 pra decidir se uma
// aresta fecha ciclo: dois vertices fecham ciclo quando ja estao no mesmo
// conjunto, ou seja, ja existe algum caminho entre eles na arvore montada ate
// agora.
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

    // acha o representante (raiz) do conjunto de x. Compressao de caminho: todo
    // no visitado na subida passa a apontar direto pra raiz, achatando a arvore
    // pras proximas chamadas.
    public int find(int x) {
        if (pai[x] != x) {
            pai[x] = find(pai[x]);
        }
        return pai[x];
    }

    // uniao por tamanho: a raiz do conjunto menor passa a apontar pra raiz do
    // maior, mantendo a arvore rasa. Devolve false se x e y ja estavam no mesmo
    // conjunto (uni-los fecharia um ciclo) e nao faz nada nesse caso.
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
