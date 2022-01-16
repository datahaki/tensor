// code by jph
package ch.alpine.tensor.lie.ad;

import java.util.Random;
import java.util.function.BinaryOperator;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.lie.LeviCivitaTensor;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.mat.ex.MatrixExp;
import ch.alpine.tensor.mat.ex.MatrixLog;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.UniformDistribution;
import ch.alpine.tensor.sca.N;

/* package */ enum TestHelper {
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

  public static Tensor so3_basis() {
    return LeviCivitaTensor.of(3).negate();
  }

  /** @return ad tensor of 3-dimensional so(3) */
  public static Tensor so3() {
    return new MatrixAlgebra(so3_basis()).ad();
  }

  public static Tensor se2_basis() {
    return Tensors.of( //
        Tensors.fromString("{{0,  0, 1}, {0, 0, 0}, {0, 0, 0}}"), //
        Tensors.fromString("{{0,  0, 0}, {0, 0, 1}, {0, 0, 0}}"), //
        Tensors.fromString("{{0, -1, 0}, {1, 0, 0}, {0, 0, 0}}"));
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

  public static Tensor sl2_basis() {
    return Tensors.of( //
        Tensors.fromString("{{1, 0}, {0, -1}}"), //
        Tensors.fromString("{{0, 1}, {-1, 0}}"), //
        Tensors.fromString("{{0, 1}, {+1, 0}}")).multiply(RationalScalar.HALF);
  }

  /** @return ad tensor of 3-dimensional sl(2) */
  public static Tensor sl2() {
    return new MatrixAlgebra(sl2_basis()).ad();
  }

  public static void check(MatrixAlgebra matrixAlgebra, int degree) {
    Distribution distribution = UniformDistribution.of(-0.1, 0.1);
    Tensor ad = matrixAlgebra.ad();
    int n = ad.length();
    BinaryOperator<Tensor> bch = BakerCampbellHausdorff.of(ad.map(N.DOUBLE), degree);
    Random random = new Random(10);
    for (int count = 0; count < 5; ++count) {
      Tensor x = RandomVariate.of(distribution, random, n);
      Tensor y = RandomVariate.of(distribution, random, n);
      Tensor z = bch.apply(x, y);
      Tensor mX = MatrixExp.of(matrixAlgebra.toMatrix(x));
      Tensor mY = MatrixExp.of(matrixAlgebra.toMatrix(y));
      Tensor mZ = MatrixLog.of(mX.dot(mY));
      Tensor z_cmp = matrixAlgebra.toVector(mZ);
      Tolerance.CHOP.requireClose(z, z_cmp);
    }
  }
}
