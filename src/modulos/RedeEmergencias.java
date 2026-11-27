package modulos;

import estruturas.GrafoPonderado;
import estruturas.HeapMinimo;
import estruturas.UnionFind;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// Modulo 8: rede de bases, hospitais e regioes (grafo nao direcionado e ponderado).
// Caminho minimo com Dijkstra e arvore geradora minima com Kruskal.
public class RedeEmergencias {

    private final GrafoPonderado grafo = new GrafoPonderado();

    // CSV: origem,destino,custo
    // linha so com a origem cadastra um ponto isolado
    public int carregarRede(String caminho) {
        int carregadas = 0;
        try (BufferedReader leitor = new BufferedReader(new FileReader(caminho))) {
            leitor.readLine(); // pula cabecalho
            String linha;
            while ((linha = leitor.readLine()) != null) {
                if (linha.isBlank()) {
                    continue;
                }
                String[] campos = linha.split(",", -1);
                String origem = campos[0].trim();
                if (origem.isEmpty()) {
                    continue;
                }
                if (campos.length < 3 || campos[1].trim().isEmpty()) {
                    grafo.adicionarVertice(origem);
                    continue;
                }
                String destino = campos[1].trim();
                try {
                    int custo = Integer.parseInt(campos[2].trim());
                    if (grafo.adicionarAresta(origem, destino, custo)) {
                        carregadas++;
                    }
                } catch (NumberFormatException e) {
                    // custo invalido: linha ignorada
                }
            }
        } catch (IOException e) {
            System.out.println("Erro ao ler o arquivo da rede: " + e.getMessage());
        }
        return carregadas;
    }

    public int getQuantidadeVertices() {
        return grafo.getQuantidadeVertices();
    }

    public int getQuantidadeArestas() {
        return grafo.getQuantidadeArestas();
    }

    public boolean contemPonto(String nome) {
        return grafo.contemVertice(nome);
    }

    public List<String> getPontos() {
        return grafo.getVertices();
    }

    public List<GrafoPonderado.Aresta> getConexoes(String nome) {
        return grafo.getVizinhos(nome);
    }

    public String nomeDoPonto(int indice) {
        return grafo.nomeDoVertice(indice);
    }

    // DIJKSTRA com heap minimo; anterior[] monta o caminho. O((V + E) log V)
    public ResultadoCaminho caminhoMinimo(String origemNome, String destinoNome) {
        Integer origem = grafo.indiceDoVertice(origemNome);
        Integer destino = grafo.indiceDoVertice(destinoNome);
        if (origem == null || destino == null) {
            return null; // ponto nao existe
        }

        int n = grafo.getQuantidadeVertices();
        int[] distancia = new int[n];
        int[] anterior = new int[n];
        boolean[] visitado = new boolean[n];
        Arrays.fill(distancia, Integer.MAX_VALUE);
        Arrays.fill(anterior, -1);
        distancia[origem] = 0;

        HeapMinimo heap = new HeapMinimo();
        heap.inserir(origem, 0);

        while (!heap.estaVazio()) {
            HeapMinimo.Item atual = heap.remover();
            int v = atual.getVertice();

            if (visitado[v]) {
                continue; // entrada antiga, v ja foi processado
            }
            visitado[v] = true;

            if (v == destino) {
                break; // chegou no destino
            }

            for (GrafoPonderado.Aresta aresta : grafo.getVizinhos(grafo.nomeDoVertice(v))) {
                int w = aresta.getDestino();
                if (visitado[w]) {
                    continue;
                }
                int novaDistancia = distancia[v] + aresta.getCusto();
                if (novaDistancia < distancia[w]) {
                    distancia[w] = novaDistancia;
                    anterior[w] = v;
                    heap.inserir(w, novaDistancia);
                }
            }
        }

        if (distancia[destino] == Integer.MAX_VALUE) {
            return new ResultadoCaminho(new ArrayList<>(), -1); // sem caminho
        }

        List<String> caminho = new ArrayList<>();
        int atual = destino;
        while (atual != -1) {
            caminho.add(0, grafo.nomeDoVertice(atual));
            atual = anterior[atual];
        }
        return new ResultadoCaminho(caminho, distancia[destino]);
    }

    public static class ResultadoCaminho {
        private final List<String> caminho;
        private final int custoTotal;

        ResultadoCaminho(List<String> caminho, int custoTotal) {
            this.caminho = caminho;
            this.custoTotal = custoTotal;
        }

        public boolean existeCaminho() {
            return custoTotal >= 0;
        }

        public List<String> getCaminho() {
            return caminho;
        }

        public int getCustoTotal() {
            return custoTotal;
        }
    }

    // KRUSKAL: ordena as arestas (merge sort) e pega a mais barata que nao fecha ciclo (Union-Find).
    // se a rede for desconexa, vira floresta. O(E log E)
    public ResultadoAGM arvoreGeradoraMinima() {
        List<GrafoPonderado.Aresta> arestas = new ArrayList<>(grafo.getTodasArestas());
        OrdenadorAtendimento.mergeSort(arestas, (a, b) -> a.getCusto() - b.getCusto());

        int n = grafo.getQuantidadeVertices();
        UnionFind uniao = new UnionFind(n);
        List<GrafoPonderado.Aresta> escolhidas = new ArrayList<>();
        int custoTotal = 0;

        for (GrafoPonderado.Aresta aresta : arestas) {
            if (uniao.union(aresta.getOrigem(), aresta.getDestino())) {
                escolhidas.add(aresta);
                custoTotal += aresta.getCusto();
            }
        }

        // componentes = quantidade de raizes. 1 = arvore, mais de 1 = floresta
        int componentes = 0;
        for (int v = 0; v < n; v++) {
            if (uniao.find(v) == v) {
                componentes++;
            }
        }

        return new ResultadoAGM(escolhidas, custoTotal, componentes);
    }

    public static class ResultadoAGM {
        private final List<GrafoPonderado.Aresta> conexoes;
        private final int custoTotal;
        private final int componentes;

        ResultadoAGM(List<GrafoPonderado.Aresta> conexoes, int custoTotal, int componentes) {
            this.conexoes = conexoes;
            this.custoTotal = custoTotal;
            this.componentes = componentes;
        }

        public boolean ehArvoreUnica() {
            return componentes == 1;
        }

        public int getComponentes() {
            return componentes;
        }

        public List<GrafoPonderado.Aresta> getConexoes() {
            return conexoes;
        }

        public int getCustoTotal() {
            return custoTotal;
        }
    }
}
