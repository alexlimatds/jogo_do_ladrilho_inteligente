/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package teste;

import alexandrelima.ladrilho.dominio.ControleLadrilho;
import alexandrelima.ladrilho.ia.Util;
import java.util.Map;

/**
 *
 * @author Alexandre
 */
public class Teste3Dimensoes {
    
    public static void main(String[] args) {
        try{
            int qtdJogos = 10000;

            testar(qtdJogos, "pol-3N-150ep.txt", 4000, false);
            
            /*String rel1 = testar(qtdJogos, "pol-3N-10ep.txt", 10000, false);
            String rel2 = testar(qtdJogos, "pol-3N-100ep.txt", 10000, false);
            String rel3 = testar(qtdJogos, "pol-3N-1000ep.txt", 10000, false);
            String rel4 = testar(qtdJogos, "pol-3N-10000ep-QL.txt", 1000, false);
            String rel5 = testar(qtdJogos, "pol-3N-100000ep.txt", 10000, false);*/
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }
    
    /**
     * Executa uma bateria de testes e retorna uma string com estatísticas.
     * @param qtdJogos Quantidade de partidas a serem realizada da bateria.
     * @param polFileName Nome do arquivo com a política a ser utilizada.
     * @param limite Indica a quantidade máxima de movimentos em uma partida. Caso a quantidade de 
     * movimentos atinja este limite, a partida é interrompida. Útil para evitar partidas em loop infinito.
     * @param mostrarJogo Indica se a configuração do jogo deve ser exibida ao final de uma partida.
     */
    public static String testar(int qtdJogos, String polFileName, int limite, 
            boolean mostrarJogo) throws Exception {
        try{
            //lê a política
            Map<String, String> politica = Util.lerPolitica(polFileName);
            
            int jogosGanhos = 0;
            int totalMovimentos = 0; //apenas das partidas ganhas
            int encerradasPorLimite = 0;
            int encerradasPorFaltaDeAcao = 0;
            ControleLadrilho game = new ControleLadrilho(3);
            for(int i = 0; i < qtdJogos; i++){
                game.reiniciar();
                if(mostrarJogo){
                    System.out.println(game.getVisualizacao());
                }
                String estado = game.getString();
                String acao = politica.get(estado);
                while(acao != null && !game.isGameOver() && game.getCliques() <= limite){
                        game.moverLadrilho(Integer.parseInt(acao));
                    estado = game.getString();
                    acao = politica.get(estado);
                }
                if(game.isGameOver()){
                    jogosGanhos++;
                    totalMovimentos += game.getCliques();
                }
                else if(game.getCliques() > limite){
                    encerradasPorLimite++;
                }
                else if(acao == null){
                    encerradasPorFaltaDeAcao++;
                }
                if(mostrarJogo){
                    System.out.println(game.getVisualizacao());
                }
            }
            double mediaMovimentos = jogosGanhos == 0?0:(totalMovimentos / (double)jogosGanhos);
            String relatorio = "Total de partidas realizadas: " + qtdJogos + "\n" +
                         "Partidas ganhas: " + jogosGanhos + "\n" +
                         "Média de movimentos por partida ganha: " + mediaMovimentos + "\n" +
                         "Partidas encerradas por limite de movimentações: " + encerradasPorLimite + "\n" +
                         "Partidas encerradas por falta de ação: " + encerradasPorFaltaDeAcao + "\n" +
                         "Limite de movimentações: " + limite + "\n" +
                         "Arquivo de política utilizado: " + polFileName;
            
            System.out.println("** RESULTADOS **\n" + relatorio);
            return relatorio;
        }
        catch(Exception ex){
            ex.printStackTrace();
            throw ex;
        }
    }
    
}
