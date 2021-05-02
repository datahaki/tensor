// code by jph
package ch.alpine.tensor.pdf;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class InverseErfcTest extends TestCase {
  public static final Chop CHOP_04 = Chop.below(1.4e-04);

  public void testInverseCDFAtMean() {
    InverseCDF inverseCDF = (InverseCDF) NormalDistribution.of(2, 8);
    Scalar phi = inverseCDF.quantile(RealScalar.of(0.5));
    Tolerance.CHOP.requireClose(phi, RealScalar.of(2));
  }

  public void testVector() {
    Tensor expect = Tensors.vector( //
        0.9061938024368229, -0.17914345462129164, -0.4769362762044699);
    Tensor actual = InverseErfc.of(Tensors.vector(0.2, 1.2, 1.5));
    Tolerance.CHOP.requireClose(expect, actual);
  }

  public void testCorners() {
    assertEquals(InverseErfc.of(RealScalar.of(0)), DoubleScalar.POSITIVE_INFINITY);
    assertEquals(InverseErfc.of(RealScalar.of(2)), DoubleScalar.NEGATIVE_INFINITY);
  }

  public void testFail() {
    InverseCDF inverseCDF = (InverseCDF) NormalDistribution.of(2, 8);
    AssertFail.of(() -> inverseCDF.quantile(RealScalar.of(1.5)));
  }
}
