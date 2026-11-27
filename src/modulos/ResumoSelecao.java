package modulos;

import estruturas.TabelaHash;
import modelo.Ocorrencia;

import java.util.List;

// Modulo 9: resume a ultima selecao por tipo e por regiao (quais viaturas saem e para onde).
public class ResumoSelecao {

    // um grupo do resumo (uma regiao ou um tipo)
    public static class Grupo {
        private final String nome;
        private int quantidade;
        private int tempo;
        private int pessoas;
        private int somaPrioridade;

        public Grupo(String nome) {
            this.nome = nome;
        }

        public String getNome() {
            return nome;
        }

        public int getQuantidade() {
            return quantidade;
        }

        public int getTempo() {
            return tempo;
        }

        public int getPessoas() {
            return pessoas;
        }

        public int getSomaPrioridade() {
            return somaPrioridade;
        }
    }

    public List<Grupo> agruparPorRegiao(List<Ocorrencia> selecionadas) {
        TabelaHash<String, Grupo> tabela = new TabelaHash<>(16);
        for (Ocorrencia o : selecionadas) {
            somar(tabela, o.getRegiao(), o);
        }
        return ordenarPorQuantidade(tabela.listarTodos());
    }

    public List<Grupo> agruparPorTipo(List<Ocorrencia> selecionadas) {
        TabelaHash<String, Grupo> tabela = new TabelaHash<>(16);
        for (Ocorrencia o : selecionadas) {
            somar(tabela, o.getTipo(), o);
        }
        return ordenarPorQuantidade(tabela.listarTodos());
    }

    // busca ou cria o grupo e soma a ocorrencia
    private void somar(TabelaHash<String, Grupo> tabela, String chave, Ocorrencia o) {
        Grupo g = tabela.buscarOuInserir(chave, () -> new Grupo(chave));
        g.quantidade++;
        g.tempo += o.getTempoEstimado();
        g.pessoas += o.getPessoasEnvolvidas();
        g.somaPrioridade += o.getPrioridade();
    }

    // maior grupo primeiro (merge sort do modulo 5)
    private List<Grupo> ordenarPorQuantidade(List<Grupo> grupos) {
        OrdenadorAtendimento.mergeSort(grupos, (a, b) -> Integer.compare(b.quantidade, a.quantidade));
        return grupos;
    }

    // recurso principal que sai pra cada tipo de ocorrencia
    public static String recursoDoTipo(String tipo) {
        switch (tipo) {
            case "FIRE":
                return "caminhão de bombeiros";
            case "TRAFFIC":
                return "viatura de trânsito";
            default:
                return "ambulância";
        }
    }
}
