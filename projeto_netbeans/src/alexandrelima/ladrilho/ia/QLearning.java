/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package alexandrelima.ladrilho.ia;

import alexandrelima.ladrilho.dominio.ControleLadrilho;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Classe que representa o algoritmo Q-Learning para o jogo do ladrilho.
 * @author Alexandre
 */
public class QLearning {
    
    ControleLadrilho jogo, auxGame;
    Map<String, TabelaValorEstadoAcao> tabelaQ; //tabela com valores estado ação
    Random random;
    private boolean echo; //indica se deve imprimir as mensagens informativas
    private String stepsPorEpisodio; //guarda a evolução de steps por episódio no formato: num episódio, qdt steps
    private int episilon = 70; //porcentagem de escolha (entre zero e cem) da ação ótima da política E-gulosa
    private Integer stepDeParada; //quantidada máxima de movimentações por episódio. Usar nulo quando não houver essa restrição.
    private int qtdEpisodiosEncerradosPorFimDeJogo = 0;
    private double mediaSteps; //média de steps por episódios
    private PoliticaTreinamento politicaTreinamento;

    /**
     * Cria uma nova instância desta classe
     * @param jog   O jogo do ladrilho a ser utilizado
     */
    public QLearning(ControleLadrilho jog) {
        this.jogo = jog;
        this.auxGame = new ControleLadrilho(jog.getDimensao());
        random = new Random();
    }

    /**
     * Define se as mensagens informativas devem ser impressas.
     * @param e
     */
    public void setEcho(boolean e){
        this.echo = e;
    }
    
    /**
     * Quantidada máxima de movimentações por episódio. No caso de nulo, o episódio 
     * somente será encerrado ao atingir o estado terminal.
     * @param step 
     */
    public void setStepDeParada(Integer step){
        this.stepDeParada = step;
    }
    
    /**
     * Retorna a média de steps (movimentações) realizadas em todos os episódios.
     * @return 
     */
    public double getMediaSteps(){
        return mediaSteps;
    }
    
    /**
     * Retorna uma ação disponível no estado atual do jogo. A açao retornada é
     * escolhida com base em uma política E-gulosa.
     * @return
     */
    /*private String getAcaoEGulosa(){
        String s = jogo.getString();
        inicializarEstado(s); //se s já foi incializado NÃO inicializa novamente
        TabelaValorEstadoAcao tableS = tabelaQ.get(s);
        String maxAction = tableS.getAcaoValorMaximo();
        int roleta = random.nextInt(100) + 1; //gera um número entre 1 e 100
        if(roleta <= getEpisilon()){
            return maxAction;
        }
        //else
        Set<String> setAcoes = tableS.getAcoes();
        String[] acoes = new String[setAcoes.size() - 1]; //maxAction não será incluída
        int i = 0;
        for(Object ob : setAcoes.toArray()){
            String a = (String)ob;
            if(!maxAction.equals(a)){
                acoes[i] = a;
                i++;
            }
        }
        roleta = random.nextInt(acoes.length);//gera um número entre 0 e (acoes.size() - 1)
        return (String)acoes[roleta];
    }*/
    
    /**
     * Inicializa os valores de um estado na tabelaQ. Caso o estado tenha sido 
     * inicilizado anteriormente, não altera os valores de sua tabela.
     * @param s Estado a ser inicializado
     */
    void inicializarEstado(String s){
        if(!tabelaQ.containsKey(s)){
            TabelaValorEstadoAcao tableS = new TabelaValorEstadoAcao(s);
            auxGame.setEstado(s);
            List<Integer> acoes = auxGame.getMovimentosPossiveis();
            for(Integer a : acoes){
                tableS.inserirValor(String.valueOf(a), 0.0);
            }
            tabelaQ.put(s, tableS);
        }
    }

    /**
     * Retorna a recompensa da ação que levou ao estado atual do jogo.
     * @return 100 caso o jogo tenha sido solucionado e zero em caso contrário.
     */
    public double getRecompensa(){
        if(jogo.isGameOver()){
            return 100;
        }
        return 0;
    }
    
    /**
     * Informa a quantidade de episódios encerrados ao atingir o estado terminal.
     * @return 
     */
    public int getQtdEpisodiosEncerradosPorFimDeJogo(){
        return qtdEpisodiosEncerradosPorFimDeJogo;
    }

    /**
     * Retorna o conjunto de tabelas com os valores estado-ação. A chave é o estado da
     * que se deseja obter a tabela estado-ação.
     * @return
     */
    public Map<String, TabelaValorEstadoAcao> getTabelaValorEstadoAcao(){
        return tabelaQ;
    }

    /**
     * Realiza o treinamento, ou seja, o algoritmo interage com o jogo para
     * obter experiência. A experiência é o conjunto de valores estado-ação que
     * são atualizados em cada episódio. Um episódio é uma partida jogada pelo
     * algoritmo.
     * @param qtdEpisodios A quantidade de episódios que o algoritmo deve realizar.
     * @param alfa taxa de aprendizagem (valor entre 0 e 1). Um valor igual a zero
     * fará com que o agente não aprenda nada. Um valor igual a um faz com que o agente
     * considere apenas a informação mais recente.
     * @param gama fator de desconto (valor entre 0 e 1). Um valor igual a zero fará
     * com que o agente seja oportunista, ou seja, irá considerar apenas a última
     * recompensa. Um valor igual a um fará com que o agente valorize as melhores
     * recompensas a longo prazo.
     * @return A política resultante do treinamento. A chave é o estado e o valor
     * a melhor ação segundo a política.
     */
    public Map<String, String> treinar(int qtdEpisodios, double alfa, double gama){
        politicaTreinamento = new PoliticaEpsilonGulosa(this);
        tabelaQ = new HashMap<String, TabelaValorEstadoAcao>();
        double alfa2 = alfa;
        StringBuilder bufferSteps = new StringBuilder();
        long totalSteps = 0;
        for(int i = 0; i < qtdEpisodios; i++){
            politicaTreinamento.novoEpisodio();
            jogo.reiniciar();
            String s = jogo.getString();
            int step = 1;
            while(!jogo.isGameOver()){
                if(stepDeParada != null && step > stepDeParada){
                    break;
                }
                String a = politicaTreinamento.getAcao();
                jogo.moverLadrilho(Integer.parseInt(a));
                String sLinha = jogo.getString();
                //RETORNO DA AÇÃO
                double r = getRecompensa();
                inicializarEstado(s);
                inicializarEstado(sLinha);
                TabelaValorEstadoAcao qS = tabelaQ.get(s);
                TabelaValorEstadoAcao qSLinha = tabelaQ.get(sLinha);
                String aLinha = qSLinha.getAcaoValorMaximo();
                double novoValorQs = qS.getValor(a) + alfa2 * (r + gama * qSLinha.getValor(aLinha) - qS.getValor(a));
                qS.inserirValor(a, novoValorQs);
                s = sLinha;
                step++;
                totalSteps++;
            }
            
            if(jogo.isGameOver()){
                qtdEpisodiosEncerradosPorFimDeJogo++;
            }
            
            if(echo){
                if(jogo.isGameOver()){
                    System.out.println("Episódio "+ i +" terminado após atingir estado final ("+ (step - 1) +" steps)");
                }
                else{
                    System.out.println("Episódio "+ i +" terminado após atingir step máximo.");
                }
            }
            
            bufferSteps.append(i + 1);
            bufferSteps.append(',');
            bufferSteps.append(step - 1);
            bufferSteps.append('\n');
        }
        
        stepsPorEpisodio = bufferSteps.toString();
        
        mediaSteps = totalSteps / (double)qtdEpisodios;
        
        //extração da política
        Map<String, String> pol = new HashMap<String, String>(tabelaQ.size());
        for(String estado : tabelaQ.keySet()){
            TabelaValorEstadoAcao qS = tabelaQ.get(estado);
            pol.put(estado, qS.getAcaoValorMaximo());
        }
        
        return pol;
    }
    
    public static void main(String[] args) {
        try {
            int qtdEpisodios = 250;
            int episilon = 40;
            double alfa = 0.1;
            Integer maxSteps = null;
            boolean echo = false;
            double gama = 0.1;
            int niveis = 3;
            
            ControleLadrilho jogo = new ControleLadrilho(niveis);
            QLearning qL = new QLearning(jogo);
            qL.setEcho(echo);
            qL.setEpisilon(episilon);
            qL.setStepDeParada(maxSteps);
            
            //treino
            long timeStart = System.currentTimeMillis();
            Map<String, String> politicaGerada = qL.treinar(qtdEpisodios, alfa, gama);
            long timeEnd = System.currentTimeMillis();
            long tempo = (timeEnd - timeStart) / 1000; //em segundos
            
            //gera arquivos da política e da tabelaQ
            String extraInfo = "Episodios encerrados por estado terminal: " + qL.getQtdEpisodiosEncerradosPorFimDeJogo() + 
                               "\nMédia de movimentações (steps): " + qL.getMediaSteps();
            Util.gerarArquivosDoTreino(politicaGerada, qL.getTabelaValorEstadoAcao(),
                    niveis, qtdEpisodios, alfa, gama, (double)episilon, tempo, 
                    "Q-Learning", extraInfo);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * @return the stepsPorEpisodio
     */
    public String getStepsPorEpisodio() {
        return stepsPorEpisodio;
    }

    /**
     * @return the episolon
     */
    public int getEpisilon() {
        return episilon;
    }

    /**
     * Define a porcentagem da ação ótima da política E-gulosa. Deve ser um
     * valor entre 0 e 100.
     * @param episolon the episolon to set
     */
    public void setEpisilon(int episolon) {
        this.episilon = episolon;
    }

}
