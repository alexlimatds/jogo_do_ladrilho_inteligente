/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package alexandrelima.ladrilho.ia;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Guarda os valores dos pares estado-ação para um estado.
 * @author Alexandre
 */
public class TabelaValorEstadoAcao {
    private Map<String, Double> tabela = new HashMap<String, Double>();
    private String estado;
    
    /**
     * 
     * @param estado
     */
    public TabelaValorEstadoAcao(String estado) {
        this.estado = estado;
    }
    
    /**
     * Retorna o valor (Q) associado com uma ação.
     * @param acao
     * @return
     */
    public Double getValor(String acao){
        return tabela.get(acao);
    }
    
    /**
     * Retorna a ação com maior valor (Q).
     * @return
     */
    public String getAcaoValorMaximo(){
        String acao = null;
        Double valorMax = null;
        for(String key : tabela.keySet()){
            if(acao != null){
                Double valorKey = tabela.get(key);
                if(valorKey > valorMax){
                    valorMax = valorKey;
                    acao = key;
                }
            }
            else{
                acao = key;
                valorMax = tabela.get(key);
            }
        }
        return acao;
    }
    
    /**
     * Insere uma ação com seu respectivo valor (Q).
     * @param acao
     * @param valor
     */
    public void inserirValor(String acao, Double valor){
        tabela.put(acao, valor);
    }
    
    /**
     * Retorna as ações deste estado.
     * @return
     */
    public Set<String> getAcoes(){
        return tabela.keySet();
    }
}
