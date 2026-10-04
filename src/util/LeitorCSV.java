package util;

import modelo.Ocorrencia;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class LeitorCSV {
    public static ArrayList<Ocorrencia> ler(String caminho) {
        ArrayList<Ocorrencia> lista = new ArrayList<>();
        int proximoId = 1;

        try (BufferedReader leitor = new BufferedReader(new FileReader(caminho))) {
            leitor.readLine(); //pula cabeçalho
            String linha;
            while ((linha = leitor.readLine()) != null)
            {
                String[] campos = linha.split(",", -1);

                String title = campos[4];
                int doisPontos = title.indexOf(':');
                String tipo = title.substring(0, doisPontos).trim();
                String motivo = title.substring(doisPontos + 1).trim();

                if (motivo.endsWith("-")) {
                    motivo = motivo.substring(0, motivo.length() - 1).trim();
                }

                String id = String.valueOf(proximoId);
                proximoId++;

                int prioridade = RegraPrioridade.calcular(tipo, motivo);
                int pessoas = RegraPessoas.calcular(motivo);
                int tempo = RegraTempo.calcular(tipo, prioridade);

                String regiao = campos[6].isEmpty() ? "DESCONHECIDA" : campos[6];

                Ocorrencia o = new Ocorrencia(
                        id, tipo.toUpperCase(), campos[2], regiao, prioridade, campos[5],
                        "PENDENTE", pessoas, tempo, campos[7], motivo, "NÃO ATRIBUÍDA"
                );

                lista.add(o);
            }
        } catch (IOException e) {
            System.out.println("Erro ao ler o arquivo CSV: " + e.getMessage());
        }

        return lista;
    }
}
