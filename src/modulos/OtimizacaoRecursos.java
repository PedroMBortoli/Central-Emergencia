package modulos;

import modelo.Ocorrencia;

import java.util.ArrayList;
import java.util.List;

// Modulo 7: escolhe quais ocorrencias atender sem passar do tempo disponivel.
// E o problema da mochila 0/1, resolvido com programacao dinamica:
//   capacidade = tempo total disponivel (minutos)
//   custo      = tempo estimado da ocorrencia
//   beneficio  = prioridade ou pessoas envolvidas (o usuario escolhe)
// cada ocorrencia ou entra inteira ou fica de fora, nao da pra atender "meia" ocorrencia
public class OtimizacaoRecursos {

    public static final int CRITERIO_PRIORIDADE = 1;
    public static final int CRITERIO_PESSOAS = 2;

    // limite pra tabela nao ficar grande demais
    // (5000 ocorrencias x 1440 minutos ja sao uns 7 milhoes de posicoes)
    public static final int CAPACIDADE_MAXIMA = 1440;

    public Resultado selecionar(List<Ocorrencia> candidatas, int capacidade, int criterio) {
        int n = candidatas.size();

        // dp[i][t] = maior beneficio possivel usando so as i primeiras ocorrencias com t minutos
        // linha 0 e coluna 0 ficam com zero (sem ocorrencia ou sem tempo nao da pra ganhar nada)
        int[][] dp = new int[n + 1][capacidade + 1];

        for (int i = 1; i <= n; i++) {
            Ocorrencia o = candidatas.get(i - 1);
            int tempo = o.getTempoEstimado();
            int ganho = beneficio(o, criterio);

            for (int t = 0; t <= capacidade; t++) {
                // decisao 1: nao atender a ocorrencia i
                dp[i][t] = dp[i - 1][t];

                // decisao 2: atender a ocorrencia i, se ela cabe nos t minutos
                if (tempo <= t && dp[i - 1][t - tempo] + ganho > dp[i][t]) {
                    dp[i][t] = dp[i - 1][t - tempo] + ganho;
                }
            }
        }

        // volta pela tabela de baixo pra cima pra descobrir quem entrou:
        // se o valor mudou da linha i-1 pra linha i, foi porque a ocorrencia i foi escolhida
        boolean[] escolhida = new boolean[n];
        int t = capacidade;
        for (int i = n; i >= 1; i--) {
            if (dp[i][t] != dp[i - 1][t]) {
                escolhida[i - 1] = true;
                t -= candidatas.get(i - 1).getTempoEstimado();
            }
        }

        List<Ocorrencia> selecionadas = new ArrayList<>();
        List<Ocorrencia> deFora = new ArrayList<>();
        int tempoUsado = 0;
        for (int i = 0; i < n; i++) {
            if (escolhida[i]) {
                selecionadas.add(candidatas.get(i));
                tempoUsado += candidatas.get(i).getTempoEstimado();
            } else {
                deFora.add(candidatas.get(i));
            }
        }

        return new Resultado(selecionadas, deFora, dp[n][capacidade], tempoUsado, capacidade, criterio);
    }

    public static int beneficio(Ocorrencia o, int criterio) {
        if (criterio == CRITERIO_PESSOAS) {
            return o.getPessoasEnvolvidas();
        }
        return o.getPrioridade();
    }

    public static String nomeCriterio(int criterio) {
        return criterio == CRITERIO_PESSOAS ? "pessoas envolvidas" : "prioridade";
    }

    // soma o beneficio de uma lista qualquer (usado pra comparar com o guloso do modulo 5)
    public static int beneficioTotal(List<Ocorrencia> lista, int criterio) {
        int total = 0;
        for (Ocorrencia o : lista) {
            total += beneficio(o, criterio);
        }
        return total;
    }

    // guarda tudo que a selecao devolve
    public static class Resultado {
        private final List<Ocorrencia> selecionadas;
        private final List<Ocorrencia> deFora;
        private final int beneficioTotal;
        private final int tempoUsado;
        private final int capacidade;
        private final int criterio;

        public Resultado(List<Ocorrencia> selecionadas, List<Ocorrencia> deFora, int beneficioTotal,
                         int tempoUsado, int capacidade, int criterio) {
            this.selecionadas = selecionadas;
            this.deFora = deFora;
            this.beneficioTotal = beneficioTotal;
            this.tempoUsado = tempoUsado;
            this.capacidade = capacidade;
            this.criterio = criterio;
        }

        public List<Ocorrencia> getSelecionadas() {
            return selecionadas;
        }

        public List<Ocorrencia> getDeFora() {
            return deFora;
        }

        public int getBeneficioTotal() {
            return beneficioTotal;
        }

        public int getTempoUsado() {
            return tempoUsado;
        }

        public int getCapacidade() {
            return capacidade;
        }

        public int getCriterio() {
            return criterio;
        }
    }
}
