package estruturas;

import java.util.ArrayList;
import java.util.List;

public class GrafoDirecionado {

    private final List<String> nomes = new ArrayList<>();
    private final List<List<Integer>> adjacencia = new ArrayList<>();
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

    // aresta antes -> depois: "antes" precisa ser concluida para "depois" poder comecar
    public boolean adicionarAresta(String antes, String depois) {
        int origem = adicionarVertice(antes);
        int destino = adicionarVertice(depois);
        if (adjacencia.get(origem).contains(destino)) {
            return false;
        }
        adjacencia.get(origem).add(destino);
        quantidadeArestas++;
        return true;
    }

    public boolean removerAresta(String antes, String depois) {
        Integer origem = indices.buscar(antes);
        Integer destino = indices.buscar(depois);
        if (origem == null || destino == null) {
            return false;
        }
        boolean removeu = adjacencia.get(origem).remove(destino);
        if (removeu) {
            quantidadeArestas--;
        }
        return removeu;
    }

    public boolean contemVertice(String nome) {
        return indices.buscar(nome) != null;
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

    public List<String> getSucessores(String nome) {
        List<String> resultado = new ArrayList<>();
        Integer v = indices.buscar(nome);
        if (v == null) {
            return resultado;
        }
        for (int w : adjacencia.get(v)) {
            resultado.add(nomes.get(w));
        }
        return resultado;
    }

    // Kahn: O(V + E). Se sobrar vertice sem entrar na ordem, existe ciclo.
    public ResultadoOrdenacao ordenacaoTopologica() {
        int n = nomes.size();
        int[] grauEntrada = new int[n];
        for (List<Integer> vizinhos : adjacencia) {
            for (int w : vizinhos) {
                grauEntrada[w]++;
            }
        }

        int[] fila = new int[n];
        int inicio = 0;
        int fim = 0;
        for (int v = 0; v < n; v++) {
            if (grauEntrada[v] == 0) {
                fila[fim++] = v;
            }
        }

        List<String> ordem = new ArrayList<>();
        while (inicio < fim) {
            int v = fila[inicio++];
            ordem.add(nomes.get(v));
            for (int w : adjacencia.get(v)) {
                grauEntrada[w]--;
                if (grauEntrada[w] == 0) {
                    fila[fim++] = w;
                }
            }
        }

        if (ordem.size() == n) {
            return new ResultadoOrdenacao(ordem, new ArrayList<>(), new ArrayList<>());
        }

        List<String> bloqueadas = new ArrayList<>();
        boolean[] restante = new boolean[n];
        for (int v = 0; v < n; v++) {
            if (grauEntrada[v] > 0) {
                restante[v] = true;
                bloqueadas.add(nomes.get(v));
            }
        }
        return new ResultadoOrdenacao(ordem, bloqueadas, encontrarCiclo(restante));
    }

    // DFS com 3 cores so nos vertices que o Kahn nao conseguiu liberar
    private List<String> encontrarCiclo(boolean[] restante) {
        int n = nomes.size();
        int[] cor = new int[n];
        int[] pai = new int[n];

        for (int v = 0; v < n; v++) {
            if (restante[v] && cor[v] == 0) {
                List<String> ciclo = dfsCiclo(v, cor, pai, restante);
                if (ciclo != null) {
                    return ciclo;
                }
            }
        }
        return new ArrayList<>();
    }

    private List<String> dfsCiclo(int v, int[] cor, int[] pai, boolean[] restante) {
        cor[v] = 1;
        for (int w : adjacencia.get(v)) {
            if (!restante[w]) {
                continue;
            }
            if (cor[w] == 1) {
                List<String> ciclo = new ArrayList<>();
                ciclo.add(nomes.get(w));
                int atual = v;
                List<String> caminho = new ArrayList<>();
                while (atual != w) {
                    caminho.add(0, nomes.get(atual));
                    atual = pai[atual];
                }
                ciclo.addAll(caminho);
                ciclo.add(nomes.get(w));
                return ciclo;
            }
            if (cor[w] == 0) {
                pai[w] = v;
                List<String> ciclo = dfsCiclo(w, cor, pai, restante);
                if (ciclo != null) {
                    return ciclo;
                }
            }
        }
        cor[v] = 2;
        return null;
    }

    public static class ResultadoOrdenacao {
        private final List<String> ordem;
        private final List<String> bloqueadas;
        private final List<String> ciclo;

        ResultadoOrdenacao(List<String> ordem, List<String> bloqueadas, List<String> ciclo) {
            this.ordem = ordem;
            this.bloqueadas = bloqueadas;
            this.ciclo = ciclo;
        }

        public boolean temCiclo() {
            return !bloqueadas.isEmpty();
        }

        public List<String> getOrdem() {
            return ordem;
        }

        public List<String> getBloqueadas() {
            return bloqueadas;
        }

        public List<String> getCiclo() {
            return ciclo;
        }
    }
}
