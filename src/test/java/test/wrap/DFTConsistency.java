// code by jph
package test.wrap;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.ext.Integers;
import ch.alpine.tensor.fft.DiscreteFourierTransform;
import ch.alpine.tensor.fft.FourierDCT;
import ch.alpine.tensor.io.Pretty;
import ch.alpine.tensor.mat.HermitianMatrixQ;
import ch.alpine.tensor.mat.SymmetricMatrixQ;
import ch.alpine.tensor.mat.re.Inverse;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.ComplexDiskUniformDistribution;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Round;

public enum DFTConsistency {
  ;
  public static void checkReal(DiscreteFourierTransform discreteFourierTransform, boolean any) {
    Distribution distribution = NormalDistribution.standard();
    check(discreteFourierTransform, distribution, any);
  }

  public static void checkComplex(DiscreteFourierTransform discreteFourierTransform, boolean any) {
    Distribution distribution = ComplexDiskUniformDistribution.of(1);
    check(discreteFourierTransform, distribution, any);
  }

  private static void check(DiscreteFourierTransform discreteFourierTransform, Distribution distribution, boolean any) {
    for (int n = 1; n <= 16; ++n) {
      if (any || Integers.isPowerOf2(n)) {
        Tensor vector = RandomVariate.of(distribution, n);
        Tensor matrix = discreteFourierTransform.matrix(n);
        Tensor res1 = matrix.dot(vector);
        Tensor res2 = discreteFourierTransform.transform(vector);
        Chop._10.requireClose(res1, res2);
        Tensor origin = discreteFourierTransform.inverse().transform(res2);
        Chop._10.requireClose(vector, origin);
        Tensor invers = discreteFourierTransform.inverse().matrix(n);
        Chop._10.requireClose(Inverse.of(matrix), invers);
      }
    }
  }

  public static void show(DiscreteFourierTransform dft) {
    Tensor matrix = dft.matrix(8);
    IO.println(dft);
    IO.println(SymmetricMatrixQ.INSTANCE.test(matrix));
    IO.println(HermitianMatrixQ.INSTANCE.test(matrix));
    IO.println(Pretty.of(matrix.maps(Round._3)));
  }

  static void main() {
    for (DiscreteFourierTransform dft : FourierDCT.values()) {
      show(dft);
    }
  }
}
