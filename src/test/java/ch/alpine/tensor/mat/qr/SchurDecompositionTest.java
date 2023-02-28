// code by jph
package ch.alpine.tensor.mat.qr;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Dot;
import ch.alpine.tensor.alg.Sort;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.lie.Symmetrize;
import ch.alpine.tensor.mat.ConjugateTranspose;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.mat.UnitaryMatrixQ;
import ch.alpine.tensor.mat.UpperTriangularize;
import ch.alpine.tensor.mat.ev.Eigensystem;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.red.Diagonal;

class SchurDecompositionTest {
  private static final SchurDecomposition _check(Tensor matrix) {
    SchurDecomposition hd = SchurDecomposition.of(matrix);
    Tensor t = hd.getT();
    Tensor p = hd.getP();
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
}
