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

    // TODO: selecionarComLimiteDeTempo(List<Ocorrencia> pendentes, int limiteTempo) - algoritmo guloso
}
