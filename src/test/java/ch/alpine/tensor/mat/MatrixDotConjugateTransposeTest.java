// code by jph
package ch.alpine.tensor.mat;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.alg.Dot;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.red.Entrywise;

public class MatrixDotConjugateTransposeTest {
  @Test
  public void testDimensions() {
    Tensor tensor = MatrixDotConjugateTranspose.of(HilbertMatrix.of(2, 3));
    assertEquals(Dimensions.of(tensor), Arrays.asList(2, 2));
  }

  @Test
  public void testDotIdentity() {
    Tensor re = RandomVariate.of(NormalDistribution.standard(), 2, 4);
    Tensor im = RandomVariate.of(NormalDistribution.standard(), 2, 4);
    Tensor tensor = Entrywise.with(ComplexScalar::of).apply(re, im);
    Tensor result = MatrixDotConjugateTranspose.of(tensor);
    Tensor expect = Dot.of(tensor, ConjugateTranspose.of(tensor));
    Tolerance.CHOP.requireClose(result, expect);
    assertEquals(result, expect);
  }
}
