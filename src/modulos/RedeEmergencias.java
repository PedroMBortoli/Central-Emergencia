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

// Modulo 8: rede de bases, hospitais, postos de apoio e regioes, usada pra
// planejar deslocamento de equipes (Modo Logistica). O grafo e nao direcionado
// e ponderado (custo = tempo/distancia entre dois pontos); duas tecnicas sobre
// o mesmo grafo: caminho minimo (Dijkstra) e arvore geradora minima (Kruskal).
public class RedeEmergencias {

    private final GrafoPonderado grafo = new GrafoPonderado();

    // formato do CSV: origem,destino,custo
    // uma linha com so a origem preenchida (destino e custo em branco) cadastra
    // um ponto isolado, sem nenhuma conexao - usado pra demonstrar o caso
    // "nao existe caminho" e a rede desconexa (floresta) na arvore geradora.
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

    // DIJKSTRA: caminho minimo entre dois pontos, com heap minimo implementado
    // na mao (HeapMinimo, delecao preguicosa). Guarda anterior[] pra reconstruir
    // o caminho a partir do destino. Custo O((V + E) log E); como E <= V^2,
    // log E <= 2 log V, entao isso e O((V + E) log V).
    public ResultadoCaminho caminhoMinimo(String origemNome, String destinoNome) {
        Integer origem = grafo.indiceDoVertice(origemNome);
        Integer destino = grafo.indiceDoVertice(destinoNome);
        if (origem == null || destino == null) {
            return null; // algum dos pontos nao existe na rede
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
                continue; // entrada desatualizada: ja processamos v com distancia menor
            }
            visitado[v] = true;

            if (v == destino) {
                break; // destino alcancado com a menor distancia possivel, pode parar
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
            return new ResultadoCaminho(new ArrayList<>(), -1); // destino inalcancavel
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

    // KRUSKAL: arvore geradora minima (ou floresta, se a rede for desconexa),
    // com Union-Find implementado na mao pra detectar ciclo. Ordena as arestas
    // pelo merge sort generico do modulo 5 (OrdenadorAtendimento) e vai pegando
    // a mais barata que ainda nao fecha ciclo. Custo O(E log E) pra ordenar, mais
    // O(E) quase-constante no union-find (compressao de caminho + uniao por tamanho).
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

        // conta quantos componentes conexos sobraram (raizes distintas no union-find).
        // 1 = arvore de verdade liga todo mundo; mais de 1 = floresta (rede desconexa)
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
