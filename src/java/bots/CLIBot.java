package bots;

import main.Game;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class CLIBot implements Bot {

   private Scanner in;

   public CLIBot(Scanner in) {
      this.in = in;
   }

   private int getIndex(int max) {
      System.out.print("Choose a move index");
      while (true) {
         System.out.print(": ");
         try {
            int res = in.nextInt();
            if (res >= 0 && res < max) return res;
            System.out.print("Index not in range, try again");
         } catch (InputMismatchException e) {
            System.out.print("Cant' parse index, try again");
         }
      }
   }

   @Override
   public int move(Game gameState, long mask) {
      List<String> moves = gameState.getReadableMoves(mask);
      System.out.println("Moves");
      for (int i = 0; i < moves.size(); i++) {
         System.out.println(i + ": " + moves.get(i));
      }
      return getIndex(moves.size());
   }

   @Override
   public String toString() {
      return "CLI Player";
   }
}
