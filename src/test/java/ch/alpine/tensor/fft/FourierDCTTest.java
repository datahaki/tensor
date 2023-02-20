// code by jph
package ch.alpine.tensor.fft;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.io.ResourceData;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.mat.re.Inverse;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.pdf.c.UniformDistribution;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Clips;
import ch.alpine.tensor.sca.Im;

class FourierDCTTest {
  @Test
  void test1Simple() {
    Tensor vector = Tensors.vector(2, 1, 3, 5, 9);
    Tensor r2 = FourierDCT._1.of(vector);
    Tensor r3 = Tensors.vector( //
        10.25304832720494, -4.474873734152917, //
        1.7677669529663682, -0.4748737341529168, 1.7677669529663693);
    Tolerance.CHOP.requireClose(r2, r3);
  }

  @Test
  void test1Complex4() {
    Tensor vector = Tensors.fromString("{1 - I, 3 + 2*I, 5 - 4*I, 2 + 9*I}");
    Tensor r2 = FourierDCT._1.of(vector);
    Tensor r3 = Tensors.of( // result of Mathematica
        ComplexScalar.of(7.756717518813398, 1.632993161855452), //
        ComplexScalar.of(-1.224744871391589, -1.6329931618554523), //
        ComplexScalar.of(-2.041241452319315, 4.08248290463863), //
        ComplexScalar.of(1.224744871391589, -8.981462390204987));
    Tolerance.CHOP.requireClose(r2, r3);
  }

  @Test
  void test1Complex5() {
    Tensor vector = Tensors.fromString("{2 + I, 1 - I, 3 + 2*I, 5 - 4*I, 2 + 9*I}");
    Tensor r2 = FourierDCT._1.of(vector);
    Tensor r3 = Tensors.of( // result of Mathematica
        ComplexScalar.of(7.778174593052022, 1.4142135623730954), //
        ComplexScalar.of(-2, -1.3284271247461898), //
        ComplexScalar.of(-0.7071067811865475, 2.121320343559643), //
        ComplexScalar.of(2., -4.32842712474619), //
        ComplexScalar.of(-0.7071067811865475, 8.48528137423857));
    Tolerance.CHOP.requireClose(r2, r3);
  }

  // ---
  @Test
  void test2Simple() {
    Tensor tensor = FourierDCT._2.of(Tensors.vector(0, 0, 1, 0, 1));
    Tensor vector = Tensors.vector( //
        0.8944271909999159, -0.4253254041760199, -0.08541019662496846, //
        -0.26286555605956674, 0.5854101966249685);
    Tolerance.CHOP.requireClose(tensor, vector);
  }

  @Test
  void testComplex() {
    Tensor tensor = FourierDCT._2.of(Tensors.fromString("{0.3-2*I, I, 1+2*I, 1}"));
    Tolerance.CHOP.requireClose(tensor.Get(0), ComplexScalar.of(1.15, 0.5));
    Tolerance.CHOP.requireClose(tensor.Get(1), ComplexScalar.of(-0.5146995525614952, -1.1152212486938315));
    Tolerance.CHOP.requireClose(tensor.Get(2), ComplexScalar.of(0.10606601717798216, -1.7677669529663689));
    Tolerance.CHOP.requireClose(tensor.Get(3), ComplexScalar.of(0.32800056492786195, +0.07925633389055353));
  }

  @Test
  void testFirst() {
    Tensor vector = Tensors.vector(0.22204305944782998, 0.779351514647771, 0.298518105256417, 0.9887304754672706);
    Tensor result = FourierDCT._2.of(vector);
    Tensor expect = Tensors.vector(1.1443215774096442, -0.2621599159963177, 0.04698862977522844, -0.3688153586988667);
    Tolerance.CHOP.requireClose(expect, result);
    Chop.NONE.requireAllZero(Im.of(result));
    Tensor raw3 = FourierDCT._3.of(result);
    Tolerance.CHOP.requireClose(raw3, vector);
    Chop.NONE.requireAllZero(Im.of(raw3));
  }

  @Test
  void testRaw() {
    Tensor vector = Tensors.vector(0.22204305944782998, 0.779351514647771, 0.298518105256417, 0.9887304754672706);
    Tensor result = FourierDCT.raw2(vector);
    Tensor expect = Tensors.vector(1.1443215774096442, -0.2621599159963177, 0.04698862977522844, -0.3688153586988667);
    Tolerance.CHOP.requireClose(expect, result);
    Tensor raw3 = FourierDCT.raw3(result);
    Tolerance.CHOP.requireClose(raw3, vector);
  }

  @RepeatedTest(4)
  void testRawRandom(RepetitionInfo repetitionInfo) {
    Tensor vector = RandomVariate.of(NormalDistribution.standard(), 1 << repetitionInfo.getCurrentRepetition());
    Tensor result = FourierDCT.raw2(vector);
    Tolerance.CHOP.requireAllZero(Im.of(result));
    Tensor backto = FourierDCT.raw3(result);
    Tolerance.CHOP.requireClose(vector, backto);
  }

  @RepeatedTest(8)
  void testUnitRandom(RepetitionInfo repetitionInfo) {
    Distribution distribution = UniformDistribution.of(Clips.absolute(Quantity.of(3, "m")));
    Tensor vector = RandomVariate.of(distribution, repetitionInfo.getCurrentRepetition());
    Tensor result = FourierDCT._2.of(vector);
    Tolerance.CHOP.requireAllZero(Im.of(result));
    Tensor backto = FourierDCT._3.of(result);
    Tolerance.CHOP.requireClose(vector, backto);
  }

  @RepeatedTest(6)
  void test1Random(RepetitionInfo repetitionInfo) {
    Tensor vector = RandomVariate.of(NormalDistribution.standard(), repetitionInfo.getCurrentRepetition());
    Tensor result = FourierDCT._1.of(vector);
    Tensor backto = FourierDCT._1.of(result);
    Tolerance.CHOP.requireClose(vector, backto);
  }

  @RepeatedTest(6)
  void test4Random(RepetitionInfo repetitionInfo) {
    Tensor vector = RandomVariate.of(NormalDistribution.standard(), repetitionInfo.getCurrentRepetition());
    Tensor result = FourierDCT._4.of(vector);
    Tensor backto = FourierDCT._4.of(result);
    Tolerance.CHOP.requireClose(vector, backto);
  }

  @RepeatedTest(8)
  void test1(RepetitionInfo repetitionInfo) {
    int n = repetitionInfo.getCurrentRepetition();
    Tensor matrix = FourierDCT._1.matrix(n);
    Tolerance.CHOP.requireClose(matrix, Inverse.of(matrix));
  }

  @RepeatedTest(8)
  void test23(RepetitionInfo repetitionInfo) {
    int n = repetitionInfo.getCurrentRepetition();
    Tensor matrix2 = FourierDCT._2.matrix(n);
    Tensor matrix3 = FourierDCT._3.matrix(n);
    Tensor result = matrix2.dot(matrix3);
    Tolerance.CHOP.requireClose(result, IdentityMatrix.of(n));
  }

  @RepeatedTest(8)
  void test4(RepetitionInfo repetitionInfo) {
    int n = repetitionInfo.getCurrentRepetition();
    Tensor matrix = FourierDCT._4.matrix(n);
    Tolerance.CHOP.requireClose(matrix, Inverse.of(matrix));
  }

  @Test
  void testSpecific() {
    Scalar scalar = FourierDCT._2.matrix(7).Get(5, 6);
    Tolerance.CHOP.requireClose(scalar, RealScalar.of(-0.2356569943861637));
  }

  @ParameterizedTest
  @EnumSource
  void testFromResource(FourierDCT fourierDCT) {
    Tensor expect = ResourceData.of("/ch/alpine/tensor/fft/dctmatrix" + fourierDCT + ".csv");
    Tensor actual = fourierDCT.matrix(5);
    Tolerance.CHOP.requireClose(expect, actual);
  }

  @ParameterizedTest
  @EnumSource
  void testFail(FourierDCT fourierDCT) {
    assertThrows(Exception.class, () -> fourierDCT.matrix(0));
  }
}
