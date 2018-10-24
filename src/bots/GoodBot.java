package bots;

import main.Game;
import main.GameUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class GoodBot implements Bot {

   private static final boolean DEBUG = false;

   static Map<Long, Integer> killerMove = new HashMap<>();

   double killerHits = 0, total = 0;
   double branchSum;

   private int timeout;

   public GoodBot(int timeout) {
      this.timeout = timeout;
   }

   @Override
   public int move(Game game, long mask) {
      long startTime = System.currentTimeMillis();
      List<Integer> bestIndices;
      int bestScore, depth;
      for (depth = 1; ; depth++) {
         bestIndices = new ArrayList<>();
         bestScore = -100000;
         if (DEBUG) System.out.println("Depth " + depth + ":");
         branchSum = 0;
         List<Long> moves = game.getMoves(mask);
         int visited = 0, nextSearchIndex;
         while ((nextSearchIndex = getNextSearchIndex(moves, visited)) != -1) {
            visited |= 1 << nextSearchIndex;
            long time = System.nanoTime();
            int score = -dfs(game, GameUtil.reverse(moves.get(nextSearchIndex)), -1000, depth);
            if (score > bestScore) {
               bestScore = score;
               bestIndices = new ArrayList<>();
               bestIndices.add(nextSearchIndex);
            } else if (score == bestScore) {
               bestIndices.add(nextSearchIndex);
            }
            if (DEBUG) System.out.println(nextSearchIndex + ": " + score + " (" + (System.nanoTime() - time) / 1000000 + "ms)");
         }
         if (DEBUG) System.out.println("Killer killerHits: " + ((int) killerHits) + " of " + ((int)total) + " (" + (killerHits / total * 100) + "%)");
         if (DEBUG) System.out.println("Average branching factor: " + (branchSum / total));
         long elapsedTime = System.currentTimeMillis() - startTime;
         if (bestScore >= 100 || bestScore <= -100) break;
         if (elapsedTime > timeout * 3 / 4) break;
      }
      System.out.println("Best score: " + bestScore + ", Depth: " + depth + ", Elapsed time: " + (System.currentTimeMillis() - startTime) + "ms");
      return bestIndices.get(new Random().nextInt(bestIndices.size()));
   }

   private int dfs(Game game, long mask, int alpha, int depth) {
      if (GameUtil.getWin(mask) != 0) return GameUtil.getWin(mask) * 100;
      if (depth == 0) return getScore(mask);
      mask = GameUtil.canonize(mask);
      int killerIndex = killerMove.getOrDefault(mask, -1);
      int v = -1000;
      int visited = 0;
      List<Long> moves = game.getMoves(mask);
      total++;
      if (killerIndex != -1) {
         visited |= 1 << killerIndex;
         v = Math.max(v, -dfs(game, GameUtil.reverse(moves.get(killerIndex)), v, depth - 1));
         if (-v <= alpha) {
            killerHits++;
            branchSum += Integer.bitCount(visited);
            return v;
         }
      }
      int nextSearchIndex;
      while ((nextSearchIndex = getNextSearchIndex(moves, visited)) != -1) {
         visited |= 1 << nextSearchIndex;
         v = Math.max(v, -dfs(game, GameUtil.reverse(moves.get(nextSearchIndex)), v, depth - 1));
         if (-v <= alpha) {
            killerMove.put(mask, nextSearchIndex);
            branchSum += Integer.bitCount(visited);
            return v;
         }
      }
      branchSum += Integer.bitCount(visited);
      return v;
   }

   private int getNextSearchIndex(List<Long> moves, int visited) {
      int bestIndex = -1, bestScore = -100000;
      for (int i = 0; i < moves.size(); i++) {
         if ((visited & 1 << i) == 0) {
            int score = getScore(moves.get(i));
            if (score > bestScore) {
               bestIndex = i;
               bestScore = score;
            }
         }
      }
      return bestIndex;
   }

   private int getScore(long mask) {
      int res = GameUtil.getWin(mask) * 100;
      long ownPawns = GameUtil.getOwnPawns(mask), enemyPawns = GameUtil.getEnemyPawns(mask);
      for (int i = 0; i < 4; i++) {
         long ownPawn = ownPawns >> i * 5 & (1 << 5) - 1;
         long enemyPawn = enemyPawns >> i * 5 & (1 << 5) - 1;
         if (ownPawn >= 1 && ownPawn < 26) {
            res++;
         }
         if (enemyPawn >= 1 && enemyPawn < 26) {
            res--;
         }
      }
      return res;
   }

   @Override
   public String toString() {
      return "GoodBot";
   }
}
