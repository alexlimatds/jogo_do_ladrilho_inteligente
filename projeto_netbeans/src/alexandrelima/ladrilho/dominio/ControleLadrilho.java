/*
 * ControleLadrilho.java
 *
 * Created dezembro de 2008, 23:09
 */

package alexandrelima.ladrilho.dominio;

import java.util.List;
import java.util.ArrayList;

/**
 *  O jogo do ladrilho � formado por 16 ladrilhos, dispostos em quatro linhas e quatro colunas. Tais
 *  ladrilhos s�o armazenados em um array de ladrilhos (uma inst�ncia de <code>Ladrilho[]</code>).
 *  As imagens dos ladrilhos s�o armazenadas em uma cole��o (uma inst�ncia de <code>java.util.List</code>).
 *  O objetivo do jogo � colocar todas as imagens na ordem correta. Em termos de programa��o, isto
 *  significa que a imagem do ladrilho no �ndice zero deve ser a imagem no �ndice da lista, e assim por
 *  diante. Cada ladrilho possui um �ndice interno para facilitar a identifica��o de sua posi��o. Eles
 *  s�o dispostos na ordem indicada na tabela abaixo.
 *  <table border="1">
 *      <tr>
 *          <td>0</td><td>1</td><td>2</td><td>3</td>
 *      </tr>
 *      <tr>
 *          <td>4</td><td>5</td><td>6</td><td>7</td>
 *      </tr>
 *      <tr>
 *          <td>8</td><td>9</td><td>10</td><td>11</td>
 *      </tr>
 *      <tr>
 *          <td>12</td><td>13</td><td>14</td><td>15</td>
 *      </tr>
 *  </table>
 *
 * Este c�digo � propriedade intelectual do autor. � permitida a livre utiliza��o para fins 
 * did�ticos e educacionais sem fins lucrativos. Para mais informa��es, consulte o autor.
 *
 * @author  Alexandre Gomes de Lima - alexlouco@zipmail.com.br
 */
public class ControleLadrilho {
    
    private int[] ladrilhos;
    private boolean gameOver;
    private int cliques;
    private int dimensao; //indica a dimensão do jogo (qtd de peças em uma linha ou coluna)
    private int posicaoCalhau; //indica a posição do calhau (posição sem peça)
    private int qtdPecas;
    private String estadoFinal; //guarda a configuração que determina fim de jogo
    
    /** 
     * Cria uma nova inst�ncia de ControleLadrilho.
     * @param dim       A dimensão do jogo, ou seja, a quantidade de peças em uma linha ou coluna.
     * @exception       <code>IllegalArgumentException</code> caso <code>dim</code> seja
     *                  inferior a três.
     */
    public ControleLadrilho(int dim){
        if(dim < 3){
            throw new IllegalArgumentException("Dimensão não pode ser inferior a três.");
        }
        this.dimensao = dim;
        this.qtdPecas = dimensao * dimensao;
        this.posicaoCalhau = qtdPecas - 1;
        this.gameOver = false;
        this.cliques = 0;
        this.ladrilhos = new int[qtdPecas];
        estadoFinal = "";
        for(int i = 0; i < ladrilhos.length; i++){
            ladrilhos[i] = i;
            estadoFinal += i + " ";
        }
        estadoFinal = estadoFinal.trim();
        //definindo aleatoriamente a imagem de cada ladrilho
        this.definirImagens(20000);
    }
    
    /**
     * Reinicia o jogo, isto �, redefine aleatoriamente a imagem de cada ladrilho, zera o n�mero de
     * cliques e define a vari�vel <code>gameOver</code> como <code>false</code>.
     */
    public void reiniciar(){
        this.definirImagens(20000);
        this.cliques = 0;
        this.gameOver = false;
    }
    
    /**
     * Reinicia o jogo zerando a quantidade de movimentações feitas pelo jogador 
     * e misturando os ladrinhos. A mistura é feita movimentando aleatoriamente os 
     * ladrilhos a partir da configuração final.
     * @param movimentos 
     */
    public void reiniciar(int movimentos){
        this.definirImagens(movimentos);
        this.cliques = 0;
        this.gameOver = false;
    }
    
    /**
     * Move um ladrilho para algum espa�o adjacente que esteja vazio. Este movimento somente ocorre caso algum
     * dos ladrilhos adjacentes esteja vazio, isto �, sua imagem seja a �ltima da lista de imagens. Em termos 
     * de programa��o, as imagens dos ladrilhos s�o trocadas.
     * @param   indice  �ndice do ladrilho a ser movido.
     */
    public boolean moverLadrilho(int indice) {
        List<Integer> movs = getMovimentosPossiveis();
        if(movs.contains(indice)){
            trocaImagens(getPosicaoCalhau(), indice);
            contaMovimento();
            return true;
        }
        return false;
    }
    
    /**
     * Retorna a posição do calhau.
     * @return
     */
    public int getPosicaoCalhau(){
        int posCalhau = 0;
        for(int i = 0; i < ladrilhos.length; i++){ //descobre a posição do calhau
            if(ladrilhos[i] == (qtdPecas - 1)){
                posCalhau = i;
                break;
            }
        }
        return posCalhau;
    }
    
    /**
     * Contabiliza um movimento realizado.
     */
    private void contaMovimento(){
        this.cliques++; //incrementa o numero de cliques
        this.verificaJogo();
    }
    
    /**
     * Troca as imagens de dois ladarilhos.
     * @param la Índice de um dos ladrilhos.
     * @param lb Índice do outro ladrilho.
     */
    private void trocaImagens(int la, int lb){
        int temp = ladrilhos[la];
        ladrilhos[la] = ladrilhos[lb];
        ladrilhos[lb] = temp;
    }
    
    //define, de forma aleat�ria, a imagem de cada ladrilho.
    private void definirImagens(int movimentos){
        /* para garantir que não será gerada uma configuração impossível de solucionar, 
         * o estado aleatório é definido por movimentações feitas a partir do estado final */
        setEstado(getEstadoFinal());
        for(int i = 0; i < movimentos; i++){
            List<Integer> acoes = getMovimentosPossiveis();
            //sorteia uma ação aleatoriamente
            int indiceAcao = new Double( Math.floor( Math.random() * acoes.size() ) ).intValue();
            int acao = acoes.get(indiceAcao);
            moverLadrilho(acao);
        }
    }
    
    //verifica se os ladrilhos est�o na ordem correta.
    private void verificaJogo(){
        for(int i = 0; i < ladrilhos.length; i++){
            if( ladrilhos[i] != i ){
                this.gameOver = false;
                return;
            }
        }
        this.gameOver = true;
    }
    
    /**
     * Retorna o array de ladrilhos.
     * @return  um array de <code>Ladrilho</code>.
     */
    public int[] getLadrilhos() {
        return this.ladrilhos;
    }
    
    /**
     * Retorna o n�mero de vezes que os ladrilhos foram movidos.
     * @return  um valor inteiro.
     */
    public int getCliques(){
        return this.cliques;
    }
    
    /**
     *  Retorna o valor da vari�vel da <code>gameOver</code>, indicando se os ladrilhos est�o
     *  na ordem correta.
     * @return <code>true</code> ou <code>false</code>.
     */
    public boolean isGameOver(){
        return this.gameOver;
    }
    
    /**
     * Retorna uma string representando o tabuleiro.
     * @return
     */
    public String getVisualizacao(){
        StringBuilder b = new StringBuilder();
        int col = 1;
        for(int l : ladrilhos){
            b.append(l + " ");
            if(col % getDimensao() == 0){
                b.append("\n");
                col = 1;
            }
            else{
                col++;
            }
        }
        return b.toString();
    }
    
    /**
     * Retorna uma string representando a configuração atual do jogo.
     * @return
     */
    public String getString(){
        StringBuilder b = new StringBuilder();
        int i = 0;
        for(int l : ladrilhos){
            b.append( l );
            if(i != (ladrilhos.length - 1)){
                b.append(" ");
            }
            i++;
        }
        return b.toString();
    }
    
    /**
     * Retorna a lista de estados possíveis que podem ser atingidos de acordo com 
     * a configuração atual do tabuleiro.
     * @return
     */
    public List<String> getPossiveisEstadosSeguintes(){
        List<String> estados = new ArrayList<String>();
        List<Integer> acoes = getMovimentosPossiveis();
        String estadoAtual = " " + getString() + " "; //espaço adicionados para que a substituição funcione para o primeiro e último elemento
        String[] ladrs = getString().split(" "); //note que estado atual foi modifica na linha cima, por isso obtemos novamente
        String calhau = String.valueOf(posicaoCalhau);
        for(int acao : acoes){
            String img = ladrs[acao]; //imagem a ser movida por acao
            String temp = estadoAtual.replaceFirst(" " + img + " ", " X "); //substituição citada acima
            temp = temp.replaceFirst(" " + calhau +" ", " Y "); //substituição citada acima
            temp = temp.replaceFirst("Y", img);
            temp = temp.replaceFirst("X", calhau);
            estados.add(temp.trim());
        }
        
        return estados;
    }
    
    /**
     * Retorna as movimentações permitidas para a configuração atual do tabuleiro.
     * @return  Um vetor de inteiros cujos valores são os índices das peças que 
     * podem ser movidas.
     */
    public List<Integer> getMovimentosPossiveis(){
        List<Integer> movimentos = new ArrayList<Integer>(getDimensao());
        
        int posCalhau = getPosicaoCalhau();
        //verifica se o calhau pode ser movido para cima
        if((posCalhau - getDimensao()) >= 0){
            movimentos.add(posCalhau - getDimensao());
        }
        //verifica se o calhau pode ser movido para baixo
        if((posCalhau + getDimensao()) < qtdPecas){
            movimentos.add(posCalhau + getDimensao());
        }
        int linhaCalhau = posCalhau / getDimensao();
        //verifica se calhau pode ser movido para a direita
        if( (posCalhau + 1) / getDimensao() == linhaCalhau){
            movimentos.add(posCalhau + 1);
        }
        //verifica se calhau pode ser movido para a esquerda
        if( (posCalhau - 1) / getDimensao() == linhaCalhau && posCalhau - 1 >= 0){
            movimentos.add(posCalhau - 1);
        }
        return movimentos;
    }
    
    /**
     * Define a disposição dos ladrilhos.
     * @param estado a configuração dos ladrilhos. Deve ser uma string com 16 
     * termos separados por espaço. O primeiro termo define a imagem do primeiro 
     * ladrilho, o segundo termo define a imagem do segundo ladrilho e assim por 
     * diante.
     */
    public void setEstado(String estado){
        String[] termos = estado.split(" ");
        if(termos.length != qtdPecas){
            throw new IllegalArgumentException("A string de estado deve possuir "+ qtdPecas +" termos");
        }
        boolean[] imgsUsadas = new boolean[qtdPecas];
        int i = 0;
        for(String t : termos){
            int indiceImg = Integer.parseInt(t);
            //verifica se a imagem já foi usada
            if(indiceImg < 0 || indiceImg > (qtdPecas - 1)){
                throw new IllegalArgumentException("Índice de imagem inválido -> " + indiceImg);
            }
            else if(imgsUsadas[indiceImg]){
                throw new IllegalArgumentException("Cada imagem só deve ocorrer uma vez na string de estado");
            }
            else{
                imgsUsadas[indiceImg] = true;
            }
            ladrilhos[i] = indiceImg;
            i++;
        }
        cliques = 0;
        verificaJogo();
    }

    public int getDimensao() {
        return dimensao;
    }

    public String getEstadoFinal() {
        return estadoFinal;
    }
}
