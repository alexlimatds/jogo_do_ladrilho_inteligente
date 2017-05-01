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
public class ConversorEstadoFeatures2 {

    /**
     * Converte um par estado-açao em um vetor de features.
     * Os primeiros n (n é a quantidade de elementos do ladrilho) elementos do
     * vetor são preenchidos com a distância manhatan de cada ladrilho (na posição
     * zero, a distância manhatan do ladrilho zero, na posição 1, a distância do
     * ladrilho 1 e assim por diante). O restante das n posições representa a ação.
     * A posição do ladrilho a ser movido é preenchida com 1 e as outras posições
     * são preenchidas com zero.
     * @return
     */
    public static int[] getFeatures(int[] s, int a){
        int[] f = new int[ s.length * 2 ]; //estados + ações
        int dim = (int)Math.sqrt(s.length);
        for(int i = 0; i < s.length; i++){
            int img = s[i];
            int linhaDest = img / dim;
            int colDest = img % dim;
            int linhaAtual = i / dim;
            int colAtual = i % dim;
            f[img] = (Math.abs(linhaDest - linhaAtual) + Math.abs(colDest - colAtual));
            f[i + s.length] = (a == i ? 1 : 0);
        }
        return f;
    }

    public static void main(String[] args) {
        //testa getFeatures
        int[] s = {0, 3, 2, 4, 6, 5, 8, 1, 7};
        int[] esperado = {0, -2, 0, -2, -1, 0, -2, -1, -2, 0, 0, 0, 1, 0, 0, 0, 0, 0};
        int[] f = getFeatures(s, 3);
        System.out.println(comp(esperado, f));
        esperado = new int[]{0, -2, 0, -2, -1, 0, -2, -1, -2, 0, 0, 0, 0, 0, 0, 0, 1, 0};
        f = getFeatures(s, 7);
        System.out.println(comp(esperado, f));
        s = new int[]{1, 8, 3, 2, 4, 5, 7, 0, 6};
        esperado = new int[]{-3, -1, -3, -3, 0, 0, -2, -1, -3, 1, 0, 0, 0, 0, 0, 0, 0, 0};
        f = getFeatures(s, 0);
        System.out.println(comp(esperado, f));
        esperado = new int[]{-3, -1, -3, -3, 0, 0, -2, -1, -3, 0, 0, 0, 0, 1, 0, 0, 0, 0};
        f = getFeatures(s, 4);
        System.out.println(comp(esperado, f));
    }

    private static boolean comp(int[] a, int[] b){
        boolean r = Arrays.equals(a, b);
        if(!r){
            System.out.print("A: ");
            for(int x : a) System.out.print(x + " ");
            System.out.print("\nB: ");
            for(int x : b) System.out.print(x + " ");
            System.out.print("\n");
        }
        return r;
    }
}
