package modulos;
import estruturas.TabelaHash;
import estruturas.Trie;
import modelo.Ocorrencia;

import java.util.ArrayList;
import java.util.List;

public class ConsultaRapida {

    private TabelaHash<String, Ocorrencia> indicePorId = new TabelaHash<>(16);
    private Trie indicePorDescricao = new Trie();

    public void indexar(List<Ocorrencia> ocorrencias){
        for (Ocorrencia o: ocorrencias){
            indicePorId.inserir(o.getId(), o);
            indicePorDescricao.inserir(o.getDescricao(), o);
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

    //implementa por tipo e por região

}
