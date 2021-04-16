// code by jph
package ch.ethz.idsc.tensor.mat;

import java.util.function.Function;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.MatrixQ;
import ch.ethz.idsc.tensor.lie.LeviCivitaTensor;
import ch.ethz.idsc.tensor.lie.MatrixExp;
import ch.ethz.idsc.tensor.lie.MatrixLog;
import ch.ethz.idsc.tensor.lie.MatrixSqrt;
import ch.ethz.idsc.tensor.mat.gr.InfluenceMatrix;
import ch.ethz.idsc.tensor.mat.qr.QRDecomposition;
import ch.ethz.idsc.tensor.mat.re.Det;
import ch.ethz.idsc.tensor.mat.re.RowReduce;
import ch.ethz.idsc.tensor.num.Pi;
import ch.ethz.idsc.tensor.sca.N;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class QRSignOperatorTest extends TestCase {
  private static <T> void _checkFail(Function<Tensor, T> function) {
    AssertFail.of(() -> function.apply(null));
    AssertFail.of(() -> function.apply(RealScalar.ONE));
    AssertFail.of(() -> function.apply(Pi.VALUE));
    AssertFail.of(() -> function.apply(Tensors.empty()));
    AssertFail.of(() -> function.apply(Tensors.vector(1, 2, 3)));
    AssertFail.of(() -> function.apply(Tensors.vector(1.0, 2, 3)));
    AssertFail.of(() -> function.apply(LeviCivitaTensor.of(3)));
    AssertFail.of(() -> function.apply(LeviCivitaTensor.of(3).map(N.DOUBLE)));
    {
      Tensor tensor = Tensors.fromString("{{1, 2, 3}, {4, 5}}");
      AssertFail.of(() -> function.apply(tensor));
    }
    {
      Tensor tensor = Tensors.fromString("{{1, 2}, {3, 4, 5}}").map(N.DOUBLE);
      AssertFail.of(() -> function.apply(tensor));
    }
    {
      Tensor tensor = Tensors.fromString("{{1, 2, 3}, {4, 5}}");
      AssertFail.of(() -> function.apply(tensor));
    }
    {
      Tensor tensor = Tensors.fromString("{{1, 2}, {3, 4, 5}}").map(N.DOUBLE);
      AssertFail.of(() -> function.apply(tensor));
    }
  }

  public void testSimple() {
    _checkFail(RowReduce::of);
    _checkFail(MatrixRank::of);
    _checkFail(NullSpace::of);
    _checkFail(QRDecomposition::of);
    _checkFail(Orthogonalize::of);
    _checkFail(CholeskyDecomposition::of);
    _checkFail(MatrixQ::require);
    _checkFail(Inverse::of);
    _checkFail(PseudoInverse::of);
    _checkFail(PseudoInverse::usingSvd);
    _checkFail(Eigensystem::ofSymmetric);
    _checkFail(InfluenceMatrix::of);
    _checkFail(Det::of);
    _checkFail(MatrixExp::of);
    _checkFail(MatrixLog::of);
    _checkFail(MatrixSqrt::of);
    _checkFail(MatrixSqrt::ofSymmetric);
  }
}
