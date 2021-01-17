// code by jph
package ch.ethz.idsc.tensor.qty;

import java.util.Map.Entry;

import ch.ethz.idsc.tensor.Scalar;
import junit.framework.Assert;

/* package */ enum TestHelper {
  ;
  public static void checkInvariant(UnitSystem unitSystem) {
    for (Entry<String, Scalar> entry : unitSystem.map().entrySet()) {
      Scalar scalar = Quantity.of(1, entry.getKey());
      Assert.assertEquals(unitSystem.apply(scalar), entry.getValue());
      Assert.assertEquals(unitSystem.apply(entry.getValue()), entry.getValue());
    }
  }
}
