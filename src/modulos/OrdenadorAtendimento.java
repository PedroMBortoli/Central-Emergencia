package modulos;

import modelo.Ocorrencia;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class OrdenadorAtendimento {

    // estabelece a ordem de atendimento combinando criterios: prioridade mais alta primeiro,
    // pessoas envolvidas como 1o desempate, tempo estimado (menor primeiro) como 2o desempate.
    // nao modifica a lista recebida, devolve uma copia ordenada.
    public List<Ocorrencia> ordenarPorPrioridade(List<Ocorrencia> ocorrencias) {
        List<Ocorrencia> ordenada = new ArrayList<>(ocorrencias);

        ordenada.sort(
            Comparator.comparingInt(Ocorrencia::getPrioridade).reversed()
                .thenComparing(Comparator.comparingInt(Ocorrencia::getPessoasEnvolvidas).reversed())
                .thenComparingInt(Ocorrencia::getTempoEstimado)
        );

        return ordenada;
    }

    // ALGORITMO GULOSO: seleciona quais ocorrencias atender dado um limite de tempo
    // (ex: tempo total disponivel das equipes). A cada passo, escolhe a ocorrencia de
    // maior "densidade de impacto" (prioridade x pessoas envolvidas, por unidade de tempo
    // que ela consome) dentre as que ainda cabem no tempo restante - e nunca reconsidera
    // essa escolha depois (caracteristica central de um algoritmo guloso).
    public List<Ocorrencia> selecionarComLimiteDeTempo(List<Ocorrencia> pendentes, int limiteTempo) {
        List<Ocorrencia> candidatas = new ArrayList<>(pendentes);

        // ordena por densidade de impacto decrescente (maior prioridade x pessoas, menor tempo)
        candidatas.sort(
            Comparator.comparingDouble(this::calcularDensidade).reversed()
        );

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

    // impacto (prioridade x pessoas envolvidas) por unidade de tempo estimado;
    // usa Math.max(1, tempo) so para esse calculo, pra nao dividir por zero
    // caso uma ocorrencia tenha tempo estimado 0 (nao afeta o tempo somado na selecao)
    private double calcularDensidade(Ocorrencia o) {
        double impacto = o.getPrioridade() * o.getPessoasEnvolvidas();
        return impacto / Math.max(1, o.getTempoEstimado());
    }
}
