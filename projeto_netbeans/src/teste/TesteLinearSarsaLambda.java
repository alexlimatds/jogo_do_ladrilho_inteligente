/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package teste;

import alexandrelima.ladrilho.dominio.ControleLadrilho;
import alexandrelima.ladrilho.ia.ConversorEstadoFeatures;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;

/**
 *
 * @author Alexandre
 */
public class TesteLinearSarsaLambda {
    
    public static void main(String[] args) {
        try{
            int qtdJogos = 1000;

            testar(qtdJogos, "vetor-teta-3N-1000ep.txt", 40000, false);
            
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
     * @param vectorFileName Nome do arquivo com os valores peso do vetor teta
     * @param limite Indica a quantidade máxima de movimentos em uma partida. Caso a quantidade de 
     * movimentos atinja este limite, a partida é interrompida. Útil para evitar partidas em loop infinito.
     * @param mostrarJogo Indica se a configuração do jogo deve ser exibida ao final de uma partida.
     */
    public static String testar(int qtdJogos, String vectorFileName, int limite,
            boolean mostrarJogo) throws Exception {
        try{
            int dimensaoJogo = 3;
            //lê vetor
            double[] vTeta = new double[dimensaoJogo * dimensaoJogo * 2];
            BufferedReader reader = new BufferedReader(new FileReader(vectorFileName));
            String line = reader.readLine();
            int j = 0;
            while(line != null && !line.trim().equals("") ){
                vTeta[j] = Double.parseDouble(line);
                line = reader.readLine();
                j++;
            }
            FuncaoEstadoAcao funcaoQ = new FuncaoEstadoAcao(vTeta);

            /*System.out.println("Q: "+ funcaoQ.getValue(new int[]{1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1})); //melhor Q
            System.out.println("Q: "+ funcaoQ.getValue(new int[]{1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0}));
            System.out.println("Q: "+ funcaoQ.getValue(new int[]{1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0}));
            System.out.println("Q: "+ funcaoQ.getValue(new int[]{1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0}));
            System.out.println("Q: "+ funcaoQ.getValue(new int[]{1, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0}));*/


            int jogosGanhos = 0;
            int totalMovimentos = 0; //apenas das partidas ganhas
            int encerradasPorLimite = 0;
            ControleLadrilho game = new ControleLadrilho(dimensaoJogo);
            for(int i = 0; i < qtdJogos; i++){
                game.reiniciar();
                if(mostrarJogo){
                    System.out.println(game.getVisualizacao());
                }
                do{
                    int[] input;
                    //procura a ação ótima
                    List<Integer> acoes = game.getMovimentosPossiveis();
                    int acao = -1;
                    double q = Double.NEGATIVE_INFINITY;
                    //System.out.println("> STEP ");
                    for(Integer a : acoes){
                        input = ConversorEstadoFeatures.getFeatures(game.getLadrilhos(), a);
                        double q2 = funcaoQ.getValue(input);
                        //System.out.println(" Q("+ a +") = " + q2);
                        if(q2 > q){
                            q = q2;
                            acao = a;
                        }
                    }
                    /*System.out.println(game.getString());
                    System.out.println(" ação tomada: " + acao);*/
                    game.moverLadrilho(acao);
                }while(!game.isGameOver() && game.getCliques() <= limite);
                if(game.isGameOver()){
                    jogosGanhos++;
                    totalMovimentos += game.getCliques();
                }
                else if(game.getCliques() > limite){
                    encerradasPorLimite++;
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
                         "Limite de movimentações: " + limite + "\n" +
                         "Arquivo de política utilizado: " + vectorFileName;
            
            System.out.println("** RESULTADOS **\n" + relatorio);
            return relatorio;
        }
        catch(Exception ex){
            ex.printStackTrace();
            throw ex;
        }
    }
    
}

class FuncaoEstadoAcao{
    private double[] vTeta;

    public FuncaoEstadoAcao(double[] vTeta) {
        this.vTeta = vTeta;
    }

    public double getValue(int[] input){
        if(input.length != vTeta.length){
            throw new IllegalArgumentException("Tamanho inválido do vetor de entrada");
        }
        double soma = 0;
        for(int i = 0; i < input.length; i++){
            soma += vTeta[i] * input[i];
        }
        return soma;
    }
}