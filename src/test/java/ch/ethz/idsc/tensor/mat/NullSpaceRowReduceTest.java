// code by jph
package ch.ethz.idsc.tensor.mat;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.Unprotect;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.alg.UnitVector;
import ch.ethz.idsc.tensor.num.GaussScalar;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;
import junit.framework.TestCase;

public class NullSpaceRowReduceTest extends TestCase {
  public void testGaussScalar() {
    Random random = new Random();
    int prime = 7741;
    int dim1 = 6;
    Tensor matrix = Array.fill(() -> GaussScalar.of(random.nextInt(), prime), dim1 - 2, dim1);
    Tensor identi = IdentityMatrix.of(Unprotect.dimension1(matrix), GaussScalar.of(1, prime));
    List<Integer> list = Dimensions.of(identi);
    assertEquals(list, Arrays.asList(dim1, dim1));
    Tensor nullsp = NullSpace.usingRowReduce(matrix, identi);
    Scalar det = Det.of(matrix);
    assertEquals(det, GaussScalar.of(0, prime));
    assertEquals(Dimensions.of(nullsp), Arrays.asList(2, 6));
  }

  public void testRectangle3x2G() {
    ScalarUnaryOperator suo = scalar -> GaussScalar.of(scalar.number().longValue(), 7);
    Tensor matrix = Tensors.fromString("{{1, 0}, {0, 0}, {0, 0}}").map(suo);
    Tensor tensor = NullSpace.usingRowReduce(matrix, IdentityMatrix.of(2, GaussScalar.of(1, 7)));
    assertEquals(tensor.get(0), UnitVector.of(2, 1).map(suo));
    assertTrue(Scalars.isZero(Det.of(matrix)));
  }

  public void testRectangle2x3G() {
    ScalarUnaryOperator suo = scalar -> GaussScalar.of(scalar.number().longValue(), 7);
    Tensor matrix = Tensors.fromString("{{1, 0, 0}, {0, 0, 0}}").map(suo);
    Tensor tensor = NullSpace.usingRowReduce(matrix, IdentityMatrix.of(3, GaussScalar.of(1, 7)));
    assertEquals(tensor.get(0), UnitVector.of(3, 1).map(suo));
    assertEquals(tensor.get(1), UnitVector.of(3, 2).map(suo));
    assertTrue(Scalars.isZero(Det.of(matrix)));
  }

  public void testRectangle3x2GVectorFail() {
    ScalarUnaryOperator suo = scalar -> GaussScalar.of(scalar.number().longValue(), 7);
    Tensor matrix = Tensors.fromString("{{1, 0}, {0, 0}, {0, 0}}").map(suo);
    try {
      NullSpace.usingRowReduce(matrix, IdentityMatrix.of(2, GaussScalar.of(1, 7)).get(0));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testRectangle3x2GRectFail() {
    ScalarUnaryOperator suo = scalar -> GaussScalar.of(scalar.number().longValue(), 7);
    Tensor matrix = Tensors.fromString("{{1, 0}, {0, 0}, {0, 0}}").map(suo);
    Tensor identity = IdentityMatrix.of(3, GaussScalar.of(1, 7)).extract(0, 2);
    try {
      NullSpace.usingRowReduce(matrix, identity);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
