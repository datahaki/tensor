// code by jph
package ch.alpine.tensor.alg;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.usr.AssertFail;

public class UnitVectorTest {
  @Test
  public void testRegular() {
    assertEquals(UnitVector.of(10, 3), IdentityMatrix.of(10).get(3));
  }

  @Test
  public void testFail() {
    AssertFail.of(() -> UnitVector.of(0, 0));
    AssertFail.of(() -> UnitVector.of(-1, 0));
    AssertFail.of(() -> UnitVector.of(3, -1));
    AssertFail.of(() -> UnitVector.of(3, 4));
    AssertFail.of(() -> UnitVector.of(10, 10));
  }
}
