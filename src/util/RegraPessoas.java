package util;

// Define a quantidade de pessoas envolvidas em uma ocorrência a partir do motivo
// da chamada. O dataset não possui essa informação, então o valor é derivado:
// o padrão é 1 (uma vítima), com exceções para motivos sem vítima (0) ou que
// envolvem mais de uma pessoa por natureza.
public class RegraPessoas {

    public static int calcular(String motivo) {
        String m = RegraPrioridade.normalizar(motivo);

        switch (m) {
            //0 - sem vítima: alarme, vistoria, apoio ou via obstruída
            case "FIRE ALARM", "FIRE INVESTIGATION", "ROAD OBSTRUCTION",
                 "HAZARDOUS ROAD CONDITIONS", "DEBRIS/FLUIDS ON HIGHWAY",
                 "VEHICLE LEAKING FUEL", "ELECTRICAL FIRE OUTSIDE", "WOODS/FIELD FIRE",
                 "TRASH/DUMPSTER FIRE", "UNKNOWN TYPE FIRE", "FIRE SPECIAL SERVICE",
                 "EMS SPECIAL SERVICE", "FIRE POLICE NEEDED", "PUMP DETAIL",
                 "TRANSFERRED CALL", "ANIMAL COMPLAINT", "S/B AT HELICOPTER LANDING":
                return 0;

            //2 - mais de um envolvido por natureza
            case "VEHICLE ACCIDENT", "MATERNITY", "SHOOTING", "STABBING",
                 "ASSAULT VICTIM", "INDUSTRIAL ACCIDENT":
                return 2;

            //3 - exposição de moradores
            case "GAS-ODOR/LEAK", "CARBON MONOXIDE DETECTOR":
                return 3;

            //4 - edificação ocupada
            case "BUILDING FIRE":
                return 4;

            //10 - acidente coletivo
            case "TRAIN CRASH":
                return 10;

            //padrão: um paciente ou um veículo
            default:
                return 1;
        }
    }
}
