# Central de Emergências: Operação Resgate

Sistema desenvolvido para o Trabalho 1 de Algoritmos e Estruturas de Dados II
(Universidade Federal de Pelotas). O programa organiza ocorrências de uma central
de atendimento de emergências, permitindo cadastro, consulta rápida, verificação
de integridade dos registros e definição da ordem de atendimento, utilizando
estruturas de dados e algoritmos vistos na disciplina.

**Integrantes:** Pedro Henrique Mognon De Bortoli e Enzo Giacomini
**Repositório:** https://github.com/PedroMBortoli/Central-Emergencia

## Como executar

**Requisitos:** JDK 21 (ou superior). Nenhuma biblioteca externa.

Versão utilizada no desenvolvimento: OpenJDK 21.0.12 (Ubuntu).

Na raiz do projeto, compile e execute:

    javac -d out $(find src -name "*.java")
    java -cp out Main

Em sistemas sem o comando `find` (Windows PowerShell), compile listando as pastas:

    javac -d out src/Main.java src/modelo/*.java src/estruturas/*.java src/modulos/*.java src/util/*.java
    java -cp out Main

O programa deve ser executado a partir da raiz do projeto, pois o arquivo de
dados é lido por caminho relativo (`data/ocorrencias.csv`).

## Fonte de dados

- **Fonte:** Emergency - 911 Calls (Kaggle), chamadas de emergência do condado
  de Montgomery, Pensilvânia (EUA).
- **Link:** https://www.kaggle.com/datasets/mchirico/montcoalert
- **Licença:** Open Database License (conforme indicado na página do Kaggle).
- **Créditos:** Dataset de Mike Chirico; dados fornecidos por montcoalert.org.
- **Registros utilizados:** 5000, em amostra aleatória do arquivo original
  (cerca de 663 mil chamadas).
- **Campos utilizados diretamente:**
   `title`: separado em `tipo` (EMS, Fire, Traffic) e motivo
   `desc`: descrição
   `twp`: região
   `addr`: endereço
   `timeStamp`: data/hora de abertura
   `zip`, `lat`, `lng`: mantidos como informação de localização
 **Campos derivados ou acrescentados:**
   `id`: (definir, por exemplo sequencial gerado na carga)
   `prioridade`: de 1 a 5, definida pelo motivo da chamada (ver
   [Regra de prioridade](#regra-de-prioridade))
   `pessoasEnvolvidas`: definida pelo motivo da chamada (ver
   [Regra de pessoas envolvidas](#regra-de-pessoas-envolvidas))
   `tempoEstimado`: em minutos, definido pelo tipo e pela prioridade (ver
   [Regra de tempo estimado](#regra-de-tempo-estimado))
   `status`: (definir, por exemplo "Pendente" na carga)
   `hashIntegridade`: SHA-256 sobre (campos a definir)
 **Adaptações:** amostragem aleatória de 5000 linhas com `shuf`, mantendo o
  cabeçalho; separação do `title` em tipo e motivo; tratamento de `zip` vazio;
  as datas são as originais do dataset (2015 em diante). O arquivo completo
  (`data/911.csv`) não é versionado por causa do tamanho.

### Regra de prioridade

Implementada em `src/util/RegraPrioridade.java`. A prioridade depende do
**motivo** da chamada (segunda parte do `title`), e não apenas do tipo, porque o
mesmo motivo aparece em tipos diferentes (por exemplo, `VEHICLE ACCIDENT` em EMS,
Traffic e Fire). O critério é o risco à vida e a urgência do atendimento. Os 69
motivos distintos da amostra (após remover o `" -"` que o dataset acrescenta aos
motivos de Traffic) foram todos classificados:

| Prioridade | Critério | Motivos |
|---|---|---|
| 5 – Crítica | risco de vida imediato | CARDIAC ARREST, UNRESPONSIVE SUBJECT, UNCONSCIOUS SUBJECT, CVA/STROKE, CHOKING, SHOOTING, STABBING, OVERDOSE, HEMORRHAGING, AMPUTATION, BUILDING FIRE, TRAIN CRASH, RESCUE - WATER |
| 4 – Alta | grave, pode piorar rápido ou oferece risco coletivo | CARDIAC EMERGENCY, RESPIRATORY EMERGENCY, SEIZURES, ALTERED MENTAL STATUS, ALLERGIC REACTION, POISONING, BURN VICTIM, MATERNITY, HEAT EXHAUSTION, INDUSTRIAL ACCIDENT, RESCUE - GENERAL, RESCUE - TECHNICAL, RESCUE - ELEVATOR, GAS-ODOR/LEAK, CARBON MONOXIDE DETECTOR, VEHICLE FIRE, WOODS/FIELD FIRE, UNKNOWN TYPE FIRE, VEHICLE LEAKING FUEL |
| 3 – Média | lesão ou emergência sem risco imediato | VEHICLE ACCIDENT, HEAD INJURY, FRACTURE, FALL VICTIM, ASSAULT VICTIM, DIABETIC EMERGENCY, SYNCOPAL EPISODE, UNKNOWN MEDICAL EMERGENCY, ABDOMINAL PAINS, EYE INJURY, DEHYDRATION, ELECTRICAL FIRE OUTSIDE, APPLIANCE FIRE, ELEVATOR EMERGENCY |
| 2 – Baixa | desconforto, alarme ou situação controlável | SUBJECT IN PAIN, NAUSEA/VOMITING, FEVER, DIZZINESS, GENERAL WEAKNESS, LACERATIONS, BACK PAINS/INJURY, ANIMAL BITE, FIRE ALARM, MEDICAL ALERT ALARM, TRASH/DUMPSTER FIRE, HAZARDOUS ROAD CONDITIONS, ROAD OBSTRUCTION, DEBRIS/FLUIDS ON HIGHWAY, FIRE POLICE NEEDED |
| 1 – Mínima | sem vítima ou apenas serviço de apoio | DISABLED VEHICLE, FIRE INVESTIGATION, FIRE SPECIAL SERVICE, EMS SPECIAL SERVICE, PUMP DETAIL, TRANSFERRED CALL, ANIMAL COMPLAINT, S/B AT HELICOPTER LANDING |

Motivos não mapeados (por exemplo, cadastrados manualmente) recebem um valor
padrão pelo tipo: EMS e Fire = 3, Traffic = 2.

Distribuição resultante nos 5000 registros: prioridade 5 = 377 (7,5%),
4 = 862 (17,2%), 3 = 2187 (43,7%), 2 = 1056 (21,1%), 1 = 518 (10,4%).

### Regra de pessoas envolvidas

Implementada em `src/util/RegraPessoas.java`. O dataset não informa quantas
pessoas estão envolvidas (o campo `desc` contém apenas endereço, cidade, estação
e horário), então o valor é derivado do motivo. Os valores são fixos por motivo,
para que toda decisão do sistema seja explicável.

| Pessoas | Critério | Motivos |
|---|---|---|
| 0 | sem vítima: alarme, vistoria, apoio ou via obstruída | FIRE ALARM, FIRE INVESTIGATION, ROAD OBSTRUCTION, HAZARDOUS ROAD CONDITIONS, DEBRIS/FLUIDS ON HIGHWAY, VEHICLE LEAKING FUEL, ELECTRICAL FIRE OUTSIDE, WOODS/FIELD FIRE, TRASH/DUMPSTER FIRE, UNKNOWN TYPE FIRE, FIRE SPECIAL SERVICE, EMS SPECIAL SERVICE, FIRE POLICE NEEDED, PUMP DETAIL, TRANSFERRED CALL, ANIMAL COMPLAINT, S/B AT HELICOPTER LANDING |
| 1 | padrão: um paciente ou um veículo | todos os demais motivos |
| 2 | mais de um envolvido por natureza | VEHICLE ACCIDENT, MATERNITY (mãe e bebê), SHOOTING, STABBING, ASSAULT VICTIM, INDUSTRIAL ACCIDENT |
| 3 | exposição de moradores | GAS-ODOR/LEAK, CARBON MONOXIDE DETECTOR |
| 4 | edificação ocupada | BUILDING FIRE |
| 10 | acidente coletivo | TRAIN CRASH |

Distribuição resultante nos 5000 registros: 0 pessoas = 766, 1 = 2676, 2 = 1438,
3 = 81, 4 = 38, 10 = 1. Ou seja, 1558 ocorrências envolvem mais de uma pessoa.

### Regra de tempo estimado

Implementada em `src/util/RegraTempo.java`, em minutos:

    tempoEstimado = base do tipo + (prioridade - 1) × 10

| Tipo | Base | Justificativa |
|---|---|---|
| EMS | 30 min | atendimento e transporte de um paciente |
| Traffic | 40 min | sinalização da via e remoção de veículos |
| Fire | 60 min | combate e rescaldo são mais demorados |

O acréscimo por prioridade reflete que ocorrências mais graves mantêm a equipe
mais tempo no local. Exemplos: FEVER (EMS, prioridade 2) = 40 min; CARDIAC ARREST
(EMS, 5) = 70 min; BUILDING FIRE (Fire, 5) = 100 min. Nos 5000 registros os tempos
vão de 30 a 100 min, com média de 53 min em EMS, 54 min em Traffic e 76 min em Fire.

## Estruturas implementadas e onde foram aplicadas

- **Tabela hash** (`src/estruturas/TabelaHash.java`): consulta rápida de ocorrências.
- **Trie** (`src/estruturas/Trie.java`): busca por prefixo.
- **Algoritmo guloso** (`src/modulos/OrdenadorAtendimento.java`): definição da ordem de atendimento.

(detalhes de cada aplicação a preencher conforme a implementação avança)

## Justificativas das escolhas

(a preencher)

## Funcionalidades

(a preencher)

## Critérios de decisão

(a preencher)

## Bibliotecas utilizadas

(a preencher)