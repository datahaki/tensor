// code by jph
package ch.alpine.tensor.fft;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.pdf.ComplexNormalDistribution;
import ch.alpine.tensor.pdf.RandomVariate;

class InverseFourierTest {
  @RepeatedTest(10)
  void testRandom() {
    for (int n = 0; n < 7; ++n) {
      Tensor vector = RandomVariate.of(ComplexNormalDistribution.STANDARD, 1 << n);
      Tolerance.CHOP.requireClose(InverseFourier.of(Fourier.of(vector)), vector);
    }
  }

  @Test
  void testFailScalar() {
    assertThrows(Exception.class, () -> InverseFourier.of(RealScalar.ONE));
  }

  @Test
  void testFailEmpty() {
    assertThrows(Exception.class, () -> InverseFourier.of(Tensors.empty()));
  }

  @Test
  void testFailMatrix() {
    assertThrows(ClassCastException.class, () -> InverseFourier.of(HilbertMatrix.of(4)));
  }
}
