// code by jph
package ch.alpine.tensor.lie.ad;

import java.util.Random;
import java.util.function.BinaryOperator;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.mat.ex.MatrixExp;
import ch.alpine.tensor.mat.ex.MatrixLog;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.UniformDistribution;
import ch.alpine.tensor.sca.N;

/* package */ enum TestHelper {
  ;
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
