package main;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.ToString;

@ToString
public class Card {
   @JsonProperty
   public String name;
   @JsonProperty
   public boolean[][] mask;

   Card(String name, boolean[][] mask) {
      this.name = name;
      this.mask = mask;
   }
}