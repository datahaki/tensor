// code by jph
package ch.alpine.tensor.mat.sv;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.OrderedQ;
import ch.alpine.tensor.alg.Reverse;
import ch.alpine.tensor.mat.MatrixDotTranspose;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.mat.ev.Eigensystem;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.UniformDistribution;
import ch.alpine.tensor.pdf.d.DiscreteUniformDistribution;
import ch.alpine.tensor.qty.Quantity;

public class SingularValueListTest {
  @Test
  public void testSimple() {
    Distribution distribution = UniformDistribution.of(-1, 1);
    Tensor x = RandomVariate.of(distribution, 3, 4);
    Tensor matrix = MatrixDotTranspose.of(x, x);
    Tensor values1 = Eigensystem.ofSymmetric(matrix).values();
    Tensor values2 = SingularValueList.of(matrix);
    Tolerance.CHOP.requireClose(values1, values2);
    OrderedQ.require(Reverse.of(values2));
  }

  @Test
  public void testMixedUnitFail() {
    Distribution distribution = DiscreteUniformDistribution.of(1, 4);
    Tensor matrix = RandomVariate.of(distribution, 7, 2);
    matrix.set(s -> Quantity.of((Scalar) s, "s"), Tensor.ALL, 1);
    assertThrows(IllegalArgumentException.class, () -> SingularValueDecomposition.of(matrix));
  }
}
