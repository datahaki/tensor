// code by jph
package ch.alpine.tensor.fft;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.mat.re.Inverse;
import ch.alpine.tensor.pdf.ComplexNormalDistribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.qty.Quantity;

class FourierDxTMatrixTest {
  private static final int[] inverse = { 0, 2, 1, 3 };

  @RepeatedTest(8)
  void testDCT(RepetitionInfo repetitionInfo) {
    int n = repetitionInfo.getCurrentRepetition();
    for (int i = 0; i < 4; ++i) {
      Tensor mat = FourierDCT.values()[i].matrix(n);
      Tensor inv = FourierDCT.values()[inverse[i]].matrix(n);
      Tolerance.CHOP.requireClose(inv, Inverse.of(mat));
    }
  }

  @RepeatedTest(8)
  void testDST(RepetitionInfo repetitionInfo) {
    int n = repetitionInfo.getCurrentRepetition();
    for (int i = 0; i < 4; ++i) {
      Tensor mat = FourierDST.values()[i].matrix(n);
      Tensor inv = FourierDST.values()[inverse[i]].matrix(n);
      Tolerance.CHOP.requireClose(inv, Inverse.of(mat));
    }
  }

  @RepeatedTest(6)
  void testDCT_vectorDCT(RepetitionInfo repetitionInfo) {
    int n = 1 << repetitionInfo.getCurrentRepetition();
    Tensor vector = RandomVariate.of(ComplexNormalDistribution.STANDARD, n);
    Tensor r1 = FourierDCT._2.of(vector);
    Tensor r2 = vector.dot(FourierDCT._2.matrix(n));
    Tolerance.CHOP.requireClose(r1, r2);
    Tensor r3 = FourierDCT._3.of(r1);
    Tolerance.CHOP.requireClose(r3, vector);
    Tensor r4 = r1.dot(FourierDCT._3.matrix(n));
    Tolerance.CHOP.requireClose(r3, r4);
  }

  @RepeatedTest(6)
  void testDST_vectorDST(RepetitionInfo repetitionInfo) {
    int n = 1 << repetitionInfo.getCurrentRepetition();
    Tensor vector = RandomVariate.of(ComplexNormalDistribution.STANDARD, n);
    Tensor r1 = FourierDST._2.of(vector);
    Tensor r2 = vector.dot(FourierDST._2.matrix(n));
    Tolerance.CHOP.requireClose(r1, r2);
    Tensor r3 = FourierDST._3.of(r1);
    Tolerance.CHOP.requireClose(r3, vector);
    Tensor r4 = r1.dot(FourierDST._3.matrix(n));
    Tolerance.CHOP.requireClose(r3, r4);
  }

  @RepeatedTest(6)
  void testDCT_vectorDCTUnit(RepetitionInfo repetitionInfo) {
    int n = 1 << repetitionInfo.getCurrentRepetition();
    Tensor vector = RandomVariate.of(ComplexNormalDistribution.STANDARD, n).map(s -> Quantity.of(s, "m"));
    Tensor r1 = FourierDCT._2.of(vector);
    Tensor r2 = vector.dot(FourierDCT._2.matrix(n));
    Tolerance.CHOP.requireClose(r1, r2);
    Tensor r3 = FourierDCT._3.of(r1);
    Tolerance.CHOP.requireClose(r3, vector);
    Tensor r4 = r1.dot(FourierDCT._3.matrix(n));
    Tolerance.CHOP.requireClose(r3, r4);
  }

  @RepeatedTest(6)
  void testDST_vectorDSTUnit(RepetitionInfo repetitionInfo) {
    int n = 1 << repetitionInfo.getCurrentRepetition();
    Tensor vector = RandomVariate.of(ComplexNormalDistribution.STANDARD, n).map(s -> Quantity.of(s, "m"));
    Tensor r1 = FourierDST._2.of(vector);
    Tensor r2 = vector.dot(FourierDST._2.matrix(n));
    Tolerance.CHOP.requireClose(r1, r2);
    Tensor r3 = FourierDST._3.of(r1);
    Tolerance.CHOP.requireClose(r3, vector);
    Tensor r4 = r1.dot(FourierDST._3.matrix(n));
    Tolerance.CHOP.requireClose(r3, r4);
  }
}
