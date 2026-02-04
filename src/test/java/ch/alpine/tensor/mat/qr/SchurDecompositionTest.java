// code by jph
package ch.alpine.tensor.mat.qr;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Sort;
import ch.alpine.tensor.lie.Symmetrize;
import ch.alpine.tensor.lie.TensorWedge;
import ch.alpine.tensor.mat.AntisymmetricMatrixQ;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.mat.UpperTriangularize;
import ch.alpine.tensor.mat.ev.Eigensystem;
import ch.alpine.tensor.mat.ex.MatrixExp;
import ch.alpine.tensor.mat.ex.MatrixLog;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.red.Diagonal;
import ch.alpine.tensor.red.Nest;
import ch.alpine.tensor.red.Total;
import ch.alpine.tensor.red.Trace;
import ch.alpine.tensor.sca.Abs;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Sign;
import test.wrap.SchurDecompositionQ;
import test.wrap.SerializableQ;

class SchurDecompositionTest {
  private static void _checkExpLog(Tensor matrix, SchurDecomposition schurDecomposition) {
    Tensor exp2 = schurDecomposition.exp();
    Tensor expm = MatrixExp.of(matrix);
    Chop._08.requireClose(expm, exp2);
    Tensor log2 = schurDecomposition.log();
    Tensor logm = MatrixLog.of(matrix);
    Chop._08.requireClose(logm, log2);
  }

  @Test
  void testSimple() {
    Tensor matrix = Tensors.matrixDouble(new double[][] { { 3.7, 0.8, 0.1 }, { .2, 5, .3 }, { .1, 0, 4.3 } });
    SchurDecomposition schurDecomposition = new SchurDecompositionQ(matrix).check(SchurDecomposition.of(matrix));
    SerializableQ.require(schurDecomposition);
    Tensor t = schurDecomposition.getT();
    Tolerance.CHOP.requireClose(UpperTriangularize.of(t), t);
    assertTrue(schurDecomposition.toString().startsWith("SchurDecomposition["));
    _checkExpLog(matrix, schurDecomposition);
  }

  @Test
  void testParlett2x2() {
    Tensor matrix = Tensors.matrixDouble(new double[][] { { 2, 1 }, { 0, 2.1 } });
    SchurDecomposition schurDecomposition = new SchurDecompositionQ(matrix).check(SchurDecomposition.of(matrix));
    _checkExpLog(matrix, schurDecomposition);
  }

  @ParameterizedTest
  @ValueSource(ints = { 2, 3, 5, 10 })
  void testRandom(int n) {
    Tensor matrix = RandomVariate.of(NormalDistribution.standard(), n, n);
    new SchurDecompositionQ(matrix).check(SchurDecomposition.of(matrix));
  }

  @ParameterizedTest
  @ValueSource(ints = { 2, 3, 5, 10 })
  void testRandomSymmetric(int n) {
    Tensor matrix = Symmetrize.of(RandomVariate.of(NormalDistribution.standard(), n, n));
    SchurDecomposition schurDecomposition = new SchurDecompositionQ(matrix).check(SchurDecomposition.of(matrix));
    Tensor t = schurDecomposition.getT();
    Tensor diag = Sort.of(Diagonal.of(t));
    Eigensystem eigensystem = Eigensystem.of(matrix);
    Tolerance.CHOP.requireClose(diag, Sort.of(eigensystem.values()));
  }

  @ParameterizedTest
  @ValueSource(ints = { 2, 3, 5, 10 })
  void testRandomUnits(int n) {
    Tensor matrix = RandomVariate.of(NormalDistribution.of(Quantity.of(3, "m"), Quantity.of(2, "m")), n, n);
    new SchurDecompositionQ(matrix).check(SchurDecomposition.of(matrix));
  }

  @ParameterizedTest
  @MethodSource(value = "test.bulk.TestDistributions#distributions")
  void testSo3(Distribution distribution) {
    Tensor x = RandomVariate.of(distribution, 3);
    Tensor y = RandomVariate.of(distribution, 3);
    Tensor matrix = TensorWedge.of(x, y);
    SchurDecomposition schurDecomposition = new SchurDecompositionQ(matrix).check(SchurDecomposition.of(matrix));
    Tensor t = schurDecomposition.getT();
    AntisymmetricMatrixQ.INSTANCE.requireMember(t);
    Tensor d = Diagonal.of(t);
    Tolerance.CHOP.requireAllZero(d);
  }

  @ParameterizedTest
  @ValueSource(ints = { 2, 3, 5, 6 })
  void testSoN(int n) {
    Random random = new Random(2);
    Tensor x = RandomVariate.of(NormalDistribution.standard(), random, n);
    Tensor y = RandomVariate.of(NormalDistribution.standard(), random, n);
    Tensor matrix = TensorWedge.of(x, y);
    SchurDecomposition schurDecomposition = new SchurDecompositionQ(matrix).check(SchurDecomposition.of(matrix));
    Tensor t = schurDecomposition.getT();
    AntisymmetricMatrixQ.INSTANCE.requireMember(t);
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
    Tensor matrix = MatrixExp.of(xy);
    SchurDecomposition schurDecomposition = new SchurDecompositionQ(matrix).check(SchurDecomposition.of(matrix));
    Tensor t = schurDecomposition.getT();
    Tensor bin = t.map(Tolerance.CHOP).map(Abs.FUNCTION).map(Sign.FUNCTION);
    Tensor r = Nest.of(Total::of, bin, 2);
    assertEquals(r, RealScalar.of(n + 2));
  }

  @Test
  void Math4x4() {
    Tensor matrix = Tensors.fromString("{{0, 1, 2, 3}, {-1, 0, 4, 5}, {-2, -4, 0, 6}, {-3, -5, -6, 0}}");
    SchurDecomposition schurDecomposition = new SchurDecompositionQ(matrix).check(SchurDecomposition.of(matrix));
    Tensor t = schurDecomposition.getT();
    Tolerance.CHOP.requireZero(Trace.of(t));
    AntisymmetricMatrixQ.INSTANCE.requireMember(t);
    Tolerance.CHOP.requireAllZero(UpperTriangularize.of(t, 2));
    Tensor exp = Tensors.vector(-9.502167235316495, 0, -0.8419131974721066);
    Tolerance.CHOP.requireClose(exp, Diagonal.of(t, 1));
  }

  @Test
  void testEps() {
    long EXPONENT_OFFSET = 1023l;
    double EPSILON = Double.longBitsToDouble((EXPONENT_OFFSET - 53l) << 52);
    double res = 1 - Math.nextDown(1.0);
    assertEquals(EPSILON, res);
  }

  @Test
  void testFail() {
    int n = 10;
    Tensor matrix = RandomVariate.of(NormalDistribution.of(0, 0.3), n, n);
    new SchurDecompositionQ(matrix).check(SchurDecomposition.of(matrix));
    SchurDecomposition.MAX_ITERATIONS.set(2);
    assertThrows(Exception.class, () -> SchurDecomposition.of(matrix));
    SchurDecomposition.MAX_ITERATIONS.remove();
  }

  @Test
  void testChop() {
    Chop chop = Chop.below(Math.nextUp(1 - Math.nextDown(1.0)));
    chop.toString();
  }
}
