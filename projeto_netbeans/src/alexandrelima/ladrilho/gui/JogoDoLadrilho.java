package alexandrelima.ladrilho.gui;


import alexandrelima.ladrilho.dominio.*;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.FlowLayout;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.BoxLayout;
import javax.swing.BorderFactory;
import javax.swing.Box;

/**
 * Esta classe � o <i>front-end</i> do jogo da ladrilho. O jogo foi implementado seguindo uma arquitetura
 * MVC. A classe <code>Ladrilho</code> � o modelo, a classe <code>ControleLadrilho</code> realiza o controle 
 * (guarda as regras de neg�cio) e esta classe � repons�vel pela visualiza��o do jogo, sendo repons�vel
 * apenas por rotinas que gerenciem a intera��o do jogador com o jogo e a apresenta��o das jogadas. Esta
 * classe, a qual extende <code>javax.swing.JPanel</code>, deve ser inserida em um cont�iner swing (exemplo: 
 * <code>javax.swing.JFrame</code> ou <code>javax.swing.JApplet</code>).
 *
 * @author Alexandre Gomes de Lima - alexlouco@zipmail.com.br
 */

/*
  Autor: Alexandre Gomes de Lima - alexlouco@zipmail.com.br
  Este c�digo � propriedade intelectual do autor. � permitida a livre utiliza��o para fins 
  did�ticos e educacionais sem fins lucrativos. Para mais informa��es, consulte o autor.
  */
public class JogoDoLadrilho extends JPanel implements MouseListener, ActionListener{
    
    ControleLadrilho jogo;
    JLabel cliques;
    JLabel[] ladrilhos;
    Object modelo;
    JButton reiniciar, macete1;
    ArrayList imagens;
    JPanel  east,
            west;
    
    /**
     * Cria uma nova inst�ncia desta classe.
     * @param imagens   Uma lista contendo as imagens dos ladrilhos. A lista deve conter 16 objetos, sendo
     *                  o �ltimo o que representa a imagem vazia.
     * @param modelo    A imagem que serve de modelo.
     * @exception   LadrilhoException   caso o n�mero de objetos da lista seja diferente de 16.
     */
    public JogoDoLadrilho(ArrayList imagens, Object modelo) throws LadrilhoException{
        this.cliques = new JLabel("cliques: 0");
        this.imagens = imagens;
        this.east = new JPanel();
        this.west = new JPanel();
        this.setBackground( Color.white );
        this.setLayout( new BorderLayout() );
        this.modelo = modelo;
        this.ladrilhos = new JLabel[ 16 ];
        
        //criando o jogo do ladrilho
        jogo = new ControleLadrilho(4);
        
        //ajustando o painel east
        this.initPainelEast();
        
        //ajustando o painel west
        this.initPainelWest();
        
        //adicionando os componentes
        this.add( west, BorderLayout.WEST );
        this.add( east, BorderLayout.EAST );
        this.setVisible( true );
    }
    
    private void initPainelEast(){
        //pain�l do modelo
        JPanel painelModelo = new JPanel();
        painelModelo.add( new JLabel((ImageIcon)modelo) );
        
        //pain�l do JLabel cliques
        JPanel painelCliques = new JPanel();        
        painelCliques.add( cliques );
        
        //pain�l do JButton reiniciar
        JPanel painelReiniciar = new JPanel();
        reiniciar = new JButton("novo jogo");
        reiniciar.addActionListener(this);
        painelReiniciar.add( reiniciar );
        
        //criando o JPanel do lado leste
        east.setLayout( new BoxLayout(east, BoxLayout.Y_AXIS) );
        east.setBorder( BorderFactory.createEmptyBorder(10, 10, 10, 10) );
        macete1 = new JButton("teste");
        macete1.addActionListener( this );
        east.add( painelModelo );
        east.add( painelCliques );
        east.add( painelReiniciar );
        east.add( macete1 );
    }
    
    private void initPainelWest(){
        //definindo o layout
        west.setLayout( new GridLayout(4, 4) );
        //incializando os ladrilhos
        for(int i = 0; i < ladrilhos.length; i++){
            ladrilhos[ i ] = new JLabel();
            //definindo a imagem do JLabel
            //TODO
            //ladrilhos[ i ].setIcon( (ImageIcon)(jogo.getLadrilhos()[ i ].getImagem()) );
            //adicionando um MouseListener
            ladrilhos[ i ].addMouseListener( this );
            //inserindo o JLabel no PainelWest
            west.add( ladrilhos[ i ] );
        }
    }
    
    //atualiza a tela
    private void atualizaTela(){
        //atualiza as imagens
        for(int i = 0; i < ladrilhos.length; i++){
            //definindo a imagem do JLabel
            //TODO
            //ladrilhos[ i ].setIcon( (ImageIcon)(jogo.getLadrilhos()[ i ].getImagem()) );
        }
        cliques.setText( "cliques: " + jogo.getCliques() );
    }
    
    //m�todos da interface MouseListener
    public void mouseClicked(MouseEvent e){
        Object origem = e.getSource();
        for(int i = 0; i < ladrilhos.length; i++){
           if( origem.equals( ladrilhos[ i ] ) ){
               //verifica se o ladrilho foi movido
               if( jogo.moverLadrilho( i ) ){
                    this.atualizaTela();
                    //verifica se o jogo terminou
                    if( jogo.isGameOver() ){
                        //travando os ladrilhos
                        for(int j = 0; j < ladrilhos.length; j++)
                            ladrilhos[ j ].removeMouseListener( this );
                    }
               }
               return;
           }
        }
    }
    public void mousePressed(MouseEvent e){}
    public void mouseReleased(MouseEvent e){}
    public void mouseEntered(MouseEvent e){}
    public void mouseExited(MouseEvent e){}
    
    //m�todo da interface ActionPerformed
    public void actionPerformed(ActionEvent e){
        Object o = e.getSource();
        //reinicia um novo jogo
        if( o.equals( reiniciar ) ){
            if( jogo.isGameOver() ){
                for(int i = 0; i < ladrilhos.length; i++)
                    ladrilhos[ i ].addMouseListener( this );
            }
            jogo.reiniciar();
            this.atualizaTela();
        }
        else if( o.equals( macete1 ) ){
            //jogo.macete1();
            this.atualizaTela();
        }
    }
    
}