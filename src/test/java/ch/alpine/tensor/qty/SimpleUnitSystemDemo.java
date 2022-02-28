// code by jph
package ch.alpine.tensor.qty;

import ch.alpine.tensor.io.ResourceData;

public enum SimpleUnitSystemDemo {
  ;
  public static void main(String[] args) {
    SimpleUnitSystem.from( //
        new UnitSystemInflator(ResourceData.properties("/unit/si.properties")).getMap());
  }
}
