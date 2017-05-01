/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package astar;

import alexandrelima.ladrilho.dominio.ControleLadrilho;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

/**
 *
 * @author Alexandre
 */
public class MyAStar {
    private String startS, //estado inicial
                   endS; //estado final
    private PriorityQueue<String> openList;
    private Set<String> closedList;
    private ControleLadrilho jogo;
    private HashMap<String, Integer> custosS = new HashMap<String, Integer>();
    private Map<String, String> parents = new HashMap<String, String>(); //guarda os pais dos estados. key = estado, value = pai

    public MyAStar(ControleLadrilho jogo) {
        this.jogo = jogo;
        AStarComparator comparator = new AStarComparator();
        openList = new PriorityQueue<String>(10, comparator);
        closedList = new HashSet<String>();
    }
    
    public void buscar(){
        this.startS = jogo.getString();
        this.endS = jogo.getEstadoFinal();
        ControleLadrilho auxJogo = new ControleLadrilho(jogo.getDimensao());
        custosS.clear();
        custosS.put(startS, 0);
        parents.clear();
        openList.clear();
        closedList.clear();
        
        openList.add(startS);
        while(!openList.isEmpty()){
            String s = openList.remove();
            closedList.add(s);
            if(s.equals(endS)){
                break;
            }
            auxJogo.setEstado(s);
            List<String> sSeguintes = auxJogo.getPossiveisEstadosSeguintes(); //expansão de s
            for(String sLinha : sSeguintes){
                int custoSLinha = custosS.get(s) + 1 + heuristica(sLinha);
                if((closedList.contains(sLinha) || openList.contains(sLinha)) && custoSLinha < custosS.get(sLinha)){
                    custosS.put(sLinha, custoSLinha);
                }
                else if(!openList.contains(sLinha) && !closedList.contains(sLinha)){
                    custosS.put(sLinha, custoSLinha);
                    openList.add(sLinha);
                    parents.put(sLinha, s);
                }
            }
        }
    }
    
    private int heuristica(String estado){
        String[] ladrs = estado.split(" ");
        int dim = (int)Math.sqrt(ladrs.length);
        int soma = 0;
        for(int i = 0; i < ladrs.length; i++){
            int img = Integer.parseInt(ladrs[i]);
            int linhaDest = img / dim;
            int colDest = img % dim;
            int linhaAtual = i / dim;
            int colAtual = i % dim;
            soma += Math.abs(linhaDest - linhaAtual) + Math.abs(colDest - colAtual);
        }
        return soma;
    }
    
    /**
     * Retorna uma lista com a seqüência de ações que devem ser tomadas para a solução do 
     * problema. Note que este método somente deve ser chamado após a busca ter sido realizada.
     * @return
     */
    public List<Integer> getAcoes() {
        List<String> seq = getEstadosPercorridos();
        List<Integer> acoes = new ArrayList<Integer>(seq.size() - 1);
        ControleLadrilho auxJogo = new ControleLadrilho(jogo.getDimensao());
        for(int i = 1; i < seq.size(); i++){
            String s = seq.get(i - 1);
            String sLinha = seq.get(i);
            auxJogo.setEstado(s);
            for(Integer a : auxJogo.getMovimentosPossiveis()){
                auxJogo.setEstado(s);
                auxJogo.moverLadrilho(a);
                if(sLinha.equals(auxJogo.getString())){
                    acoes.add(a);
                    break;
                }
            }
        }
        return acoes;
    }
    
    /**
     * Retorna a lista de estados pecorridos do início até o fim. Note que 
     * este método somente deve ser utilizados após a busca ter sido realizada.
     * @return
     */
    public List<String> getEstadosPercorridos(){
        List<String> caminho = new ArrayList<String>();
        String atual = endS;
        caminho.add(atual);
        do{
            atual = parents.get(atual);
            caminho.add(atual);
        }while(!startS.equals(atual) && atual != null);
        Collections.reverse(caminho);
        return caminho;
    }
    
    public static void main(String[] args) {
        String start = "1 0 6 7 2 4 8 3 5";
        ControleLadrilho game = new ControleLadrilho(3);
        game.setEstado(start);
        MyAStar star = new MyAStar(game);
        System.out.println(game.getString());
        star.buscar();
        System.out.println(game.getString());
        List<Integer> acoesTomadas = star.getAcoes();
        System.out.println("AÇÕES: " + acoesTomadas);
        System.out.println("ESTADOS PERCORRIDOS: " + star.getEstadosPercorridos().size());
        
        System.out.println("\n*** Teste ações ***");
        game.setEstado(start);
        System.out.println("> START: " + game.getString());
        for(Integer a : star.getAcoes()){
            game.moverLadrilho(a);
        }
        System.out.println("> END: " + game.getString());
    }
    
    /**
     * Comparador utilizado na fila de prioridade
     */
    class AStarComparator implements Comparator<String>{

        public int compare(String s1, String s2) {
            int pesoS1 = custosS.get(s1);
            int pesoS2 = custosS.get(s2);
            return pesoS1 - pesoS2;
        }
    }
}
