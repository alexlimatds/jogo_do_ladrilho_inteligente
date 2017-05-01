/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package teste;

import alexandrelima.ladrilho.dominio.ControleLadrilho;
import alexandrelima.ladrilho.ia.Util;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

/**
 *
 * @author Alexandre
 */
public class BateriaDeTesteQLearning {
    
    public void executar(String fileName) throws IOException {
        //lê a política
        Map<String, String> politica = Util.lerPolitica(fileName);

        int jogosGanhos = 0;
        int totalMovimentos = 0; //apenas das partidas ganhas
        int encerradasPorLimite = 0;
        int encerradasPorFaltaDeAcao = 0;
        ControleLadrilho game = new ControleLadrilho(3);
        int limite = 400;
        int qtdJogos = 0;
        long time = 0;

        BufferedReader reader = new BufferedReader(new FileReader("partidas.txt"));
        String line = reader.readLine();
        while(line != null && !line.trim().equals("") ){
            game.setEstado(line);
            String estado = game.getString();
            String acao = politica.get(estado);
            long startTime = System.currentTimeMillis();
            while(acao != null && !game.isGameOver() && game.getCliques() <= limite){
                    game.moverLadrilho(Integer.parseInt(acao));
                estado = game.getString();
                acao = politica.get(estado);
            }
            long endTime = System.currentTimeMillis();
            if(game.isGameOver()){
                jogosGanhos++;
                totalMovimentos += game.getCliques();
                time += endTime - startTime;
            }
            else if(game.getCliques() > limite){
                encerradasPorLimite++;
            }
            else if(acao == null){
                encerradasPorFaltaDeAcao++;
            }
            qtdJogos++;
            line = reader.readLine();
        }
        double mediaMovimentos = jogosGanhos == 0?0:(totalMovimentos / (double)jogosGanhos);
        String relatorio = "Arquivo de política: " + fileName + "\n" +
                        "Total de partidas realizadas: " + qtdJogos + "\n" +
                        "Partidas ganhas: " + jogosGanhos + "\n" +
                        "Média de tempo por partida ganha: " + (time / 1000.0 / qtdJogos) + " segundos\n" +
                        "Média de movimentos por partida ganha: " + mediaMovimentos + "\n" +
                        "Partidas encerradas por limite de movimentações: " + encerradasPorLimite + "\n" +
                        "Partidas encerradas por falta de ação: " + encerradasPorFaltaDeAcao + "\n" +
                        "Limite de movimentações: " + limite + "\n";

        System.out.println("** RESULTADOS **\n" + relatorio);
    }
    
    public static void main(String[] args) {
        try{
            //lê a política
            String fileName = "2014-07-06/1404681217585-pol-3N-50ep-Q-Learning.txt";
            
            BateriaDeTesteQLearning bateria = new BateriaDeTesteQLearning();
            bateria.executar(fileName);
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }

}
