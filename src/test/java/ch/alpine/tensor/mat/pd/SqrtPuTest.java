// code by jph
package ch.alpine.tensor.mat.pd;

import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.mat.PositiveSemidefiniteMatrixQ;
import ch.alpine.tensor.mat.UnitaryMatrixQ;
import junit.framework.TestCase;

public class SqrtPuTest extends TestCase {
  public void testSimple() {
    SqrtPu sqrtPu = new SqrtPu(HilbertMatrix.of(3));
    UnitaryMatrixQ.require(sqrtPu.getUnitary());
    assertTrue(PositiveSemidefiniteMatrixQ.ofHermitian(sqrtPu.getPositiveSemidefinite()));
  }
}
