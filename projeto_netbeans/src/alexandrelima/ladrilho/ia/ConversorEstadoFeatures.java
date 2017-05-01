/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package alexandrelima.ladrilho.ia;

import java.util.Arrays;

/**
 * Métodos para converter um par estado-ação do jogo do ladrilho em um vetor
 * de features.
 * @author Alexandre
 */
public class ConversorEstadoFeatures {

    /**
     * Converte um para estado-ação do jogo do ladrilho em um vetor de features.
     * @param s O estado do jogo
     * @param a A ação
     * @return
     */
    public static int[] getFeatures(int[] s, int a){
        int[] f = new int[ s.length * 2 ];
        for(int i = 0; i < s.length; i++){
            f[i] = (s[i] == i ? 1 : 0); //atribui 1 para os ladrilhos que estejam no local desejado
            f[i + s.length] = (a == i ? 1 : 0);
        }
        return f;
    }

    public static void main(String[] args) {
        //testa getFeatures
        int[] s = {0, 3, 2, 4, 6, 5, 8, 6, 7};
        int[] esperado = {1, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0};
        int[] f = getFeatures(s, 3);
        System.out.println(Arrays.equals(esperado, f));
        esperado = new int[]{1, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1};
        f = getFeatures(s, 8);
        System.out.println(Arrays.equals(esperado, f));
        s = new int[]{6, 2, 0, 7, 4, 5, 1, 3, 8};
        esperado = new int[]{0, 0, 0, 0, 1, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0};
        f = getFeatures(s, 7);
        System.out.println(Arrays.equals(esperado, f));
    }
}
