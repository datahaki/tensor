// code by jph
package ch.alpine.tensor.fft;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.red.Entrywise;

class FourierTest {
  @Test
  public void test2() {
    Tensor vector = Tensors.fromString("{1 + 2*I, 3 + 11*I}");
    Tensor expect = Tensors.fromString("{2.828427124746190 + 9.19238815542512*I, -1.414213562373095 - 6.36396103067893*I}");
    Tolerance.CHOP.requireClose(Fourier.of(vector), expect);
    Tolerance.CHOP.requireClose(InverseFourier.of(Fourier.of(vector)), vector);
  }

  @Test
  public void test2Quantity() {
    Tensor vector = Tensors.fromString("{1 + 2*I[m], 3 + 11*I[m]}");
    Tensor expect = Tensors.fromString("{2.828427124746190 + 9.19238815542512*I[m], -1.414213562373095 - 6.36396103067893*I[m]}");
    Tolerance.CHOP.requireClose(Fourier.of(vector), expect);
    Tolerance.CHOP.requireClose(InverseFourier.of(Fourier.of(vector)), vector);
  }

  @Test
  public void test4() {
    Tensor vector = Tensors.vector(1, 2, 0, 0);
    Tensor tensor = Fourier.of(vector);
    Tensor expect = Tensors.fromString("{1.5, 0.5 + I, -0.5, 0.5 - I}");
    Tolerance.CHOP.requireClose(FourierMatrix.of(vector.length()).dot(vector), expect);
    Tolerance.CHOP.requireClose(vector.dot(FourierMatrix.of(vector.length())), expect);
    Tolerance.CHOP.requireClose(tensor, expect);
    Tensor backed = Fourier.of(expect);
    Tolerance.CHOP.requireClose(backed, Tensors.vector(1, 0, 0, 2));
  }

  @RepeatedTest(10)
  public void testRandom() {
    Distribution distribution = NormalDistribution.standard();
    for (int n = 0; n < 7; ++n) {
      Tensor vector = Entrywise.with(ComplexScalar::of).apply( //
          RandomVariate.of(distribution, 1 << n), //
          RandomVariate.of(distribution, 1 << n));
      Tensor result = Fourier.of(vector);
      Tensor dotmat = vector.dot(FourierMatrix.of(vector.length()));
      Tolerance.CHOP.requireClose(dotmat, result);
    }
  }

  @Test
  public void testFailScalar() {
    assertThrows(IllegalArgumentException.class, () -> Fourier.of(RealScalar.ONE));
  }

  @Test
  public void testFailEmpty() {
    assertThrows(IllegalArgumentException.class, () -> Fourier.of(Tensors.empty()));
  }

  @Test
  public void test3Fail() {
    Tensor vector = Tensors.vector(1, 2, 0);
    assertThrows(TensorRuntimeException.class, () -> Fourier.of(vector));
  }

  @Test
  public void testFailMatrix() {
    assertThrows(ClassCastException.class, () -> Fourier.of(HilbertMatrix.of(4)));
  }
}
