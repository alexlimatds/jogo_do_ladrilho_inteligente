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
import java.util.Set;

/**
 * Classe que representa o algoritmo Sarsa para o jogo do ladrilho.
 * @author Alexandre
 */
public class Sarsa {
    
    private ControleLadrilho jogo, auxGame;
    private Map<String, TabelaValorEstadoAcao> tabelaQ; //tabela com valores estado ação
    private Random random;
    private boolean echo; //indica se deve imprimir as mensagens informativas
    private String stepsPorEpisodio; //guarda a evolução de steps por episódio no formato: num episódio, qdt steps
    private int episilon = 50; //porcentagem de escolha (entre zero e cem) da ação ótima da política E-gulosa

    /**
     * Cria uma nova instância desta classe
     * @param jog   O jogo do ladrilho a ser utilizado
     */
    public Sarsa(ControleLadrilho jog) {
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
     * Retorna uma ação disponível no estado atual do jogo. A açao retornada é
     * escolhida com base em uma política E-gulosa.
     * @return
     */
    private String getAcaoEGulosa(){
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
    }
    
    /**
     * Inicializa os valores de um estado na tabelaQ. Caso o estado tenha sido 
     * inicilizado anteriormente, não altera os valores de sua tabela.
     * @param s Estado a ser inicializado
     */
    private void inicializarEstado(String s){
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
        tabelaQ = new HashMap<String, TabelaValorEstadoAcao>();
        double alfa2 = alfa;
        StringBuilder bufferSteps = new StringBuilder();
        for(int i = 0; i < qtdEpisodios; i++){
            jogo.reiniciar();
            String s = jogo.getString();
            String a = getAcaoEGulosa();
            int step = 1;
            while(!jogo.isGameOver()){
                jogo.moverLadrilho(Integer.parseInt(a));
                String sLinha = jogo.getString();
                //RETORNO DA AÇÃO
                double r = getRecompensa();
                String aLinha = getAcaoEGulosa(); //estado atual é sLinha
                inicializarEstado(s);
                inicializarEstado(sLinha);
                TabelaValorEstadoAcao qS = tabelaQ.get(s);
                TabelaValorEstadoAcao qSLinha = tabelaQ.get(sLinha);
                double novoValorQs = qS.getValor(a) + alfa2 * (r + gama * qSLinha.getValor(aLinha) - qS.getValor(a));
                qS.inserirValor(a, novoValorQs);
                s = sLinha;
                a = aLinha;
                step++;
            }
            if(echo){
                if(jogo.isGameOver()){
                    System.out.println("Episódio "+ i +" terminado após atingir estado final ("+ (step - 1) +" steps)");
                }
            }
            bufferSteps.append(i + 1);
            bufferSteps.append(',');
            bufferSteps.append(step - 1);
            bufferSteps.append('\n');
        }
        stepsPorEpisodio = bufferSteps.toString();
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
            int qtdEpisodios = 400;
            int niveis = 3;
            double alfa = 0.05;
            double gama = 1;
            int episilon = 40;
            
            ControleLadrilho jogo = new ControleLadrilho(niveis);
            Sarsa qL = new Sarsa(jogo);
            qL.setEcho(false);
            qL.setEpisilon(episilon);
            
            //treino
            long timeStart = System.currentTimeMillis();
            Map<String, String> politicaGerada = qL.treinar(qtdEpisodios, alfa, gama);
            long timeEnd = System.currentTimeMillis();
            long tempo = (timeEnd - timeStart) / 1000; //em segundos
            
            //gera arquivos da política e da tabelaQ
            Util.gerarArquivosDoTreino(politicaGerada, qL.getTabelaValorEstadoAcao(),
                    niveis, qtdEpisodios, alfa, gama, (double)episilon, tempo, 
                    "Sarsa", null);
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
