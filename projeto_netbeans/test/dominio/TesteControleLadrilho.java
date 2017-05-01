/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dominio;

import alexandrelima.ladrilho.dominio.ControleLadrilho;
import java.util.List;
import junit.framework.TestCase;

/**
 *
 * @author Alexandre
 */
public class TesteControleLadrilho extends TestCase{
    
    public void testPossiveisEstadosSeguintes(){
        ControleLadrilho jogo = new ControleLadrilho(4);
        jogo.setEstado("4 5 3 12 2 0 1 7 9 10 13 8 15 11 14 6");
        List<String> seguintes = jogo.getPossiveisEstadosSeguintes();
        assertEquals(2, seguintes.size());
        assertTrue(seguintes.contains("4 5 3 12 2 0 1 7 15 10 13 8 9 11 14 6"));
        assertTrue(seguintes.contains("4 5 3 12 2 0 1 7 9 10 13 8 11 15 14 6"));
        
        jogo.setEstado("12 8 9 3 0 7 15 2 10 1 4 14 13 11 5 6");
        seguintes = jogo.getPossiveisEstadosSeguintes();
        assertEquals(4, seguintes.size());
        assertTrue(seguintes.contains("12 8 15 3 0 7 9 2 10 1 4 14 13 11 5 6"));
        assertTrue(seguintes.contains("12 8 9 3 0 15 7 2 10 1 4 14 13 11 5 6"));
        assertTrue(seguintes.contains("12 8 9 3 0 7 2 15 10 1 4 14 13 11 5 6"));
        assertTrue(seguintes.contains("12 8 9 3 0 7 4 2 10 1 15 14 13 11 5 6"));
    }
    
    public void testSetEstado(){
        ControleLadrilho jogo = new ControleLadrilho(4);
        String expected = "2 3 4 8 9 10 1 5 6 11 12 13 14 15 7 0";
        jogo.setEstado(expected);
        String gerado = jogo.getString();
        assertEquals(expected, gerado);
        
        try{
            jogo.setEstado("abc fg");
            fail("Aceitou string com dois termos");
        }
        catch(IllegalArgumentException ex){}
        
        try{
            jogo.setEstado("1 2 3 4 5 6 7 8 9 10 11 12 13 14 15"); 
            fail("Aceitou string com 15 termos");
        }
        catch(IllegalArgumentException ex){}
        
        try{
            jogo.setEstado("1 2 16 4 5 6 7 8 9 10 11 12 13 14 15 0"); 
            fail("Aceitou string com índice de imagem inválido");
        }
        catch(IllegalArgumentException ex){}
        
        try{
            jogo.setEstado("1 2 3 4 5 6 7 8 9 10 11 3 13 14 15 0"); 
            fail("Aceitou string com imagem ocorrendo duas vezes");
        }
        catch(IllegalArgumentException ex){}
    }
    
    public void testGetMovimentosPossiveis(){
        ControleLadrilho jogo = new ControleLadrilho(4);
        jogo.setEstado("4 5 3 12 2 0 1 7 9 10 13 8 15 11 14 6");
        List<Integer> movimentos = jogo.getMovimentosPossiveis();
        assertEquals(2, movimentos.size());
        assertTrue(movimentos.contains(8));
        assertTrue(movimentos.contains(13));
        
        jogo.setEstado("12 8 9 3 0 7 15 2 10 1 4 14 13 11 5 6");
        movimentos = jogo.getMovimentosPossiveis();
        assertEquals(4, movimentos.size());
        assertTrue(movimentos.contains(2));
        assertTrue(movimentos.contains(5));
        assertTrue(movimentos.contains(7));
        assertTrue(movimentos.contains(10));
    }
}
