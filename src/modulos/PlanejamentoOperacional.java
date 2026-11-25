package modulos;

import estruturas.GrafoDirecionado;
import estruturas.TabelaHash;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PlanejamentoOperacional {

    private final TabelaHash<String, GrafoDirecionado> operacoes = new TabelaHash<>(16);
    private final List<String> nomesOperacoes = new ArrayList<>();

    // formato: operacao,antes,depois
    public int carregarDependencias(String caminho) {
        int carregadas = 0;
        try (BufferedReader leitor = new BufferedReader(new FileReader(caminho))) {
            leitor.readLine();
            String linha;
            while ((linha = leitor.readLine()) != null) {
                if (linha.isBlank()) {
                    continue;
                }
                String[] campos = linha.split(",", -1);
                if (campos.length < 3) {
                    continue;
                }
                if (cadastrarDependencia(campos[0], campos[1], campos[2])) {
                    carregadas++;
                }
            }
        } catch (IOException e) {
            System.out.println("Erro ao ler o arquivo de dependências: " + e.getMessage());
        }
        return carregadas;
    }

    public boolean cadastrarDependencia(String operacao, String antes, String depois) {
        operacao = normalizar(operacao);
        antes = normalizar(antes);
        depois = normalizar(depois);
        if (operacao.isEmpty() || antes.isEmpty() || depois.isEmpty() || antes.equals(depois)) {
            return false;
        }
        return obterOuCriar(operacao).adicionarAresta(antes, depois);
    }

    public boolean cadastrarAtividade(String operacao, String atividade) {
        operacao = normalizar(operacao);
        atividade = normalizar(atividade);
        if (operacao.isEmpty() || atividade.isEmpty()) {
            return false;
        }
        GrafoDirecionado grafo = obterOuCriar(operacao);
        if (grafo.contemVertice(atividade)) {
            return false;
        }
        grafo.adicionarVertice(atividade);
        return true;
    }

    public boolean removerDependencia(String operacao, String antes, String depois) {
        GrafoDirecionado grafo = operacoes.buscar(normalizar(operacao));
        return grafo != null && grafo.removerAresta(normalizar(antes), normalizar(depois));
    }

    public GrafoDirecionado buscarOperacao(String operacao) {
        return operacoes.buscar(normalizar(operacao));
    }

    public GrafoDirecionado.ResultadoOrdenacao ordemDeExecucao(String operacao) {
        GrafoDirecionado grafo = buscarOperacao(operacao);
        return grafo == null ? null : grafo.ordenacaoTopologica();
    }

    public List<String> operacoesComCiclo() {
        List<String> resultado = new ArrayList<>();
        for (String nome : nomesOperacoes) {
            if (operacoes.buscar(nome).ordenacaoTopologica().temCiclo()) {
                resultado.add(nome);
            }
        }
        return resultado;
    }

    // cada tipo de ocorrencia (EMS, FIRE, TRAFFIC) tem a sua operacao padrao
    public String operacaoDoTipo(String tipo) {
        return "ATENDIMENTO_" + normalizar(tipo);
    }

    public List<String> getNomesOperacoes() {
        return new ArrayList<>(nomesOperacoes);
    }

    private GrafoDirecionado obterOuCriar(String operacao) {
        GrafoDirecionado grafo = operacoes.buscar(operacao);
        if (grafo == null) {
            grafo = new GrafoDirecionado();
            operacoes.inserir(operacao, grafo);
            nomesOperacoes.add(operacao);
        }
        return grafo;
    }

    private String normalizar(String texto) {
        return texto == null ? "" : texto.trim().toUpperCase().replace(' ', '_');
    }
}
