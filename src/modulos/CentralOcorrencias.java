package modulos;

import estruturas.TabelaHash;
import modelo.Ocorrencia;
import util.LeitorCSV;
import util.RegraTempo;
import util.RegraPessoas;
import util.RegraPrioridade;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;

public class CentralOcorrencias {
    private TabelaHash<String, Ocorrencia> tabela;
    private int proximoId;

    public CentralOcorrencias() {
        this.tabela = new TabelaHash<>(16);
        this.proximoId = 1;
    }

    public int carregarDados(String path) {
         ArrayList<Ocorrencia> lista = LeitorCSV.ler(path);

         for(int i = 0; i < lista.size(); i++) {
             Ocorrencia o = lista.get(i);
             tabela.inserir(o.getId(), o);
         }

         proximoId = lista.size() + 1;
         return  lista.size();
    }

    public Ocorrencia cadastrar(String tipo, String motivo, String descricao, String regiao, String endereco)
    {
        if (!("EMS".equalsIgnoreCase(tipo) || "TRAFFIC".equalsIgnoreCase(tipo) || "FIRE".equalsIgnoreCase(tipo))) {
            System.out.println("Tipo inválido: " + tipo);
            return null; // sinaliza que não cadastrou
        }

        String id = String.valueOf(proximoId);
        proximoId++;

        String dataHora = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        int prioridade = RegraPrioridade.calcular(tipo, motivo);
        int pessoas = RegraPessoas.calcular(motivo);
        int tempo = RegraTempo.calcular(tipo, prioridade);

        Ocorrencia o = new Ocorrencia(id, tipo.trim().toUpperCase(), descricao.trim().toUpperCase(), regiao.trim().toUpperCase(), prioridade, dataHora, "PENDENTE", pessoas, tempo, null , endereco.trim().toUpperCase(), motivo.trim().toUpperCase(), "NÃO ATRIBUÍDA");

        tabela.inserir(id, o);

        return o;
    }

    public boolean alterarStatus(int id, String status) {
        String idBuscar = String.valueOf(id);
        status = status.trim().toUpperCase();

        if (!("Pendente".equalsIgnoreCase(status) || "Em andamento".equalsIgnoreCase(status) || "Finalizado".equalsIgnoreCase(status))) {
            System.out.println("Status inválido: " + status);
            return false;
        }

        Ocorrencia o = tabela.buscar(idBuscar);

        if(o == null)
        {
            System.out.println("Ocorrencia não encontrada.");
            return false;
        }

        o.setStatus(status);

        return true;
    }

    public boolean alterarEquipe(int id, String equipe) {
        String idBuscar = String.valueOf(id);

        Ocorrencia o = tabela.buscar(idBuscar);

        if(o == null)
        {
            System.out.println("Ocorrencia não encontrada.");
            return false;
        }

        o.setEquipe(equipe.trim().toUpperCase());

        return true;
    }

    public boolean alterarPrioridade(int id, int prioridade) {
        String idBuscar = String.valueOf(id);

        if(prioridade > 5 || prioridade < 1) {
            System.out.println("Prioridade inválida: " + prioridade);
            System.out.println("Prioridade deve estar dentro do seguinte alcance: [1, 5]");
            return false;
        }

        Ocorrencia o = tabela.buscar(idBuscar);

        if(o == null)
        {
            System.out.println("Ocorrencia não encontrada.");
            return false;
        }

        int tempo = RegraTempo.calcular(o.getTipo(), prioridade);

        o.setPrioridade(prioridade);
        o.setTempoEstimado(tempo);

        return true;
    }

    public boolean remover(int id) {
        String idBuscar = String.valueOf(id);

        return tabela.remover(idBuscar);
    }

    public List<Ocorrencia> listar() {
        List<Ocorrencia> lista = tabela.listarTodos();

        return lista;
    }

    public Ocorrencia consultar(int id) {
        String idBuscar = String.valueOf(id);

        return tabela.buscar(idBuscar);
    }


}
