// code by jph
package ch.alpine.tensor.mat.gr;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.Random;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Unprotect;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.ConstantArray;
import ch.alpine.tensor.alg.Dot;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.chq.ExactTensorQ;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.io.ResourceData;
import ch.alpine.tensor.mat.HermitianMatrixQ;
import ch.alpine.tensor.mat.MatrixDotTranspose;
import ch.alpine.tensor.mat.PositiveSemidefiniteMatrixQ;
import ch.alpine.tensor.mat.SymmetricMatrixQ;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.mat.VandermondeMatrix;
import ch.alpine.tensor.mat.ev.Eigensystem;
import ch.alpine.tensor.mat.pi.LeastSquares;
import ch.alpine.tensor.mat.pi.PseudoInverse;
import ch.alpine.tensor.mat.re.MatrixRank;
import ch.alpine.tensor.mat.sv.SingularValueDecomposition;
import ch.alpine.tensor.num.GaussScalar;
import ch.alpine.tensor.num.Rationalize;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.pdf.c.TriangularDistribution;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.qty.Unit;
import ch.alpine.tensor.red.Entrywise;
import ch.alpine.tensor.red.Total;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Clips;
import ch.alpine.tensor.sca.Imag;

class InfluenceMatrixTest {
  private static void _check(InfluenceMatrix influenceMatrix) throws ClassNotFoundException, IOException {
    InfluenceMatrix _influenceMatrix = Serialization.copy(influenceMatrix);
    Tensor leverages = _influenceMatrix.leverages();
    leverages.stream() //
        .map(Scalar.class::cast) //
        .forEach(Clips.unit()::requireInside);
    Tensor leverages_sqrt = _influenceMatrix.leverages_sqrt();
    leverages_sqrt.stream() //
        .map(Scalar.class::cast) //
        .forEach(Clips.unit()::requireInside);
  }

  private static Tensor imageQR(Tensor design, Tensor vector) {
    return design.dot(LeastSquares.of(design, vector));
  }

  @Test
  void testLeftKernel() throws ClassNotFoundException, IOException {
    Random random = new Random(2);
    Tensor design = RandomVariate.of(NormalDistribution.standard(), random, 10, 3);
    InfluenceMatrix influenceMatrix = Serialization.copy(InfluenceMatrix.of(design));
    _check(influenceMatrix);
    Tensor vector = RandomVariate.of(NormalDistribution.standard(), random, 10);
    Tensor ker1 = influenceMatrix.kernel(vector);
    Tolerance.CHOP.requireAllZero(ker1.dot(design));
    Tensor ker2 = influenceMatrix.residualMaker().dot(vector);
    Tolerance.CHOP.requireClose(ker1, ker2);
    assertTrue(PositiveSemidefiniteMatrixQ.ofHermitian(influenceMatrix.matrix()));
  }

  private static Tensor proj(InfluenceMatrix influenceMatrix, Tensor v) {
    Tensor w1 = Transpose.of(Tensor.of(v.stream().map(influenceMatrix::image)));
    Tensor w2 = Tensor.of(v.stream().map(influenceMatrix::image));
    Tensor x = influenceMatrix.matrix();
    Tolerance.CHOP.requireClose(x.dot(w1), w1);
    Tolerance.CHOP.requireClose(w2.dot(x), w2);
    Tensor w = w1.add(w2).multiply(RationalScalar.HALF);
    SymmetricMatrixQ.require(w, Tolerance.CHOP);
    return w;
  }

  @Test
  void testLeftImage() throws ClassNotFoundException, IOException {
    int n = 10;
    Distribution distribution = NormalDistribution.standard();
    Tensor design = RandomVariate.of(distribution, n, 3);
    Tensor v0 = RandomVariate.of(distribution, n);
    Tensor v1 = RandomVariate.of(distribution, n);
    InfluenceMatrix influenceMatrix = InfluenceMatrix.of(design);
    _check(influenceMatrix);
    influenceMatrix.image(v0);
    Tensor vim1 = influenceMatrix.image(v1);
    Tensor x = influenceMatrix.matrix();
    Tensor vim2 = x.dot(v1);
    Tolerance.CHOP.requireClose(vim1, vim2);
    Tolerance.CHOP.requireClose( //
        v1.dot(design), //
        vim1.dot(design));
    Tensor vim3 = imageQR(design, v1);
    Tolerance.CHOP.requireClose(vim1, vim3);
    {
      Tensor v = RandomVariate.of(distribution, n, n);
      // Tensor w1 = Transpose.of(Tensor.of(v.stream().map(influenceMatrix::image)));
      // Tensor w2 = Tensor.of(v.stream().map(influenceMatrix::image));
      // Tolerance.CHOP.requireClose(x.dot(w1), w1);
      // Tolerance.CHOP.requireClose(w2.dot(x), w2);
      Tensor w = proj(influenceMatrix, v);
      // w1.add(w2).multiply(RationalScalar.HALF);
      // System.out.println(MatrixNorm2.bound(w.subtract(v)));
      // System.out.println(MatrixNorm2.bound(w.subtract(x.dot(w).add(w.dot(x)))));
      w = proj(influenceMatrix, v);
      w.length();
      // System.out.println(MatrixNorm2.bound(w.subtract(x.dot(w).add(w.dot(x)))));
      // Chop._08.requireClose(x.dot(w).add(w.dot(x)),w);
      // Chop._08.requireClose(w2.dot(x),w2);
      // Chop._08.requireClose(w, x.dot(w).add(w.dot(x)));
    }
  }

  @Test
  void testBicChallenge() {
    Tensor matrix = ResourceData.of("/ch/alpine/tensor/mat/pi/bic_fail.csv");
    InfluenceMatrix influenceMatrix = InfluenceMatrix.of(matrix);
    influenceMatrix.leverages();
  }

  @Test
  void testGaussScalar() {
    int prime = 131797;
    Tensor vector = Tensor.of(IntStream.range(100, 110).mapToObj(i -> GaussScalar.of(3 * i, prime)));
    Tensor design = VandermondeMatrix.of(vector, 3);
    {
      InfluenceMatrix influenceMatrix = InfluenceMatrix.of(design);
      assertTrue(IdempotentQ.of(influenceMatrix.matrix()));
      Scalar scalar = Total.ofVector(influenceMatrix.leverages());
      assertEquals(scalar, GaussScalar.of(4, prime));
    }
    {
      InfluenceMatrix influenceMatrix = InfluenceMatrix.of(Transpose.of(design));
      assertTrue(IdempotentQ.of(influenceMatrix.matrix()));
      Scalar scalar = Total.ofVector(influenceMatrix.leverages());
      assertEquals(scalar, GaussScalar.of(4, prime));
    }
  }

  @Test
  void testGaussScalar3() throws ClassNotFoundException, IOException {
    int n = 7;
    int m = 3;
    int prime = 6577;
    Random random = new Random();
    Tensor design = Tensors.matrix((i, j) -> GaussScalar.of(random.nextInt(), prime), n, m);
    if (MatrixRank.of(design) == m) {
      InfluenceMatrix influenceMatrix = Serialization.copy(InfluenceMatrix.of(design));
      assertInstanceOf(InfluenceMatrixImpl.class, influenceMatrix);
      Tensor matrix = influenceMatrix.matrix();
      SymmetricMatrixQ.require(matrix);
      assertEquals(Total.ofVector(influenceMatrix.leverages()), GaussScalar.of(m, prime));
      Tensor zeros = Dot.of(influenceMatrix.residualMaker(), matrix);
      Chop.NONE.requireAllZero(zeros);
      assertEquals(zeros, Array.fill(() -> GaussScalar.of(0, prime), n, n));
    }
  }

  @Test
  void testGaussScalar5() throws ClassNotFoundException, IOException {
    int n = 7;
    int m = 5;
    int prime = 7919;
    Random random = new Random(1);
    Tensor design = Tensors.matrix((i, j) -> GaussScalar.of(random.nextInt(), prime), n, m);
    Tensor d_dt = MatrixDotTranspose.of(design, design);
    ExactTensorQ.require(d_dt);
    if (MatrixRank.of(d_dt) == m) { // apparently rank(design) == m does not imply rank(d dt) == m !
      PseudoInverse.usingCholesky(design);
      InfluenceMatrix influenceMatrix = Serialization.copy(InfluenceMatrix.of(design));
      assertInstanceOf(InfluenceMatrixImpl.class, influenceMatrix);
      Tensor matrix = influenceMatrix.matrix();
      SymmetricMatrixQ.require(matrix);
      assertEquals(Total.ofVector(influenceMatrix.leverages()), GaussScalar.of(m, prime));
      Tensor zeros = Dot.of(influenceMatrix.residualMaker(), matrix);
      Chop.NONE.requireAllZero(zeros);
      assertEquals(zeros, Array.fill(() -> GaussScalar.of(0, prime), n, n));
    }
  }

  @Test
  void testSvdWithUnits() {
    Tensor design = ResourceData.of("/ch/alpine/tensor/mat/sv/svd1.csv");
    SingularValueDecomposition.of(design);
    InfluenceMatrix.of(design);
  }

  @Test
  void testComplex5x3() {
    Tensor re = RandomVariate.of(NormalDistribution.standard(), 5, 3);
    Tensor im = RandomVariate.of(NormalDistribution.standard(), 5, 3);
    Tensor design = Entrywise.with(ComplexScalar::of).apply(re, im);
    InfluenceMatrix influenceMatrix = InfluenceMatrix.of(design);
    assertEquals(Imag.of(influenceMatrix.leverages()), Array.zeros(5));
    Tensor matrix = influenceMatrix.matrix();
    InfluenceMatrixQ.require(matrix);
    Eigensystem eigensystem = Eigensystem.ofHermitian(matrix);
    Tolerance.CHOP.requireClose(eigensystem.values(), Tensors.vector(1, 1, 1, 0, 0));
  }

  @Test
  void testComplex5x3Exact() {
    Distribution distribution = TriangularDistribution.with(0, 2);
    Tensor re = RandomVariate.of(distribution, 5, 3);
    Tensor im = RandomVariate.of(distribution, 5, 3);
    Tensor design = Entrywise.with(ComplexScalar::of).apply(re, im).map(Rationalize._5);
    InfluenceMatrix influenceMatrix = InfluenceMatrix.of(design);
    assertEquals(Imag.of(influenceMatrix.leverages()), Array.zeros(5));
    Tensor matrix = influenceMatrix.matrix();
    InfluenceMatrixQ.require(matrix);
    ExactTensorQ.require(matrix);
  }

  @Test
  void testComplex3x5() {
    Tensor re = RandomVariate.of(NormalDistribution.standard(), 3, 5);
    Tensor im = RandomVariate.of(NormalDistribution.standard(), 3, 5);
    Tensor design = Entrywise.with(ComplexScalar::of).apply(re, im);
    InfluenceMatrix influenceMatrix = InfluenceMatrix.of(design);
    assertEquals(Imag.of(influenceMatrix.leverages()), Array.zeros(3));
    Tensor matrix = influenceMatrix.matrix();
    assertTrue(HermitianMatrixQ.of(matrix));
    InfluenceMatrixQ.require(matrix);
  }

  @Test
  void testComplex3x5Exact() {
    Tensor re = RandomVariate.of(NormalDistribution.standard(), 3, 5);
    Tensor im = RandomVariate.of(NormalDistribution.standard(), 3, 5);
    Tensor design = Entrywise.with(ComplexScalar::of).apply(re, im).map(Rationalize._5);
    InfluenceMatrix influenceMatrix = InfluenceMatrix.of(design);
    assertEquals(Imag.of(influenceMatrix.leverages()), Array.zeros(3));
    Tensor matrix = influenceMatrix.matrix();
    InfluenceMatrixQ.require(matrix);
    ExactTensorQ.require(matrix);
  }

  @Test
  void testZeroQuantity() {
    Tensor design = ConstantArray.of(Quantity.of(0, "m"), 4, 3);
    SingularValueDecomposition svd = SingularValueDecomposition.of(design);
    assertEquals(Unprotect.getUnitUnique(svd.getU()), Unit.ONE);
    assertEquals(Unprotect.getUnitUnique(svd.values()), Unit.of("m"));
    InfluenceMatrix influenceMatrix = InfluenceMatrix.of(design);
    Tensor matrix = SymmetricMatrixQ.require(influenceMatrix.matrix());
    ExactTensorQ.require(matrix);
    assertEquals(matrix, Array.zeros(4, 4));
    Tensor image = influenceMatrix.image(Tensors.vector(1, 2, 3, 4));
    assertEquals(image, Array.zeros(4));
    InfluenceMatrixImpl influenceMatrixImpl = (InfluenceMatrixImpl) influenceMatrix;
    assertFalse(influenceMatrixImpl.dotMatrix());
  }

  @Test
  void testNumericZeroQuantity() {
    Tensor design = ConstantArray.of(Quantity.of(0.0, "m"), 4, 3);
    InfluenceMatrix influenceMatrix = InfluenceMatrix.of(design);
    Tensor matrix = SymmetricMatrixQ.require(influenceMatrix.matrix());
    assertEquals(matrix, Array.zeros(4, 4));
    SingularValueDecomposition svd = SingularValueDecomposition.of(design);
    assertEquals(Unprotect.getUnitUnique(svd.getU()), Unit.ONE);
    assertEquals(Unprotect.getUnitUnique(svd.values()), Unit.of("m"));
  }

  @Test
  void testNullFail() {
    assertThrows(NullPointerException.class, () -> InfluenceMatrix.of(null));
  }

  @Test
  void testModifierPublic() {
    assertTrue(Modifier.isPublic(InfluenceMatrix.class.getModifiers()));
  }
}
