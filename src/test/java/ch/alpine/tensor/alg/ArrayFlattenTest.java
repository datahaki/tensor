// code by jph
package ch.alpine.tensor.alg;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ExactTensorQ;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.mat.DiagonalMatrix;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.num.Pi;

public class ArrayFlattenTest {
  @Test
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

  @Test
  public void testRect() {
    Tensor[][] blocks = new Tensor[][] { //
        { HilbertMatrix.of(2, 3), IdentityMatrix.of(2) }, //
        { IdentityMatrix.of(4), HilbertMatrix.of(4, 1) }, //
        { IdentityMatrix.of(5) } };
    Tensor tensor = ArrayFlatten.of(blocks);
    assertEquals(Dimensions.of(tensor), Arrays.asList(11, 5));
    ExactTensorQ.require(tensor);
  }

  @Test
  public void testNullFail() {
    assertThrows(NullPointerException.class, () -> ArrayFlatten.of(null));
  }
}
