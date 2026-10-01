package modulos;
import estruturas.TabelaHash;
import estruturas.Trie;
import modelo.Ocorrencia;

import java.util.ArrayList;
import java.util.List;

public class ConsultaRapida {

    private TabelaHash<String, Ocorrencia> indicePorId = new TabelaHash<>(16);
    private Trie indicePorDescricao = new Trie();
    private TabelaHash<String, List<String>> indicePorTipo = new TabelaHash<>(16);
    private TabelaHash<String, List<String>> indicePorRegiao = new TabelaHash<>(16);

    public void indexar(List<Ocorrencia> ocorrencias){
        for (Ocorrencia o: ocorrencias){
            indicePorId.inserir(o.getId(), o);
            indicePorDescricao.inserir(o.getDescricao(), o);
            adicionarEmLista(indicePorTipo, o.getTipo(), o.getId());
            adicionarEmLista(indicePorRegiao, o.getRegiao(), o.getId());
        }
    }

    public Ocorrencia buscarPorId(String id){
        return indicePorId.buscar(id);

    }

    public List<Ocorrencia> buscarPorprefixoDescricao(String prefixo){
        List<Object> resultadoBruto = indicePorDescricao.buscarPorPrefixo(prefixo);
        List<Ocorrencia> resultado = new ArrayList<>();
        for (Object obj: resultadoBruto){
            resultado.add((Ocorrencia) obj);
        }
        return resultado;
    }

    public List<Ocorrencia> buscarPorTipo(String tipo){
        List<String> ids = indicePorTipo.buscar(normalizar(tipo));
        return resolverIds(ids);
    }

    public List<Ocorrencia> buscarPorRegiao(String regiao){
        List<String> ids = indicePorRegiao.buscar(normalizar(regiao));
        return resolverIds(ids);
    }

    // insere o id na lista de ids da categoria (tipo/regiao), criando a lista
    // se ainda nao existir uma para aquela chave
    private void adicionarEmLista(TabelaHash<String, List<String>> indice, String chave, String id) {
        List<String> lista = indice.buscar(normalizar(chave));
        if (lista == null) {
            lista = new ArrayList<>();
            indice.inserir(normalizar(chave), lista);
        }
        lista.add(id);
    }

    // troca uma lista de ids pelas ocorrencias de verdade, buscando cada uma no indice por id
    private List<Ocorrencia> resolverIds(List<String> ids) {
        List<Ocorrencia> resultado = new ArrayList<>();
        if (ids == null) {
            return resultado; // categoria nao encontrada: lista vazia, nao null
        }
        for (String id : ids) {
            Ocorrencia o = indicePorId.buscar(id);
            if (o != null) {
                resultado.add(o);
            }
        }
        return resultado;
    }

    // mesmo padrao de normalizacao usado no cadastro: sem espaco nas pontas, maiusculo
    private String normalizar(String valor) {
        return valor == null ? "" : valor.trim().toUpperCase();
    }

}
