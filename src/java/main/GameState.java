package main;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Builder
@Getter
@Setter
@ToString
public class GameState {

   private Card[] player1Cards, player2Cards;
   private Card spareCard;
   private int[][] positions;

}
