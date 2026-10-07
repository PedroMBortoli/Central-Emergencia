package modulos;

import modelo.Ocorrencia;
import java.util.ArrayList;
import java.util.List;

public class ComparacaoArquivo {

    public static class Conflito {
        private final Ocorrencia armazenada;
        private final Ocorrencia doArquivo;
        private final String diagnostico;

        public Conflito(Ocorrencia armazenada, Ocorrencia doArquivo, String diagnostico) {
            this.armazenada = armazenada;
            this.doArquivo = doArquivo;
            this.diagnostico = diagnostico;
        }

        public Ocorrencia getArmazenada() {
            return armazenada;
        }

        public Ocorrencia getDoArquivo() {
            return doArquivo;
        }

        public String getDiagnostico() {
            return diagnostico;
        }
    }

    private int iguais;
    private final List<Conflito> conflitos = new ArrayList<>();
    private final List<Ocorrencia> ausentesNaMemoria = new ArrayList<>();

    void contarIgual() {
        iguais++;
    }

    void adicionarConflito(Conflito c) {
        conflitos.add(c);
    }

    void adicionarAusente(Ocorrencia o) {
        ausentesNaMemoria.add(o);
    }

    public int getIguais() {
        return iguais;
    }

    public List<Conflito> getConflitos() {
        return conflitos;
    }

    public List<Ocorrencia> getAusentesNaMemoria() {
        return ausentesNaMemoria;
    }
}
