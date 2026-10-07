package modulos;

import estruturas.TabelaHash;
import modelo.Ocorrencia;
import util.LeitorCSV;
import util.RegraPessoas;
import util.RegraTempo;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class Integridade {
    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private CentralOcorrencias central;

    public Integridade(CentralOcorrencias central) {
        this.central = central;
    }

    public boolean foiAlterada(Ocorrencia o) {
        return !o.getHashIntegridade().equals(o.calcularHash());
    }

    public Boolean foiAlterada(int id) {
        Ocorrencia o = central.consultar(id);

        if (o == null) {
            return null;
        }

        return foiAlterada(o);
    }

    public List<Ocorrencia> verificarTodas() {
        List<Ocorrencia> alteradas = new ArrayList<>();

        for (Ocorrencia o : central.listar()) {
            if (foiAlterada(o)) {
                alteradas.add(o);
            }
        }

        return alteradas;
    }

    public List<String> validar(Ocorrencia o) {
        List<String> problemas = new ArrayList<>();

        if (vazio(o.getTipo()) || vazio(o.getMotivo()) || vazio(o.getDescricao()) || vazio(o.getRegiao())
                || vazio(o.getEndereco()) || vazio(o.getDataHora()) || vazio(o.getStatus()) || vazio(o.getEquipe())) {
            problemas.add("campo obrigatório vazio");
        }

        if (!tipoValido(o.getTipo())) {
            problemas.add("tipo inválido: " + o.getTipo());
        }

        if (!statusValido(o.getStatus())) {
            problemas.add("status inválido: " + o.getStatus());
        }

        if (o.getPrioridade() < 1 || o.getPrioridade() > 5) {
            problemas.add("prioridade fora de 1 a 5: " + o.getPrioridade());
        } else if (o.getTempoEstimado() != RegraTempo.calcular(o.getTipo(), o.getPrioridade())) {
            problemas.add("tempo estimado " + o.getTempoEstimado() + " não corresponde à regra ("
                    + RegraTempo.calcular(o.getTipo(), o.getPrioridade()) + ")");
        }

        if (o.getPessoasEnvolvidas() != RegraPessoas.calcular(o.getMotivo())) {
            problemas.add("pessoas envolvidas " + o.getPessoasEnvolvidas() + " não corresponde à regra ("
                    + RegraPessoas.calcular(o.getMotivo()) + ")");
        }

        LocalDateTime data = lerData(o.getDataHora());
        if (data == null) {
            problemas.add("data/hora em formato inválido: " + o.getDataHora());
        } else if (data.isAfter(LocalDateTime.now())) {
            problemas.add("data/hora no futuro: " + o.getDataHora());
        }

        return problemas;
    }

    public List<Ocorrencia> verificarInconsistentes() {
        List<Ocorrencia> inconsistentes = new ArrayList<>();

        for (Ocorrencia o : central.listar()) {
            if (!validar(o).isEmpty()) {
                inconsistentes.add(o);
            }
        }

        return inconsistentes;
    }

    public List<List<Ocorrencia>> detectarDuplicatas() {
        TabelaHash<String, List<Ocorrencia>> porConteudo = new TabelaHash<>(16);

        for (Ocorrencia o : central.listar()) {
            adicionarNoGrupo(porConteudo, o.calcularHash(), o);
        }

        return gruposComMaisDeUm(porConteudo);
    }

    public List<List<Ocorrencia>> detectarDuplicatasDivergentes() {
        TabelaHash<String, List<Ocorrencia>> porEvento = new TabelaHash<>(16);

        for (Ocorrencia o : central.listar()) {
            adicionarNoGrupo(porEvento, chaveEvento(o), o);
        }

        List<List<Ocorrencia>> divergentes = new ArrayList<>();
        for (List<Ocorrencia> grupo : gruposComMaisDeUm(porEvento)) {
            if (temConteudosDiferentes(grupo)) {
                divergentes.add(grupo);
            }
        }

        return divergentes;
    }

    public ComparacaoArquivo compararComArquivo(String caminho) {
        ComparacaoArquivo resultado = new ComparacaoArquivo();

        for (Ocorrencia doArquivo : LeitorCSV.ler(caminho)) {
            Ocorrencia armazenada = central.consultar(Integer.parseInt(doArquivo.getId()));

            if (armazenada == null) {
                resultado.adicionarAusente(doArquivo);
                continue;
            }

            String hashArquivo = doArquivo.getHashIntegridade();
            String hashCadastro = armazenada.getHashIntegridade();
            String hashAtual = armazenada.calcularHash();

            boolean arquivoIgualCadastro = hashArquivo.equals(hashCadastro);
            boolean memoriaIntegra = hashCadastro.equals(hashAtual);

            if (arquivoIgualCadastro && memoriaIntegra) {
                resultado.contarIgual();
            } else if (arquivoIgualCadastro) {
                resultado.adicionarConflito(new ComparacaoArquivo.Conflito(armazenada, doArquivo,
                        "registro alterado na memória após a carga"));
            } else if (memoriaIntegra) {
                resultado.adicionarConflito(new ComparacaoArquivo.Conflito(armazenada, doArquivo,
                        "arquivo modificado após a carga"));
            } else {
                resultado.adicionarConflito(new ComparacaoArquivo.Conflito(armazenada, doArquivo,
                        "registro alterado na memória e no arquivo"));
            }
        }

        return resultado;
    }

    private String chaveEvento(Ocorrencia o) {
        return o.getTipo() + "|" + o.getDataHora() + "|" + o.getEndereco();
    }

    private void adicionarNoGrupo(TabelaHash<String, List<Ocorrencia>> grupos, String chave, Ocorrencia o) {
        List<Ocorrencia> grupo = grupos.buscar(chave);

        if (grupo == null) {
            grupo = new ArrayList<>();
            grupos.inserir(chave, grupo);
        }

        grupo.add(o);
    }

    private List<List<Ocorrencia>> gruposComMaisDeUm(TabelaHash<String, List<Ocorrencia>> grupos) {
        List<List<Ocorrencia>> resultado = new ArrayList<>();

        for (List<Ocorrencia> grupo : grupos.listarTodos()) {
            if (grupo.size() > 1) {
                resultado.add(grupo);
            }
        }

        return resultado;
    }

    private boolean temConteudosDiferentes(List<Ocorrencia> grupo) {
        String primeiro = grupo.get(0).calcularHash();

        for (Ocorrencia o : grupo) {
            if (!o.calcularHash().equals(primeiro)) {
                return true;
            }
        }

        return false;
    }

    private boolean vazio(String valor) {
        return valor == null || valor.trim().isEmpty();
    }

    private boolean tipoValido(String tipo) {
        return "EMS".equals(tipo) || "FIRE".equals(tipo) || "TRAFFIC".equals(tipo);
    }

    private boolean statusValido(String status) {
        return "PENDENTE".equals(status) || "EM ANDAMENTO".equals(status) || "FINALIZADO".equals(status);
    }

    private LocalDateTime lerData(String dataHora) {
        if (dataHora == null) {
            return null;
        }

        try {
            return LocalDateTime.parse(dataHora, FORMATO_DATA);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}
