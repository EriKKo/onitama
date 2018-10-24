package main;

import bots.Bot;
import bots.CLIBot;
import bots.GoodBot;
import bots.RandomBot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Main {

   public static List<Card> cards = new ArrayList<>();

   static {
      cards.add(new Card("Tiger", moveFromString(new String[] {
         "..X..",
         ".....",
         "..O..",
         "..X..",
         ".....",
      })));
      cards.add(new Card("Dragon", moveFromString(new String[] {
         ".....",
         "X...X",
         "..O..",
         ".X.X.",
         ".....",
      })));
      cards.add(new Card("Frog", moveFromString(new String[] {
         ".....",
         ".X...",
         "X.O..",
         "...X.",
         ".....",
      })));
      cards.add(new Card("Rabbit", moveFromString(new String[] {
         ".....",
         "...X.",
         "..O.X",
         ".X...",
         ".....",
      })));
      cards.add(new Card("Crab", moveFromString(new String[] {
         ".....",
         "..X..",
         "X.O.X",
         ".....",
         ".....",
      })));
      cards.add(new Card("Elephant", moveFromString(new String[] {
         ".....",
         ".X.X.",
         ".XOX.",
         ".....",
         ".....",
      })));
      cards.add(new Card("Goose", moveFromString(new String[] {
         ".....",
         ".X...",
         ".XOX.",
         "...X.",
         ".....",
      })));
      cards.add(new Card("Rooster", moveFromString(new String[] {
         ".....",
         "...X.",
         ".XOX.",
         ".X...",
         ".....",
      })));
      cards.add(new Card("Monkey", moveFromString(new String[] {
         ".....",
         ".X.X.",
         "..O..",
         ".X.X.",
         ".....",
      })));
      cards.add(new Card("Mantis", moveFromString(new String[] {
         ".....",
         ".X.X.",
         "..O..",
         "..X..",
         ".....",
      })));
      cards.add(new Card("Horse", moveFromString(new String[] {
         ".....",
         "..X..",
         ".XO..",
         "..X..",
         ".....",
      })));
      cards.add(new Card("Ox", moveFromString(new String[] {
         ".....",
         "..X..",
         "..OX.",
         "..X..",
         ".....",
      })));
      cards.add(new Card("Crane", moveFromString(new String[] {
         ".....",
         "..X..",
         "..O..",
         ".X.X.",
         ".....",
      })));
      cards.add(new Card("Boar", moveFromString(new String[] {
         ".....",
         "..X..",
         ".XOX.",
         ".....",
         ".....",
      })));
      cards.add(new Card("Eel", moveFromString(new String[] {
         ".....",
         ".X...",
         "..OX.",
         ".X...",
         ".....",
      })));
      cards.add(new Card("Cobra", moveFromString(new String[] {
         ".....",
         "...X.",
         ".XO..",
         "...X.",
         ".....",
      })));
   }

   static boolean[][] moveFromString(String[] input) {
      boolean[][] res = new boolean[input.length][];
      for (int i = 0; i < input.length; i++) {
         res[i] = new boolean[input[i].length()];
         for (int j = 0; j < res[i].length; j++) {
            res[i][j] = input[i].charAt(j) == 'X';
         }
      }
      return res;
   }

   private static void printMoves(Game gameState, long mask) {
      System.out.println("Possible moves:");
      System.out.println("--------------------");
      List<Long> moves = gameState.getMoves(mask);
      for (int i = 0; i < moves.size(); i++) {
         System.out.println("main.Move #" + (i+1));
         gameState.print(moves.get(i));
      }
   }

   private static int play(Bot bot1, Bot bot2, int startingPlayer, List<Card> cards) {
      Game game = new Game(cards);
      long mask = GameUtil.createInitialBoard();
      if (startingPlayer == -1) mask = GameUtil.reverse(mask);
      int turn = startingPlayer;
      while (GameUtil.getWin(mask) == 0) {
         Bot currentBot = turn == 1 ? bot1 : bot2;
         System.out.println(currentBot + "'s turn:");
         game.print(turn == 1 ? mask : GameUtil.reverse(mask));
         mask = game.getMoves(mask).get(currentBot.move(game, mask));
         mask = GameUtil.reverse(mask);
         turn *= -1;
      }
      game.print(turn == 1 ? mask : GameUtil.reverse(mask));
      return turn * GameUtil.getWin(mask);
   }

   private static void printCards() {
      System.out.println("Cards to choose from:");
      for (int i = 0; i < cards.size(); i++) {
         System.out.println(i + ": " + cards.get(i).name);
      }
      System.out.print("Choose your cards: ");
   }

   private static List<Card> chooseCards(Scanner in) {
      printCards();
      List<Card> chosenCards = new ArrayList<>();
      while (chosenCards.size() < 5) {
         int index = in.nextInt();
         if (index >= 0 && index < cards.size() && chosenCards.stream().noneMatch(card -> card.name.equals(cards.get(index).name))) {
            chosenCards.add(cards.get(index));
         } else {
            chosenCards.clear();
            printCards();
         }
      }
      System.out.println("Cards chosen: " + String.join(" ", chosenCards.stream().map(card -> card.name).collect(Collectors.toList())));
      return chosenCards;
   }

   private static int chooseStartingPlayer(Scanner in) {
      while (true) {
         try {
            System.out.print("Choose starting player (1 or -1): ");
            int startingPlayer = in.nextInt();
            if (startingPlayer == 1 || startingPlayer == -1) return startingPlayer;
         } catch (InputMismatchException e) {

         }
      }
   }

   public static void main(String[] args) {
      Scanner in = new Scanner(System.in);
      List<Card> cards = chooseCards(in);
      int startingPlayer = chooseStartingPlayer(in);
      System.out.println(play(new CLIBot(in), new GoodBot(10000), startingPlayer, cards));
   }
}
