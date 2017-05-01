/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package alexandrelima.ladrilho.ia;

import alexandrelima.ladrilho.dominio.ControleLadrilho;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Alexandre
 */
public class MonteCarlo {
    
    private ControleLadrilho jogo;
    Map<String, TabelaValorEstadoAcao> tabelaQ;

    public MonteCarlo(ControleLadrilho jog) {
        this.jogo = jog;
    }
    
    public Map<String, String> treinar(int qtdEpisodios){
        Map<String, String> politica = new LinkedHashMap<String, String>(); //política gerada ao final
        tabelaQ = new LinkedHashMap<String, TabelaValorEstadoAcao>(); //valores dos pares estado-ação. a chave é o estado
        Map<String, List<Double>> retornosEstadoAcao = new HashMap<String, List<Double>>(); //listas de retornos dos pares estado-ação. a chave é a junção das string de estado e ação separadas por ponto e vírgula
        
        Map<String, String> paresOcorridos = new HashMap<String, String>(); //guarda pares estado-ação ocorridos no episódio
        for(int i = 0; i < qtdEpisodios; i++){
            //System.out.println("Episódio " + i);
            jogo.reiniciar();
            List<Integer> acoes = jogo.getMovimentosPossiveis();
            paresOcorridos.clear();
            //executa jogadas de acordo com a política enquanto for possível mover peças
            boolean inicioEpisodio = true;
            int step = 1; //cada episódio possui 1000 steps
            while(step <= 1000 && !jogo.isGameOver()){
                step++;
                String estado = jogo.getString();
                //verifica se já há uma tabela de valores para o estado atual
                TabelaValorEstadoAcao tabela = tabelaQ.get(estado);
                if(tabela == null){//não há tabela, então a cria
                    tabela = new TabelaValorEstadoAcao(estado);
                    for(Integer acao : acoes){
                        tabela.inserirValor(String.valueOf(acao), 0.0);
                    }
                    tabelaQ.put(estado, tabela);
                }
                
                String acao = null;
                if(inicioEpisodio){
                    //escolhe uma ação aleatoriamente
                    int j = (int)Math.round(0 + Math.random() * (acoes.size() - 1));
                    acao = String.valueOf(acoes.get(j));
                    inicioEpisodio = false;
                }
                else{
                    if(politica.containsKey(estado)){ //escolhe a ação de acordo com a política
                        acao = politica.get(estado);
                    }
                    else{ //não há ação definida para o estado de acordo com a política, então usa qualquer ação
                        int j = (int)Math.round(0 + Math.random() * (acoes.size() - 1));//gera um número entre 1 e maxCasasVazias
                        acao = String.valueOf(acoes.get(j));
                    }
                }
                boolean ok = jogo.moverLadrilho(Integer.parseInt(acao));
                if(ok){//ação realizada
                    paresOcorridos.put(estado, acao);
                }
                else{//ação não realizada, o que significa que há algum erro de implementação
                    System.out.println("Ação não realizada -> existe algum BUG!!");
                }
                
                acoes = jogo.getMovimentosPossiveis();
            }
            
            //percorre os pares estado-ação ocorridos no episódio
            for(String estado : paresOcorridos.keySet()){
                String acao = paresOcorridos.get(estado);
                double valorRetorno = getRetorno(acao);
                String key = estado + ";" + acao;
                List<Double> listaRetornos = retornosEstadoAcao.get(key);
                if(listaRetornos != null){//verifica se já há uma lista de retornos para o par estado-ação
                    retornosEstadoAcao.get(key).add(valorRetorno);
                }
                else{//não há lista de retornos para o par estado-ação, então a cria e insere o valor de retorno
                    listaRetornos = new ArrayList<Double>();
                    listaRetornos.add(valorRetorno);
                    retornosEstadoAcao.put(key, listaRetornos);
                }
                //calcula a média dos retornos do par estado-ação
                double media = 0;
                for(Double d : listaRetornos){
                    media += d;
                }
                media = media / listaRetornos.size();
                //atualiza o valor do par estado-ação
                TabelaValorEstadoAcao tabela = tabelaQ.get(estado);
                tabela.inserirValor(acao, media);
                //atualiza a política com a ação com maior valor Q
                politica.put(estado, tabela.getAcaoValorMaximo());
            }
        }
        
        return politica;
    }
    
    /**
     * Retorna o valor de retorno de uma ação realizada.
     * @param acao  ação que levou ao estado atual do tabuleiro.
     * @return
     */
    public double getRetorno(String acao){
        int qtdCasasOrdenadas = 0;
        for(int i = 0; i < jogo.getLadrilhos().length; i++){
            if(jogo.getLadrilhos()[i] == i){
                qtdCasasOrdenadas++;
            }
        }
        if(qtdCasasOrdenadas == jogo.getLadrilhos().length){//estado terminal
            return qtdCasasOrdenadas * 10;
        }
        return qtdCasasOrdenadas * 2;
    }
    
    public Map<String, TabelaValorEstadoAcao> getTabelaValorEstadoAcao(){
        return tabelaQ;
    }
    
    public static void main(String[] args) {
        FileWriter fWriter = null;
        try {
            int qtdEpisodios = 100000;
            int niveis = 3;
            
            ControleLadrilho jogo = new ControleLadrilho(niveis);
            MonteCarlo mc = new MonteCarlo(jogo);
            long timeStart = System.currentTimeMillis();
            Map<String, String> politicaGerada = mc.treinar(qtdEpisodios);
            long timeEnd = System.currentTimeMillis();
            long tempo = (timeEnd - timeStart) / 1000 ; //em segundos
            //gera string de tempo
            String strTempo = "Tempo de processamento: ";
            if(tempo < 60){
                strTempo += tempo + " segundos";
            }
            else if(tempo < 3600){ //verifica se tempo é menor que uma hora
                strTempo += (tempo / 60.0) + " minutos";
            }
            else{
                strTempo += (tempo / 3600.0) + " horas";
            }
            
            //gera arquivo da política
            fWriter = new FileWriter("pol-" + niveis + "N-" + qtdEpisodios + "ep-MC.txt");
            for (String key : politicaGerada.keySet()) {
                fWriter.write(key + "#" + politicaGerada.get(key) + "\n");
            }
            fWriter.write("\n");
            fWriter.write(qtdEpisodios + " episódios\n");
            fWriter.write(strTempo + "\n");
            fWriter.write("Algoritmo: Monter Carlo ES\n");
            fWriter.close();
            
            //gera arquivo da tabela de pares estado-ação
            Map<String, TabelaValorEstadoAcao> table = mc.getTabelaValorEstadoAcao();
            fWriter = new FileWriter("tabela-Q-"+ niveis +"N-"+ qtdEpisodios +"ep-MC.txt");
            for (String key : table.keySet()) {
                TabelaValorEstadoAcao ta = table.get(key);
                for(String acao : ta.getAcoes()){
                    fWriter.write("[" + key + ", " + acao + "] = " + ta.getValor(acao) + "\n");
                }
            }
            fWriter.write("\n");
            fWriter.write(qtdEpisodios + " episódios\n");
            fWriter.write(strTempo + "\n");
            fWriter.write("Algoritmo: Monter Carlo ES\n");
            fWriter.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
