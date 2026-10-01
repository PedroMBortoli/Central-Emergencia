package util;

// Define a prioridade (1 a 5) de uma ocorrência a partir do motivo da chamada,
// segundo o risco à vida e a urgência. Motivos não mapeados recebem um valor
// padrão de acordo com o tipo (EMS/Fire = 3, Traffic = 2).
public class RegraPrioridade {

    public static int calcular(String tipo, String motivo) {
        String m = normalizar(motivo);

        switch (m) {
            //5 - crítica: risco de vida imediato
            case "CARDIAC ARREST", "UNRESPONSIVE SUBJECT", "UNCONSCIOUS SUBJECT",
                 "CVA/STROKE", "CHOKING", "SHOOTING", "STABBING", "OVERDOSE",
                 "HEMORRHAGING", "AMPUTATION", "BUILDING FIRE", "TRAIN CRASH",
                 "RESCUE - WATER":
                return 5;

            //4 - alta: grave, pode piorar rápido ou oferece risco coletivo
            case "CARDIAC EMERGENCY", "RESPIRATORY EMERGENCY", "SEIZURES",
                 "ALTERED MENTAL STATUS", "ALLERGIC REACTION", "POISONING",
                 "BURN VICTIM", "MATERNITY", "HEAT EXHAUSTION", "INDUSTRIAL ACCIDENT",
                 "RESCUE - GENERAL", "RESCUE - TECHNICAL", "RESCUE - ELEVATOR",
                 "GAS-ODOR/LEAK", "CARBON MONOXIDE DETECTOR", "VEHICLE FIRE",
                 "WOODS/FIELD FIRE", "UNKNOWN TYPE FIRE", "VEHICLE LEAKING FUEL":
                return 4;

            //3 - média: lesão ou emergência sem risco imediato
            case "VEHICLE ACCIDENT", "HEAD INJURY", "FRACTURE", "FALL VICTIM",
                 "ASSAULT VICTIM", "DIABETIC EMERGENCY", "SYNCOPAL EPISODE",
                 "UNKNOWN MEDICAL EMERGENCY", "ABDOMINAL PAINS", "EYE INJURY",
                 "DEHYDRATION", "ELECTRICAL FIRE OUTSIDE", "APPLIANCE FIRE",
                 "ELEVATOR EMERGENCY":
                return 3;

            //2 - baixa: desconforto, alarme ou situação controlável
            case "SUBJECT IN PAIN", "NAUSEA/VOMITING", "FEVER", "DIZZINESS",
                 "GENERAL WEAKNESS", "LACERATIONS", "BACK PAINS/INJURY", "ANIMAL BITE",
                 "FIRE ALARM", "MEDICAL ALERT ALARM", "TRASH/DUMPSTER FIRE",
                 "HAZARDOUS ROAD CONDITIONS", "ROAD OBSTRUCTION",
                 "DEBRIS/FLUIDS ON HIGHWAY", "FIRE POLICE NEEDED":
                return 2;

            //1 - mínima: sem vítima ou apenas serviço de apoio
            case "DISABLED VEHICLE", "FIRE INVESTIGATION", "FIRE SPECIAL SERVICE",
                 "EMS SPECIAL SERVICE", "PUMP DETAIL", "TRANSFERRED CALL",
                 "ANIMAL COMPLAINT", "S/B AT HELICOPTER LANDING":
                return 1;

            //motivo não mapeado: padrão pelo tipo
            default:
                return padraoPorTipo(tipo);
        }
    }

    private static int padraoPorTipo(String tipo) {
        if (tipo == null) {
            return 3;
        }

        switch (tipo.trim().toUpperCase()) {
            case "TRAFFIC":
                return 2;
            default: //EMS, Fire ou tipo desconhecido
                return 3;
        }
    }

    // deixa o motivo no formato da tabela: sem espaços nas pontas, em maiúsculas
    // e sem o " -" que o dataset coloca no fim dos motivos de Traffic
    // (sem modificador de acesso para ser reaproveitado pelas outras regras do pacote util)
    static String normalizar(String motivo) {
        if (motivo == null) {
            return "";
        }

        String m = motivo.trim().toUpperCase();

        if (m.endsWith("-")) {
            m = m.substring(0, m.length() - 1).trim();
        }
        return m;
    }
}
