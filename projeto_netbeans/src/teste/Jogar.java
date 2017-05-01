package teste;


import alexandrelima.ladrilho.dominio.ControleLadrilho;
import java.util.Scanner;

/**
 *
 * @author Alexandre
 */
public class Jogar {
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        System.out.print("Dimensão do jogo: ");
        int dim = scan.nextInt();
        ControleLadrilho jogo = new ControleLadrilho(dim);
        String acao = null;
        String tabuleiro = null;
        do{
            if(acao != null){
                int movimento = Integer.parseInt(acao);
                jogo.moverLadrilho(movimento);
            }
            tabuleiro = jogo.getVisualizacao();
            System.out.println(tabuleiro);
            System.out.println("Ações (S para sair): " + jogo.getMovimentosPossiveis());
            acao = scan.next();
        }while(!acao.equals("S"));
    }

}
