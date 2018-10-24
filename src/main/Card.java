package main;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Card {
   @JsonProperty
   String name;
   @JsonProperty
   boolean[][] mask;

   Card(String name, boolean[][] mask) {
      this.name = name;
      this.mask = mask;
   }
}