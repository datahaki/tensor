// code by jph
package ch.ethz.idsc.tensor.alg;

import ch.ethz.idsc.tensor.mat.IdentityMatrix;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class UnitVectorTest extends TestCase {
  public void testRegular() {
    assertEquals(UnitVector.of(10, 3), IdentityMatrix.of(10).get(3));
  }

  public void testFail() {
    AssertFail.of(() -> UnitVector.of(0, 0));
    AssertFail.of(() -> UnitVector.of(-1, 0));
    AssertFail.of(() -> UnitVector.of(3, -1));
    AssertFail.of(() -> UnitVector.of(3, 4));
    AssertFail.of(() -> UnitVector.of(10, 10));
  }
}
