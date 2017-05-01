package alexandrelima.ladrilho.gui;


import alexandrelima.ladrilho.dominio.*;
import alexandrelima.ladrilho.ia.Util;
import astar.MyAStar;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.BoxLayout;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

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
public class PnlJogo extends JPanel implements MouseListener, ActionListener{
    
    private MyAStar star;
    ControleLadrilho jogo;
    JLabel cliques, lblMsg;
    JLabel[] ladrilhos;
    JButton btnReiniciar, btnQlearning, btnAStar;
    JPanel  east,
            west;
    Map<String, String> politica;
    
    /**
     * Cria uma nova inst�ncia desta classe.
     */
    public PnlJogo() throws Exception{
        this.cliques = new JLabel("cliques: 0");
        this.east = new JPanel();
        this.west = new JPanel();
        this.setBackground( Color.white );
        this.setLayout( new BorderLayout() );
        this.ladrilhos = new JLabel[ 9 ];
        
        //obtendo a politica
        //politica = Util.lerPolitica("pol-3N-2000000ep-QL.txt");
        politica = Util.lerPolitica("pol-3N-400ep.txt");
        
        //criando o jogo do ladrilho
        jogo = new ControleLadrilho(3);
        jogo.reiniciar();
        
        //ajustando o painel east
        this.initPainelEast();
        
        //ajustando o painel west
        this.initPainelWest();
        
        //adicionando os componentes
        this.add( west, BorderLayout.WEST );
        this.add( east, BorderLayout.EAST );
        this.setVisible( true );
        
        atualizaTela();
    }
    
    private void initPainelEast(){
        
        //pain�l do JLabel cliques
        JPanel painelCliques = new JPanel();        
        painelCliques.add( cliques );
        
        //pain�l do JButton btnReiniciar
        JPanel painelReiniciar = new JPanel();
        btnReiniciar = new JButton("novo jogo");
        btnReiniciar.addActionListener(this);
        painelReiniciar.add( btnReiniciar  );
        
        //botão Q-Learning
        btnQlearning = new JButton("Resolver (Q-Learning)");
        btnQlearning.addActionListener(this);
        
        //botão A*
        btnAStar = new JButton("Resolver (A*)");
        btnAStar.addActionListener(this);
        
        //label de mensagens
        lblMsg = new JLabel(" ");
        
        //criando o JPanel do lado leste
        east.setLayout( new BoxLayout(east, BoxLayout.Y_AXIS) );
        east.setBorder( BorderFactory.createEmptyBorder(10, 10, 10, 10) );
        east.add( painelCliques );
        east.add( painelReiniciar );
        east.add( btnAStar );
        east.add( btnQlearning );
        east.add(lblMsg);
    }
    
    private void initPainelWest(){
        //definindo o layout
        west.setLayout( new GridLayout(3, 3) );
        //incializando os ladrilhos
        for(int i = 0; i < ladrilhos.length; i++){
            ladrilhos[ i ] = new JLabel();
            ladrilhos[ i ].setText(String.valueOf(i));
            ladrilhos[ i ].setHorizontalAlignment(JLabel.CENTER);
            ladrilhos[ i ].setVerticalAlignment(JLabel.CENTER);
            ladrilhos[ i ].addMouseListener( this );
            ladrilhos[ i ].setFont(Font.decode(Font.MONOSPACED+"-"+"60"));
            ladrilhos[i].setBorder(BorderFactory.createLineBorder(Color.BLACK));
            ladrilhos[i].setPreferredSize(new Dimension(100, 100));
            //inserindo o JLabel no PainelWest
            west.add( ladrilhos[ i ] );
        }
    }
    
    //atualiza a tela
    private void atualizaTela(){
        //atualiza as imagens
        for(int i = 0; i < ladrilhos.length; i++){
            //definindo a "imagem" do JLabel
            String str = null;
            if(jogo.getLadrilhos()[i] != 8){
                str = String.valueOf(jogo.getLadrilhos()[i]);
            }
            else{
                str = "";
            }
                
            ladrilhos[ i ].setText( str );
        }
        cliques.setText( "cliques: " + jogo.getCliques() );
    }
    
    //m�todos da interface MouseListener
    public void mouseClicked(MouseEvent e){
        Object origem = e.getSource();
        for(int i = 0; i < ladrilhos.length; i++){
           if( origem.equals( ladrilhos[ i ] ) ){ //verifica se o clique foi em um ladrilho
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
        if( o.equals( btnReiniciar  ) ){
            if( jogo.isGameOver() ){
                for(int i = 0; i < ladrilhos.length; i++)
                    ladrilhos[ i ].addMouseListener( this );
            }
            jogo.reiniciar();
            this.atualizaTela();
        }
        else if( o.equals( btnAStar ) ){
            star = new MyAStar(jogo);
            lblMsg.setText("Buscando...");
            star.buscar();
            lblMsg.setText("");
            
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    pilotoAutomatico(star.getAcoes());
                }
            });
        }
        else if( o.equals( btnQlearning ) ){
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    pilotoAutomatico();
                }
            });
        }
    }
    
    private void pilotoAutomatico(){
        try{
            while(!jogo.isGameOver()){
                String s = jogo.getString();
                String a = politica.get(s);
                jogo.moverLadrilho(Integer.parseInt(a));
                atualizaTela();
                update(getGraphics());
                paintChildren(getGraphics());
                Thread.sleep(500);
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }
    
    private void pilotoAutomatico(List<Integer> acoes){
        try{
            for(Integer a : acoes){
                jogo.moverLadrilho(a);
                atualizaTela();
                update(getGraphics());
                paintChildren(getGraphics());
                Thread.sleep(500);
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }
}