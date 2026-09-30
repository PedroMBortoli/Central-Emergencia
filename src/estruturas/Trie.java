package estruturas;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Trie {
    private Object raiz;

    //Classe do nó
    private static class No {
        Map<Character, No> filhos = new HashMap<>();
        boolean fimDePalavra = false;
        List<Object> valores = new LinkedList<>();
    }

    
    // inserir, buscar e buscarPorPrefixo serão adicionados depois
}
