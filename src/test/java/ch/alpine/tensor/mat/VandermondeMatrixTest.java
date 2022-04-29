// code by jph
package ch.alpine.tensor.mat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.chq.ExactTensorQ;
import ch.alpine.tensor.jet.DurationScalar;
import ch.alpine.tensor.mat.re.MatrixRank;
import ch.alpine.tensor.num.GaussScalar;

public class VandermondeMatrixTest {
  @Test
  public void testSimple() {
    Tensor tensor = VandermondeMatrix.of(Tensors.vector(2, 1, 3, 4));
    ExactTensorQ.require(tensor);
    assertEquals(Transpose.of(tensor), Tensors.fromString("{{1, 1, 1, 1}, {2, 1, 3, 4}, {4, 1, 9, 16}, {8, 1, 27, 64}}"));
  }

  @Test
  public void test1() {
    Tensor tensor = VandermondeMatrix.of(Tensors.vector(2));
    ExactTensorQ.require(tensor);
    assertEquals(tensor, IdentityMatrix.of(1));
  }

  @Test
  public void test2() {
    Tensor tensor = VandermondeMatrix.of(Tensors.vector(2, 5));
    ExactTensorQ.require(tensor);
    assertEquals(Transpose.of(tensor), Tensors.fromString("{{1, 1}, {2, 5}}"));
  }

  @Test
  public void testGaussScalar() {
    Tensor xdata = Tensors.of(GaussScalar.of(3, 173), GaussScalar.of(13, 173), GaussScalar.of(4, 173));
    Tensor matrix = VandermondeMatrix.of(xdata);
    ExactTensorQ.require(matrix);
    int rank = MatrixRank.of(matrix);
    assertEquals(rank, 3);
  }

  @Test
  public void testGaussScalarDefree() {
    Tensor xdata = Tensors.of(GaussScalar.of(3, 19), GaussScalar.of(13, 19), GaussScalar.of(4, 19));
    Tensor matrix = VandermondeMatrix.of(xdata, 7);
    assertEquals(Dimensions.of(matrix), Arrays.asList(3, 8));
    int rank = MatrixRank.of(matrix);
    assertEquals(rank, 3);
  }

  @Test
  public void testDurationScalar() {
    Tensor vector = Tensors.of( //
        DurationScalar.fromSeconds(RealScalar.of(10)), //
        DurationScalar.fromSeconds(RealScalar.of(13.4)), //
        DurationScalar.fromSeconds(RealScalar.of(4.8)), //
        DurationScalar.fromSeconds(RealScalar.of(7)) //
    );
    VandermondeMatrix.of(vector, 1);
  }

  @Test
  public void testDegrees() {
    VandermondeMatrix.of(Tensors.empty(), 0);
    VandermondeMatrix.of(Tensors.empty(), 1);
    assertThrows(IllegalArgumentException.class, () -> VandermondeMatrix.of(Tensors.empty(), -1));
  }

  @Test
  public void testEmptyFail() {
    assertThrows(IllegalArgumentException.class, () -> VandermondeMatrix.of(Tensors.empty()));
  }

  @Test
  public void testScalarFail() {
    assertThrows(IllegalArgumentException.class, () -> VandermondeMatrix.of(RealScalar.ONE));
  }

  @Test
  public void testMatrixFail() {
    assertThrows(ClassCastException.class, () -> VandermondeMatrix.of(HilbertMatrix.of(3)));
  }
}
