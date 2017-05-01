/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package teste;

import astar.AStar;
import astar.No;

/**
 *
 * @author Alexandre
 */
public class TestesAStar {
    public static void main(String[] args) {
        try{
            AStar busca = new AStar();
            String estadoInicial = "13 14 1 8 4 3 2 15 6 10 12 0 5 9 7 11";
            long startTime = System.currentTimeMillis();
            No no = busca.iniciarBusca(estadoInicial);
            long endTime = System.currentTimeMillis();
            System.out.println("Tempo: " + ((endTime - startTime) / 1000) + " s" );
            if(no == null){
                System.out.println("Não encontrou solução para o jogo");
            }
            else{
                System.out.println("Estado final: " + no.estado);
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }

}
