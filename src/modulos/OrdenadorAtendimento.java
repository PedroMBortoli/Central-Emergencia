package modulos;

import modelo.Ocorrencia;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;

public class OrdenadorAtendimento {

    // estabelece a ordem de atendimento combinando criterios: prioridade mais alta primeiro,
    // pessoas envolvidas como 1o desempate, tempo estimado (menor primeiro) como 2o desempate.
    // nao modifica a lista recebida, devolve uma copia ordenada.
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

    // outro criterio: atendimentos mais rapidos primeiro, pra liberar as equipes antes;
    // desempata por prioridade e depois pessoas
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

    // ALGORITMO GULOSO: seleciona quais ocorrencias atender dado um limite de tempo
    // (ex: tempo total disponivel das equipes). A cada passo, escolhe a ocorrencia de
    // maior "densidade de impacto" (prioridade x pessoas envolvidas, por unidade de tempo
    // que ela consome) dentre as que ainda cabem no tempo restante - e nunca reconsidera
    // essa escolha depois (caracteristica central de um algoritmo guloso).
    public List<Ocorrencia> selecionarComLimiteDeTempo(List<Ocorrencia> pendentes, int limiteTempo) {
        List<Ocorrencia> candidatas = new ArrayList<>(pendentes);

        // ordena por densidade de impacto decrescente (maior prioridade x pessoas, menor tempo)
        mergeSort(candidatas, this::compararPorDensidade);

        List<Ocorrencia> selecionadas = new ArrayList<>();
        int tempoUsado = 0;

        for (Ocorrencia o : candidatas) {
            int tempo = o.getTempoEstimado();

            // escolha gulosa: se essa ocorrencia cabe no tempo restante, pega ela agora
            // e nunca mais revisita essa decisao; se nao cabe, pula (nao tenta trocar
            // depois por uma combinacao melhor - isso seria busca exaustiva, nao guloso)
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

    // impacto (prioridade x pessoas envolvidas) por unidade de tempo estimado;
    // usa Math.max(1, tempo) so para esse calculo, pra nao dividir por zero
    // caso uma ocorrencia tenha tempo estimado 0 (nao afeta o tempo somado na selecao)
    private double calcularDensidade(Ocorrencia o) {
        double impacto = o.getPrioridade() * o.getPessoasEnvolvidas();
        return impacto / Math.max(1, o.getTempoEstimado());
    }

    // MERGE SORT implementado do zero (sem usar List.sort/Collections.sort/Arrays.sort):
    // O(n log n) e estavel - divide a lista ao meio recursivamente ate sobrar 1 elemento,
    // ordena cada metade e intercala as duas metades ja ordenadas.
    // 'comparador' define apenas o CRITERIO de comparacao (qual veio primeiro);
    // o algoritmo de ordenacao em si (a divisao e a intercalacao) e todo escrito aqui.
    private void mergeSort(List<Ocorrencia> lista, Comparator<Ocorrencia> comparador) {
        if (lista.size() <= 1) {
            return; // lista de 0 ou 1 elemento ja esta ordenada
        }

        int meio = lista.size() / 2;
        List<Ocorrencia> esquerda = new ArrayList<>(lista.subList(0, meio));
        List<Ocorrencia> direita = new ArrayList<>(lista.subList(meio, lista.size()));

        mergeSort(esquerda, comparador);
        mergeSort(direita, comparador);

        intercalar(lista, esquerda, direita, comparador);
    }

    // junta as duas metades (ja ordenadas) de volta em 'destino', mantendo a ordem;
    // em caso de empate, prioriza o elemento da esquerda primeiro - e isso que garante
    // a estabilidade (quem entrou primeiro na lista original sai primeiro em empate)
    private void intercalar(List<Ocorrencia> destino, List<Ocorrencia> esquerda, List<Ocorrencia> direita, Comparator<Ocorrencia> comparador) {
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
