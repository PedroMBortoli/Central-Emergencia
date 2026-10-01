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
  - `title`: separado em `tipo` (EMS, FIRE, TRAFFIC) e `motivo` (por exemplo,
    `EMS: CARDIAC ARREST` → tipo `EMS`, motivo `CARDIAC ARREST`)
  - `desc`: descrição
  - `twp`: região
  - `addr`: endereço
  - `timeStamp`: data/hora de abertura
- **Campos não utilizados:** `lat`, `lng`, `zip` e `e` (este último vale sempre 1
  no dataset). Permanecem no arquivo, mas não são carregados nas ocorrências.
- **Campos derivados ou acrescentados:**
  - `id`: número sequencial gerado na carga (1 a 5000). Ocorrências cadastradas
    pelo usuário continuam a sequência (5001, 5002, ...). Ids de ocorrências
    removidas não são reaproveitados, para que um id nunca identifique duas
    ocorrências diferentes.
  - `prioridade`: de 1 a 5, definida pelo motivo da chamada (ver
    [Regra de prioridade](#regra-de-prioridade)).
  - `pessoasEnvolvidas`: definida pelo motivo da chamada (ver
    [Regra de pessoas envolvidas](#regra-de-pessoas-envolvidas)).
  - `tempoEstimado`: em minutos, definido pelo tipo e pela prioridade (ver
    [Regra de tempo estimado](#regra-de-tempo-estimado)).
  - `status`: `PENDENTE` na carga e no cadastro. Valores possíveis: `PENDENTE`,
    `EM ANDAMENTO` e `FINALIZADO`.
  - `equipe`: `NÃO ATRIBUÍDA` na carga e no cadastro; pode ser alterada pelo usuário.
  - `dataHora` de ocorrências cadastradas: data/hora do momento do cadastro, no
    mesmo formato do dataset (`aaaa-MM-dd HH:mm:ss`).
  - `hashIntegridade`: SHA-256 sobre (campos a definir no módulo 4).
- **Adaptações:**
  - amostragem aleatória de 5000 linhas com `shuf`, mantendo o cabeçalho. O
    arquivo completo (`data/911.csv`) não é versionado por causa do tamanho;
  - remoção do `" -"` que o dataset acrescenta ao fim dos motivos de Traffic
    (`VEHICLE ACCIDENT -` → `VEHICLE ACCIDENT`), para que o mesmo motivo seja
    tratado igualmente em todos os tipos;
  - região vazia (2 registros) recebe o valor `DESCONHECIDA`;
  - **padrão de maiúsculas:** todos os campos textuais (tipo, motivo, região,
    endereço, status e equipe) são armazenados em maiúsculas, padrão que o
    dataset já usa na maior parte dos campos. As entradas do usuário são
    normalizadas (`trim` e `toUpperCase`) antes de serem gravadas ou comparadas,
    para que filtros e buscas encontrem igualmente registros carregados e
    cadastrados;
  - as datas são as originais do dataset (2015 em diante).

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

- **Tabela hash** (`src/estruturas/TabelaHash.java`): armazenamento principal
  das ocorrências, com o `id` como chave. É usada pela Central de Ocorrências
  (módulo 1) para cadastrar, consultar, alterar, remover e listar.
  - função hash polinomial própria: `h = 31 * h + c` para cada caractere da
    chave, com `(h & 0x7fffffff) % capacidade` para obter um índice válido
    (o `&` zera o bit de sinal e evita índices negativos causados por overflow);
  - colisões tratadas por encadeamento: cada posição do vetor guarda uma lista
    ligada de nós com chave, valor e próximo;
  - capacidade inicial 16, dobrada sempre que o fator de carga
    (`tamanho / capacidade`) passa de 0,75. Ao redimensionar, todos os elementos
    são reinseridos, pois o índice depende da capacidade;
  - com as 5000 ocorrências, a tabela fica com capacidade 8192, fator de carga
    0,61 e a maior lista com 4 nós.
- **Trie** (`src/estruturas/Trie.java`): busca por prefixo (a implementar).
- **Algoritmo guloso** (`src/modulos/OrdenadorAtendimento.java`): definição da
  ordem de atendimento (a implementar).

## Justificativas das escolhas

- **Tabela hash para o armazenamento por id:** a operação mais frequente da
  central é localizar uma ocorrência pelo identificador. Uma busca sequencial
  custa O(n), enquanto a tabela hash calcula a posição a partir da chave, com
  custo O(1) em média para inserir, buscar e remover. O pior caso é O(n), se
  todas as chaves caíssem na mesma posição, situação evitada pela função hash
  polinomial e pelo redimensionamento, que mantêm as listas curtas. A
  limitação da tabela hash é não manter ordem nem permitir busca por prefixo,
  por isso a listagem não sai ordenada por id e a busca por prefixo usa a Trie.

(demais justificativas a preencher)

## Funcionalidades

### Módulo 1 – Central de Ocorrências

Implementado em `src/modulos/CentralOcorrencias.java`, que guarda a tabela hash
e o contador de ids. A leitura do arquivo fica em `src/util/LeitorCSV.java`.

- **Carregar:** lê o CSV, gera os campos derivados de cada linha e insere as
  ocorrências na tabela hash.
- **Cadastrar:** o usuário informa tipo, motivo, descrição, região e endereço; o
  sistema gera o id, a data/hora, o status, a equipe e os campos derivados. Só
  são aceitos os tipos EMS, FIRE e TRAFFIC.
- **Consultar:** busca uma ocorrência pelo id.
- **Alterar:** apenas os campos operacionais podem ser alterados: `status`
  (PENDENTE, EM ANDAMENTO ou FINALIZADO), `equipe` e `prioridade` (1 a 5). Ao
  alterar a prioridade, o tempo estimado é recalculado. Os campos que descrevem o
  que aconteceu (id, tipo, motivo, descrição, região, endereço e data/hora) não
  podem ser alterados, pois serão usados pela verificação de integridade.
- **Remover:** remove uma ocorrência pelo id.
- **Listar:** retorna todas as ocorrências armazenadas, na ordem das posições da
  tabela hash.

(demais módulos a preencher)

## Critérios de decisão

(a preencher)

## Bibliotecas utilizadas

Apenas a biblioteca padrão do Java, como apoio. Nenhuma substitui as estruturas
de dados exigidas no trabalho.

- `java.io.BufferedReader`, `java.io.FileReader`: leitura do CSV linha por linha.
- `java.util.ArrayList`: lista temporária com as ocorrências lidas do arquivo,
  já que a quantidade de linhas não é conhecida antes da leitura. O
  armazenamento do sistema é feito na tabela hash implementada pelo grupo.
- `java.time.LocalDateTime`, `java.time.format.DateTimeFormatter`: data/hora
  de abertura das ocorrências cadastradas pelo usuário.