// code by jph
package ch.ethz.idsc.tensor.mat;

import java.io.IOException;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Join;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.ext.HomeDirectory;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.lie.Symmetrize;
import ch.ethz.idsc.tensor.nrm.MatrixNormInfinity;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.qty.QuantityMagnitude;
import ch.ethz.idsc.tensor.sca.N;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class EigensystemTest extends TestCase {
  public void testPhase1Tuning() throws IOException {
    Distribution distribution = UniformDistribution.of(-2, 2);
    for (int n = 1; n < 13; ++n)
      for (int count = 0; count < 10; ++count) {
        Tensor matrix = Symmetrize.of(RandomVariate.of(distribution, n, n));
        Eigensystem eigensystem = Eigensystem.ofSymmetric(matrix);
        Tensor vectors = eigensystem.vectors();
        Tensor values = eigensystem.values();
        OrthogonalMatrixQ.require(vectors);
        Tensor recons = Transpose.of(vectors).dot(values.pmul(vectors));
        Scalar err = MatrixNormInfinity.of(matrix.subtract(recons));
        if (!Tolerance.CHOP.isClose(matrix, recons)) {
          System.err.println(err);
          // System.err.println("error");
          System.out.println("n=" + n);
          System.out.println(matrix);
          Export.of(HomeDirectory.file("eigensystem.csv"), matrix);
          fail();
        }
      }
  }

  public void testQuantity() {
    Tensor matrix = Tensors.fromString("{{10[m], -2[m]}, {-2[m], 4[m]}}");
    SymmetricMatrixQ.require(matrix);
    {
      Eigensystem eigensystem = Eigensystem.ofSymmetric(matrix);
      assertTrue(eigensystem.values().Get(0) instanceof Quantity);
      assertTrue(eigensystem.values().Get(1) instanceof Quantity);
    }
    {
      Eigensystem eigensystem = Eigensystem.ofSymmetric(N.DOUBLE.of(matrix));
      assertTrue(eigensystem.values().Get(0) instanceof Quantity);
      assertTrue(eigensystem.values().Get(1) instanceof Quantity);
    }
  }

  public void testQuantityLarge() {
    for (int n = 8; n < 10; ++n) {
      Tensor x = Symmetrize.of(RandomVariate.of(NormalDistribution.standard(), n, n)).map(s -> Quantity.of(s, "m"));
      Eigensystem eigensystem = Eigensystem.ofSymmetric(x);
      eigensystem.values().map(QuantityMagnitude.singleton("m"));
    }
  }

  public void testQuantityDegenerate() {
    int r = 4;
    for (int n = 8; n < 10; ++n) {
      Tensor v = Join.of( //
          RandomVariate.of(NormalDistribution.standard(), r), //
          Array.zeros(n - 4)).map(s -> Quantity.of(s, "m"));
      Tensor x = RandomVariate.of(NormalDistribution.standard(), n, n);
      Tensor matrix = Transpose.of(x).dot(v.pmul(x));
      assertEquals(MatrixRank.of(matrix), r);
      Eigensystem eigensystem = Eigensystem.ofSymmetric(matrix);
      eigensystem.values().map(QuantityMagnitude.singleton("m"));
    }
  }

  public void testQuantityMixed() {
    Tensor matrix = Tensors.fromString("{{10[m^2], 2[m*kg]}, {2[m*kg], 4[kg^2]}}");
    SymmetricMatrixQ.require(matrix);
    AssertFail.of(() -> Eigensystem.ofSymmetric(matrix));
  }

  public void testEmptyFail() {
    AssertFail.of(() -> Eigensystem.ofSymmetric(Tensors.empty()));
  }

  public void testNonSymmetricFail() {
    AssertFail.of(() -> Eigensystem.ofSymmetric(Tensors.fromString("{{1, 2}, {3, 4}}")));
  }

  public void testComplexFail() {
    Tensor matrix = Tensors.fromString("{{I, 0}, {0, I}}");
    SymmetricMatrixQ.require(matrix);
    AssertFail.of(() -> Eigensystem.ofSymmetric(matrix));
  }

  public void testComplex2Fail() {
    Tensor matrix = Tensors.fromString("{{0, I}, {I, 0}}");
    SymmetricMatrixQ.require(matrix);
    AssertFail.of(() -> Eigensystem.ofSymmetric(matrix));
  }

  public void testNonSymmetric2Fail() {
    AssertFail.of(() -> Eigensystem.ofSymmetric(Array.zeros(2, 3)));
  }
}
