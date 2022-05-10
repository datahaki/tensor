// code by jph
package ch.alpine.tensor.mat;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.function.Function;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.lie.LeviCivitaTensor;
import ch.alpine.tensor.mat.cd.CholeskyDecomposition;
import ch.alpine.tensor.mat.ev.Eigensystem;
import ch.alpine.tensor.mat.ex.MatrixExp;
import ch.alpine.tensor.mat.ex.MatrixLog;
import ch.alpine.tensor.mat.ex.MatrixSqrt;
import ch.alpine.tensor.mat.gr.InfluenceMatrix;
import ch.alpine.tensor.mat.pd.Orthogonalize;
import ch.alpine.tensor.mat.pi.PseudoInverse;
import ch.alpine.tensor.mat.qr.QRDecomposition;
import ch.alpine.tensor.mat.re.Det;
import ch.alpine.tensor.mat.re.Inverse;
import ch.alpine.tensor.mat.re.MatrixRank;
import ch.alpine.tensor.mat.re.RowReduce;
import ch.alpine.tensor.mat.sv.SingularValueDecomposition;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.sca.N;

class MatrixTest {
  private static <T> void _checkFail(Function<Tensor, T> function) {
    assertThrows(NullPointerException.class, () -> function.apply(null));
    assertThrows(Exception.class, () -> function.apply(RealScalar.ONE));
    assertThrows(Exception.class, () -> function.apply(Pi.VALUE));
    assertThrows(Exception.class, () -> function.apply(Tensors.empty()));
    assertThrows(Exception.class, () -> function.apply(Tensors.vector(1, 2, 3)));
    assertThrows(Exception.class, () -> function.apply(Tensors.vector(1.0, 2, 3)));
    assertThrows(Exception.class, () -> function.apply(LeviCivitaTensor.of(3)));
    assertThrows(Exception.class, () -> function.apply(LeviCivitaTensor.of(3).map(N.DOUBLE)));
    {
      Tensor tensor = Tensors.fromString("{{1, 2, 3}, {4, 5}}");
      assertThrows(Exception.class, () -> function.apply(tensor));
    }
    {
      Tensor tensor = Tensors.fromString("{{1, 2}, {3, 4, 5}}").map(N.DOUBLE);
      assertThrows(Exception.class, () -> function.apply(tensor));
    }
    {
      Tensor tensor = Tensors.fromString("{{1, 2, 3}, {4, 5}}");
      assertThrows(Exception.class, () -> function.apply(tensor));
    }
    {
      Tensor tensor = Tensors.fromString("{{1, 2}, {3, 4, 5}}").map(N.DOUBLE);
      assertThrows(Exception.class, () -> function.apply(tensor));
    }
  }

  @Test
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
    _checkFail(t -> PseudoInverse.of(SingularValueDecomposition.of(t)));
    _checkFail(Eigensystem::ofSymmetric);
    _checkFail(InfluenceMatrix::of);
    _checkFail(Det::of);
    _checkFail(MatrixExp::of);
    _checkFail(MatrixLog::of);
    _checkFail(MatrixSqrt::of);
    _checkFail(MatrixSqrt::ofSymmetric);
  }
}
