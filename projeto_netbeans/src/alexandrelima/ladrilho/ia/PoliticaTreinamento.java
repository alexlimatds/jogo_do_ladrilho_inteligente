package alexandrelima.ladrilho.ia;

/**
 * Representa uma política de escolha de ação utilizada durante o treinamento do agente.
 * @author alexandre
 */
public interface PoliticaTreinamento {

    /**
     * Retorna uma ação disponível no estado atual do jogo. A açao retornada é
     * escolhida com base em uma política E-gulosa.
     * @return
     */
    String getAcao();
    
    /**
     * Informa esta política de que um novo episódio de treinamento foi iniciado.
     */
    void novoEpisodio();
}
