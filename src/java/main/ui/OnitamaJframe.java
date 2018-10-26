package main.ui;

import main.Card;
import main.Game;
import main.GameState;
import main.Main;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.List;

public class OnitamaJframe extends JFrame {

   private Board board;
   private GameState gameState;


   private CardUi card1,card2,card3;
   private ReveresedCard card4,card5;


   public OnitamaJframe(GameState gameState) {
      setSize(900, 900);
      this.gameState = gameState;
      setLayout(null);
      setVisible(true);
      dostuff(true);
   }

   private void dostuff(boolean add){
      board = new Board(150,200,gameState.getPositions());
      card1 = new CardUi(200, 650, gameState.getPlayer2Cards()[0]);
      card2 = new CardUi(350, 650, gameState.getPlayer2Cards()[1]);
      card3 = new CardUi(600, 300, gameState.getSpareCard());
      AffineTransform transform = new AffineTransform();
      transform.rotate(180);

      card4 = new ReveresedCard(200, 75, gameState.getPlayer1Cards()[0]);
      card5 = new ReveresedCard(375, 75, gameState.getPlayer1Cards()[1]);
      if(add) {
         add(board);
         add(card1);
         add(card2);
         add(card3);
         add(card4);
         add(card5);
      }
      setVisible(false);
      setVisible(true);
      System.out.println("should repaint");

   }

   public void updateState(GameState gameState) {
      this.gameState = gameState;
      removethestuff();
      dostuff(true);
   }

   private void removethestuff() {
      remove(board);
      remove(card1);
      remove(card2);
      remove(card3);
      remove(card4);
      remove(card5);
   }
}
