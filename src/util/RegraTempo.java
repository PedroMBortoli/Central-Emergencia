package util;

// Define o tempo estimado de atendimento (em minutos) de uma ocorrência:
//   tempoEstimado = base do tipo + (prioridade - 1) * 10
// A base reflete a natureza do atendimento e o acréscimo reflete que
// ocorrências mais graves mantêm a equipe mais tempo no local.
public class RegraTempo {
    private static final int BASE_EMS = 30;      //atendimento e transporte de um paciente
    private static final int BASE_TRAFFIC = 40;  //sinalização da via e remoção de veículos
    private static final int BASE_FIRE = 60;     //combate e rescaldo
    private static final int ACRESCIMO_POR_PRIORIDADE = 10;

    public static int calcular(String tipo, int prioridade) {
        return base(tipo) + (prioridade - 1) * ACRESCIMO_POR_PRIORIDADE;
    }

    private static int base(String tipo) {
        if (tipo == null) {
            return BASE_EMS;
        }

        switch (tipo.trim().toUpperCase()) {
            case "TRAFFIC":
                return BASE_TRAFFIC;
            case "FIRE":
                return BASE_FIRE;
            default: //EMS ou tipo desconhecido
                return BASE_EMS;
        }
    }
}
