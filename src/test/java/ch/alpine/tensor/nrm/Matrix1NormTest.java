// code by jph
package ch.alpine.tensor.nrm;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.num.Rationalize;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.TrapezoidalDistribution;
import junit.framework.TestCase;

public class Matrix1NormTest extends TestCase {
  public void testOneInfNorm2() {
    Tensor a = Tensors.vector(1, 2);
    Tensor b = Tensors.vector(3, 4);
    Tensor c = Tensors.of(a, b);
    assertEquals(Matrix1Norm.of(c), Scalars.fromString("6"));
    assertEquals(MatrixInfinityNorm.of(c), Scalars.fromString("7"));
  }

  public void testTranspose() {
    Distribution distribution = TrapezoidalDistribution.with(1, 2, 2);
    Tensor matrix = RandomVariate.of(distribution, 3, 6).map(Rationalize._3);
    Scalar mn1 = Matrix1Norm.of(matrix);
    Scalar mni = MatrixInfinityNorm.of(Transpose.of(matrix));
    assertEquals(mn1, mni);
  }
}
