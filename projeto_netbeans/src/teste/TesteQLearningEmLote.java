/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package teste;

import java.io.File;
import java.io.FilenameFilter;

/**
 *
 * @author alexandre
 */
public class TesteQLearningEmLote {
    
    public static void main(String[] args) {
        try{
            String nomeDiretorio = "2014-07-06";
            File diretorio = new File(nomeDiretorio);
            String[] arquivos = diretorio.list(
                new FilenameFilter() {
                    public boolean accept(File file, String name) {
                        return name.contains("-pol-");
                    }
                }
            );
            
            BateriaDeTesteQLearning bateria = new BateriaDeTesteQLearning();
            for(String fileName : arquivos){
                bateria.executar(nomeDiretorio + "/" + fileName);
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }
}
