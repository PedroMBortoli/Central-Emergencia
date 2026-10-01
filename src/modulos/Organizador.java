package modulos;

import estruturas.TabelaHash;
import modelo.Ocorrencia;
import java.util.List;
import java.util.ArrayList;

public class Organizador {
    private TabelaHash<String, List<String>> indicePorStatus;

    public Organizador() {
        this.indicePorStatus = new TabelaHash<>(16);
    }

    public void adicionar(Ocorrencia o) { ... }      // indexa status no cadastro
    public void remover(String id, String status) { ... } // remove do índice
    public void atualizarStatus(String id, String statusAntigo, String statusNovo) { ... }

    public List<Ocorrencia> filtrarPorStatus(String status) { ... }
    public List<Ocorrencia> filtrarPorPrioridadeMinima(List<Ocorrencia> todas, int limiar) { ... }
    public List<Ocorrencia> filtrarPorPessoasEnvolvidas(List<Ocorrencia> todas, int minimo) { ... }
    public List<Ocorrencia> filtrarPorRegiao(ConsultaRapida consulta, String regiao) { ... } // delega
    public List<Ocorrencia> filtrarPorTipo(ConsultaRapida consulta, String tipo) { ... }       // delega
}