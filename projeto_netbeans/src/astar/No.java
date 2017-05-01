/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package astar;

import java.util.ArrayList;
import java.util.List;

/**
 * Nó do grafo.
 * @author Alexandre
 */
public class No {
    public String estado;
    public int acao;
    public No noPai;
    public int step;
    public double custoCaminho = 0.0;
    public double custoStep = 0.0;
    
    public List<No> recuperarArvore(){
        No atual = this;
        List<No> retorno = new ArrayList<No>();
        while(atual.noPai == null){
            retorno.add(0, atual);
            atual = atual.noPai;
        }
        retorno.add(0, atual);
        return retorno;
    }

    @Override
    public boolean equals(Object obj) {
        if((obj == null) || (this.getClass() != obj.getClass())){
            return false;
        }
        if(this == obj){
            return true;
        }
        No x = (No)obj;
        return x.estado.equals(this.estado);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + (this.estado != null ? this.estado.hashCode() : 0);
        return hash;
    }
}
