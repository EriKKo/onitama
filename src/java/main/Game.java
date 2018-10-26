package main;

import java.util.ArrayList;
import java.util.List;

public class Game {

   static final int OWN_KING = 1;
   static final int OWN_PAWN = 2;
   static final int ENEMY_KING = 3;
   static final int ENEMY_PAWN = 4;

   Card[] cards;
   int[][] cardMoves;

   public Game(List<Card> cardList) {
      this.cards = cardList.toArray(new Card[0]);
      cardMoves = new int[cardList.size()][];
      for (int i = 0; i < cardMoves.length; i++) {
         List<Integer> moves = new ArrayList<>();
         for (int j = 0; j < 5; j++) {
            for (int k = 0; k < 5; k++) {
               if (cards[i].mask[j][k]) {
                  moves.add(j * 5 + k);
               }
            }
         }
         cardMoves[i] = moves.stream().mapToInt(n -> n).toArray();
      }
   }

   public List<Long> getMoves(long mask) {
      List<Long> moves = new ArrayList<>();
      int occupied = getOwnOccupiedMask(mask);
      for (int cardIndex = 0; cardIndex < 5; cardIndex++) {
         if ((GameUtil.getOwnCards(mask) & 1 << cardIndex) > 0) {
            for (int move : cardMoves[cardIndex]) {
               for (int i = 0; i < 5; i++) {
                  int index = (int) (mask >> i * 5 & (1 << 5) - 1) - 1;
                  if (index >= 0 && index < 25) {
                     int r = index / 5, c = index % 5;
                     int nr = r + 2 - move / 5, nc = c + move % 5 - 2;
                     int nIndex = nr * 5 + nc;
                     if (nr >= 0 && nr < 5 && nc >= 0 && nc < 5 && (occupied & 1 << nIndex) == 0) {
                        moves.add(GameUtil.applyMove(mask, cardIndex, index, nIndex));
                     }
                  }
               }
            }
         }
      }
      return moves;
   }

   private char getChar(int i) {
      return (char) ('A' + i);
   }

   private String getCoordinateString(int r, int c) {
      return "" + getChar(c) + (r + 1);
   }

   public List<String> getReadableMoves(long mask) {
      List<String> moves = new ArrayList<>();
      int occupied = getOwnOccupiedMask(mask);
      for (int cardIndex = 0; cardIndex < 5; cardIndex++) {
         if ((GameUtil.getOwnCards(mask) & 1 << cardIndex) > 0) {
            for (int move : cardMoves[cardIndex]) {
               for (int i = 0; i < 5; i++) {
                  int index = (int) (mask >> i * 5 & (1 << 5) - 1) - 1;
                  if (index >= 0 && index < 25) {
                     int r = index / 5, c = index % 5;
                     int nr = r + 2 - move / 5, nc = c + move % 5 - 2;
                     int nIndex = nr * 5 + nc;
                     if (nr >= 0 && nr < 5 && nc >= 0 && nc < 5 && (occupied & 1 << nIndex) == 0) {
                        moves.add(cards[cardIndex].name + " " + getCoordinateString(r, c) + " -> " + getCoordinateString(nr, nc) + (GameUtil.getOwnKing(mask) == index + 1 ? " (King)" : ""));
                     }
                  }
               }
            }
         }
      }
      return moves;
   }

   private static int getOwnOccupiedMask(long mask) {
      int res = 0;
      int ownKing = (int) GameUtil.getOwnKing(mask) - 1;
      res |= 1 << ownKing;
      int ownPawns = (int) GameUtil.getOwnPawns(mask);
      for (int i = 0; i < 4; i++) {
         int pawn = (ownPawns >> i * 5 & (1 << 5) - 1) - 1;
         if (pawn >= 0 && pawn < 25) {
            res |= 1 << pawn;
         }
      }
      return res;
   }

   public void print(long mask) {
      for (int i = 0; i < 5; i++) {
         if ((GameUtil.getEnemyCards(mask) & 1 << i) > 0) {
            System.out.print(cards[i].name + " ");
         }
      }
      System.out.println();
      for (int i = 4; i >= 0; i--) {
         for (int j = 0; j < 5; j++) {
            int index = i * 5 + j;
            int s = -1;
            if (GameUtil.getOwnKing(mask) == index + 1) {
               s = OWN_KING;
            } else if (GameUtil.getEnemyKing(mask) == index + 1) {
               s = ENEMY_KING;
            } else {
               long ownPawns = GameUtil.getOwnPawns(mask), enemyPawns = GameUtil.getEnemyPawns(mask);
               for (int k = 0; k < 4; k++) {
                  if ((ownPawns >> k * 5 & (1 << 5) - 1) == index + 1) {
                     s = OWN_PAWN;
                  } else if ((enemyPawns >> k * 5 & (1 << 5) - 1) == index + 1) {
                     s = ENEMY_PAWN;
                  }
               }
            }
            System.out.print((s == -1 ? "." : s) + " ");
         }
         if (i == 2) {
            for (int j = 0; j < 5; j++) {
               if ((GameUtil.getEnemyCards(mask) & 1 << j) == 0 && (GameUtil.getOwnCards(mask) & 1 << j) == 0) {
                  System.out.print(cards[j].name + " ");
               }
            }
         }
         System.out.println();
      }
      System.out.println();
      for (int i = 0; i < 5; i++) {
         if ((GameUtil.getOwnCards(mask) & 1 << i) > 0) {
            System.out.print(cards[i].name + " ");
         }
      }
      System.out.println();
      System.out.println();
   }
   public  GameState niceGameState(long mask) {
      Card[] player1Cards = new Card[2];
      Card[] player2Cards = new Card[2];
      int cardIndex=0;
      for(int i =0; i<5; i++){
         if((GameUtil.getEnemyCards(mask) & 1 << i)>0){
            player1Cards[cardIndex] = cards[i];
            cardIndex++;
         }
      }
      cardIndex=0;
      int[][] positions = new int[5][5];
      for (int i = 4; i >= 0; i--) {
         for (int j = 0; j < 5; j++) {
            int index = i * 5 + j;
            int s = -1;
            if (GameUtil.getOwnKing(mask) == index + 1) {
               s = OWN_KING;
            } else if (GameUtil.getEnemyKing(mask) == index + 1) {
               s = ENEMY_KING;
            } else {
               long ownPawns = GameUtil.getOwnPawns(mask), enemyPawns = GameUtil.getEnemyPawns(mask);
               for (int k = 0; k < 4; k++) {
                  if ((ownPawns >> k * 5 & (1 << 5) - 1) == index + 1) {
                     s = OWN_PAWN;
                  } else if ((enemyPawns >> k * 5 & (1 << 5) - 1) == index + 1) {
                     s = ENEMY_PAWN;
                  }
               }
            }
            positions[4-i][j] = s;
         }/**
         if (i == 2) {
            for (int j = 0; j < 5; j++) {
               if ((GameUtil.getEnemyCards(mask) & 1 << j) == 0 && (GameUtil.getOwnCards(mask) & 1 << j) == 0) {
                  player2Cards[cardIndex] = cards[j];
                  cardIndex++;
               }
            }
         }*/
      }
      for (int i = 0; i < 5; i++) {
         if ((GameUtil.getOwnCards(mask) & 1 << i) > 0) {
            player2Cards[cardIndex] = cards[i];
            cardIndex++;
         }
      }
      Card spareCard = cards[0];
      boolean found = false;
      for(int i = 0;i<5;i++){
         found = false;
         for(int j = 0; j<2;j++) {
            if(cards[i].equals(player1Cards[j]) || cards[i].equals(player2Cards[j])) {
               found = true;
            }
         }
         if(found==false){
            spareCard = cards[i];
            break;
         }
      }

      return GameState.builder()
         .player1Cards(player1Cards)
         .player2Cards(player2Cards)
         .spareCard(spareCard)
         .positions(positions)
         .build();
   }
}