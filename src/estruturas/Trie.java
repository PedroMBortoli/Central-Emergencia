package estruturas;

import java.util.*;

public class Trie {

    //Classe do nó
    private static class No {
        Map<Character, No> filhos = new HashMap<>();
        boolean fimDePalavra = false;
        List<Object> valores = new LinkedList<>();
    }

    private No raiz;

    public Trie(){
        raiz = new No();
    }

    // inserir, buscar e buscarPorPrefixo serão adicionados depois

    public void inserir(String chave, Object valor){
        String chaveNormalizada = chave.toUpperCase();
        No atual = raiz;

        for (int i = 0; i < chaveNormalizada.length(); i++){
            char c = chaveNormalizada.charAt(i);

            No proximo = atual.filhos.get(c);
            if (proximo == null){
                proximo = new No();
                atual.filhos.put(c, proximo);
            }

            atual = proximo;
        }

        atual.fimDePalavra = true;
        atual.valores.add(valor);
    }

    private  void coletarValores (No no, List<Object> resultado){
        if(no.fimDePalavra){
            resultado.addAll(no.valores);
        }

        for(No filho : no.filhos.values()){
            coletarValores(filho, resultado);
        }

    }

    public List<Object> buscarPorPrefixo(String prefixo){
        String prefixoNormalizado = prefixo.toUpperCase();
        No atual = raiz;

        for(int i = 0; i < prefixoNormalizado.length(); i++){
            char c = prefixoNormalizado.charAt(i);
            No proximo = atual.filhos.get(c);

            //caso do prefixo nao existir na arvore
            if(proximo == null){
                return new LinkedList<>();

            }
            atual = proximo;

        }

        List<Object> resultado = new LinkedList<>();
        coletarValores (atual, resultado);
        return  resultado;

    }

    // Remove apenas o 'valor' indicado da chave (nao apaga a chave inteira), porque
    // duas ocorrencias diferentes podem ter a mesma descricao - cada uma foi guardada
    // como um item separado na lista 'valores' do no final da palavra.
    // Devolve true se algo foi de fato removido.
    public boolean remover(String chave, Object valor){
        // a recursao devolve, em cada nivel, se o NO PODE SER PODADO do pai - isso nao
        // e a mesma coisa que "o valor foi removido", entao o resultado de verdade que
        // interessa pra quem chamou o metodo e guardado nesse array de 1 posicao (um jeito
        // simples de ter "dois retornos" de uma funcao recursiva em Java).
        boolean[] removido = new boolean[1];
        removerRecursivo(raiz, chave.toUpperCase(), 0, valor, removido);
        return removido[0];
    }

    // Percorre a trie caractere a caractere (igual o inserir/buscar) e, na volta da
    // recursao, poda (remove) os nos que ficaram sem uso - sem marcar fim de palavra
    // e sem nenhum filho - para nao deixar "galhos mortos" acumulando na arvore.
    // Retorna true quando o 'no' atual pode ser removido do pai (ficou vazio);
    // grava em removido[0] se o valor pedido foi de fato encontrado e removido.
    private boolean removerRecursivo(No no, String chaveNormalizada, int indice, Object valor, boolean[] removido){

        if (indice == chaveNormalizada.length()){
            //chegou no fim da palavra: so remove se ela realmente estava marcada como existente
            if (!no.fimDePalavra){
                return false; //chave nunca foi inserida - nada a fazer
            }

            removido[0] = no.valores.remove(valor); //remove so essa ocorrencia (por identidade), preserva as outras

            if (no.valores.isEmpty()){
                no.fimDePalavra = false; //nao sobrou nenhuma ocorrencia para essa chave
            }

            //esse no so pode sumir do pai se nao representa mais palavra nenhuma e nao tem filhos
            //(se for prefixo de outra palavra maior, ele continua tendo filho e tem que ficar)
            return !no.fimDePalavra && no.filhos.isEmpty();
        }

        char c = chaveNormalizada.charAt(indice);
        No proximo = no.filhos.get(c);

        if (proximo == null){
            return false; //caminho nao existe na arvore - chave nunca foi inserida
        }

        boolean filhoFicouVazio = removerRecursivo(proximo, chaveNormalizada, indice + 1, valor, removido);

        if (filhoFicouVazio){
            no.filhos.remove(c);
        }

        //depois de possivelmente remover o filho, verifica se este no tambem ficou vazio
        return !no.fimDePalavra && no.filhos.isEmpty();
    }

}
