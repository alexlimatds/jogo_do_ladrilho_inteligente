/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package teste;

import alexandrelima.ladrilho.dominio.ControleLadrilho;
import java.io.FileWriter;

/**
 *
 * @author Alexandre
 */
public class GerarPartidasDeTeste {
    public static void main(String[] args) {
        try{
            ControleLadrilho game = new ControleLadrilho(3);
            //gera arquivo com as partidas
            FileWriter fWriter = new FileWriter("partidas.txt");
            for(int i = 0; i < 10000; i++) {
                game.reiniciar();
                fWriter.write(game.getString() + "\n");
            }
            fWriter.close();
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }

}
