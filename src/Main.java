import modelo.Ocorrencia;
import modulos.CentralOcorrencias;
import modulos.ComparacaoArquivo;
import modulos.Integridade;
import modulos.OrdenadorAtendimento;
import modulos.OtimizacaoRecursos;
import modulos.PlanejamentoOperacional;
import modulos.ResumoSelecao;
import modulos.RedeEmergencias;
import estruturas.GrafoDirecionado;
import estruturas.GrafoPonderado;

import java.util.List;
import java.util.Scanner;

public class Main {

    private static final String ARQUIVO_DADOS = "data/ocorrencias.csv";
    private static final String ARQUIVO_TESTE_INTEGRIDADE = "data/ocorrencias_teste_integridade.csv";
    private static final String ARQUIVO_DEPENDENCIAS = "data/dependencias.csv";
    private static final String ARQUIVO_REDE = "data/rede.csv";

    private static final Scanner scanner = new Scanner(System.in);
    private static final OrdenadorAtendimento ordenador = new OrdenadorAtendimento();
    private static final PlanejamentoOperacional planejamento = new PlanejamentoOperacional();
    private static final OtimizacaoRecursos otimizacao = new OtimizacaoRecursos();
    private static final ResumoSelecao resumo = new ResumoSelecao();
    private static final RedeEmergencias rede = new RedeEmergencias();

    // ultima selecao feita (usada no resumo do modulo 9)
    private static List<Ocorrencia> ultimaSelecao;

    private static CentralOcorrencias central;
    private static Integridade integridade;
    private static String arquivoCarregado;

    public static void main(String[] args) {
        System.out.println("Central de Emergências: Operação Resgate");

        carregarBase(ARQUIVO_DADOS);
        if (central == null) {
            System.out.println("Não foi possível iniciar sem dados. Execute o programa a partir da raiz do projeto.");
            return;
        }

        int dependencias = planejamento.carregarDependencias(ARQUIVO_DEPENDENCIAS);
        System.out.println(dependencias + " dependências carregadas de " + ARQUIVO_DEPENDENCIAS + ".");

        int conexoes = rede.carregarRede(ARQUIVO_REDE);
        System.out.println(rede.getQuantidadeVertices() + " pontos e " + conexoes
                + " conexões carregados de " + ARQUIVO_REDE + ".");

        boolean sair = false;
        while (!sair) {
            System.out.println();
            System.out.println("===== MENU PRINCIPAL =====");
            System.out.println("1 - Modo Consulta Rápida (módulos 1, 2 e 3)");
            System.out.println("2 - Modo Investigação (módulos 4 e 6)");
            System.out.println("3 - Modo Planejamento Estratégico (módulos 5, 7 e 9)");
            System.out.println("4 - Modo Logística (módulo 8)");
            System.out.println("0 - Sair");
            System.out.print("Escolha uma opção: ");

            switch (lerOpcao()) {
                case 1 -> modoConsultaRapida();
                case 2 -> modoInvestigacao();
                case 3 -> modoPlanejamentoEstrategico();
                case 4 -> modoLogistica();
                case 0 -> sair = true;
                default -> System.out.println("Opção inválida.");
            }
        }

        System.out.println("Encerrando o sistema.");
    }

    // ===================== MODOS DE INTERAÇÃO =====================
    // cada modo junta os menus dos modulos com a mesma finalidade

    private static void modoConsultaRapida() {
        System.out.println();
        System.out.println("=== Modo Consulta Rápida ===");
        System.out.println("1 - Central de Ocorrências (cadastro, consulta, alteração, remoção, listagem)");
        System.out.println("2 - Busca rápida (id, prefixo da descrição, tipo, região)");
        System.out.println("3 - Filtros (status, prioridade, pessoas, região, tipo)");
        System.out.print("Escolha uma opção: ");

        switch (lerOpcao()) {
            case 1 -> menuCentralOcorrencias();
            case 2 -> menuConsultaRapida();
            case 3 -> menuOrganizador();
            default -> System.out.println("Opção inválida.");
        }
    }

    private static void modoInvestigacao() {
        System.out.println();
        System.out.println("=== Modo Investigação ===");
        System.out.println("1 - Integridade dos registros");
        System.out.println("2 - Dependências entre atividades (planejamento operacional)");
        System.out.println("3 - Detectar ciclos no planejamento de atividades");
        System.out.print("Escolha uma opção: ");

        switch (lerOpcao()) {
            case 1 -> menuIntegridade();
            case 2 -> menuPlanejamentoOperacional();
            case 3 -> detectarCiclos();
            default -> System.out.println("Opção inválida.");
        }
    }

    private static void modoPlanejamentoEstrategico() {
        System.out.println();
        System.out.println("=== Modo Planejamento Estratégico ===");
        System.out.println("1 - Ordem de atendimento e seleção gulosa (módulo 5)");
        System.out.println("2 - Selecionar ocorrências com programação dinâmica (módulo 7)");
        System.out.println("3 - Comparar cenários de atendimento");
        System.out.println("4 - Resumo e recursos da última seleção (módulo 9)");
        System.out.print("Escolha uma opção: ");

        switch (lerOpcao()) {
            case 1 -> menuOperacaoResgate();
            case 2 -> otimizarSelecao();
            case 3 -> compararCenarios();
            case 4 -> mostrarResumoSelecao();
            default -> System.out.println("Opção inválida.");
        }
    }

    private static void modoLogistica() {
        System.out.println();
        System.out.println("=== Modo Logística ===");
        System.out.println("1 - Caminho mínimo entre dois pontos");
        System.out.println("2 - Árvore geradora mínima da rede");
        System.out.println("3 - Listar conexões de um ponto (vizinhos e custos)");
        System.out.println("4 - Listar todos os pontos da rede");
        System.out.println("5 - Caminho mínimo até a região de uma ocorrência");
        System.out.print("Escolha uma opção: ");

        switch (lerOpcao()) {
            case 1 -> mostrarCaminhoMinimo();
            case 2 -> mostrarArvoreGeradoraMinima();
            case 3 -> listarConexoesDoPonto();
            case 4 -> listarPontosDaRede();
            case 5 -> caminhoAteOcorrencia();
            default -> System.out.println("Opção inválida.");
        }
    }

    // ===================== MÓDULO 8 / REDE DE EMERGÊNCIAS =====================

    private static void mostrarCaminhoMinimo() {
        String origem = lerTexto("Ponto de origem: ").trim().toUpperCase();
        String destino = lerTexto("Ponto de destino: ").trim().toUpperCase();

        RedeEmergencias.ResultadoCaminho resultado = rede.caminhoMinimo(origem, destino);
        if (resultado == null) {
            System.out.println("Ponto de origem ou de destino não existe na rede.");
            return;
        }
        if (!resultado.existeCaminho()) {
            System.out.println("Não existe caminho entre " + origem + " e " + destino + " na rede atual.");
            return;
        }
        System.out.println("Caminho: " + String.join(" -> ", resultado.getCaminho()));
        System.out.println("Custo total: " + resultado.getCustoTotal());
    }

    private static void mostrarArvoreGeradoraMinima() {
        RedeEmergencias.ResultadoAGM resultado = rede.arvoreGeradoraMinima();

        System.out.println("Conexões escolhidas (" + resultado.getConexoes().size() + "):");
        for (GrafoPonderado.Aresta aresta : resultado.getConexoes()) {
            System.out.println(rede.nomeDoPonto(aresta.getOrigem()) + " - " + rede.nomeDoPonto(aresta.getDestino())
                    + " | custo " + aresta.getCusto());
        }
        System.out.println("Custo total: " + resultado.getCustoTotal());

        if (resultado.ehArvoreUnica()) {
            System.out.println("A rede é conexa: uma única árvore liga todos os "
                    + rede.getQuantidadeVertices() + " pontos.");
        } else {
            System.out.println("A rede não é conexa: o resultado é uma floresta com "
                    + resultado.getComponentes() + " componentes, não uma única árvore.");
        }
    }

    private static void listarConexoesDoPonto() {
        String ponto = lerTexto("Ponto: ").trim().toUpperCase();
        if (!rede.contemPonto(ponto)) {
            System.out.println("Ponto não encontrado na rede.");
            return;
        }
        List<GrafoPonderado.Aresta> conexoes = rede.getConexoes(ponto);
        if (conexoes.isEmpty()) {
            System.out.println(ponto + " não tem nenhuma conexão cadastrada (ponto isolado).");
            return;
        }
        System.out.println(conexoes.size() + " conexão(ões) de " + ponto + ":");
        for (GrafoPonderado.Aresta aresta : conexoes) {
            System.out.println("  " + rede.nomeDoPonto(aresta.getDestino()) + " | custo " + aresta.getCusto());
        }
    }

    private static void listarPontosDaRede() {
        List<String> pontos = rede.getPontos();
        System.out.println(pontos.size() + " ponto(s) na rede:");
        for (String ponto : pontos) {
            System.out.println("  " + ponto);
        }
    }

    // usa a regiao da ocorrencia como destino do Dijkstra
    private static void caminhoAteOcorrencia() {
        int id = lerInteiro("Id da ocorrência: ");
        Ocorrencia o = central.consultar(id);
        if (o == null) {
            System.out.println("Ocorrência não encontrada.");
            return;
        }
        String regiao = o.getRegiao();
        System.out.println("Ocorrência " + id + " | " + o.getTipo() + " | " + o.getMotivo() + " | região " + regiao);
        if (!rede.contemPonto(regiao)) {
            System.out.println("A região desta ocorrência não está cadastrada na rede logística.");
            return;
        }

        String origem = lerTexto("Ponto de partida (base/hospital/posto): ").trim().toUpperCase();
        RedeEmergencias.ResultadoCaminho resultado = rede.caminhoMinimo(origem, regiao);
        if (resultado == null) {
            System.out.println("Ponto de partida não existe na rede.");
            return;
        }
        if (!resultado.existeCaminho()) {
            System.out.println("Não existe caminho até " + regiao + " na rede atual.");
            return;
        }
        System.out.println("Caminho: " + String.join(" -> ", resultado.getCaminho()));
        System.out.println("Custo total: " + resultado.getCustoTotal());
    }

    // ===================== MÓDULO 1 — CENTRAL DE OCORRÊNCIAS =====================

    private static void menuCentralOcorrencias() {
        System.out.println();
        System.out.println("--- Central de Ocorrências ---");
        System.out.println("1 - Cadastrar nova ocorrência");
        System.out.println("2 - Consultar ocorrência por id");
        System.out.println("3 - Alterar status");
        System.out.println("4 - Alterar equipe responsável");
        System.out.println("5 - Alterar prioridade");
        System.out.println("6 - Remover ocorrência");
        System.out.println("7 - Listar todas as ocorrências");
        System.out.print("Escolha uma opção: ");

        switch (lerOpcao()) {
            case 1 -> cadastrar();
            case 2 -> consultarPorId();
            case 3 -> alterarStatus();
            case 4 -> alterarEquipe();
            case 5 -> alterarPrioridade();
            case 6 -> removerOcorrencia();
            case 7 -> listarTodas();
            default -> System.out.println("Opção inválida.");
        }
    }

    private static void cadastrar() {
        String tipo = lerTexto("Tipo (EMS, FIRE ou TRAFFIC): ");
        String motivo = lerTexto("Motivo: ");
        String descricao = lerTexto("Descrição: ");
        String regiao = lerTexto("Região: ");
        String endereco = lerTexto("Endereço: ");

        Ocorrencia o = central.cadastrar(tipo, motivo, descricao, regiao, endereco);
        if (o != null) {
            System.out.println("Ocorrência cadastrada com sucesso:");
            System.out.println(o);
        }
    }

    private static void consultarPorId() {
        int id = lerInteiro("Id da ocorrência: ");
        Ocorrencia o = central.consultar(id);
        if (o == null) {
            System.out.println("Ocorrência não encontrada.");
        } else {
            System.out.println(o);
        }
    }

    private static void alterarStatus() {
        int id = lerInteiro("Id da ocorrência: ");
        String status = lerTexto("Novo status (PENDENTE, EM ANDAMENTO ou FINALIZADO): ");
        boolean ok = central.alterarStatus(id, status);
        System.out.println(ok ? "Status alterado com sucesso." : "Não foi possível alterar o status.");
    }

    private static void alterarEquipe() {
        int id = lerInteiro("Id da ocorrência: ");
        String equipe = lerTexto("Nova equipe responsável: ");
        boolean ok = central.alterarEquipe(id, equipe);
        System.out.println(ok ? "Equipe alterada com sucesso." : "Não foi possível alterar a equipe.");
    }

    private static void alterarPrioridade() {
        int id = lerInteiro("Id da ocorrência: ");
        int prioridade = lerInteiro("Nova prioridade (1 a 5): ");
        boolean ok = central.alterarPrioridade(id, prioridade);
        System.out.println(ok ? "Prioridade alterada com sucesso." : "Não foi possível alterar a prioridade.");
    }

    private static void removerOcorrencia() {
        int id = lerInteiro("Id da ocorrência: ");
        boolean ok = central.remover(id);
        System.out.println(ok ? "Ocorrência removida com sucesso." : "Ocorrência não encontrada.");
    }

    private static void listarTodas() {
        imprimirResumo(central.listar());
    }

    // ===================== MÓDULO 2 / MODO CONSULTA RÁPIDA =====================

    private static void menuConsultaRapida() {
        System.out.println();
        System.out.println("--- Consulta Rápida ---");
        System.out.println("1 - Buscar por id");
        System.out.println("2 - Buscar por prefixo da descrição");
        System.out.println("3 - Buscar por tipo");
        System.out.println("4 - Buscar por região");
        System.out.print("Escolha uma opção: ");

        switch (lerOpcao()) {
            case 1 -> {
                String id = lerTexto("Id da ocorrência: ");
                Ocorrencia o = central.getConsultaRapida().buscarPorId(id);
                System.out.println(o == null ? "Ocorrência não encontrada." : o);
            }
            case 2 -> {
                String prefixo = lerTexto("Prefixo da descrição: ");
                imprimirResumo(central.getConsultaRapida().buscarPorprefixoDescricao(prefixo));
            }
            case 3 -> {
                String tipo = lerTexto("Tipo: ");
                imprimirResumo(central.getConsultaRapida().buscarPorTipo(tipo));
            }
            case 4 -> {
                String regiao = lerTexto("Região: ");
                imprimirResumo(central.getConsultaRapida().buscarPorRegiao(regiao));
            }
            default -> System.out.println("Opção inválida.");
        }
    }

    // ===================== MÓDULO 3 — ORGANIZAÇÃO DAS OCORRÊNCIAS =====================

    private static void menuOrganizador() {
        System.out.println();
        System.out.println("--- Organização das Ocorrências ---");
        System.out.println("1 - Filtrar por status");
        System.out.println("2 - Filtrar por prioridade mínima");
        System.out.println("3 - Filtrar por quantidade mínima de pessoas envolvidas");
        System.out.println("4 - Filtrar por região");
        System.out.println("5 - Filtrar por tipo");
        System.out.print("Escolha uma opção: ");

        switch (lerOpcao()) {
            case 1 -> {
                String status = lerTexto("Status (PENDENTE, EM ANDAMENTO ou FINALIZADO): ");
                imprimirResumo(central.getOrganizador().filtrarPorStatus(status));
            }
            case 2 -> {
                int limiar = lerInteiro("Prioridade mínima (1 a 5): ");
                imprimirResumo(central.getOrganizador().filtrarPorPrioridadeMinima(central.listar(), limiar));
            }
            case 3 -> {
                int minimo = lerInteiro("Quantidade mínima de pessoas envolvidas: ");
                imprimirResumo(central.getOrganizador().filtrarPorPessoasEnvolvidas(central.listar(), minimo));
            }
            case 4 -> {
                String regiao = lerTexto("Região: ");
                imprimirResumo(central.getOrganizador().filtrarPorRegiao(regiao));
            }
            case 5 -> {
                String tipo = lerTexto("Tipo: ");
                imprimirResumo(central.getOrganizador().filtrarPorTipo(tipo));
            }
            default -> System.out.println("Opção inválida.");
        }
    }

    // ===================== MÓDULO 4 / MODO INVESTIGAÇÃO =====================

    private static void menuIntegridade() {
        System.out.println();
        System.out.println("--- Investigação: Integridade dos Registros ---");
        System.out.println("1 - Verificar se uma ocorrência foi alterada");
        System.out.println("2 - Listar todas as ocorrências alteradas");
        System.out.println("3 - Listar ocorrências inconsistentes");
        System.out.println("4 - Detectar duplicatas (mesmo conteúdo)");
        System.out.println("5 - Detectar duplicatas divergentes (mesmo evento, conteúdo diferente)");
        System.out.println("6 - Comparar com o arquivo carregado (" + arquivoCarregado + ")");
        System.out.println("7 - Simular adulteração de um registro (demonstração)");
        System.out.println("8 - Carregar outra base de dados");
        System.out.println("9 - Detectar ciclos no planejamento de atividades");
        System.out.print("Escolha uma opção: ");

        switch (lerOpcao()) {
            case 1 -> {
                int id = lerInteiro("Id da ocorrência: ");
                Boolean alterada = integridade.foiAlterada(id);
                System.out.println(alterada == null ? "Ocorrência não encontrada."
                        : (alterada ? "A ocorrência foi alterada desde o cadastro." : "A ocorrência está íntegra."));
            }
            case 2 -> imprimirResumo(integridade.verificarTodas());
            case 3 -> imprimirResumo(integridade.verificarInconsistentes());
            case 4 -> imprimirGrupos(integridade.detectarDuplicatas());
            case 5 -> imprimirGrupos(integridade.detectarDuplicatasDivergentes());
            case 6 -> compararComArquivoCarregado();
            case 7 -> simularAdulteracao();
            case 8 -> trocarBase();
            case 9 -> detectarCiclos();
            default -> System.out.println("Opção inválida.");
        }
    }

    // compara sempre com o arquivo que foi carregado, senao os ids nao batem
    private static void compararComArquivoCarregado() {
        System.out.println("Comparando a memória com " + arquivoCarregado + "...");
        ComparacaoArquivo resultado = integridade.compararComArquivo(arquivoCarregado);

        System.out.println("Ocorrências íntegras: " + resultado.getIguais());
        System.out.println("Conflitos: " + resultado.getConflitos().size());
        for (ComparacaoArquivo.Conflito c : resultado.getConflitos()) {
            System.out.println("  Id " + c.getArmazenada().getId() + " - " + c.getDiagnostico());
        }
        System.out.println("Presentes no arquivo e ausentes na memória: " + resultado.getAusentesNaMemoria().size());
        for (Ocorrencia o : resultado.getAusentesNaMemoria()) {
            System.out.println("  Id " + o.getId() + " - removida ou perdida");
        }
    }

    // so pra demonstracao: muda o registro pelo setter, sem passar pela central
    private static void simularAdulteracao() {
        int id = lerInteiro("Id da ocorrência: ");
        Ocorrencia o = central.consultar(id);
        if (o == null) {
            System.out.println("Ocorrência não encontrada.");
            return;
        }

        System.out.println("ATENÇÃO: altera o registro diretamente, sem passar pela Central.");
        System.out.println("Use apenas para demonstrar a detecção de alterações e inconsistências.");
        System.out.println("1 - Região");
        System.out.println("2 - Descrição");
        System.out.println("3 - Endereço");
        System.out.println("4 - Motivo");
        System.out.println("5 - Prioridade (sem recalcular o tempo estimado)");
        System.out.println("6 - Status (sem validação)");
        System.out.print("Campo a adulterar: ");

        int campo = lerOpcao();
        switch (campo) {
            case 1 -> o.setRegiao(lerTexto("Nova região: ").trim().toUpperCase());
            case 2 -> o.setDescricao(lerTexto("Nova descrição: ").trim().toUpperCase());
            case 3 -> o.setEndereco(lerTexto("Novo endereço: ").trim().toUpperCase());
            case 4 -> o.setMotivo(lerTexto("Novo motivo: ").trim().toUpperCase());
            case 5 -> o.setPrioridade(lerInteiro("Nova prioridade: "));
            case 6 -> o.setStatus(lerTexto("Novo status: ").trim().toUpperCase());
            default -> {
                System.out.println("Opção inválida.");
                return;
            }
        }

        // campos 1 a 4 entram no hash, 5 e 6 so aparecem na validacao
        if (campo <= 4) {
            System.out.println("Registro " + id + " adulterado em um campo do hash. Detecte com as opções 1, 2 ou 6.");
        } else {
            System.out.println("Registro " + id + " adulterado fora do hash. Detecte com a opção 3 (inconsistentes).");
        }
    }

    private static void trocarBase() {
        System.out.println("Base atual: " + arquivoCarregado);
        System.out.println("1 - Base principal (" + ARQUIVO_DADOS + ")");
        System.out.println("2 - Base de teste de integridade (" + ARQUIVO_TESTE_INTEGRIDADE + ")");
        System.out.println("3 - Outro arquivo");
        System.out.print("Escolha uma opção: ");

        switch (lerOpcao()) {
            case 1 -> carregarBase(ARQUIVO_DADOS);
            case 2 -> carregarBase(ARQUIVO_TESTE_INTEGRIDADE);
            case 3 -> carregarBase(lerTexto("Caminho do arquivo: ").trim());
            default -> System.out.println("Opção inválida.");
        }
    }

    // cria uma central nova para o arquivo; se nao carregar nada, fica com a base antiga
    private static void carregarBase(String caminho) {
        CentralOcorrencias nova = new CentralOcorrencias();
        int carregadas = nova.carregarDados(caminho);

        if (carregadas == 0) {
            System.out.println("Nenhuma ocorrência carregada de " + caminho + ". A base atual foi mantida.");
            return;
        }

        central = nova;
        integridade = new Integridade(central);
        arquivoCarregado = caminho;
        // a selecao era da base antiga
        ultimaSelecao = null;
        System.out.println(carregadas + " ocorrências carregadas de " + caminho + ".");
    }

    // ===================== MÓDULO 5 / MODO OPERAÇÃO RESGATE =====================

    private static void menuOperacaoResgate() {
        System.out.println();
        System.out.println("--- Operação Resgate: Ordem de Atendimento ---");
        System.out.println("1 - Montar a ordem de atendimento das ocorrências pendentes");
        System.out.println("2 - Selecionar ocorrências pendentes dentro de um limite de tempo");
        System.out.print("Escolha uma opção: ");

        int opcao = lerOpcao();
        if (opcao != 1 && opcao != 2) {
            System.out.println("Opção inválida.");
            return;
        }

        List<Ocorrencia> candidatas = escolherCandidatas();
        if (candidatas.isEmpty()) {
            System.out.println("Nenhuma ocorrência pendente atende às restrições.");
            return;
        }

        if (opcao == 1) {
            ordenarCandidatas(candidatas);
        } else {
            selecionarCandidatas(candidatas);
        }
    }

    // comeca com as pendentes e aplica as restricoes escolhidas, uma em cima da outra
    private static List<Ocorrencia> escolherCandidatas() {
        List<Ocorrencia> candidatas = central.getOrganizador().filtrarPorStatus("PENDENTE");
        String restricoes = "";

        while (true) {
            System.out.println();
            System.out.println("Candidatas: " + candidatas.size() + " ocorrência(s) pendente(s)"
                    + (restricoes.isEmpty() ? "" : " |" + restricoes));
            System.out.println("Adicionar restrição:");
            System.out.println("1 - Região");
            System.out.println("2 - Tipo");
            System.out.println("3 - Prioridade mínima");
            System.out.println("4 - Quantidade mínima de pessoas envolvidas");
            System.out.println("0 - Continuar sem mais restrições");
            System.out.print("Escolha uma opção: ");

            switch (lerOpcao()) {
                case 1 -> {
                    String regiao = lerTexto("Região: ").trim().toUpperCase();
                    candidatas = central.getOrganizador().filtrarPorRegiao(candidatas, regiao);
                    restricoes += " região " + regiao;
                }
                case 2 -> {
                    String tipo = lerTexto("Tipo (EMS, FIRE ou TRAFFIC): ").trim().toUpperCase();
                    candidatas = central.getOrganizador().filtrarPorTipo(candidatas, tipo);
                    restricoes += " tipo " + tipo;
                }
                case 3 -> {
                    int prioridade = lerInteiro("Prioridade mínima (1 a 5): ");
                    candidatas = central.getOrganizador().filtrarPorPrioridadeMinima(candidatas, prioridade);
                    restricoes += " prioridade >= " + prioridade;
                }
                case 4 -> {
                    int pessoas = lerInteiro("Quantidade mínima de pessoas: ");
                    candidatas = central.getOrganizador().filtrarPorPessoasEnvolvidas(candidatas, pessoas);
                    restricoes += " pessoas >= " + pessoas;
                }
                case 0 -> {
                    return candidatas;
                }
                default -> System.out.println("Opção inválida.");
            }
        }
    }

    private static void ordenarCandidatas(List<Ocorrencia> candidatas) {
        System.out.println("Critério de ordenação:");
        System.out.println("1 - Prioridade (desempate: mais pessoas, depois menor tempo)");
        System.out.println("2 - Pessoas envolvidas (desempate: maior prioridade, depois menor tempo)");
        System.out.println("3 - Menor tempo estimado (desempate: maior prioridade, depois mais pessoas)");
        System.out.print("Escolha uma opção: ");

        switch (lerOpcao()) {
            case 1 -> imprimirResumo(ordenador.ordenarPorPrioridade(candidatas));
            case 2 -> imprimirResumo(ordenador.ordenarPorPessoas(candidatas));
            case 3 -> imprimirResumo(ordenador.ordenarPorTempo(candidatas));
            default -> System.out.println("Opção inválida.");
        }
    }

    private static void selecionarCandidatas(List<Ocorrencia> candidatas) {
        int limiteTempo = lerInteiro("Tempo disponível (em minutos): ");
        List<Ocorrencia> selecionadas = ordenador.selecionarComLimiteDeTempo(candidatas, limiteTempo);

        imprimirResumo(selecionadas);

        int tempoUsado = 0;
        for (Ocorrencia o : selecionadas) {
            tempoUsado += o.getTempoEstimado();
        }
        System.out.println("Tempo usado: " + tempoUsado + " de " + limiteTempo + " minutos.");

        // guarda para o resumo (modulo 9)
        ultimaSelecao = selecionadas;
    }

    // ===================== MÓDULO 7 / OTIMIZAÇÃO DE RECURSOS =====================

    // candidatas + tempo disponivel
    private record Cenario(List<Ocorrencia> candidatas, int capacidade) {
    }

    // devolve null se nenhuma ocorrencia passou nas restricoes
    private static Cenario prepararCenario() {
        List<Ocorrencia> candidatas = escolherCandidatas();
        if (candidatas.isEmpty()) {
            System.out.println("Nenhuma ocorrência pendente atende às restrições.");
            return null;
        }
        return new Cenario(candidatas, lerCapacidade());
    }

    private static void otimizarSelecao() {
        Cenario c = prepararCenario();
        if (c == null) {
            return;
        }
        int criterio = lerCriterio();

        OtimizacaoRecursos.Resultado r = otimizacao.selecionar(c.candidatas(), c.capacidade(), criterio);
        ultimaSelecao = r.getSelecionadas();
        mostrarResultado(r, c.candidatas());
    }

    private static int lerCapacidade() {
        while (true) {
            int c = lerInteiro("Tempo disponível em minutos (1 a " + OtimizacaoRecursos.CAPACIDADE_MAXIMA + "): ");
            if (c >= 1 && c <= OtimizacaoRecursos.CAPACIDADE_MAXIMA) {
                return c;
            }
            System.out.println("Valor fora do limite.");
        }
    }

    private static int lerCriterio() {
        while (true) {
            System.out.println("O que maximizar?");
            System.out.println("1 - Prioridade total");
            System.out.println("2 - Quantidade de pessoas atendidas");
            System.out.print("Escolha uma opção: ");
            switch (lerOpcao()) {
                case 1 -> {
                    return OtimizacaoRecursos.CRITERIO_PRIORIDADE;
                }
                case 2 -> {
                    return OtimizacaoRecursos.CRITERIO_PESSOAS;
                }
                default -> System.out.println("Opção inválida.");
            }
        }
    }

    private static void mostrarResultado(OtimizacaoRecursos.Resultado r, List<Ocorrencia> candidatas) {
        int criterio = r.getCriterio();
        String nome = OtimizacaoRecursos.nomeCriterio(criterio);

        System.out.println();
        System.out.println("SELECIONADAS (" + r.getSelecionadas().size() + "):");
        for (Ocorrencia o : r.getSelecionadas()) {
            System.out.println("Id " + o.getId() + " | " + o.getTipo() + " | " + o.getMotivo() + " | " + o.getRegiao()
                    + " | " + o.getTempoEstimado() + " min | " + nome + " " + OtimizacaoRecursos.beneficio(o, criterio));
        }
        System.out.println("Tempo usado: " + r.getTempoUsado() + " de " + r.getCapacidade() + " minutos.");
        System.out.println("Total de " + nome + ": " + r.getBeneficioTotal());

        System.out.println();
        System.out.println("Ficaram de fora: " + r.getDeFora().size() + " ocorrência(s).");
        if (!r.getDeFora().isEmpty()
                && lerTexto("Listar as que ficaram de fora? (s/n): ").trim().equalsIgnoreCase("s")) {
            for (Ocorrencia o : r.getDeFora()) {
                String motivo;
                if (o.getTempoEstimado() > r.getCapacidade()) {
                    motivo = "sozinha já passa do tempo disponível";
                } else if (OtimizacaoRecursos.beneficio(o, criterio) == 0) {
                    motivo = "não soma nada nesse critério";
                } else {
                    motivo = "cabe, mas a combinação escolhida rende mais";
                }
                System.out.println("Id " + o.getId() + " | " + o.getMotivo() + " | " + o.getTempoEstimado() + " min | "
                        + nome + " " + OtimizacaoRecursos.beneficio(o, criterio) + " | " + motivo);
            }
        }

        // compara com o guloso do modulo 5 usando o mesmo tempo
        List<Ocorrencia> gulosa = ordenador.selecionarComLimiteDeTempo(candidatas, r.getCapacidade());
        int totalGuloso = OtimizacaoRecursos.beneficioTotal(gulosa, criterio);

        System.out.println();
        System.out.println("Justificativa:");
        System.out.println("- Cada ocorrência é uma decisão: entra inteira na seleção ou fica de fora.");
        System.out.println("- dp[i][t] guarda o maior total de " + nome + " usando as i primeiras ocorrências com t minutos.");
        System.out.println("- Tabela de " + (candidatas.size() + 1) + " x " + (r.getCapacidade() + 1)
                + " posições; a resposta ótima é dp[" + candidatas.size() + "][" + r.getCapacidade() + "] = "
                + r.getBeneficioTotal() + ".");
        System.out.println("- O guloso do módulo 5, com o mesmo tempo, chega a " + totalGuloso + " de " + nome + ".");
        System.out.println("Obs.: é uma simulação para planejamento, não substitui os protocolos oficiais de atendimento.");
    }

    // mesmas candidatas em 3 estrategias, lado a lado
    private static void compararCenarios() {
        Cenario c = prepararCenario();
        if (c == null) {
            return;
        }
        List<Ocorrencia> candidatas = c.candidatas();
        int capacidade = c.capacidade();

        List<Ocorrencia> porPrioridade = otimizacao.selecionar(candidatas, capacidade,
                OtimizacaoRecursos.CRITERIO_PRIORIDADE).getSelecionadas();
        List<Ocorrencia> porPessoas = otimizacao.selecionar(candidatas, capacidade,
                OtimizacaoRecursos.CRITERIO_PESSOAS).getSelecionadas();
        List<Ocorrencia> gulosa = ordenador.selecionarComLimiteDeTempo(candidatas, capacidade);

        System.out.println();
        System.out.println("Cenários com " + candidatas.size() + " candidatas e " + capacidade + " minutos:");
        System.out.printf("%-30s %12s %12s %12s %10s%n", "Cenário", "Ocorrências", "Tempo usado", "Prioridade", "Pessoas");
        linhaCenario("PD maximizando prioridade", porPrioridade);
        linhaCenario("PD maximizando pessoas", porPessoas);
        linhaCenario("Guloso (módulo 5)", gulosa);
    }

    private static void linhaCenario(String nome, List<Ocorrencia> lista) {
        int tempo = 0;
        for (Ocorrencia o : lista) {
            tempo += o.getTempoEstimado();
        }
        System.out.printf("%-30s %12d %12d %12d %10d%n", nome, lista.size(), tempo,
                OtimizacaoRecursos.beneficioTotal(lista, OtimizacaoRecursos.CRITERIO_PRIORIDADE),
                OtimizacaoRecursos.beneficioTotal(lista, OtimizacaoRecursos.CRITERIO_PESSOAS));
    }

    // ===================== MÓDULO 9 / RESUMO DA SELEÇÃO =====================

    private static void mostrarResumoSelecao() {
        if (ultimaSelecao == null || ultimaSelecao.isEmpty()) {
            System.out.println("Nenhuma seleção feita ainda. Faça uma seleção (opção 1 ou 2 deste modo) antes.");
            return;
        }

        int tempo = 0;
        int pessoas = 0;
        int prioridade = 0;
        for (Ocorrencia o : ultimaSelecao) {
            tempo += o.getTempoEstimado();
            pessoas += o.getPessoasEnvolvidas();
            prioridade += o.getPrioridade();
        }

        System.out.println();
        System.out.println("Resumo da última seleção: " + ultimaSelecao.size() + " ocorrência(s)");
        System.out.println("Tempo total: " + tempo + " min | Pessoas: " + pessoas
                + " | Prioridade média: " + String.format("%.1f", (double) prioridade / ultimaSelecao.size()));

        System.out.println();
        System.out.println("Recursos necessários (por tipo):");
        for (ResumoSelecao.Grupo g : resumo.agruparPorTipo(ultimaSelecao)) {
            System.out.println(g.getNome() + ": " + g.getQuantidade() + " x " + ResumoSelecao.recursoDoTipo(g.getNome())
                    + " | " + g.getTempo() + " min | " + g.getPessoas() + " pessoa(s)");
        }

        List<ResumoSelecao.Grupo> regioes = resumo.agruparPorRegiao(ultimaSelecao);
        System.out.println();
        System.out.println("Por região:");
        for (ResumoSelecao.Grupo g : regioes) {
            System.out.println(g.getNome() + ": " + g.getQuantidade() + " ocorrência(s) | " + g.getTempo() + " min | "
                    + g.getPessoas() + " pessoa(s) | prioridade média "
                    + String.format("%.1f", (double) g.getSomaPrioridade() / g.getQuantidade()));
        }
        System.out.println("Região que mais recebe equipes: " + regioes.get(0).getNome());
    }

    // ===================== MÓDULO 6 / PLANEJAMENTO OPERACIONAL =====================

    private static void menuPlanejamentoOperacional() {
        System.out.println();
        System.out.println("--- Planejamento Operacional: Dependências de Atendimento ---");
        System.out.println("1 - Listar operações cadastradas");
        System.out.println("2 - Mostrar dependências de uma operação");
        System.out.println("3 - Ordem de execução de uma operação");
        System.out.println("4 - Ordem de execução para uma ocorrência (pelo tipo)");
        System.out.println("5 - Cadastrar dependência");
        System.out.println("6 - Cadastrar atividade sem dependências");
        System.out.println("7 - Remover dependência");
        System.out.println("8 - Verificar ciclos em todas as operações");
        System.out.print("Escolha uma opção: ");

        switch (lerOpcao()) {
            case 1 -> listarOperacoes();
            case 2 -> mostrarDependencias(lerTexto("Operação: "));
            case 3 -> mostrarOrdem(lerTexto("Operação: "));
            case 4 -> ordemParaOcorrencia();
            case 5 -> {
                String operacao = lerTexto("Operação (nova ou existente): ");
                String antes = lerTexto("Atividade que deve ser concluída antes: ");
                String depois = lerTexto("Atividade que depende dela: ");
                boolean ok = planejamento.cadastrarDependencia(operacao, antes, depois);
                System.out.println(ok ? "Dependência cadastrada." : "Dependência inválida ou já existente.");
            }
            case 6 -> {
                String operacao = lerTexto("Operação (nova ou existente): ");
                String atividade = lerTexto("Atividade: ");
                boolean ok = planejamento.cadastrarAtividade(operacao, atividade);
                System.out.println(ok ? "Atividade cadastrada." : "Atividade inválida ou já existente.");
            }
            case 7 -> {
                String operacao = lerTexto("Operação: ");
                String antes = lerTexto("Atividade de origem: ");
                String depois = lerTexto("Atividade dependente: ");
                boolean ok = planejamento.removerDependencia(operacao, antes, depois);
                System.out.println(ok ? "Dependência removida." : "Dependência não encontrada.");
            }
            case 8 -> detectarCiclos();
            default -> System.out.println("Opção inválida.");
        }
    }

    private static void listarOperacoes() {
        List<String> nomes = planejamento.getNomesOperacoes();
        if (nomes.isEmpty()) {
            System.out.println("Nenhuma operação cadastrada.");
            return;
        }
        for (String nome : nomes) {
            GrafoDirecionado grafo = planejamento.buscarOperacao(nome);
            System.out.println(nome + " | " + grafo.getQuantidadeVertices() + " atividades | "
                    + grafo.getQuantidadeArestas() + " dependências");
        }
    }

    private static void mostrarDependencias(String operacao) {
        GrafoDirecionado grafo = planejamento.buscarOperacao(operacao);
        if (grafo == null) {
            System.out.println("Operação não encontrada.");
            return;
        }
        for (String atividade : grafo.getVertices()) {
            List<String> sucessores = grafo.getSucessores(atividade);
            System.out.println(atividade + (sucessores.isEmpty() ? "" : " -> " + String.join(", ", sucessores)));
        }
    }

    private static void mostrarOrdem(String operacao) {
        GrafoDirecionado.ResultadoOrdenacao resultado = planejamento.ordemDeExecucao(operacao);
        if (resultado == null) {
            System.out.println("Operação não encontrada.");
            return;
        }

        if (!resultado.temCiclo()) {
            System.out.println("Sequência válida de execução:");
            List<String> ordem = resultado.getOrdem();
            for (int i = 0; i < ordem.size(); i++) {
                System.out.println((i + 1) + ". " + ordem.get(i));
            }
            return;
        }

        System.out.println("Não é possível determinar uma ordem válida para todas as atividades.");
        System.out.println("Ciclo encontrado: " + String.join(" -> ", resultado.getCiclo()));
        System.out.println("Atividades bloqueadas pelo ciclo: " + String.join(", ", resultado.getBloqueadas()));
        if (!resultado.getOrdem().isEmpty()) {
            System.out.println("Atividades que podem ser executadas antes do bloqueio: "
                    + String.join(", ", resultado.getOrdem()));
        }
    }

    private static void ordemParaOcorrencia() {
        int id = lerInteiro("Id da ocorrência: ");
        Ocorrencia o = central.consultar(id);
        if (o == null) {
            System.out.println("Ocorrência não encontrada.");
            return;
        }
        String operacao = planejamento.operacaoDoTipo(o.getTipo());
        System.out.println("Ocorrência " + id + " | " + o.getTipo() + " | " + o.getMotivo() + " | " + o.getRegiao());
        System.out.println("Operação utilizada: " + operacao);
        mostrarOrdem(operacao);
    }

    private static void detectarCiclos() {
        List<String> comCiclo = planejamento.operacoesComCiclo();
        if (comCiclo.isEmpty()) {
            System.out.println("Nenhum ciclo encontrado. Todas as operações possuem ordem válida.");
            return;
        }
        System.out.println(comCiclo.size() + " operação(ões) com dependências circulares:");
        for (String nome : comCiclo) {
            GrafoDirecionado.ResultadoOrdenacao resultado = planejamento.ordemDeExecucao(nome);
            System.out.println(nome + ": " + String.join(" -> ", resultado.getCiclo()));
        }
    }

    // ===================== AUXILIARES DE ENTRADA E SAÍDA =====================

    private static int lerOpcao() {
        return lerInteiro("");
    }

    private static int lerInteiro(String mensagem) {
        if (!mensagem.isEmpty()) {
            System.out.print(mensagem);
        }
        while (!scanner.hasNextInt()) {
            scanner.next();
            System.out.print("Valor inválido, digite um número: ");
        }
        int valor = scanner.nextInt();
        scanner.nextLine();
        return valor;
    }

    private static String lerTexto(String mensagem) {
        System.out.print(mensagem);
        return scanner.nextLine();
    }

    private static void imprimirResumo(List<Ocorrencia> ocorrencias) {
        if (ocorrencias.isEmpty()) {
            System.out.println("Nenhuma ocorrência encontrada.");
            return;
        }
        System.out.println(ocorrencias.size() + " ocorrência(s) encontrada(s):");
        for (Ocorrencia o : ocorrencias) {
            System.out.println("Id " + o.getId() + " | " + o.getTipo() + " | " + o.getMotivo()
                    + " | Prioridade " + o.getPrioridade() + " | " + o.getStatus()
                    + " | " + o.getRegiao());
        }
    }

    private static void imprimirGrupos(List<List<Ocorrencia>> grupos) {
        if (grupos.isEmpty()) {
            System.out.println("Nenhum grupo encontrado.");
            return;
        }
        System.out.println(grupos.size() + " grupo(s) encontrado(s):");
        for (List<Ocorrencia> grupo : grupos) {
            StringBuilder ids = new StringBuilder();
            for (Ocorrencia o : grupo) {
                if (ids.length() > 0) {
                    ids.append(", ");
                }
                ids.append(o.getId());
            }
            System.out.println("Ids: [" + ids + "]");
        }
    }
}
