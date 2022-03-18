// code by jph
package ch.alpine.tensor.mat.pd;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.mat.PositiveSemidefiniteMatrixQ;
import ch.alpine.tensor.mat.UnitaryMatrixQ;

public class SqrtPuTest {
  @Test
  public void testSimple() {
    SqrtPu sqrtPu = new SqrtPu(HilbertMatrix.of(3));
    UnitaryMatrixQ.require(sqrtPu.getUnitary());
    assertTrue(PositiveSemidefiniteMatrixQ.ofHermitian(sqrtPu.getPositiveSemidefinite()));
  }
}
