package estruturas;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class TabelaHash <K, V> {

    //Definição da estrutura da tabela
    private static class Par<K, V> {

        K chave;
        V valor;

        Par(K chave, V valor){
            this.chave = chave;
            this.valor = valor;
        }
    }

    private LinkedList<Par<K,V>>[] buckets;
    private int capacidade;
    private int tamanhoTabela;

    public TabelaHash(int capacidadeInicial){
        this.capacidade = capacidadeInicial;
        this.buckets = new LinkedList[capacidade];

    }

    //Métodos do hash code
    private int calculaHash (K chave){
        String s = chave.toString();
        int hash = 0;
            for (int i = 0; i < s.length(); i++){

                //31 pois é primo e gerando resutltados bastante aleatório mesmo para palavras parecidas
                //chatAt(i) retorna o caracter da String que ocupa a posição i;
                hash = 31 * hash + s.charAt(i);
            }

        return hash;
    }

    private int indice (K chave){
        int hash = calculaHash(chave);

        //Math.abs(hash) retorna o valor em modulo de hash - nunca tera codigo negativo
        return Math.abs(hash) % capacidade;
    }

    //METODOS DE MANIPULAÇÃO DA ESTRUTURA DA TABELA

    public void inserir (K chave, V valor) {

        int idx = indice(chave);

        //caso de ser uma posição vazia - insere o objeto nela e prepara uma lista encadeada a partir dessa posição
        if (buckets[idx] == null) {
            buckets[idx] = new LinkedList<>();
        }

        //para cada par definido no vetor
        for (Par<K, V> par : buckets[idx]) {

            if (par.chave.equals(chave)) {
                par.valor = valor;
                return;
            }
        }
        buckets[idx].add(new Par<>(chave, valor));
        tamanhoTabela++;

        if((double) tamanhoTabela / capacidade > 0.75){
            rehash();
        }
    }

    public boolean remover (K chave){

        int idx = indice(chave);

        //se nao tiver nada nao precisa remover
        if (buckets[idx] == null){
            return false;
        }

        //percorre a lista e remove apenas o objeto Par<K, V> cuja a chave corresponde a passada no parâmetro do metodo
        boolean removeu = buckets[idx].removeIf(par -> par.chave.equals(chave));
        if (removeu){
            tamanhoTabela--;
        }
        return removeu;
    }

    public V buscar(K chave){
        int idx = indice(chave);

        if(buckets[idx] == null){
            return null;
        }

        for (Par<K, V> par : buckets[idx]){
            if(par.chave.equals(chave)){
                return par.valor;
            }
        }
        return null;
    }

    //percorre todos os buckets e devolve todos os valores guardados na tabela
    //(retorna List<V> em vez de V[] porque Java nao permite criar array de tipo generico)
    public List<V> listarTodos(){
        List<V> resultado = new ArrayList<>();

        for (LinkedList<Par<K,V>> bucket : buckets){
            if (bucket != null){
                for (Par<K,V> par : bucket){
                    resultado.add(par.valor);
                }
            }
        }
        return resultado;
    }

    public int getTamanho(){
        return tamanhoTabela;
    }

    private void rehash(){

        //referencia para o vetor de dados antigos
        LinkedList<Par<K,V>>[] antigos = buckets;
        capacidade = capacidade*2;
        buckets = new LinkedList[capacidade];
        tamanhoTabela = 0;

        for(LinkedList<Par<K,V>> bucket : antigos){
            if(bucket != null){
                for(Par <K,V> par : bucket){
                    inserir(par.chave, par.valor); //reinsere toda a tabela com nova capacidade
                }
            }
        }
    }
}


