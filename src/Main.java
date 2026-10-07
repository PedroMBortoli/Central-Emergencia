import modelo.Ocorrencia;
import modulos.CentralOcorrencias;
import modulos.ComparacaoArquivo;
import modulos.Integridade;
import modulos.OrdenadorAtendimento;

import java.util.List;
import java.util.Scanner;

public class Main {

    private static final String ARQUIVO_DADOS = "data/ocorrencias.csv";
    private static final String ARQUIVO_TESTE_INTEGRIDADE = "data/ocorrencias_teste_integridade.csv";

    private static final Scanner scanner = new Scanner(System.in);
    private static final OrdenadorAtendimento ordenador = new OrdenadorAtendimento();

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

        boolean sair = false;
        while (!sair) {
            System.out.println();
            System.out.println("===== MENU PRINCIPAL =====");
            System.out.println("1 - Módulo 1 / Central de Ocorrências (cadastro, consulta, alteração, remoção, listagem)");
            System.out.println("2 - Módulo 2 / Modo Consulta Rápida (busca por id, descrição, tipo, região, prefixo)");
            System.out.println("3 - Módulo 3 / Organização das Ocorrências (filtros por status, prioridade, pessoas, região, tipo)");
            System.out.println("4 - Módulo 4 / Modo Investigação (integridade dos registros)");
            System.out.println("5 - Módulo 5 / Modo Operação Resgate (ordem e seleção de atendimento)");
            System.out.println("0 - Sair");
            System.out.print("Escolha uma opção: ");

            switch (lerOpcao()) {
                case 1 -> menuCentralOcorrencias();
                case 2 -> menuConsultaRapida();
                case 3 -> menuOrganizador();
                case 4 -> menuIntegridade();
                case 5 -> menuOperacaoResgate();
                case 0 -> sair = true;
                default -> System.out.println("Opção inválida.");
            }
        }

        System.out.println("Encerrando o sistema.");
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

    // comeca com as pendentes e vai aplicando as restricoes que o usuario escolher,
    // uma em cima da outra (assim da pra combinar regiao + tipo + prioridade etc)
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
