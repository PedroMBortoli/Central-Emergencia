package estruturas;

import java.util.ArrayList;
import java.util.List;

// Grafo nao direcionado e ponderado (lista de adjacencia).
// Cada aresta entra na lista dos dois vertices.
public class GrafoPonderado {

    public static class Aresta {
        private final int origem;
        private final int destino;
        private final int custo;

        Aresta(int origem, int destino, int custo) {
            this.origem = origem;
            this.destino = destino;
            this.custo = custo;
        }

        public int getOrigem() {
            return origem;
        }

        public int getDestino() {
            return destino;
        }

        public int getCusto() {
            return custo;
        }
    }

    private final List<String> nomes = new ArrayList<>();
    private final List<List<Aresta>> adjacencia = new ArrayList<>();
    private final TabelaHash<String, Integer> indices = new TabelaHash<>(16);
    private int quantidadeArestas;

    public int adicionarVertice(String nome) {
        Integer existente = indices.buscar(nome);
        if (existente != null) {
            return existente;
        }
        int novo = nomes.size();
        nomes.add(nome);
        adjacencia.add(new ArrayList<>());
        indices.inserir(nome, novo);
        return novo;
    }

    // recusa laco, custo negativo e aresta repetida
    public boolean adicionarAresta(String a, String b, int custo) {
        int va = adicionarVertice(a);
        int vb = adicionarVertice(b);
        if (va == vb || custo < 0 || existeAresta(va, vb)) {
            return false;
        }
        adjacencia.get(va).add(new Aresta(va, vb, custo));
        adjacencia.get(vb).add(new Aresta(vb, va, custo));
        quantidadeArestas++;
        return true;
    }

    private boolean existeAresta(int va, int vb) {
        for (Aresta aresta : adjacencia.get(va)) {
            if (aresta.getDestino() == vb) {
                return true;
            }
        }
        return false;
    }

    public boolean contemVertice(String nome) {
        return indices.buscar(nome) != null;
    }

    public Integer indiceDoVertice(String nome) {
        return indices.buscar(nome);
    }

    public String nomeDoVertice(int indice) {
        return nomes.get(indice);
    }

    public int getQuantidadeVertices() {
        return nomes.size();
    }

    public int getQuantidadeArestas() {
        return quantidadeArestas;
    }

    public List<String> getVertices() {
        return new ArrayList<>(nomes);
    }

    // lista vazia se o vertice nao existir ou for isolado
    public List<Aresta> getVizinhos(String nome) {
        Integer v = indices.buscar(nome);
        if (v == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(adjacencia.get(v));
    }

    // cada aresta uma vez so (origem < destino), usado pelo Kruskal
    public List<Aresta> getTodasArestas() {
        List<Aresta> resultado = new ArrayList<>();
        for (List<Aresta> lista : adjacencia) {
            for (Aresta aresta : lista) {
                if (aresta.getOrigem() < aresta.getDestino()) {
                    resultado.add(aresta);
                }
            }
        }
        return resultado;
    }
}
