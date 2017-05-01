/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package alexandrelima.ladrilho.ia;

import java.io.BufferedReader;
import java.io.FileReader;

/**
 * Conta, a partir do arquivo com a tabela de valor dos pares estado-ação, quantos 
 * pares foram visitados no algorimo de MonteCarlo.
 * @author Alexandre
 */
public class ContaParesEstadoAcaoVisitados {
     public static void main(String[] args) {
        try{
            //lê o arquivo da tabela
            BufferedReader reader = new BufferedReader(new FileReader("tabela-estado-acao.txt"));
            int paresGerados = 0;
            int paresVisitados = 0;
            String line = reader.readLine();
            while(line != null && !line.trim().equals("") ){
                int posLastEspaco = line.lastIndexOf(" ");
                String valorStr = line.substring(posLastEspaco + 1);
                double valorDouble = Double.parseDouble(valorStr);
                paresGerados++;
                if(valorDouble != 0){
                    paresVisitados++;
                }
                
                line = reader.readLine();
            }
            int paresNaoVisitados = paresGerados - paresVisitados;
            System.out.println("- Pares gerados: " + paresGerados + 
                               "\nPares visitados: " + paresVisitados +
                               "\nPares não-visitados: " + paresNaoVisitados);
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }

}
