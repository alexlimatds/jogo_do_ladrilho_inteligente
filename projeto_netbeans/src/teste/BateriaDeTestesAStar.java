/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package teste;

import alexandrelima.ladrilho.dominio.ControleLadrilho;
import astar.MyAStar;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;

/**
 *
 * @author Alexandre
 */
public class BateriaDeTestesAStar {
    
    public static void main(String[] args) {
        try{
            ControleLadrilho jogo = new ControleLadrilho(3);
            MyAStar star = new MyAStar(jogo);
            long time = 0;
            int qtdJogos = 0;
            int qtdMovimentacoes = 0;
            BufferedReader reader = new BufferedReader(new FileReader("partidas.txt"));
            String line = reader.readLine();
            int i = 1;
            while(line != null && !line.trim().equals("") ){
                jogo.setEstado(line);
                long startTime = System.currentTimeMillis();
                star.buscar();
                long endTime = System.currentTimeMillis();
                time += endTime - startTime;
                
                List<String> estados = star.getEstadosPercorridos();
                String estadoFinal = estados.get(estados.size() - 1);
                if(!jogo.getEstadoFinal().equals(estadoFinal)){
                    System.out.println("Erro: não chegou ao estado final");
                }
                else{
                    qtdJogos++;
                    qtdMovimentacoes += (estados.size() - 1);
                }
                
                line = reader.readLine();
                System.out.println("EP: " + i);
                i++;
            }
            double timeInSeconds = time / 1000.0;
            double mediaTempo = timeInSeconds / qtdJogos;
            double mediaMovimentos = qtdMovimentacoes / qtdJogos;
            System.out.println("Tempo total: " + timeInSeconds + " segundos");
            System.out.println("Média de tempo por jogo: " + mediaTempo + " segundos");
            System.out.println("Média de movimentos por jogo: " + mediaMovimentos + " segundos");
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }

}
