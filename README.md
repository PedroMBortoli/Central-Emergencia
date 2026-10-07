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

> **Estado atual:** os cinco módulos estão implementados como classes em
> `src/modulos/`. O menu interativo (`src/Main.java`) dá acesso a eles e aos
> modos de interação (Investigação, Operação Resgate e Consulta Rápida).

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
  - `hashIntegridade`: identificador de 32 caracteres hexadecimais calculado a
    partir do conteúdo da ocorrência no momento em que ela é criada (ver
    [Módulo 4](#módulo-4--integridade-dos-registros)).
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
- **Arquivo de teste de integridade:** `data/ocorrencias_teste_integridade.csv`
  contém as 20 primeiras linhas da amostra e mais 5 linhas inseridas de propósito,
  pois a amostra real não possui registros duplicados:
  - linhas 21, 22 e 23: cópias exatas das ocorrências 2, 5 e 7;
  - linha 24: mesmo evento da ocorrência 3, com o motivo trocado
    (FALL VICTIM → CARDIAC ARREST);
  - linha 25: mesmo evento da ocorrência 8, com a descrição alterada.

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

- **Tabela hash** (`src/estruturas/TabelaHash.java`): tabela genérica
  `TabelaHash<K, V>`, usada em vários pontos do sistema:
  - armazenamento principal das ocorrências, com o `id` como chave (módulo 1);
  - índices dos módulos 2 e 3 (por tipo, região e status);
  - agrupamento por hash do conteúdo e por chave de evento, para detectar
    duplicatas (módulo 4).

  Detalhes da implementação:
  - função hash polinomial própria: `h = 31 * h + c` para cada caractere da
    chave, com `|h| % capacidade` para obter um índice válido;
  - colisões tratadas por encadeamento: cada posição do vetor guarda uma lista
    encadeada de pares chave/valor;
  - capacidade inicial 16, dobrada sempre que o fator de carga
    (`tamanho / capacidade`) passa de 0,75. Ao redimensionar (rehash), todos os
    elementos são reinseridos, pois o índice depende da capacidade;
  - com as 5000 ocorrências, a tabela fica com capacidade 8192, fator de carga
    0,61 e a maior lista com 4 elementos.
- **Trie** (`src/estruturas/Trie.java`): busca por prefixo da descrição na
  Consulta Rápida (módulo 2).
  - cada nó guarda os filhos (um por caractere), uma marca de fim de palavra e a
    lista de valores (ocorrências) associados à chave que termina naquele nó.
    Uma lista é necessária porque ocorrências diferentes podem ter a mesma
    descrição;
  - as chaves são normalizadas para maiúsculas na inserção, na busca e na remoção;
  - **inserir:** percorre a chave caractere a caractere, criando os nós que
    faltam, e adiciona o valor ao nó final. Custo O(K), em que K é o tamanho da
    chave;
  - **buscar por prefixo:** desce pelos nós do prefixo e, a partir do nó
    alcançado, percorre recursivamente a subárvore coletando os valores. Custo
    O(K) para chegar ao nó, mais o tamanho da subárvore percorrida;
  - **remover:** remove apenas a ocorrência indicada (as outras com a mesma
    descrição continuam) e, na volta da recursão, poda os nós que ficaram sem
    valores e sem filhos, para não acumular ramos vazios.
- **Algoritmo guloso** (`src/modulos/OrdenadorAtendimento.java`): seleção das
  ocorrências a atender dentro de um limite de tempo, no módulo 5 (ver
  [Critérios de decisão](#critérios-de-decisão)).
- **Merge sort** (`src/modulos/OrdenadorAtendimento.java`), estrutura auxiliar:
  ordenação implementada pelo grupo, usada para montar a fila de atendimento e
  para ordenar as candidatas do algoritmo guloso. Divide a lista ao meio
  recursivamente e intercala as metades ordenadas; em caso de empate, o
  elemento da metade esquerda vem primeiro, o que torna a ordenação estável.

## Justificativas das escolhas

- **Tabela hash para o armazenamento por id:** a operação mais frequente da
  central é localizar uma ocorrência pelo identificador. Uma busca sequencial
  custa O(n), enquanto a tabela hash calcula a posição a partir da chave, com
  custo O(1) em média para inserir, buscar e remover. O pior caso é O(n), se
  todas as chaves caíssem na mesma posição, situação evitada pela função hash
  polinomial e pelo redimensionamento, que mantêm as listas curtas. A
  limitação da tabela hash é não manter ordem nem permitir busca por prefixo,
  por isso a listagem não sai ordenada por id e a busca por prefixo usa a Trie.
- **Custo amortizado do rehash:** redimensionar custa O(n), pois todos os
  elementos são reinseridos. Como a capacidade dobra a cada redimensionamento,
  esse custo é diluído entre as inserções anteriores, e o custo amortizado de
  cada inserção continua O(1) (análise amortizada vista em aula).
- **Hash polinomial de 64 bits com duas bases para a integridade:** o
  identificador de integridade usa o mesmo hash polinomial visto em aula
  (`H = c1·p^(n-1) + c2·p^(n-2) + ... + cn·p^0`), mas apenas a etapa de geração
  do hash code, sem a etapa de compressão (`% capacidade`), pois o valor não é
  usado como índice de vetor. É calculado em `long` (64 bits) em vez de `int`,
  o que aumenta muito o número de valores possíveis. Com uma única base há
  colisões fáceis de construir: com a base 31, `"Aa"` e `"BB"` geram o mesmo
  valor (2112). Por isso o hash é calculado com duas bases (31 e 37) e os dois
  resultados são concatenados; com a base 37, os mesmos textos geram 2502 e 2508.
  Uma alteração que engane uma das bases dificilmente engana a outra ao mesmo
  tempo.
- **Tabela hash para detectar duplicatas:** comparar todas as ocorrências entre
  si custaria O(n²) (cerca de 12,5 milhões de comparações para 5000 registros).
  Inserindo cada ocorrência em uma tabela hash indexada pelo hash do conteúdo,
  ocorrências iguais caem na mesma lista, e a detecção é feita em uma única
  passada, O(n).
- **Tabelas hash como índices por categoria (tipo, região e status):** para
  responder "quais ocorrências são do tipo FIRE?" sem percorrer as 5000, cada
  categoria é uma chave de uma tabela hash cujo valor é a lista de ids daquela
  categoria. A consulta passa a custar O(1) para achar a lista, mais o tamanho
  do resultado. Os índices guardam ids, e não cópias das ocorrências, e são
  atualizados no cadastro, na remoção e na alteração de status.
- **Varredura para filtros por faixa (prioridade mínima e pessoas
  envolvidas):** a tabela hash só resolve buscas por igualdade. Para "prioridade
  maior ou igual a 4" seria preciso consultar várias chaves, e o resultado
  costuma ser uma fração grande da base (1239 de 5000). Por isso esses filtros
  percorrem a lista uma vez, em O(n).
- **Trie para busca por prefixo:** a tabela hash não permite buscar por parte
  da chave: `"MAIN"` e `"MAIN ST"` geram índices sem relação entre si. Na Trie,
  todas as chaves que começam com o mesmo prefixo ficam na mesma subárvore, e
  chegar até ela custa O(K), em que K é o tamanho do prefixo, independentemente
  do número de ocorrências. Como a descrição do dataset começa pelo endereço
  (por exemplo, `MAIN ST & ...; NORRISTOWN; ...`), a busca por prefixo da
  descrição funciona na prática como uma busca por rua ou cruzamento.
- **Merge sort para a ordenação:** O(n log n) em qualquer caso e estável. A
  estabilidade garante que ocorrências empatadas em todos os critérios mantenham
  a ordem em que chegaram. A ordenação foi implementada pelo grupo em vez de
  usar `Collections.sort`; apenas o critério de comparação é passado como
  parâmetro (`Comparator`).
- **Algoritmo guloso para a seleção com limite de tempo:** o problema de
  escolher ocorrências que caibam em um tempo disponível maximizando o impacto é
  uma variação do problema da mochila. Uma solução exata exigiria testar
  combinações; o algoritmo guloso ordena as candidatas uma vez (O(n log n)) e
  decide cada uma em O(1), escolhendo sempre a de maior impacto por minuto que
  ainda cabe e nunca revendo a decisão. A solução não é garantidamente ótima,
  mas é rápida e cada escolha pode ser justificada pelo critério.

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

A Central também mantém os índices dos módulos 2 e 3 sincronizados: carregar e
cadastrar indexam a ocorrência, remover a retira dos índices e alterar o status
move o id para a lista do novo status.

### Módulo 2 – Consulta Rápida

Implementado em `src/modulos/ConsultaRapida.java`, acessado por
`CentralOcorrencias.getConsultaRapida()`.

| Consulta | Estrutura | Custo |
|---|---|---|
| por id | tabela hash (id → ocorrência) | O(1) médio |
| por prefixo da descrição | Trie | O(K) + tamanho do resultado |
| por tipo | tabela hash (tipo → lista de ids) | O(1) + tamanho do resultado |
| por região | tabela hash (região → lista de ids) | O(1) + tamanho do resultado |

As entradas do usuário são normalizadas (`trim` e maiúsculas), de modo que
`"fire"`, `" Fire "` e `"FIRE"` dão o mesmo resultado. Exemplos com a amostra:
prefixo `MAIN ST` → 135 ocorrências; tipo EMS / FIRE / TRAFFIC → 2443 / 756 / 1801;
região `LOWER MERION` → 415.

### Módulo 3 – Organização das Ocorrências

Implementado em `src/modulos/Organizador.java`, acessado por
`CentralOcorrencias.getOrganizador()`.

| Filtro | Como funciona | Exemplo com a amostra |
|---|---|---|
| por status | índice próprio em tabela hash (status → lista de ids) | PENDENTE → 5000 (após a carga) |
| prioridade mínima | varredura da lista, O(n) | prioridade ≥ 4 → 1239; prioridade 5 → 377 |
| pessoas envolvidas mínimas | varredura da lista, O(n) | 2 ou mais pessoas → 1558 |
| por região | usa o índice do módulo 2 | — |
| por tipo | usa o índice do módulo 2 | — |

Região e tipo reaproveitam os índices da Consulta Rápida, para não manter duas
cópias do mesmo índice.

### Módulo 4 – Integridade dos Registros

Implementado em `src/modulos/Integridade.java`. O resultado da comparação com
arquivos fica em `src/modulos/ComparacaoArquivo.java`.

**Identificador de integridade.** Cada ocorrência recebe, no construtor de
`Ocorrencia`, um hash calculado sobre os campos que descrevem o que aconteceu,
sempre nesta ordem e separados por `|`:

    tipo | motivo | descrição | região | endereço | data/hora

- O separador evita ambiguidade entre campos (sem ele, `"ABING" + "TON RD"` e
  `"ABINGTON" + " RD"` gerariam o mesmo texto). O `|` não aparece no dataset; o
  `;` não serve porque aparece dentro de todas as descrições.
- Ficam de fora os campos operacionais (`status`, `equipe`, `prioridade` e
  `tempoEstimado`), que podem ser alterados legitimamente pelo sistema.
- Fica de fora o `id`, para que duas ocorrências com o mesmo conteúdo tenham o
  mesmo hash e possam ser detectadas como duplicatas.
- O hash é calculado duas vezes (bases 31 e 37) e cada resultado é escrito com
  16 dígitos hexadecimais (`%016x`), formando 32 caracteres. O tamanho fixo
  garante que os 16 primeiros caracteres sejam sempre da base 31 e os 16 últimos
  da base 37.
- O atributo `hashIntegridade` é `final`: é a "foto" do conteúdo no momento da
  criação. O método `calcularHash()` gera a "foto" do conteúdo atual.

**Verificações:**

- **Alterações não autorizadas** (`foiAlterada`, `verificarTodas`): compara o
  hash guardado com o hash recalculado. Se forem diferentes, algum campo que não
  pode ser alterado pelo sistema foi modificado. Alterações de status, equipe e
  prioridade feitas pela Central não são acusadas.
- **Registros inconsistentes** (`validar`, `verificarInconsistentes`): regras que
  o hash não cobre. Indicam o motivo de cada problema:
  - campos obrigatórios vazios;
  - tipo diferente de EMS, FIRE ou TRAFFIC;
  - status diferente de PENDENTE, EM ANDAMENTO ou FINALIZADO;
  - prioridade fora de 1 a 5;
  - tempo estimado diferente do calculado pela regra para o tipo e a
    prioridade atual;
  - pessoas envolvidas diferente do calculado pela regra para o motivo;
  - data/hora fora do formato `aaaa-MM-dd HH:mm:ss` ou no futuro.

  O hash detecta mudanças em campos imutáveis mesmo quando o novo valor parece
  válido; as regras detectam valores absurdos ou incoerentes, inclusive em
  campos que não entram no hash. As duas verificações se complementam.
- **Duplicatas** (`detectarDuplicatas`): ocorrências com ids diferentes e o mesmo
  conteúdo. As ocorrências são agrupadas em uma tabela hash indexada pelo hash
  do conteúdo; grupos com mais de um elemento são duplicatas.
- **Duplicatas com conteúdo diferente** (`detectarDuplicatasDivergentes`): o
  mesmo evento registrado mais de uma vez com informações diferentes. As
  ocorrências são agrupadas por uma chave de evento (**tipo + data/hora +
  endereço**: o que, quando e onde); grupos com hashes diferentes são versões
  divergentes do mesmo evento. O tipo faz parte da chave porque um mesmo
  acidente costuma gerar chamadas legítimas de EMS, TRAFFIC e FIRE no mesmo
  horário e endereço. Na amostra real nenhuma chave de evento se repete.
- **Conflitos entre versões e armazenado × recuperado** (`compararComArquivo`):
  relê um arquivo sem carregá-lo na memória e compara cada ocorrência do arquivo
  com a armazenada de mesmo id, usando três hashes: o do arquivo, o guardado na
  carga e o recalculado agora. Isso permite indicar qual lado mudou:

  | Arquivo = carga? | Carga = atual? | Diagnóstico |
  |---|---|---|
  | sim | sim | íntegro |
  | sim | não | registro alterado na memória após a carga |
  | não | sim | arquivo modificado após a carga |
  | não | não | registro alterado na memória e no arquivo |

  Ocorrências que estão no arquivo mas não na memória (removidas ou perdidas)
  também são listadas. Ocorrências cadastradas depois da carga não são acusadas.

**Eficiência:** todas as verificações percorrem as ocorrências uma única vez,
com consultas O(1) na tabela hash. O custo é O(n · L), em que L é o tamanho do
conteúdo de uma ocorrência (cerca de 130 caracteres) e não cresce com a base;
na prática, o custo é linear no número de registros. Com os 5000 registros, as
verificações levam dezenas de milissegundos.

**Resultados com os dados do projeto:**

| Verificação | Amostra real (5000) | Arquivo de teste (25) |
|---|---|---|
| Duplicatas | nenhuma | [2, 21], [5, 22], [7, 23] |
| Duplicatas com conteúdo diferente | nenhuma | [3, 24], [8, 25] |
| Alteradas / inconsistentes | nenhuma | nenhuma |

**Limitações:**

- O hash polinomial não é criptográfico. Ele detecta alterações acidentais ou
  feitas sem conhecimento do mecanismo, mas alguém que conheça o algoritmo
  poderia, com esforço, forjar uma alteração com o mesmo hash. Para segurança
  contra ataques seria usado um hash criptográfico (como o SHA-256); o grupo
  optou por implementar a função com o conteúdo visto na disciplina.
- O CSV não tem coluna de id: o id é gerado pela posição da linha. Se linhas do
  arquivo forem apagadas ou reordenadas, as seguintes mudam de id, e a
  comparação com o arquivo acusa conflitos em todas elas.
- Como o sistema impede alterar os campos imutáveis pelos menus, a detecção de
  alterações não autorizadas é demonstrada simulando a adulteração (alteração
  direta do registro, sem passar pela Central) ou editando o arquivo de origem.

### Módulo 5 – Operação Resgate

Implementado em `src/modulos/OrdenadorAtendimento.java`.

- **Ordem de atendimento** (`ordenarPorPrioridade`): devolve uma cópia da lista
  ordenada pelos critérios descritos em
  [Critérios de decisão](#critérios-de-decisão), usando o merge sort do grupo.
  A lista recebida não é modificada.
- **Seleção com limite de tempo** (`selecionarComLimiteDeTempo`): recebe as
  ocorrências candidatas (por exemplo, as pendentes) e o tempo disponível em
  minutos, e devolve as ocorrências escolhidas pelo algoritmo guloso.

Os critérios podem ser combinados com os filtros dos módulos 2 e 3: por
exemplo, ordenar apenas as ocorrências pendentes de uma região.

## Critérios de decisão

### Ordem de atendimento

As ocorrências são ordenadas por três critérios, aplicados em sequência (o
seguinte só desempata o anterior):

1. **prioridade, da maior para a menor:** o risco à vida vem primeiro (ver
   [Regra de prioridade](#regra-de-prioridade));
2. **pessoas envolvidas, da maior para a menor:** com a mesma gravidade, atende
   antes quem afeta mais pessoas;
3. **tempo estimado, do menor para o maior:** com a mesma gravidade e o mesmo
   número de pessoas, o atendimento mais rápido libera a equipe antes.

Persistindo o empate, vale a ordem de chegada (o merge sort é estável).

Com a amostra, a fila começa por TRAIN CRASH (prioridade 5, 10 pessoas), seguida
das ocorrências de BUILDING FIRE (prioridade 5, 4 pessoas).

### Seleção com limite de tempo (algoritmo guloso)

Para cada ocorrência é calculada uma **densidade de impacto**:

    densidade = (prioridade × pessoas envolvidas) / tempo estimado

O numerador mede o impacto do atendimento (gravidade multiplicada pelo número de
pessoas) e a divisão pelo tempo mede quanto impacto cada minuto da equipe
produz. O algoritmo:

1. ordena as candidatas por densidade, da maior para a menor;
2. percorre a lista uma vez: se a ocorrência cabe no tempo restante, ela é
   selecionada e o tempo é descontado; se não cabe, é ignorada;
3. nenhuma escolha é revista depois de feita (escolha gulosa).

Exemplos com as 5000 ocorrências pendentes:

| Tempo disponível | Selecionadas |
|---|---|
| 120 min | TRAIN CRASH (100 min). Nenhuma outra cabe nos 20 min restantes, pois o menor tempo estimado é 30 min |
| 480 min | TRAIN CRASH (100), cinco BUILDING FIRE do tipo EMS (70 cada) e S/B AT HELICOPTER LANDING (30), somando 480 min |

Consequências do critério:

- ocorrências sem pessoas envolvidas (por exemplo, FIRE ALARM, WOODS/FIELD FIRE)
  têm impacto zero e ficam por último na seleção, independentemente da
  prioridade. Elas só são escolhidas para aproveitar o tempo que sobra;
- como em todo algoritmo guloso para esse tipo de problema, a seleção não é
  garantidamente ótima: uma ocorrência de alta densidade e tempo longo pode
  ocupar o espaço de uma combinação de ocorrências menores com impacto total
  maior.

## Bibliotecas utilizadas

Apenas a biblioteca padrão do Java, como apoio. Nenhuma substitui as estruturas
de dados exigidas no trabalho.

- `java.io.BufferedReader`, `java.io.FileReader`: leitura do CSV linha por linha.
- `java.util.ArrayList` / `java.util.List`: listas usadas para devolver
  resultados de consultas, filtros e verificações, e para guardar as ocorrências
  lidas do arquivo antes de inseri-las na tabela hash, já que a quantidade de
  linhas não é conhecida antes da leitura. O armazenamento do sistema é feito na
  tabela hash implementada pelo grupo.
- `java.util.LinkedList` (em `TabelaHash`): lista encadeada de cada posição do
  vetor (bucket), usada como recipiente dos pares chave/valor que colidem. A
  função hash, o cálculo do índice, a política de tratamento de colisões
  (encadeamento), o controle do fator de carga e o rehash são implementados pelo
  grupo.
- `java.util.Comparator` (em `OrdenadorAtendimento`): apenas define o critério
  de comparação passado ao merge sort; o algoritmo de ordenação é do grupo.
- `java.time.LocalDateTime`, `java.time.format.DateTimeFormatter`: data/hora
  de abertura das ocorrências cadastradas pelo usuário e, no módulo 4,
  validação do formato da data/hora e verificação de datas no futuro.