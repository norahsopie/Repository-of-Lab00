package ch.heigvd.res.lab00;


public class Flute implements IInstrument {
   
   private int soundVolume = 2;
   private String color;
   
   public String play(){
      return "trilili";
   }
   
   
   public int getSoundVolume(){
      return soundVolume;
   }
   
    public String getColor(){
      return color;
   }
   
   
}
