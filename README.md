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
   `prioridade`: (definir a regra)
   `pessoasEnvolvidas`: (definir a regra)
   `tempoEstimado`: (definir a regra)
   `status`: (definir, por exemplo "Pendente" na carga)
   `hashIntegridade`: SHA-256 sobre (campos a definir)
 **Adaptações:** amostragem aleatória de 5000 linhas com `shuf`, mantendo o
  cabeçalho; separação do `title` em tipo e motivo; tratamento de `zip` vazio;
  as datas são as originais do dataset (2015 em diante). O arquivo completo
  (`data/911.csv`) não é versionado por causa do tamanho.

## Estruturas implementadas e onde foram aplicadas

- **Tabela hash** (`src/estruturas/TabelaHash.java`): consulta rápida de ocorrências.

(demais estruturas a definir)

## Justificativas das escolhas

(a preencher)

## Funcionalidades

(a preencher)

## Critérios de decisão

(a preencher)

## Bibliotecas utilizadas

(a preencher)