package ch.heigvd.res.lab00;



import ch.heigvd.res.lab00.IInstrument;


public class Trumpet implements IInstrument{
   
   private int soundVolume = 5;
   private String color = "golden";
   
   public String play(){
      return "pouet";
   }
   
   public int getSoundVolume(){
      return soundVolume;
   }
   
    public String getColor(){
      return color;
   }
   
}
