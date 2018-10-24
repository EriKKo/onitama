package bots;

import main.Game;

import java.util.Random;

public class RandomBot implements Bot {

   public int move(Game gameState, long mask) {
      return new Random().nextInt(gameState.getMoves(mask).size());
   }

   @Override
   public String toString() {
      return "RandomBot";
   }
}
