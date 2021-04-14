// code by guedelmi
// modified by jph
package ch.ethz.idsc.tensor.mat;

import java.util.stream.IntStream;

import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.io.ScalarArray;
import ch.ethz.idsc.tensor.nrm.Hypot;
import ch.ethz.idsc.tensor.red.Diagonal;
import ch.ethz.idsc.tensor.sca.Abs;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Sign;

/* package */ class JacobiMethod implements Eigensystem {
  private static final int MAX_ITERATIONS = 50;
  // higher phase 1 count increases numerical precision
  private static final int[] PHASE1 = { //
      0, 0, 0, // n==0,1,2
      4, // n==3
      5, 5, // n==4,5
      6, 6, 6, 6, // n==6,...,9
      7 };
  private static final Scalar HUNDRED = DoubleScalar.of(100);
  private static final Scalar EPS = DoubleScalar.of(Math.ulp(1));
  // ---
  private final int n;
  private final Scalar[][] A;
  private Tensor d;
  private final Tensor V;

  public JacobiMethod(Tensor matrix, Chop chop) {
    n = matrix.length();
    A = ScalarArray.ofMatrix(matrix);
    int phase1 = PHASE1[Math.min(n, PHASE1.length - 1)];
    for (int ip = 0; ip < n; ++ip) {
      if (A[ip].length != n)
        throw TensorRuntimeException.of(matrix);
      for (int iq = ip + 1; iq < n; ++iq)
        chop.requireClose(A[ip][iq], A[iq][ip]);
    }
    V = IdentityMatrix.of(n);
    Tensor z = Array.zeros(n);
    Tensor b = Diagonal.of(matrix);
    d = b.copy();
    Scalar factor = DoubleScalar.of(0.2 / (n * n));
    for (int iteration = 0; iteration < MAX_ITERATIONS; ++iteration) {
      Scalar sum = A[0][0].zero();
      for (int ip = 0; ip < n - 1; ++ip)
        for (int iq = ip + 1; iq < n; ++iq)
          sum = sum.add(Abs.FUNCTION.apply(A[ip][iq]));
      if (Scalars.isZero(sum)) {
        return;
      }
      Scalar tresh = sum.multiply(factor);
      if (phase1 <= iteration)
        tresh = tresh.zero(); // preserve unit
      for (int ip = 0; ip < n - 1; ++ip)
        for (int iq = ip + 1; iq < n; ++iq) {
          Scalar aipiq = A[ip][iq];
          Scalar Aipiq = Abs.FUNCTION.apply(aipiq);
          Scalar g = HUNDRED.multiply(Aipiq);
          if (phase1 < iteration && //
              Scalars.lessEquals(g, EPS.multiply(Abs.FUNCTION.apply(d.Get(ip)))) && //
              Scalars.lessEquals(g, EPS.multiply(Abs.FUNCTION.apply(d.Get(iq)))))
            A[ip][iq] = aipiq.zero();
          else //
          if (Scalars.lessThan(tresh, Aipiq))
            process(ip, iq, g, z);
        }
      b = b.add(z);
      z = Array.zeros(n);
      d = b.copy();
    }
    throw TensorRuntimeException.of(matrix);
  }

  private void process(int ip, int iq, Scalar g, Tensor z) {
    Scalar aipiq = A[ip][iq];
    Scalar h = d.Get(iq).subtract(d.Get(ip));
    Scalar t;
    if (Scalars.lessEquals(g, EPS.multiply(Abs.FUNCTION.apply(h))))
      t = aipiq.divide(h);
    else {
      Scalar theta = h.divide(aipiq.add(aipiq));
      t = Abs.FUNCTION.apply(theta).add(Hypot.withOne(theta)).reciprocal();
      if (Sign.isNegative(theta))
        t = t.negate();
    }
    Scalar c = Hypot.withOne(t);
    Scalar s = t.divide(c);
    Scalar tau = t.divide(c.add(c.one()));
    Scalar fh = t.multiply(aipiq);
    z.set(fh::add, iq);
    d.set(fh::add, iq);
    Scalar fn = fh.negate();
    z.set(fn::add, ip);
    d.set(fn::add, ip);
    A[ip][iq] = aipiq.zero();
    IntStream.range(0, ip).forEach(j -> rotateA(s, tau, j, ip, j, iq));
    IntStream.range(ip + 1, iq).forEach(j -> rotateA(s, tau, ip, j, j, iq));
    IntStream.range(iq + 1, n).forEach(j -> rotateA(s, tau, ip, j, iq, j));
    IntStream.range(0, n).forEach(j -> rotateV(s, tau, ip, j, iq, j));
  }

  private void rotateA(Scalar s, Scalar tau, int i, int j, int k, int l) {
    Scalar g = A[i][j];
    Scalar h = A[k][l];
    A[i][j] = g.subtract(g.multiply(tau).add(h).multiply(s));
    A[k][l] = g.subtract(h.multiply(tau)).multiply(s).add(h);
  }

  private void rotateV(Scalar s, Scalar tau, int i, int j, int k, int l) {
    Scalar g = V.Get(i, j);
    Scalar h = V.Get(k, l);
    V.set(g.subtract(g.multiply(tau).add(h).multiply(s)), i, j);
    V.set(g.subtract(h.multiply(tau)).multiply(s).add(h), k, l);
  }

  @Override // from Eigensystem
  public Tensor values() {
    return d;
  }

  @Override // from Eigensystem
  public Tensor vectors() {
    return V;
  }
}