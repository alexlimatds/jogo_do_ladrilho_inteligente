/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package alexandrelima.ladrilho.ia;

import alexandrelima.ladrilho.dominio.ControleLadrilho;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Classe que representa o algoritmo Linear gradient-descendent Sarsa Lambda para o jogo do ladrilho.
 * @author Alexandre
 */
public class LinearSarsa {
    
    private ControleLadrilho jogo, auxGame;
    private Random random;
    private boolean echo; //indica se deve imprimir as mensagens informativas
    private String stepsPorEpisodio; //guarda a evolução de steps por episódio no formato: num episódio, qdt steps
    private double episilon = 50; //porcentagem de escolha (entre zero e cem) da ação ótima da política E-gulosa
    private double[] vTeta;
    private int sizeVetor; //tamanho do vetor teta, do vetor de traces e do vetor de features

    /**
     * Cria uma nova instância desta classe
     * @param jog   O jogo do ladrilho a ser utilizado
     */
    public LinearSarsa(ControleLadrilho jog) {
        this.jogo = jog;
        this.auxGame = new ControleLadrilho(jog.getDimensao());
        random = new Random();
        sizeVetor = jog.getDimensao() * jog.getDimensao() * 2;
        vTeta = new double[sizeVetor];
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
    private int getAcaoAleatoria(){
        List<Integer> acoes = jogo.getMovimentosPossiveis();
        int roleta = random.nextInt(acoes.size());//gera um número entre 0 e (acoes.size() - 1)
        return acoes.get(roleta);
    }

    /**
     * Retorna a recompensa da ação que levou ao estado atual do jogo.
     * @return 100 caso o jogo tenha sido solucionado e zero em caso contrário.
     */
    public double getRecompensa(){
        if(jogo.isGameOver()){
            return 1000;
        }
        return 0;
    }

    private void zeraVetor(int[] v){
        for(int i = 0; i < v.length; i++){
            v[i] = 0;
        }
    }

    private void zeraVetor(double[] v){
        for(int i = 0; i < v.length; i++){
            v[i] = 0;
        }
    }

    private int getSomaVetorTeta(int[] features){
        int soma = 0;
        for(int i = 0; i < features.length; i++){
            if(features[i] != 0){
                soma += vTeta[i] * features[i];
            }
        }
        return soma;
    }

    private void multiplicaVetorPorEscalar(double[] vector, double escalar){
        double[] antes = Arrays.copyOf(vector, vector.length); //for debug

        for(int i = 0; i < vector.length; i++){
            vector[i] *= escalar;
        }

        for(double vl : vector){ //for debug
            if(Double.isNaN(vl) || Double.isInfinite(vl)){
                System.out.print("Antes: ");
                printVector(antes);
                System.out.print("Depois: ");
                printVector(vector);
                break;
            }
        }
    }

    private void initTeta(){
        for(int i = 0; i < vTeta.length; i++){
            vTeta[i] = random.nextDouble() * 10;
        }
    }

    private int[] getFeaturesEstadoAtual(int a){
        int[] f = Arrays.copyOf(jogo.getLadrilhos(), sizeVetor);
        f[sizeVetor - 1] = a;
        return f;
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
    public double[] treinar(int qtdEpisodios, double alfa, double gama){
        //inicializa vetor teta
        initTeta();
        double alfa2 = alfa;
        StringBuilder bufferSteps = new StringBuilder();
        for(int i = 0; i < qtdEpisodios; i++){
            jogo.reiniciar();
            String s = jogo.getString();
            int a = getAcaoAleatoria();
            int[] features = ConversorEstadoFeatures2.getFeatures(jogo.getLadrilhos(), a);
            int step = 1;
            while(!jogo.isGameOver()){
                jogo.moverLadrilho(a); //take action a
                s = jogo.getString();
                double r = getRecompensa();
                //calcula erro
                double erro = r - getSomaVetorTeta(features);
                double q = 0;
                if(random.nextDouble() <= episilon){ //with probability 1 - E
                    List<Integer> actions = jogo.getMovimentosPossiveis();
                    double maxValue = Double.NEGATIVE_INFINITY;
                    int maxAction = -1;
                    for(int action : actions){
                        int[] f = ConversorEstadoFeatures2.getFeatures(jogo.getLadrilhos(), action);
                        q = getSomaVetorTeta(f);
                        if(q > maxValue){
                            maxValue = q;
                            maxAction = action;
                        }
                    }
                    a = maxAction;
                }
                else{
                    a = getAcaoAleatoria();
                    int[] f = ConversorEstadoFeatures2.getFeatures(jogo.getLadrilhos(), a);
                    q = getSomaVetorTeta(f);
                }
                erro = erro + gama * q;
                features = getFeaturesEstadoAtual(a);
                for(int j = 0; j < vTeta.length; j++){
                    vTeta[j] += erro * alfa2 * features[j];
                }
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
        printTeta();
        stepsPorEpisodio = bufferSteps.toString();
        return Arrays.copyOf(vTeta, vTeta.length);
    }

    private void printTeta(){
        System.out.print("> TETA: ");
        for(double v : vTeta)
            System.out.print(v + "; ");
        System.out.println("");
    }

    private void printVector(double[] v){
        for(double vl : v)
            System.out.print(vl + "; ");
        System.out.println("");
    }

    public static void main(String[] args) {
        try {
            int qtdEpisodios = 1000;
            int niveis = 3;
            double alfa = 0.05;
            double gama = 1;
            double episilon = 0.1;
            
            ControleLadrilho jogo = new ControleLadrilho(niveis);
            LinearSarsa qL = new LinearSarsa(jogo);
            qL.setEcho(true);
            qL.setEpisilon(episilon);
            
            //treino
            long timeStart = System.currentTimeMillis();
            double[] vTeta = qL.treinar(qtdEpisodios, alfa, gama);
            long timeEnd = System.currentTimeMillis();
            long tempo = (timeEnd - timeStart) / 1000; //em segundos
            
            //gera arquivos com valores de teta
            Util.gerarArquivoVetorTeta(vTeta, niveis, qtdEpisodios, alfa, gama, 
                    episilon, tempo, "Linear Sarsa", "");
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }

    /**
     * @return the stepsPorEpisodio
     */
    public String getStepsPorEpisodio() {
        return stepsPorEpisodio;
    }

    private List<Integer> getAcoesDisponiveis(String s){
        auxGame.setEstado(s);
        return auxGame.getMovimentosPossiveis();
    }

    /**
     * @return the episolon
     */
    public double getEpisilon() {
        return episilon;
    }

    /**
     * Define a porcentagem da ação ótima da política E-gulosa. Deve ser um
     * valor entre 0 e 1.
     * @param episolon the episolon to set
     */
    public void setEpisilon(double episolon) {
        this.episilon = episolon;
    }
    
}