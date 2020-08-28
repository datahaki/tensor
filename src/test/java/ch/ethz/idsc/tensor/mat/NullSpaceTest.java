// code by jph
package ch.ethz.idsc.tensor.mat;

import java.util.Arrays;

import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.MachineNumberQ;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.alg.Normalize;
import ch.ethz.idsc.tensor.alg.Reverse;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.alg.UnitVector;
import ch.ethz.idsc.tensor.lie.LieAlgebras;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.qty.QuantityTensor;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.N;
import junit.framework.TestCase;

public class NullSpaceTest extends TestCase {
  private static void _checkZeros(Tensor zeros) {
    int n = zeros.length();
    Tensor nul = NullSpace.usingSvd(zeros);
    assertEquals(Dimensions.of(nul), Arrays.asList(n, n));
    assertEquals(nul.get(0, 0), RealScalar.ONE);
    assertEquals(nul, IdentityMatrix.of(n));
  }

  public void testZerosUsingSvd() {
    for (int n = 1; n < 10; ++n) {
      _checkZeros(Array.zeros(n, n));
      _checkZeros(N.DOUBLE.of(Array.zeros(n, n)));
    }
  }

  public void testRowReduce() {
    Tensor m = Tensors.fromString("{{1, 2, 3, 4}, {5, 6, 7, 8}, {9, 10, 11, 12}, {13, 14, 15, 16}}");
    Tensor r = NullSpace.of(m);
    for (Tensor v : r)
      assertEquals(m.dot(v), Array.zeros(4));
    assertEquals(Dimensions.of(r), Arrays.asList(2, 4));
    assertFalse(MachineNumberQ.any(r));
    assertTrue(ExactTensorQ.of(r));
  }

  public void testZeros2() {
    Tensor m = Array.zeros(5, 5);
    Tensor r = NullSpace.of(m);
    assertEquals(r, IdentityMatrix.of(5));
    assertFalse(MachineNumberQ.any(r));
    assertTrue(ExactTensorQ.of(r));
  }

  public void testIdentity() {
    Tensor m = IdentityMatrix.of(5);
    Tensor r = NullSpace.of(m);
    assertEquals(r, Tensors.empty());
    assertFalse(MachineNumberQ.any(r));
    assertTrue(ExactTensorQ.of(r));
  }

  public void testIdentityReversed() {
    Tensor m = Reverse.of(IdentityMatrix.of(5));
    Tensor r = NullSpace.of(m);
    assertEquals(r, Tensors.empty());
    assertFalse(MachineNumberQ.any(r));
    assertTrue(ExactTensorQ.of(r));
  }

  public void testWikipediaKernel() {
    Tensor A = Tensors.matrix(new Number[][] { //
        { 1, 0, -3, 0, 2, -8 }, //
        { 0, 1, 5, 0, -1, 4 }, //
        { 0, 0, 0, 1, 7, -9 }, //
        { 0, 0, 0, 0, 0, 0 } //
    });
    Tensor nul = NullSpace.of(A);
    for (Tensor v : nul)
      assertEquals(A.dot(v), Array.zeros(4));
    assertEquals(Dimensions.of(nul), Arrays.asList(3, 6));
    assertFalse(MachineNumberQ.any(nul));
    assertTrue(ExactTensorQ.of(nul));
  }

  public void testSome1() {
    Tensor A = Tensors.matrix(new Number[][] { //
        { -1, -2, -1 }, //
        { -3, 1, 5 }, //
        { 3, 6, 3 }, //
        { 1, 2, 1 } //
    });
    Tensor nul = NullSpace.of(A);
    for (Tensor v : nul)
      assertEquals(A.dot(v), Array.zeros(4));
    assertEquals(Dimensions.of(nul), Arrays.asList(1, 3));
    assertFalse(MachineNumberQ.any(nul));
    assertTrue(ExactTensorQ.of(nul));
    Tensor nrr = NullSpace.usingRowReduce(A, IdentityMatrix.of(3));
    assertEquals(nul, nrr);
  }

  public void testSome2() {
    Tensor A = Tensors.matrix(new Number[][] { //
        { 1, 0, -3, 0, 2, -8 }, //
        { 0, 0, 1, 0, -1, 4 }, //
        { 0, 0, 0, 1, 7, -9 }, //
        { 0, 0, 0, 0, 0, 0 } //
    });
    Tensor nul = NullSpace.of(A);
    for (Tensor v : nul)
      assertEquals(A.dot(v), Array.zeros(4));
    assertEquals(Dimensions.of(nul), Arrays.asList(3, 6));
    assertFalse(MachineNumberQ.any(nul));
    assertTrue(ExactTensorQ.of(nul));
  }

  public void testSome3() {
    Tensor A = Tensors.matrix(new Number[][] { //
        { 0, 0, 0, 0, 0, 0 }, //
        { 0, 0, 1, 0, -1, 4 }, //
        { 0, 0, 0, 0, 1, -9 }, //
        { 1, 9, -3, 1, 2, -8 } //
    });
    Tensor nul = NullSpace.of(A);
    for (Tensor v : nul)
      assertEquals(A.dot(v), Array.zeros(4));
    assertEquals(Dimensions.of(nul), Arrays.asList(3, 6));
    assertFalse(MachineNumberQ.any(nul));
    assertTrue(ExactTensorQ.of(nul));
  }

  public void testSingleVector() {
    Tensor nullsp = NullSpace.of(Tensors.of(Tensors.vector(0.0, 1.0)));
    Chop._12.requireClose(nullsp, Tensors.of(Tensors.vector(1.0, 0.0)));
  }

  public void testComplex() {
    // {{17/101-32/101*I, 0, 1, -99/101+20/101*I},
    // {106/505-253/505*I, 1, 0, -89/101+19/101*I}}
    Tensor m = Tensors.fromString("{{1+3*I, 2, 3, 4+I}, {5, 6+I, 7, 8}}");
    Tensor nul = NullSpace.of(m);
    // {{1, 0, 17/13+32/13*I, -23/13-28/13*I},
    // {0, 1, -98/65+9/65*I, 37/65-16/65*I}}
    assertEquals(Dimensions.of(nul), Arrays.asList(2, 4));
    for (Tensor v : nul)
      assertEquals(m.dot(v), Array.zeros(2));
    assertFalse(MachineNumberQ.any(nul));
    assertTrue(ExactTensorQ.of(nul));
  }

  public void testMatsim() {
    Tensor matrix = Tensors.matrixDouble(new double[][] { //
        { 1.0, -0.2, -0.8 }, //
        { -0.2, 1.0, -0.8 }, //
        { -0.2, -0.8, 1.0 } });
    Tensor nullspace = NullSpace.of(matrix);
    assertEquals(Dimensions.of(nullspace), Arrays.asList(1, 3));
    assertTrue(Chop._14.close(nullspace.get(0), Normalize.with(Norm._2).apply(Tensors.vector(1, 1, 1))) //
        || Chop._14.close(nullspace.get(0), Normalize.with(Norm._2::ofVector).apply(Tensors.vector(-1, -1, -1))));
  }

  public void testQuantity() {
    Tensor mat = Tensors.of(QuantityTensor.of(Tensors.vector(1, 2), "m"));
    Tensor nul = NullSpace.of(mat);
    assertEquals(nul, Tensors.fromString("{{1, -1/2}}"));
    assertFalse(MachineNumberQ.any(nul));
    assertTrue(ExactTensorQ.of(nul));
  }

  public void testQuantityMixed() {
    Tensor mat = Tensors.of( //
        Tensors.of(Quantity.of(-2, "m"), Quantity.of(1, "kg"), Quantity.of(3, "s")));
    Tensor nul = NullSpace.of(mat);
    Chop.NONE.requireAllZero(mat.dot(Transpose.of(nul)));
  }

  public void testQuantityMixed2() {
    Tensor mat = Tensors.of( //
        Tensors.of(Quantity.of(-2, "m"), Quantity.of(1, "kg"), Quantity.of(3, "s")), //
        Tensors.of(Quantity.of(-4, "m"), Quantity.of(2, "kg"), Quantity.of(6, "s")), //
        Tensors.of(Quantity.of(+1, "m"), Quantity.of(3, "kg"), Quantity.of(1, "s")) //
    );
    Tensor nul = NullSpace.of(mat);
    assertEquals(Dimensions.of(nul), Arrays.asList(1, 3));
    Chop.NONE.requireAllZero(mat.dot(Transpose.of(nul)));
  }

  public void testRectangle2x3() {
    Tensor matrix = Tensors.fromString("{{1, 0, 0}, {0, 0, 0}}");
    Tensor tensor = NullSpace.of(matrix);
    assertEquals(tensor.get(0), UnitVector.of(3, 1));
    assertEquals(tensor.get(1), UnitVector.of(3, 2));
    assertTrue(Scalars.isZero(Det.of(matrix)));
  }

  public void testRectangle3x2() {
    Tensor matrix = Tensors.fromString("{{1, 0}, {0, 0}, {0, 0}}");
    Tensor tensor = NullSpace.of(matrix);
    assertEquals(tensor.get(0), UnitVector.of(2, 1));
    assertTrue(Scalars.isZero(Det.of(matrix)));
  }

  public void testZeros() {
    for (int d = 3; d < 6; ++d) {
      Tensor matrix = Array.zeros(3, d);
      Tensor id = IdentityMatrix.of(d);
      assertEquals(id, NullSpace.of(matrix));
      assertEquals(id, NullSpace.usingQR(matrix.map(N.DOUBLE)));
    }
  }

  public void testExtended() {
    Distribution distribution = UniformDistribution.unit();
    int n = 10;
    for (int d = 1; d < n; ++d) {
      Tensor matrix = RandomVariate.of(distribution, n, d);
      assertEquals(NullSpace.of(matrix), Tensors.empty());
      Tensor mt = Transpose.of(matrix);
      {
        Tensor nullspace = NullSpace.usingRowReduce(mt);
        assertEquals(Dimensions.of(nullspace), Arrays.asList(n - d, n));
        Chop._10.requireAllZero(mt.dot(Transpose.of(nullspace)));
      }
      {
        Tensor nullspace = NullSpace.usingQR(mt);
        assertEquals(Dimensions.of(nullspace), Arrays.asList(n - d, n));
        Chop._10.requireAllZero(mt.dot(Transpose.of(nullspace)));
        Chop._10.requireClose(nullspace.dot(Transpose.of(nullspace)), IdentityMatrix.of(n - d));
      }
      {
        Tensor nullspace = NullSpace.of(mt);
        assertEquals(Dimensions.of(nullspace), Arrays.asList(n - d, n));
        Chop._10.requireAllZero(mt.dot(Transpose.of(nullspace)));
      }
    }
  }

  public void testFailScalar() {
    try {
      NullSpace.of(RealScalar.ONE);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testFailVector() {
    try {
      NullSpace.of(Tensors.vector(1, 2, 3, 1));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testFailRank3() {
    try {
      NullSpace.of(LieAlgebras.sl2());
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
