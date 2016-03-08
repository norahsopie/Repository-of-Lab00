package ch.heigvd.res.lab00;

import ch.heigvd.res.lab00.IInstrument;
import ch.heigvd.res.lab00.Orchestra;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Olivier Liechti (olivier.liechti@heig-vd.ch)
 */
public class OrchestraTest {
  
  public OrchestraTest() {
  }

  @Test
  public void thereShouldBeNoInstrumentInANewOrchestra() {
    Orchestra orchestra = new Orchestra();
    assertEquals(0, orchestra.getNumberOfInstruments());
  }

  @Test
  public void thereShouldBeAWayToAddSeveralInstrumentsToAnOrchestra() {
    Orchestra orchestra = new Orchestra();
    orchestra.addInstrument(new Trumpet());
    orchestra.addInstrument(new Flute());
    orchestra.addInstrument(new Flute());
    assertEquals(3, orchestra.getNumberOfInstruments());
  }
  
  @Test 
  public void itShouldBePossibleToAddAnyNumberOfInstrumentsInAnOrchestra() {
    Orchestra orchestra = new Orchestra();
    int numberOfFlutes = (int)(Math.random() * 100);
    for (int i=0; i<numberOfFlutes; i++) {
      orchestra.addInstrument(new Flute());
    }
    assertEquals(numberOfFlutes, orchestra.getNumberOfInstruments());
  }
  
  @Test
  public void itShouldBePossibleToHearAllInstrumentsWhenTheOrchestraPlays() {
    Orchestra orchestra = new Orchestra();
    orchestra.addInstrument(new Flute());
    orchestra.addInstrument(new Trumpet());
    orchestra.addInstrument(new Flute());
    List<String> sounds = orchestra.makeMusic();
    assertEquals(3, sounds.size());
    assertEquals(sounds.get(0), "trilili");
    assertEquals(sounds.get(1), "pouet");
    assertEquals(sounds.get(2), "trilili");
  }
  
}
