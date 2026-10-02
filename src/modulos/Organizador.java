package modulos;

import estruturas.TabelaHash;
import modelo.Ocorrencia;
import java.util.List;
import java.util.ArrayList;

public class Organizador {
    private TabelaHash<String, List<String>> indicePorStatus;
    private ConsultaRapida consultaRapida;

    public Organizador(ConsultaRapida consultaRapida) {
        this.indicePorStatus = new TabelaHash<>(16);
        this.consultaRapida = consultaRapida;
    }

    // indexa o status de uma ocorrencia (chamado no cadastro, por enquanto)
    public void adicionar(Ocorrencia o) {
        adicionarEmLista(indicePorStatus, o.getStatus(), o.getId());
    }

    // mesmo padrao usado no ConsultaRapida: insere o id na lista de uma categoria,
    // criando a lista se ainda nao existir uma para aquela chave
    private void adicionarEmLista(TabelaHash<String, List<String>> indice, String chave, String id) {
        String chaveNormalizada = normalizar(chave);
        List<String> lista = indice.buscar(chaveNormalizada);
        if (lista == null) {
            lista = new ArrayList<>();
            indice.inserir(chaveNormalizada, lista);
        }
        lista.add(id);
    }

    // mesmo padrao de normalizacao usado no ConsultaRapida: sem espaco nas pontas, maiusculo
    private String normalizar(String valor) {
        return valor == null ? "" : valor.trim().toUpperCase();
    }

    // TODO: remover(String id, String status)
    // TODO: atualizarStatus(String id, String statusAntigo, String statusNovo)
    // TODO: filtrarPorStatus(String status)
    // TODO: filtrarPorPrioridadeMinima(List<Ocorrencia> todas, int limiar)
    // TODO: filtrarPorPessoasEnvolvidas(List<Ocorrencia> todas, int minimo)
    // TODO: filtrarPorRegiao / filtrarPorTipo (delegando para consultaRapida)
}
