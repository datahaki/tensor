// code by jph
package ch.alpine.tensor.mat.qr;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.Random;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Dot;
import ch.alpine.tensor.alg.Sort;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.lie.Symmetrize;
import ch.alpine.tensor.lie.TensorWedge;
import ch.alpine.tensor.mat.AntisymmetricMatrixQ;
import ch.alpine.tensor.mat.ConjugateTranspose;
import ch.alpine.tensor.mat.SquareMatrixQ;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.mat.UnitaryMatrixQ;
import ch.alpine.tensor.mat.UpperTriangularize;
import ch.alpine.tensor.mat.ev.Eigensystem;
import ch.alpine.tensor.mat.ex.MatrixExp;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.red.Diagonal;
import ch.alpine.tensor.red.Nest;
import ch.alpine.tensor.red.Total;
import ch.alpine.tensor.sca.Abs;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Sign;

class SchurDecompositionTest {
  private static final SchurDecomposition _check(Tensor matrix) {
    SchurDecomposition hd = SchurDecomposition.of(matrix);
    Tensor t = hd.getT();
    Tensor p = hd.getUnitary();
    // System.out.println("P and T");
    // System.out.println(Pretty.of(p.map(Round._3)));
    // System.out.println(Pretty.of(t.map(Round._3)));
    UnitaryMatrixQ.require(p);
    Tensor result = Dot.of(p, t, ConjugateTranspose.of(p));
    Tolerance.CHOP.requireClose(matrix, result);
    return hd;
  }

  @Test
  void testSimple() throws ClassNotFoundException, IOException {
    Tensor matrix = Tensors.matrixDouble(new double[][] { { 3.7, 0.8, 0.1 }, { .2, 5, .3 }, { .1, 0, 4.3 } });
    SchurDecomposition schurDecomposition = Serialization.copy(_check(matrix));
    Tensor t = schurDecomposition.getT();
    Tolerance.CHOP.requireClose(UpperTriangularize.of(t), t);
    assertTrue(schurDecomposition.toString().startsWith("SchurDecomposition["));
  }

  @ParameterizedTest
  @ValueSource(ints = { 2, 3, 5, 10 })
  void testRandom(int n) {
    Tensor matrix = RandomVariate.of(NormalDistribution.standard(), n, n);
    _check(matrix);
  }

  @ParameterizedTest
  @ValueSource(ints = { 2, 3, 5, 10 })
  void testRandomSymmetric(int n) {
    Tensor matrix = Symmetrize.of(RandomVariate.of(NormalDistribution.standard(), n, n));
    SchurDecomposition schurDecomposition = _check(matrix);
    Tensor t = schurDecomposition.getT();
    Tensor diag = Sort.of(Diagonal.of(t));
    Eigensystem eigensystem = Eigensystem.of(matrix);
    Tolerance.CHOP.requireClose(diag, Sort.of(eigensystem.values()));
  }

  @ParameterizedTest
  @ValueSource(ints = { 2, 3, 5, 10 })
  void testRandomUnits(int n) {
    Tensor matrix = RandomVariate.of(NormalDistribution.of(Quantity.of(3, "m"), Quantity.of(2, "m")), n, n);
    _check(matrix);
  }

  @Test
  void testSo3() {
    Tensor x = RandomVariate.of(NormalDistribution.standard(), 3);
    Tensor y = RandomVariate.of(NormalDistribution.standard(), 3);
    Tensor xy = TensorWedge.of(x, y);
    SchurDecomposition sd = _check(xy);
    Tensor t = sd.getT();
    AntisymmetricMatrixQ.require(t);
    Tensor d = Diagonal.of(t);
    Tolerance.CHOP.requireAllZero(d);
  }

  @ParameterizedTest
  @ValueSource(ints = { 2, 3, 5, 6 })
  void testSoN(int n) {
    Random random = new Random(2);
    Tensor x = RandomVariate.of(NormalDistribution.standard(), random, n);
    Tensor y = RandomVariate.of(NormalDistribution.standard(), random, n);
    Tensor xy = TensorWedge.of(x, y);
    SchurDecomposition sd = _check(xy);
    Tensor t = sd.getT();
    // System.out.println(Pretty.of(t.map(Round._4)));
    AntisymmetricMatrixQ.require(t);
    Tensor d = Diagonal.of(t);
    Tolerance.CHOP.requireAllZero(d);
    Tensor bin = t.map(Tolerance.CHOP).map(Abs.FUNCTION).map(Sign.FUNCTION);
    Tensor r = Nest.of(Total::of, bin, 2);
    assertEquals(r, RealScalar.TWO);
  }

  @ParameterizedTest
  @ValueSource(ints = { 2, 3, 5, 6 })
  void testSON(int n) {
    Random random = new Random(2);
    Tensor x = RandomVariate.of(NormalDistribution.of(0, 0.3), random, n);
    Tensor y = RandomVariate.of(NormalDistribution.of(0, 0.3), random, n);
    Tensor xy = TensorWedge.of(x, y);
    Tensor orth = MatrixExp.of(xy);
    SchurDecomposition sd = _check(orth);
    Tensor t = sd.getT();
    // System.out.println(Pretty.of(t.map(Round._4)));
    Tensor bin = t.map(Tolerance.CHOP).map(Abs.FUNCTION).map(Sign.FUNCTION);
    Tensor r = Nest.of(Total::of, bin, 2);
    assertEquals(r, RealScalar.of(n + 2));
  }

  @Test
  void Math4x4() {
    Tensor matrix = Tensors.fromString("{{0, 1, 2, 3}, {-1, 0, 4, 5}, {-2, -4, 0, 6}, {-3, -5, -6, 0}}");
    SchurDecomposition sd = _check(matrix);
    Tensor t = sd.getT();
    SquareMatrixQ.require(t);
    // confirmed with mathematica
    // System.out.println(Pretty.of(t.map(Round._4)));
  }

  @Test
  void testEps() {
    long EXPONENT_OFFSET = 1023l;
    double EPSILON = Double.longBitsToDouble((EXPONENT_OFFSET - 53l) << 52);
    double res = 1 - Math.nextDown(1.0);
    assertEquals(EPSILON, res);
    // System.out.println(res);
  }

  @Test
  void testChop() {
    Chop chop = Chop.below(Math.nextUp(1 - Math.nextDown(1.0)));
    chop.toString();
    // System.out.println(Chop.below(1e-16));
    // System.out.println(chop);
  }
}
