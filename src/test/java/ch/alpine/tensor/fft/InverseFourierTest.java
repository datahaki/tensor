// code by jph
package ch.alpine.tensor.fft;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.NormalDistribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.red.Entrywise;
import ch.alpine.tensor.usr.AssertFail;
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
