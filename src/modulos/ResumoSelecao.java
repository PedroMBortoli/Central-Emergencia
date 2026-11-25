package modulos;

import estruturas.TabelaHash;
import modelo.Ocorrencia;

import java.util.List;

// Modulo 9: resumo das ocorrencias selecionadas no planejamento (modulo 7 ou guloso do modulo 5).
// Ideia: depois de escolher quem vai ser atendido, a central precisa saber pra onde vao as
// equipes e quais viaturas vao sair. Entao a gente junta as selecionadas por regiao e por tipo
// usando a TabelaHash do T1 (chave = nome da regiao/tipo).
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

    // acha o grupo da chave (ou cria se ainda nao existe) e soma a ocorrencia nele
    private void somar(TabelaHash<String, Grupo> tabela, String chave, Ocorrencia o) {
        Grupo g = tabela.buscar(chave);
        if (g == null) {
            g = new Grupo(chave);
            tabela.inserir(chave, g);
        }
        g.quantidade++;
        g.tempo += o.getTempoEstimado();
        g.pessoas += o.getPessoasEnvolvidas();
        g.somaPrioridade += o.getPrioridade();
    }

    // insertion sort simples, do maior grupo pro menor (sao poucos grupos, entao nao pesa)
    private List<Grupo> ordenarPorQuantidade(List<Grupo> grupos) {
        for (int i = 1; i < grupos.size(); i++) {
            Grupo atual = grupos.get(i);
            int j = i - 1;
            while (j >= 0 && grupos.get(j).quantidade < atual.quantidade) {
                grupos.set(j + 1, grupos.get(j));
                j--;
            }
            grupos.set(j + 1, atual);
        }
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
