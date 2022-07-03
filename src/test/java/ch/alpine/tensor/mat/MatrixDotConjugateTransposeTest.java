// code by jph
package ch.alpine.tensor.mat;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.alg.Dot;
import ch.alpine.tensor.pdf.ComplexNormalDistribution;
import ch.alpine.tensor.pdf.RandomVariate;

class MatrixDotConjugateTransposeTest {
  @Test
  void testDimensions() {
    Tensor tensor = MatrixDotConjugateTranspose.of(HilbertMatrix.of(2, 3));
    assertEquals(Dimensions.of(tensor), Arrays.asList(2, 2));
  }

  @Test
  void testDotIdentity() {
    Tensor tensor = RandomVariate.of(ComplexNormalDistribution.STANDARD, 2, 4);
    Tensor result = MatrixDotConjugateTranspose.of(tensor);
    Tensor expect = Dot.of(tensor, ConjugateTranspose.of(tensor));
    Tolerance.CHOP.requireClose(result, expect);
    assertEquals(result, expect);
  }
}
