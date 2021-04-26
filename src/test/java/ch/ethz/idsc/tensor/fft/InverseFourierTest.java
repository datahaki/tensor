// code by jph
package ch.ethz.idsc.tensor.fft;

import ch.ethz.idsc.tensor.ComplexScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.mat.HilbertMatrix;
import ch.ethz.idsc.tensor.mat.Tolerance;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.red.Entrywise;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class InverseFourierTest extends TestCase {
  public void testRandom() {
    Distribution distribution = NormalDistribution.standard();
    for (int n = 0; n < 7; ++n)
      for (int count = 0; count < 10; ++count) {
        Tensor vector = Entrywise.with(ComplexScalar::of).apply( //
            RandomVariate.of(distribution, 1 << n), //
            RandomVariate.of(distribution, 1 << n));
        Tolerance.CHOP.requireClose(InverseFourier.of(Fourier.of(vector)), vector);
      }
  }

  public void testFailScalar() {
    AssertFail.of(() -> InverseFourier.of(RealScalar.ONE));
  }

  public void testFailEmpty() {
    AssertFail.of(() -> InverseFourier.of(Tensors.empty()));
  }

  public void testFailMatrix() {
    AssertFail.of(() -> InverseFourier.of(HilbertMatrix.of(4)));
  }
}
