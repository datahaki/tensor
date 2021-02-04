// code by jph
package ch.ethz.idsc.tensor.alg;

import java.util.Arrays;

import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.mat.DiagonalMatrix;
import ch.ethz.idsc.tensor.mat.HilbertMatrix;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;
import ch.ethz.idsc.tensor.num.Pi;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class ArrayFlattenTest extends TestCase {
  public void testSimple() {
    Tensor[][] blocks = new Tensor[2][2];
    blocks[0][0] = DiagonalMatrix.of(2, RealScalar.of(1));
    blocks[0][1] = DiagonalMatrix.of(2, RealScalar.of(2));
    blocks[1][0] = DiagonalMatrix.of(2, RealScalar.of(3));
    blocks[1][1] = DiagonalMatrix.of(2, RealScalar.of(4));
    Tensor tensor = ArrayFlatten.of(blocks);
    assertEquals(tensor, Tensors.fromString("{{1, 0, 2, 0}, {0, 1, 0, 2}, {3, 0, 4, 0}, {0, 3, 0, 4}}"));
    tensor.set(Pi.VALUE::add, Tensor.ALL, Tensor.ALL);
    assertEquals(blocks[0][0], DiagonalMatrix.of(2, RealScalar.of(1)));
    assertEquals(blocks[0][1], DiagonalMatrix.of(2, RealScalar.of(2)));
    assertEquals(blocks[1][0], DiagonalMatrix.of(2, RealScalar.of(3)));
    assertEquals(blocks[1][1], DiagonalMatrix.of(2, RealScalar.of(4)));
  }

  public void testRect() {
    Tensor[][] blocks = new Tensor[][] { //
        { HilbertMatrix.of(2, 3), IdentityMatrix.of(2) }, //
        { IdentityMatrix.of(4), HilbertMatrix.of(4, 1) }, //
        { IdentityMatrix.of(5) } };
    Tensor tensor = ArrayFlatten.of(blocks);
    assertEquals(Dimensions.of(tensor), Arrays.asList(11, 5));
    ExactTensorQ.require(tensor);
  }

  public void testNullFail() {
    AssertFail.of(() -> ArrayFlatten.of(null));
  }
}
