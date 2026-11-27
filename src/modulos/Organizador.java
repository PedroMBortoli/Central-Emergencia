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

    // indexa o status de uma ocorrencia
    public void adicionar(Ocorrencia o) {
        adicionarEmLista(indicePorStatus, o.getStatus(), o.getId());
    }

    // adiciona o id na lista da categoria (cria a lista se nao existir)
    private void adicionarEmLista(TabelaHash<String, List<String>> indice, String chave, String id) {
        indice.buscarOuInserir(normalizar(chave), ArrayList::new).add(id);
    }

    // sem espaco nas pontas, maiusculo
    private String normalizar(String valor) {
        return valor == null ? "" : valor.trim().toUpperCase();
    }

    // tira o id do status; se a lista ficar vazia, remove o status
    public void remover(String id, String status) {
        removerDeLista(indicePorStatus, status, id);
    }

    private void removerDeLista(TabelaHash<String, List<String>> indice, String chave, String id) {
        String chaveNormalizada = normalizar(chave);
        List<String> lista = indice.buscar(chaveNormalizada);
        if (lista == null) {
            return;
        }
        lista.remove(id);
        if (lista.isEmpty()) {
            indice.remover(chaveNormalizada);
        }
    }

    // move o id do status antigo para o novo
    public void atualizarStatus(String id, String statusAntigo, String statusNovo) {
        removerDeLista(indicePorStatus, statusAntigo, id);
        adicionarEmLista(indicePorStatus, statusNovo, id);
    }
    // devolve as ocorrencias cujo status (normalizado) bate com o informado
    public List<Ocorrencia> filtrarPorStatus(String status) {
        List<String> ids = indicePorStatus.buscar(normalizar(status));
        return resolverIds(ids);
    }

    // troca os ids pelas ocorrencias (usa o indice por id do ConsultaRapida)
    private List<Ocorrencia> resolverIds(List<String> ids) {
        List<Ocorrencia> resultado = new ArrayList<>();
        if (ids == null) {
            return resultado; // status nao encontrado
        }
        for (String id : ids) {
            Ocorrencia o = consultaRapida.buscarPorId(id);
            if (o != null) {
                resultado.add(o);
            }
        }
        return resultado;
    }
    // prioridade >= limiar: hash so serve para igualdade, entao percorre a lista
    public List<Ocorrencia> filtrarPorPrioridadeMinima(List<Ocorrencia> todas, int limiar) {
        List<Ocorrencia> resultado = new ArrayList<>();
        for (Ocorrencia o : todas) {
            if (o.getPrioridade() >= limiar) {
                resultado.add(o);
            }
        }
        return resultado;
    }

    // mesma ideia: comparacao numerica, percorre a lista
    public List<Ocorrencia> filtrarPorPessoasEnvolvidas(List<Ocorrencia> todas, int minimo) {
        List<Ocorrencia> resultado = new ArrayList<>();
        for (Ocorrencia o : todas) {
            if (o.getPessoasEnvolvidas() >= minimo) {
                resultado.add(o);
            }
        }
        return resultado;
    }
    // regiao e tipo ja tem indice no ConsultaRapida, so delega
    public List<Ocorrencia> filtrarPorRegiao(String regiao) {
        return consultaRapida.buscarPorRegiao(regiao);
    }

    public List<Ocorrencia> filtrarPorTipo(String tipo) {
        return consultaRapida.buscarPorTipo(tipo);
    }

    // filtram uma lista ja filtrada, para combinar restricoes
    public List<Ocorrencia> filtrarPorRegiao(List<Ocorrencia> lista, String regiao) {
        String regiaoNormalizada = normalizar(regiao);
        List<Ocorrencia> resultado = new ArrayList<>();
        for (Ocorrencia o : lista) {
            if (normalizar(o.getRegiao()).equals(regiaoNormalizada)) {
                resultado.add(o);
            }
        }
        return resultado;
    }

    public List<Ocorrencia> filtrarPorTipo(List<Ocorrencia> lista, String tipo) {
        String tipoNormalizado = normalizar(tipo);
        List<Ocorrencia> resultado = new ArrayList<>();
        for (Ocorrencia o : lista) {
            if (normalizar(o.getTipo()).equals(tipoNormalizado)) {
                resultado.add(o);
            }
        }
        return resultado;
    }
}
