package main;

public class GameUtil {

   static final int OWN_KING_OFFSET = 0;
   static final int OWN_PAWNS_OFFSET = OWN_KING_OFFSET + 5;
   static final int ENEMY_KING_OFFSET = OWN_PAWNS_OFFSET + 20;
   static final int ENEMY_PAWNS_OFFSET = ENEMY_KING_OFFSET + 5;
   static final int OWN_CARDS_OFFSET = ENEMY_PAWNS_OFFSET + 20;
   static final int ENEMY_CARDS_OFFSET = OWN_CARDS_OFFSET + 5;

   static final long OWN_KING_MASK = ((1L << 5) - 1) << OWN_KING_OFFSET;
   static final long OWN_PAWNS_MASK = ((1L << 20) - 1) << OWN_PAWNS_OFFSET;
   static final long ENEMY_KING_MASK = ((1L << 5) - 1) << ENEMY_KING_OFFSET;
   static final long ENEMY_PAWNS_MASK = ((1L << 20) - 1) << ENEMY_PAWNS_OFFSET;
   static final long OWN_CARDS_MASK = ((1L << 5) - 1) << OWN_CARDS_OFFSET;
   static final long ENEMY_CARDS_MASK = ((1L << 5) - 1) << ENEMY_CARDS_OFFSET;

   static final long REVERSE_PAWNS_MASK = 26 | 26 << 5 | 26 << 10 | 26 << 15;

   public static long createInitialBoard() {
      long res = 3L << OWN_KING_OFFSET | 23L << ENEMY_KING_OFFSET;
      res |= 1L << OWN_PAWNS_OFFSET;
      res |= 2L << OWN_PAWNS_OFFSET + 5;
      res |= 4L << OWN_PAWNS_OFFSET + 10;
      res |= 5L << OWN_PAWNS_OFFSET + 15;
      res |= 21L << ENEMY_PAWNS_OFFSET;
      res |= 22L << ENEMY_PAWNS_OFFSET + 5;
      res |= 24L << ENEMY_PAWNS_OFFSET + 10;
      res |= 25L << ENEMY_PAWNS_OFFSET + 15;
      res |= 3L << OWN_CARDS_OFFSET | 24L << ENEMY_CARDS_OFFSET;
      return res;
   }

   public static long reverse(long mask) {
      return (26 - getOwnKing(mask) << ENEMY_KING_OFFSET) | (26 - getEnemyKing(mask)) << OWN_KING_OFFSET
         | (REVERSE_PAWNS_MASK - getOwnPawns(mask)) << ENEMY_PAWNS_OFFSET | (REVERSE_PAWNS_MASK - getEnemyPawns(mask)) << OWN_PAWNS_OFFSET
         | (mask & OWN_CARDS_MASK) << 5 | (mask & ENEMY_CARDS_MASK) >> 5;
   }

   public static long getOwnKing(long mask) {
      return (mask & OWN_KING_MASK) >> OWN_KING_OFFSET;
   }

   public static long getEnemyKing(long mask) {
      return (mask & ENEMY_KING_MASK) >> ENEMY_KING_OFFSET;
   }

   public static long getOwnPawns(long mask) {
      return (mask & OWN_PAWNS_MASK) >> OWN_PAWNS_OFFSET;
   }

   public static long getEnemyPawns(long mask) {
      return (mask & ENEMY_PAWNS_MASK) >> ENEMY_PAWNS_OFFSET;
   }

   public static long getOwnCards(long mask) {
      return (mask & OWN_CARDS_MASK) >> OWN_CARDS_OFFSET;
   }

   public static long getEnemyCards(long mask) {
      return (mask & ENEMY_CARDS_MASK) >> ENEMY_CARDS_OFFSET;
   }

   public static long canonize(long mask) {
      long newOwnPawns = canonizeHelper(getOwnPawns(mask));
      long newEnemyPawns = canonizeHelper(getEnemyPawns(mask));
      return mask & ~OWN_PAWNS_MASK & ~ENEMY_PAWNS_MASK | newOwnPawns << OWN_PAWNS_OFFSET | newEnemyPawns << ENEMY_PAWNS_OFFSET;
   }

   private static long canonizeHelper(long mask) {
      for (int i = 0; i < 4; i++) {
         long m = mask >> i * 5 & (1 << 5) - 1;
         if (m > 25) {
            mask ^= m << i * 5;
         }
      }
      boolean swapped = true;
      while (swapped) {
         swapped = false;
         for (int i = 0; i < 3; i++) {
            long m1 = mask >> i * 5 & (1 << 5) - 1;
            long m2 = mask >> (i + 1) * 5 & (1 << 5) - 1;
            if (m1 > m2) {
               swapped = true;
               mask &= ~((1 << 10) - 1 << i * 5);
               mask |= m1 << (i + 1) * 5;
               mask |= m2 << i * 5;
            }
         }
      }
      return mask;
   }

   public static int getWin(long mask) {
      long enemyKing = getEnemyKing(mask), ownKing = getOwnKing(mask);
      if (enemyKing < 1 || enemyKing > 25) {
         return 1;
      } else if (ownKing < 1 || ownKing > 25) {
         return -1;
      } else if (ownKing == 23) {
         return 1;
      } else if (enemyKing == 3) {
         return -1;
      }
      return 0;
   }

   public long applyMove(long mask, Move move) {
      int cardIndex = -1;
      int m = move.cardIndex;
      for (int i = 0; i < 5; i++) {
         if ((getOwnCards(mask) & 1 << i) > 0) {
            if (m == 0) {
               cardIndex = i;
               break;
            } else {
               m--;
            }
         }
      }
      return applyMove(mask, cardIndex, move.y1 * 5 + move.x1, move.y2 * 5 + move.x2);
   }

   public static long applyMove(long mask, int cardIndex, int from, int to) {
      long res = 0;
      for (int i = 0; i < OWN_CARDS_OFFSET / 5; i++) {
         long index = mask >> i * 5 & (1 << 5) - 1;
         if (index == to + 1) {
            index = 0;
         } else if (index == from + 1) {
            index = to + 1;
         }
         res |= index << i * 5;
      }
      long pivotCard = (1 << 5) - 1 & ~(getOwnCards(mask) ^ getEnemyCards(mask));
      res |= (getOwnCards(mask) & ~(1 << cardIndex) | pivotCard) << OWN_CARDS_OFFSET;
      res |= getEnemyCards(mask) << ENEMY_CARDS_OFFSET;
      return res;
   }

}
