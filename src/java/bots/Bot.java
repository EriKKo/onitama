package bots;

import main.Game;

public interface Bot {
   int move(Game gameState, long mask);
}
