package estruturas;

import java.util.ArrayList;
import java.util.List;

// Min-heap: sempre devolve o item de menor distancia (usado no Dijkstra).
// Um vertice pode entrar mais de uma vez; o Dijkstra ignora as entradas antigas.
public class HeapMinimo {

    public static class Item {
        private final int vertice;
        private final int distancia;

        Item(int vertice, int distancia) {
            this.vertice = vertice;
            this.distancia = distancia;
        }

        public int getVertice() {
            return vertice;
        }

        public int getDistancia() {
            return distancia;
        }
    }

    private final List<Item> itens = new ArrayList<>();

    public boolean estaVazio() {
        return itens.isEmpty();
    }

    public int getTamanho() {
        return itens.size();
    }

    public void inserir(int vertice, int distancia) {
        itens.add(new Item(vertice, distancia));
        subir(itens.size() - 1);
    }

    // tira a raiz (menor distancia)
    public Item remover() {
        Item raiz = itens.get(0);
        Item ultimo = itens.remove(itens.size() - 1);
        if (!itens.isEmpty()) {
            itens.set(0, ultimo);
            descer(0);
        }
        return raiz;
    }

    // sobe enquanto for menor que o pai
    private void subir(int i) {
        while (i > 0) {
            int pai = (i - 1) / 2;
            if (itens.get(i).distancia >= itens.get(pai).distancia) {
                break;
            }
            trocar(i, pai);
            i = pai;
        }
    }

    // desce trocando com o menor filho
    private void descer(int i) {
        int n = itens.size();
        while (true) {
            int esquerda = 2 * i + 1;
            int direita = 2 * i + 2;
            int menor = i;
            if (esquerda < n && itens.get(esquerda).distancia < itens.get(menor).distancia) {
                menor = esquerda;
            }
            if (direita < n && itens.get(direita).distancia < itens.get(menor).distancia) {
                menor = direita;
            }
            if (menor == i) {
                break;
            }
            trocar(i, menor);
            i = menor;
        }
    }

    private void trocar(int i, int j) {
        Item tmp = itens.get(i);
        itens.set(i, itens.get(j));
        itens.set(j, tmp);
    }
}
