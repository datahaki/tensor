// code by jph
package ch.alpine.tensor.alg;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.mat.IdentityMatrix;

public class UnitVectorTest {
  @Test
  public void testRegular() {
    assertEquals(UnitVector.of(10, 3), IdentityMatrix.of(10).get(3));
  }

  @Test
  public void testFail() {
    assertThrows(IllegalArgumentException.class, () -> UnitVector.of(0, 0));
    assertThrows(IllegalArgumentException.class, () -> UnitVector.of(-1, 0));
    assertThrows(IllegalArgumentException.class, () -> UnitVector.of(3, -1));
    assertThrows(IllegalArgumentException.class, () -> UnitVector.of(3, 4));
    assertThrows(IllegalArgumentException.class, () -> UnitVector.of(10, 10));
  }
}
