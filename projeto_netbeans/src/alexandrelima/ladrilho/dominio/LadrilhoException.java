package alexandrelima.ladrilho.dominio;

/**
 * Representa uma execeção relacionada à lógica do jogo do ladrilho.
 * 
 * @author Alexandre
 */
public class LadrilhoException extends Exception{
    
    public LadrilhoException() {
        super();
    }
    
    public LadrilhoException(String mensagem) {
        super(mensagem);
    }
        
}
