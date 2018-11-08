package main;

public class Move {

   int cardIndex, x1, y1, x2, y2;

   Move(String s) {
      cardIndex = s.charAt(0) - '1';
      x1 = s.charAt(1) - 'A';
      y1 = s.charAt(2) - '1';
      x2 = s.charAt(3) - 'A';
      y2 = s.charAt(4) - '1';
   }

   @Override
   public String toString() {
      return "main.Move{" +
         "cardIndex=" + cardIndex +
         ", x1=" + x1 +
         ", y1=" + y1 +
         ", x2=" + x2 +
         ", y2=" + y2 +
         '}';
   }
}
