/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package astar;

import java.util.Comparator;

/**
 * Comparador de nós.
 * @author Alexandre
 */
public class ComparadorNo implements Comparator<No>{
    
    /**
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    @Override
    public int compare(No no1, No no2) {
        int d1 = AStar.heuristica(no1.estado);
        int d2 = AStar.heuristica(no2.estado);
        return d2 - d1;
    }
    
}
