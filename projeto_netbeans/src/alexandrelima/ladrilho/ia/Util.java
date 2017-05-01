/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package alexandrelima.ladrilho.ia;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author Alexandre
 */
public class Util {
    
    public static Map<String, String> lerPolitica(String fileName) throws FileNotFoundException, 
            IOException{
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        Map<String, String> politica = new HashMap<String, String>();
        String line = reader.readLine();
        while(line != null && !line.trim().equals("") ){
            String[] campos = line.split("#");
            String estado = campos[0];
            String acao = campos[1];
            politica.put(estado, acao);
            line = reader.readLine();
        }
        return politica;
    }
    
    public static Map<String, TabelaValorEstadoAcao> lerTabelaQ(String fileName) throws Exception{
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        Map<String, TabelaValorEstadoAcao> tabela = new HashMap<String, TabelaValorEstadoAcao>();
        String line = reader.readLine();
        while(line != null && !line.trim().equals("") ){
            int posVirgula = line.indexOf(",");
            String estado = line.substring(1, posVirgula);
            String acao = line.substring(posVirgula + 2, posVirgula + 3);
            int posIgual = line.indexOf("=");
            double valor = Double.parseDouble(line.substring(posIgual + 2));
            TabelaValorEstadoAcao sTable = tabela.get(estado);
            if(sTable == null){
                sTable = new TabelaValorEstadoAcao(estado);
                tabela.put(estado, sTable);
            }
            sTable.inserirValor(acao, valor);
            line = reader.readLine();
        }
        return tabela;
    }

    /**
     * Gera arquivo da política e da tabela Q resultantes de um treinamento.
     * @param politica política resultante do treinamento
     * @param niveis quantidade de níveis do jogo do ladrilho utilizado no treinamento
     * @param qtdEpisodios quantidade de episódios utilizados no treinamento
     * @param alfa taxa de aprendizagem utilizada no treinamento
     * @param gama fator de desconto utilizado no treinamento
     * @param episilon valor episilon da política E-gulosa utilizada no treinamento
     * @param tempoTreino duração do treinamento em milissegundos
     * @param nomeAlgoritmo nome do algoritmo de AR utilizado
     * @throws java.io.IOException
     */
    public static void gerarArquivosDoTreino(Map<String, String> politica, Map<String, TabelaValorEstadoAcao> table,
            int niveis, int qtdEpisodios, double alfa, double gama, Double episilon,
            long tempoTreino, String nomeAlgoritmo, String extraInfo) throws IOException{
        //cria diretório dos arquivos
        DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        File diretorio = new File(fmt.format(new Date()).replace('/', '-'));
        diretorio.mkdir();
        String prefixo = diretorio.getName() + "/" + String.valueOf(System.currentTimeMillis());
        String data = fmt.format(new Date());
        gerarArquivoPolitica(politica, niveis, qtdEpisodios, alfa, gama, episilon, 
                tempoTreino, prefixo, data, nomeAlgoritmo, extraInfo);
        gerarArquivoTabelaQ(table, niveis, qtdEpisodios, alfa, gama, episilon, 
                tempoTreino, prefixo, data, nomeAlgoritmo, extraInfo);
    }

    /**
     * Gera arquivo da política resultante de um treinamento.
     * @param politica política resultante do treinamento
     * @param niveis quantidade de níveis do jogo do ladrilho utilizado no treinamento
     * @param qtdEpisodios quantidade de episódios utilizados no treinamento
     * @param alfa taxa de aprendizagem utilizada no treinamento
     * @param gama fator de desconto utilizado no treinamento
     * @param episilon valor episilon da política E-gulosa utilizada no treinamento
     * @param tempoTreino duração do treinamento em milissegundos
     * @param nomeAlgoritmo nome do algoritmo de AR utilizado
     * @throws java.io.IOException
     */
    private static void gerarArquivoPolitica(Map<String, String> politica,
            int niveis, int qtdEpisodios, double alfa, double gama, Double episilon,
            long tempoTreino, String prefixo, String data, String nomeAlgoritmo, String extraInfo) throws IOException{
        //gera string de tempo
        String strTempo = "Tempo de processamento: " + formatarTempo(tempoTreino);

        //gera arquivo da política
        FileWriter fWriter = new FileWriter(prefixo + "-pol-" + niveis + "N-" 
                + qtdEpisodios + "ep-" + nomeAlgoritmo + ".txt");
        for (String key : politica.keySet()) {
            fWriter.write(key + "#" + politica.get(key) + "\n");
        }
        fWriter.write("\n");
        fWriter.write(qtdEpisodios + " episódios\n");
        fWriter.write(strTempo + "\n");
        fWriter.write("Step-size parameter: "+ alfa +"\n");
        fWriter.write("Fator de desconto: "+ gama +"\n");
        fWriter.write("Algoritmo: " + nomeAlgoritmo +"\n");
        if(episilon != null){
            fWriter.write("Episilon da política E-gulosa: " + episilon + "%\n");
        }
        if(extraInfo != null){
            fWriter.write(extraInfo + "\n");
        }
        fWriter.write("Data: "+ data +"\n");
        fWriter.close();
    }

    /** Gera arquivo da tabela de pares estado-ação.
     *
     * @param table tabela com valores resultante do treinamento
     * @param niveis quantidade de níveis do jogo do ladrilho utilizado no treinamento
     * @param qtdEpisodios quantidade de espiódios utilizada no treinamento do agente
     * @param alfa taxa de aprendizagem utilizada no treinamento
     * @param gama fator de desconto utilizado no treinamento
     * @param episilon valor episilon da política E-gulosa
     * @param tempoTreino duração do treinamento em milissegundos
     * @param nomeAlgoritmo nome do algoritmo de AR utilizado
     * @throws java.io.IOException
     */
    private static void gerarArquivoTabelaQ(Map<String, TabelaValorEstadoAcao> table,
            int niveis, int qtdEpisodios, double alfa, double gama, Double episilon,
            long tempoTreino, String prefixo, String data, String nomeAlgoritmo, String extraInfo) throws IOException{
        //gera string de tempo
        String strTempo = "Tempo de processamento: " + formatarTempo(tempoTreino);

        FileWriter fWriter = new FileWriter(prefixo + "-tabela-Q-"+ niveis 
                +"N-"+ qtdEpisodios +"ep-"+ nomeAlgoritmo +".txt");
        for (String key : table.keySet()) {
            TabelaValorEstadoAcao ta = table.get(key);
            for(String acao : ta.getAcoes()){
                fWriter.write("[" + key + ", " + acao + "] = " + ta.getValor(acao) + "\n");
            }
        }
        fWriter.write("\n");
        fWriter.write(qtdEpisodios + " episódios\n");
        fWriter.write(strTempo + "\n");
        fWriter.write("Step-size parameter: "+ alfa +"\n");
        fWriter.write("Fator de desconto: "+ gama +"\n");
        fWriter.write("Algoritmo: " + nomeAlgoritmo +"\n");
        if(episilon != null){
            fWriter.write("Episilon da política E-gulosa: " + episilon + "%\n");
        }
        if(extraInfo != null){
            fWriter.write(extraInfo + "\n");
        }
        fWriter.write("Data: "+ data +"\n");
        fWriter.close();
    }

    public static void gerarArquivoVetorTeta(double[] vTeta,
            int niveis, int qtdEpisodios, double alfa, double gama, Double episilon,
            long tempoTreino, String nomeAlgoritmo, String extraInfo) throws IOException{
        //cria diretório dos arquivos
        DateFormat fmt = DateFormat.getDateInstance(DateFormat.SHORT);
        File diretorio = new File(fmt.format(new Date()).replace('/', '-'));
        diretorio.mkdir();
        String prefixo = diretorio.getName() + "/" + String.valueOf(System.currentTimeMillis());
        String data = fmt.format(new Date());
        //gera string de tempo
        String strTempo = "Tempo de processamento: " + formatarTempo(tempoTreino);

        FileWriter fWriter = new FileWriter(prefixo + "-vetor-teta-"+ niveis
                +"N-"+ qtdEpisodios +"ep-"+ nomeAlgoritmo +".txt");
        for (double v : vTeta) {
            fWriter.write(v + "\n");
        }
        fWriter.write("\n");
        fWriter.write(qtdEpisodios + " episódios\n");
        fWriter.write(strTempo + "\n");
        fWriter.write("Step-size parameter: "+ alfa +"\n");
        fWriter.write("Fator de desconto: "+ gama +"\n");
        fWriter.write("Algoritmo: " + nomeAlgoritmo +"\n");
        if(episilon != null){
            fWriter.write("Episilon: " + episilon + "%\n");
        }
        if(extraInfo != null){
            fWriter.write(extraInfo + "\n");
        }
        fWriter.write("Data: "+ data +"\n");
        fWriter.close();
    }
    
    private static NumberFormat fmtTime = new DecimalFormat("00");
    private static String formatarTempo(long tempo){
        int horas = (int)(tempo / 3600);
        int minutos = (int)((tempo - horas * 3600) / 60);
        int segundos = (int)((tempo - horas * 3600 - minutos * 60));
        String strTempo = fmtTime.format(horas) + ":" + fmtTime.format(minutos) + ":" + fmtTime.format(segundos);
        
        return strTempo;
    }
    
    /*public static void main(String[] args) {
        System.out.println( formatarTempo(35) ); //00:00:35
        System.out.println( formatarTempo(60) ); //00:01:00
        System.out.println( formatarTempo(80) ); //00:01:20
        System.out.println( formatarTempo(83) ); //00:01:23
        System.out.println( formatarTempo(128) ); //00:02:08
        System.out.println( formatarTempo(732) ); //00:12:12
        System.out.println( formatarTempo(4509) ); //01:15:09
    }*/
}
