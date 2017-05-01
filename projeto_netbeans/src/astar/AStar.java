/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package astar;

import alexandrelima.ladrilho.dominio.ControleLadrilho;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

/**
 *
 * @author Alexandre
 */
public class AStar {
    
    private static String solucao = "0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15";
    
    private PriorityQueue<No> fila = new PriorityQueue<No>(10, new ComparadorNo());
    private int tamanhoFila = 0;
    private int tamanhoFilaMax = 0;
    private int nosExpandidos = 0;
    private double custoSolucao = 0;
    private Set<String> processados = new HashSet<String>();
    private ControleLadrilho auxJogo;
    
    public AStar(){
        auxJogo = new ControleLadrilho(4);
    }
    
    public static int heuristica(String estado){
        String[] ladrs = estado.split(" ");
        int soma = 0;
        for(int i = 0; i < ladrs.length; i++){
            int img = Integer.parseInt(ladrs[i]);
            int linhaDest = img / 4;
            int colDest = img % 4;
            int linhaAtual = i / 4;
            int colAtual = i % 4;
            soma += Math.abs(linhaDest - linhaAtual) + Math.abs(colDest - colAtual);
        }
        return soma;
    }
    
    public No iniciarBusca(String estadoInicial){
        No noInicial = new No();
        noInicial.estado = estadoInicial;
        fila.add(noInicial);
        tamanhoFila = 1;
        while(!fila.isEmpty()){
            tamanhoFila--;
            No no = fila.remove();
            if(solucao.equals(no.estado)){
                custoSolucao = no.custoCaminho;
                return no;
            }
            adicionarNosAlternativosNafila(no);
            tamanhoFila = fila.size();
            if(tamanhoFilaMax < tamanhoFila){ //estatística
                tamanhoFilaMax = tamanhoFila;
            }
        }
        return null; //falhou
    }
    
    /**
     * Expande um nó adicionando seus nós subsequentes na fila.
     * @param no nó a ser expandido.
     */
    private void adicionarNosAlternativosNafila(No no){
        if(!processados.contains(no.estado)){
            processados.add(no.estado);
            List<No> expandidos = expandirNos(no);
            for(No n : expandidos){
                fila.add(n);
            }
        }
    }
    
    /**
     * Expande um nó jutamente com seus sucessores.
     * @param no
     * @return
     */
    private List<No> expandirNos(No no){
        List<No> nos = new ArrayList<No>();
        auxJogo.setEstado(no.estado);
        List<Integer> acoes = auxJogo.getMovimentosPossiveis();
        for(int acao : acoes){
            auxJogo.setEstado(no.estado);
            auxJogo.moverLadrilho(acao);
            No no0 = new No();
            no0.noPai = no;
            no0.estado = auxJogo.getString();
            no0.step = no.step + 1;
            no0.acao = acao;
            no0.custoStep = 1.0;
            no0.custoCaminho += no0.noPai.custoCaminho + 1.0;
            nos.add(no0);
        }
        nosExpandidos++;
        
        return nos;
    }

    public double getCustoSolucao() {
        return custoSolucao;
    }

    public int getNosExpandidos() {
        return nosExpandidos;
    }
}
