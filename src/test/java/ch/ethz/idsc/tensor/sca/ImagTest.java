// code by jph
package ch.ethz.idsc.tensor.sca;

import ch.ethz.idsc.tensor.ExactScalarQ;
import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.StringScalar;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class ImagTest extends TestCase {
  public void testExact() {
    Scalar scalar = Imag.FUNCTION.apply(Scalars.fromString("3+I*6/7"));
    assertEquals(scalar, RationalScalar.of(6, 7));
    ExactScalarQ.require(scalar);
  }

  public void testTensorExact() {
    Tensor tensor = Imag.of(Tensors.fromString("{{3+I*6/7, 5*I}, 2, {}}"));
    assertEquals(tensor, Tensors.fromString("{{6/7, 5}, 0, {}}"));
    ExactTensorQ.require(tensor);
  }

  public void testIncrement() {
    Tensor matrix = Tensors.matrixInt(new int[][] { { -8, 3, -3 }, { 2, -2, 7 } });
    matrix.set(RealScalar.ONE::add, 0, 0);
    Tensor result = matrix.map(RealScalar.ONE::add);
    Tensor check = Tensors.matrixInt(new int[][] { { -6, 4, -2 }, { 3, -1, 8 } });
    assertEquals(result, check);
  }

  public void testFail() {
    Scalar scalar = StringScalar.of("string");
    AssertFail.of(() -> Imag.of(scalar));
  }
}
