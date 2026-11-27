package modulos;

import modelo.Ocorrencia;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;

public class OrdenadorAtendimento {

    // ordem de atendimento: prioridade (maior primeiro), depois pessoas, depois tempo (menor primeiro).
    // devolve uma copia, nao mexe na lista original
    public List<Ocorrencia> ordenarPorPrioridade(List<Ocorrencia> ocorrencias) {
        List<Ocorrencia> ordenada = new ArrayList<>(ocorrencias);
        mergeSort(ordenada, this::compararPorPrioridade);
        return ordenada;
    }

    // criterio de ordenacao do ordenarPorPrioridade: prioridade desc, pessoas desc, tempo asc
    private int compararPorPrioridade(Ocorrencia a, Ocorrencia b) {
        if (a.getPrioridade() != b.getPrioridade()) {
            return b.getPrioridade() - a.getPrioridade();
        }
        if (a.getPessoasEnvolvidas() != b.getPessoasEnvolvidas()) {
            return b.getPessoasEnvolvidas() - a.getPessoasEnvolvidas();
        }
        return a.getTempoEstimado() - b.getTempoEstimado();
    }

    // outro criterio: quem envolve mais pessoas primeiro; desempata por prioridade e depois tempo
    public List<Ocorrencia> ordenarPorPessoas(List<Ocorrencia> ocorrencias) {
        List<Ocorrencia> ordenada = new ArrayList<>(ocorrencias);
        mergeSort(ordenada, this::compararPorPessoas);
        return ordenada;
    }

    private int compararPorPessoas(Ocorrencia a, Ocorrencia b) {
        if (a.getPessoasEnvolvidas() != b.getPessoasEnvolvidas()) {
            return b.getPessoasEnvolvidas() - a.getPessoasEnvolvidas();
        }
        if (a.getPrioridade() != b.getPrioridade()) {
            return b.getPrioridade() - a.getPrioridade();
        }
        return a.getTempoEstimado() - b.getTempoEstimado();
    }

    // mais rapidos primeiro (libera as equipes antes); desempata por prioridade e pessoas
    public List<Ocorrencia> ordenarPorTempo(List<Ocorrencia> ocorrencias) {
        List<Ocorrencia> ordenada = new ArrayList<>(ocorrencias);
        mergeSort(ordenada, this::compararPorTempo);
        return ordenada;
    }

    private int compararPorTempo(Ocorrencia a, Ocorrencia b) {
        if (a.getTempoEstimado() != b.getTempoEstimado()) {
            return a.getTempoEstimado() - b.getTempoEstimado();
        }
        if (a.getPrioridade() != b.getPrioridade()) {
            return b.getPrioridade() - a.getPrioridade();
        }
        return b.getPessoasEnvolvidas() - a.getPessoasEnvolvidas();
    }

    // GULOSO: ordena por densidade (prioridade x pessoas / tempo) e pega cada ocorrencia
    // que ainda cabe no tempo, sem voltar atras
    public List<Ocorrencia> selecionarComLimiteDeTempo(List<Ocorrencia> pendentes, int limiteTempo) {
        List<Ocorrencia> candidatas = new ArrayList<>(pendentes);

        // maior densidade primeiro
        mergeSort(candidatas, this::compararPorDensidade);

        List<Ocorrencia> selecionadas = new ArrayList<>();
        int tempoUsado = 0;

        for (Ocorrencia o : candidatas) {
            int tempo = o.getTempoEstimado();

            // se cabe, pega e nao revisita a decisao
            if (tempoUsado + tempo <= limiteTempo) {
                selecionadas.add(o);
                tempoUsado += tempo;
            }
        }

        return selecionadas;
    }

    // criterio de ordenacao do selecionarComLimiteDeTempo: densidade desc
    private int compararPorDensidade(Ocorrencia a, Ocorrencia b) {
        double diferenca = calcularDensidade(b) - calcularDensidade(a);
        if (diferenca > 0) return 1;
        if (diferenca < 0) return -1;
        return 0;
    }

    // (prioridade x pessoas) / tempo; Math.max evita divisao por zero
    private double calcularDensidade(Ocorrencia o) {
        double impacto = o.getPrioridade() * o.getPessoasEnvolvidas();
        return impacto / Math.max(1, o.getTempoEstimado());
    }

    // MERGE SORT: O(n log n) e estavel. Divide ao meio, ordena cada metade e intercala.
    // generico para ser reaproveitado em outros modulos
    static <T> void mergeSort(List<T> lista, Comparator<T> comparador) {
        if (lista.size() <= 1) {
            return; // lista de 0 ou 1 elemento ja esta ordenada
        }

        int meio = lista.size() / 2;
        List<T> esquerda = new ArrayList<>(lista.subList(0, meio));
        List<T> direita = new ArrayList<>(lista.subList(meio, lista.size()));

        mergeSort(esquerda, comparador);
        mergeSort(direita, comparador);

        intercalar(lista, esquerda, direita, comparador);
    }

    // intercala as duas metades; no empate pega a da esquerda (deixa o sort estavel)
    private static <T> void intercalar(List<T> destino, List<T> esquerda, List<T> direita, Comparator<T> comparador) {
        int i = 0, j = 0, k = 0;

        while (i < esquerda.size() && j < direita.size()) {
            if (comparador.compare(esquerda.get(i), direita.get(j)) <= 0) {
                destino.set(k++, esquerda.get(i++));
            } else {
                destino.set(k++, direita.get(j++));
            }
        }

        while (i < esquerda.size()) {
            destino.set(k++, esquerda.get(i++));
        }

        while (j < direita.size()) {
            destino.set(k++, direita.get(j++));
        }
    }
}
