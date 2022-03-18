// code by jph
package ch.alpine.tensor.lie;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ExactTensorQ;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.mat.SymmetricMatrixQ;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.pdf.c.UniformDistribution;
import ch.alpine.tensor.pdf.d.DiscreteUniformDistribution;
import ch.alpine.tensor.sca.Chop;

public class SymmetrizeTest {
  @Test
  public void testSimple() {
    Distribution distribution = UniformDistribution.unit();
    Tensor tensor = RandomVariate.of(distribution, 3, 3, 3);
    Tensor symmet = Symmetrize.of(tensor);
    Chop._04.requireClose(symmet, Transpose.of(symmet, 0, 1, 2));
    Chop._04.requireClose(symmet, Transpose.of(symmet, 0, 2, 1));
    Chop._04.requireClose(symmet, Transpose.of(symmet, 1, 0, 2));
    Chop._04.requireClose(symmet, Transpose.of(symmet, 1, 2, 0));
    Chop._04.requireClose(symmet, Transpose.of(symmet, 2, 0, 1));
    Chop._04.requireClose(symmet, Transpose.of(symmet, 2, 1, 0));
    Tensor zeros = TensorWedge.of(symmet);
    Chop._10.requireAllZero(zeros);
  }

  @Test
  public void testMatrixExplicit() {
    Distribution distribution = UniformDistribution.unit();
    Tensor tensor = RandomVariate.of(distribution, 3, 3);
    Tensor symmet = Symmetrize.of(tensor);
    Tolerance.CHOP.requireClose(symmet, tensor.add(Transpose.of(tensor)).multiply(RationalScalar.HALF));
  }

  @Test
  public void testMatrixExact() {
    Distribution distribution = DiscreteUniformDistribution.of(-10, 10);
    Tensor tensor = RandomVariate.of(distribution, 3, 3);
    Tensor symmet = Symmetrize.of(tensor);
    Tolerance.CHOP.requireClose(symmet, tensor.add(Transpose.of(tensor)).multiply(RationalScalar.HALF));
    ExactTensorQ.require(symmet);
  }

  @Test
  public void testScalar() {
    Tensor tensor = Symmetrize.of(RealScalar.ONE);
    assertEquals(tensor, RealScalar.ONE);
  }

  @Test
  public void testEmpty() {
    assertEquals(Symmetrize.of(Tensors.empty()), Tensors.empty());
  }

  @Test
  public void testVector() {
    Tensor vector = Tensors.vector(1, 2, 3, 4);
    Tensor tensor = Symmetrize.of(vector);
    assertEquals(tensor, vector);
    vector.set(RealScalar.ONE::add, 2);
    assertFalse(vector.equals(tensor));
  }

  @Test
  public void testMatrix() {
    Distribution distribution = NormalDistribution.standard();
    Tensor tensor = RandomVariate.of(distribution, 9, 9);
    SymmetricMatrixQ.require(Symmetrize.of(tensor));
    assertEquals(Symmetrize.of(IdentityMatrix.of(10)), IdentityMatrix.of(10));
  }

  @Test
  public void testRectangularFail() {
    Distribution distribution = UniformDistribution.unit();
    assertThrows(IllegalArgumentException.class, () -> Symmetrize.of(RandomVariate.of(distribution, 3, 2)));
    assertThrows(IllegalArgumentException.class, () -> Symmetrize.of(RandomVariate.of(distribution, 3, 3, 2)));
  }

  @Test
  public void testNonArrayFail() {
    Tensor tensor = Tensors.fromString("{{1, 2}, {3}}");
    assertThrows(TensorRuntimeException.class, () -> Symmetrize.of(tensor));
  }

  @Test
  public void test01() {
    Tensor tensor = Array.of(Tensors::vector, 3, 3); // results in dimensions [3 x 3 x 2]
    assertFalse(SymmetricMatrixQ.of(tensor.get(Tensor.ALL, Tensor.ALL, 0)));
    Tensor symmetrize01 = Symmetrize._01(tensor);
    assertEquals(Dimensions.of(tensor), Dimensions.of(symmetrize01));
    assertFalse(Chop._06.allZero(symmetrize01));
    assertTrue(SymmetricMatrixQ.of(symmetrize01.get(Tensor.ALL, Tensor.ALL, 0)));
    assertTrue(SymmetricMatrixQ.of(symmetrize01.get(Tensor.ALL, Tensor.ALL, 1)));
  }
}
