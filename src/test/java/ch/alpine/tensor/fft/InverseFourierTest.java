// code by jph
package ch.alpine.tensor.fft;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.red.Entrywise;

public class InverseFourierTest {
  @RepeatedTest(10)
  public void testRandom() {
    Distribution distribution = NormalDistribution.standard();
    for (int n = 0; n < 7; ++n) {
      Tensor vector = Entrywise.with(ComplexScalar::of).apply( //
          RandomVariate.of(distribution, 1 << n), //
          RandomVariate.of(distribution, 1 << n));
      Tolerance.CHOP.requireClose(InverseFourier.of(Fourier.of(vector)), vector);
    }
  }

  @Test
  public void testFailScalar() {
    assertThrows(IllegalArgumentException.class, () -> InverseFourier.of(RealScalar.ONE));
  }

  @Test
  public void testFailEmpty() {
    assertThrows(IllegalArgumentException.class, () -> InverseFourier.of(Tensors.empty()));
  }

  @Test
  public void testFailMatrix() {
    assertThrows(ClassCastException.class, () -> InverseFourier.of(HilbertMatrix.of(4)));
  }
}
