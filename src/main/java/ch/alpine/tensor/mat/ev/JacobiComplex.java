// code by guedelmi
// modified by jph
package ch.alpine.tensor.mat.ev;

import java.util.stream.IntStream;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.BasisTransform;
import ch.alpine.tensor.mat.HermitianMatrixQ;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.mat.re.Inverse;
import ch.alpine.tensor.mat.re.LinearSolve;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.red.Diagonal;
import ch.alpine.tensor.sca.Abs;
import ch.alpine.tensor.sca.Arg;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Real;

/** https://en.wikipedia.org/wiki/Jacobi_method_for_complex_Hermitian_matrices */
/* package */ class JacobiComplex implements Eigensystem {
  private final int n;
  private Tensor H;
  private Tensor V;

  public JacobiComplex(Tensor matrix, Chop chop) {
    HermitianMatrixQ.require(matrix, chop);
    n = matrix.length();
    H = matrix.copy();
    V = IdentityMatrix.of(n); // init vectors
    for (int iteration = 0; iteration < 10; ++iteration) {
      for (int p = 0; p < n; ++p)
        for (int q = 0; q < n; ++q)
          if (p != q) {
            Scalar hpp = H.Get(p, p);
            Scalar hpq = H.Get(p, q);
            Scalar hqq = H.Get(q, q);
            Scalar phi1 = Arg.FUNCTION.apply(hpq);
            Scalar abs = Abs.FUNCTION.apply(hpq);
            Scalar phi2 = abs.add(abs).divide(hpp.subtract(hqq));
            Scalar theta1 = phi1.add(phi1).subtract(Pi.VALUE).multiply(RationalScalar.of(1, 4));
            Scalar theta2 = phi2.divide(RealScalar.TWO);
            Tensor v = Inverse.of(new Givens2(n, theta1, theta2, p, q).matrix());
            H = BasisTransform.ofMatrix(H, v);
            IntStream.range(0, n).forEach(i -> H.set(Real.FUNCTION, i, i));
            V = LinearSolve.of(v, V);
          }
    }
  }

  @Override // from Eigensystem
  public Tensor values() {
    return Diagonal.of(H);
  }

  @Override // from Eigensystem
  public Tensor vectors() {
    return V;
  }

  Tensor h() {
    return H;
  }
}
