/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package alexandrelima.ladrilho.ia;

import java.util.Set;

/**
 * Política de treinamento epsilon-gulosa clássica.
 * @author alexandre
 */
public class PoliticaEpsilonGulosa implements PoliticaTreinamento {
    
    private QLearning qLearning;

    public PoliticaEpsilonGulosa(QLearning qLearning) {
        this.qLearning = qLearning;
    }
    
    /**
     * Retorna uma ação disponível no estado atual do jogo. A açao retornada é
     * escolhida com base em uma política E-gulosa.
     * @return
     */
    @Override
    public String getAcao(){
        String s = qLearning.jogo.getString();
        qLearning.inicializarEstado(s); //se s já foi incializado NÃO inicializa novamente
        TabelaValorEstadoAcao tableS = qLearning.tabelaQ.get(s);
        String maxAction = tableS.getAcaoValorMaximo();
        int roleta = qLearning.random.nextInt(100) + 1; //gera um número entre 1 e 100
        if(roleta <= qLearning.getEpisilon()){
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
        roleta = qLearning.random.nextInt(acoes.length);//gera um número entre 0 e (acoes.size() - 1)
        return (String)acoes[roleta];
    }

    public void novoEpisodio() {
    }
}
