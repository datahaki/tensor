// code by jph
package ch.alpine.tensor.lie.ad;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.lie.LeviCivitaTensor;

/** class provides ad-tensors of several low-dimensional Lie-algebras */
public enum LieAlgebras {
  ;
  private static final Scalar P1 = RealScalar.ONE;
  private static final Scalar N1 = RealScalar.ONE.negate();

  /** @return ad tensor of 3-dimensional Heisenberg Lie-algebra */
  public static Tensor he1() {
    Tensor ad = Array.zeros(3, 3, 3);
    ad.set(P1, 2, 1, 0);
    ad.set(N1, 2, 0, 1);
    return ad;
  }

  /** @return ad tensor of 3-dimensional so(3) */
  public static Tensor so3() {
    return LeviCivitaTensor.of(3).negate();
  }

  /** @return ad tensor of 3-dimensional se(2) */
  public static Tensor se2() {
    Tensor ad = Array.zeros(3, 3, 3);
    ad.set(N1, 1, 2, 0);
    ad.set(P1, 1, 0, 2);
    ad.set(N1, 0, 1, 2);
    ad.set(P1, 0, 2, 1);
    return ad;
  }

  // ---
  public static Tensor sl2_basis() {
    Tensor b1 = Tensors.fromString("{{1, 0}, {0, -1}}");
    Tensor b2 = Tensors.fromString("{{0, 1}, {-1, 0}}");
    Tensor b3 = Tensors.fromString("{{0, 1}, {1, 0}}");
    return Tensors.of(b1, b2, b3);
  }

  /** @return ad */
  public static Tensor sl2() {
    return Tensors.fromString( //
        "{{{0, 0, 0}, {0, 0, -2}, {0, 2, 0}}, {{0, 0, -2}, {0, 0, 0}, {2, 0, 0}}, {{0, -2, 0}, {2, 0, 0}, {0, 0, 0}}}");
  }
}
