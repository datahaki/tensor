// code by jph
package ch.ethz.idsc.tensor.lie;

import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;
import ch.ethz.idsc.tensor.mat.SymmetricMatrixQ;
import ch.ethz.idsc.tensor.mat.Tolerance;
import ch.ethz.idsc.tensor.pdf.DiscreteUniformDistribution;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class SymmetrizeTest extends TestCase {
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

  public void testMatrixExplicit() {
    Distribution distribution = UniformDistribution.unit();
    Tensor tensor = RandomVariate.of(distribution, 3, 3);
    Tensor symmet = Symmetrize.of(tensor);
    Tolerance.CHOP.requireClose(symmet, tensor.add(Transpose.of(tensor)).multiply(RationalScalar.HALF));
  }

  public void testMatrixExact() {
    Distribution distribution = DiscreteUniformDistribution.of(-10, 10);
    Tensor tensor = RandomVariate.of(distribution, 3, 3);
    Tensor symmet = Symmetrize.of(tensor);
    Tolerance.CHOP.requireClose(symmet, tensor.add(Transpose.of(tensor)).multiply(RationalScalar.HALF));
    ExactTensorQ.require(symmet);
  }

  public void testScalar() {
    Tensor tensor = Symmetrize.of(RealScalar.ONE);
    assertEquals(tensor, RealScalar.ONE);
  }

  public void testEmpty() {
    assertEquals(Symmetrize.of(Tensors.empty()), Tensors.empty());
  }

  public void testVector() {
    Tensor vector = Tensors.vector(1, 2, 3, 4);
    Tensor tensor = Symmetrize.of(vector);
    assertEquals(tensor, vector);
    vector.set(RealScalar.ONE::add, 2);
    assertFalse(vector.equals(tensor));
  }

  public void testMatrix() {
    Distribution distribution = NormalDistribution.standard();
    Tensor tensor = RandomVariate.of(distribution, 9, 9);
    SymmetricMatrixQ.require(Symmetrize.of(tensor));
    assertEquals(Symmetrize.of(IdentityMatrix.of(10)), IdentityMatrix.of(10));
  }

  public void testRectangularFail() {
    Distribution distribution = UniformDistribution.unit();
    AssertFail.of(() -> Symmetrize.of(RandomVariate.of(distribution, 3, 2)));
    AssertFail.of(() -> Symmetrize.of(RandomVariate.of(distribution, 3, 3, 2)));
  }

  public void testNonArrayFail() {
    Tensor tensor = Tensors.fromString("{{1, 2}, {3}}");
    AssertFail.of(() -> Symmetrize.of(tensor));
  }

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
