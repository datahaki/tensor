// code by jph
package ch.alpine.tensor.lie;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.mat.AntisymmetricMatrixQ;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.mat.re.MatrixRank;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class TensorWedgeTest extends TestCase {
  public void testLength0() {
    assertEquals(TensorWedge.of(), RealScalar.ONE);
  }

  public void testFailEmpty() {
    assertEquals(TensorWedge.of(Tensors.empty()), Tensors.empty());
  }

  public void testVectors() {
    Tensor x = Tensors.vector(5, 7, 6);
    Tensor y = Tensors.vector(1, 2, -4);
    Tensor xy = TensorWedge.of(x, y);
    Tensor expected = Tensors.fromString("{{0, 3, -26}, {-3, 0, -40}, {26, 40, 0}}"); // mathematica
    assertEquals(xy, expected);
  }

  public void testVectorSequence() {
    Tensor x = Tensors.vector(6, 2, -1);
    Tensor y = Tensors.vector(3, 4, 5);
    Tensor z = Tensors.vector(1, 2, 3);
    Tensor xy = TensorWedge.of(x, y);
    Tensor xy_z = TensorWedge.of(xy, z);
    Tensor xyz = TensorWedge.of(x, y, z);
    assertEquals(xy_z, xyz);
    assertEquals(xyz.get(2, 0), Tensors.vector(0, 2, 0));
  }

  public void testAntisymmetric() {
    Tensor matrix = RandomVariate.of(NormalDistribution.standard(), 4, 4);
    Tensor skewsy = TensorWedge.of(matrix);
    assertTrue(AntisymmetricMatrixQ.of(skewsy));
    assertEquals(MatrixRank.of(matrix), 4);
  }

  public void testCreate() {
    Tensor matrix = Tensors.fromString("{{1, 2}, {0, 4}}");
    Tensor wedged = TensorWedge.of(matrix);
    assertEquals(wedged, Tensors.fromString("{{0, 1}, {-1, 0}}"));
  }

  public void testVector() {
    Tensor vector = RandomVariate.of(NormalDistribution.standard(), 10);
    Tensor skewsy = TensorWedge.of(vector);
    assertEquals(vector, skewsy);
  }

  public void testScalar() {
    Tensor scalar = RealScalar.of(3.14);
    assertEquals(scalar, TensorWedge.of(scalar));
  }

  public void testAlternating() {
    Tensor alt = LeviCivitaTensor.of(3);
    assertEquals(alt, TensorWedge.of(alt));
  }

  public void testSome() {
    Tensor x = Tensors.vector(2, 3, 4);
    Tensor y = Tensors.vector(1, 1, 1);
    Tensor xy = TensorWedge.of(x, y);
    Tensor expected = Tensors.fromString("{{0, -1, -2}, {1, 0, -1}, {2, 1, 0}}"); // mathematica
    assertEquals(xy, expected);
    assertTrue(AntisymmetricMatrixQ.of(xy));
  }

  public void testCross() {
    Tensor vector = Tensors.vector(2, 3, 4);
    Tensor matrix = Cross.skew3(vector);
    assertTrue(AntisymmetricMatrixQ.of(matrix));
    assertEquals(matrix, TensorWedge.of(matrix));
  }

  public void testFailIrrectangular() {
    Tensor matrix = Tensors.fromString("{{1, 2}, {0, 4, 3}}");
    AssertFail.of(() -> TensorWedge.of(matrix));
  }

  public void testFailRectangularMatrix() {
    AssertFail.of(() -> TensorWedge.of(HilbertMatrix.of(3, 4)));
  }

  public void testFailRectangularArray() {
    AssertFail.of(() -> TensorWedge.of(Array.zeros(2, 2, 3)));
  }

  public void testFailLength() {
    TensorWedge.of(Array.zeros(3), Array.zeros(3));
    AssertFail.of(() -> TensorWedge.of(Array.zeros(3), Array.zeros(4)));
  }
}
