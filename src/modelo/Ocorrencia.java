package modelo;

public class Ocorrencia {
    private final String id;
    private String tipo;
    private String descricao;
    private String regiao;
    private int prioridade;
    private String dataHora;
    private String status;
    private int pessoasEnvolvidas;
    private int tempoEstimado;
    private final String hashIntegridade;
    private String endereco;
    private String motivo;
    private String equipe;

    //CONSTRUTOR:

    public Ocorrencia(String id, String tipo, String descricao, String regiao, int prioridade, String dataHora, String status, int pessoasEnvolvidas, int tempoEstimado, String hashIntegridade, String endereco, String motivo, String equipe)
    {
        this.id = id;
        setTipo(tipo);
        setDescricao(descricao);
        setRegiao(regiao);
        setPrioridade(prioridade);
        setDataHora(dataHora);
        setStatus(status);
        setPessoasEnvolvidas(pessoasEnvolvidas);
        setTempoEstimado(tempoEstimado);
        this.hashIntegridade = hashIntegridade;
        setEndereco(endereco);
        setMotivo(motivo);
        setEquipe(equipe);


    }

    //METODO PARA IMPRIMIR O OBJETO:

    @Override
    public String toString() {
        return  "ID: " + id + "\n" +
                "Tipo: " + tipo + "\n" +
                "Descrição: " + descricao + "\n" +
                "Região: " + regiao + "\n" +
                "Prioridade: " + prioridade + "\n" +
                "Data/Hora: " + dataHora + "\n" +
                "Status: " + status;
    }

    //SETTERS:

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public void setDataHora(String dataHora) {
        this.dataHora = dataHora;
    }

    public void setRegiao(String regiao) {
        this.regiao = regiao;
    }

    public void setPrioridade(int prioridade) {
        this.prioridade = prioridade;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setPessoasEnvolvidas(int pessoasEnvolvidas) {
        this.pessoasEnvolvidas = pessoasEnvolvidas;
    }

    public void setTempoEstimado(int tempoEstimado) {
        this.tempoEstimado = tempoEstimado;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public void setEquipe(String equipe) {
        this.equipe = equipe;
    }

    //GETTERS:

    public String getId() {
        return id;
    }

    public String getTipo() {
        return tipo;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getRegiao() {
        return regiao;
    }

    public int getPrioridade() {
        return prioridade;
    }

    public String getDataHora() {
        return dataHora;
    }

    public String getStatus() {
        return status;
    }

    public int getPessoasEnvolvidas() {
        return pessoasEnvolvidas;
    }

    public int getTempoEstimado() {
        return tempoEstimado;
    }

    public String getHashIntegridade() {
        return hashIntegridade;
    }

    public String getEndereco() {
        return endereco;
    }

    public String getMotivo() {
        return motivo;
    }

    public String getEquipe() {
        return equipe;
    }

}
