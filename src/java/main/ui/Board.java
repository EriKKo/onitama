package main.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;

public class Board extends JComponent {

   Color bgColor = Color.cyan;
   Dimension dimension = new Dimension(400,400);
   int[][] positions;



   public Board(int x,int y,int[][] positions) {
      setVisible(true);
      setSize(dimension);
      setBounds(x,y,400,400);
      this.positions = positions;
   }

   @Override
   public void paintComponent(Graphics g){
      Graphics2D g2 = (Graphics2D)g;
      for (int i = 0; i< 5;i++){
         for(int j = 0; j<5; j++){
            g2.setColor(Color.black);
            Shape rectangle = new Rectangle(i*80,j*80,80,80);
            Shape circle = new Ellipse2D.Double(i*80+5, j*80+5, 70, 70);
            Shape king = new RoundRectangle2D.Double(i*80+5,j*80+5,70,70,5,5);
            if(i==2 && j==4){
               g2.setColor(Color.pink);
               g2.fill(rectangle);
            }
            if(i==2 && j==0){
               g2.setColor(Color.cyan);
               g2.fill(rectangle);
            }
            g2.draw(rectangle);
            if(positions[j][i]==1){
               g2.setColor(Color.RED);
               g2.draw(king);
            }
            if(positions[j][i]==2){
               g2.setColor(Color.RED);
               g2.draw(circle);
            }
            if(positions[j][i]==3){
               g2.setColor(Color.BLUE);
               g2.draw(king);
            }
            if(positions[j][i]==4){
               g2.setColor(Color.BLUE);
               g2.draw(circle);
            }
         }
      }

   }

   public void updateBoard(long mask){

   }
}
