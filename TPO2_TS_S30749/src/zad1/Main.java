/**
 *
 *  @author Trauth Szymon  S30749
 *
 */

package zad1;

import java.io.IOException;


public class Main {
  public static void main(String[] args) throws IOException {
    Service s = new Service("Polska");
    String weatherJson = s.getWeather("Warsaw");
    Double rate1 = s.getRateFor("USD");
    Double rate2 = s.getNBPRate();
    GUI gui = new GUI(s, weatherJson, rate1, rate2);
  }
}


