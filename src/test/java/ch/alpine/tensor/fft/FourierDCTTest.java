// code by jph
package ch.alpine.tensor.fft;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Set;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.io.Import;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.mat.re.Inverse;
import ch.alpine.tensor.pdf.ComplexNormalDistribution;
import ch.alpine.tensor.pdf.ComplexUniformDistribution;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.pdf.c.UniformDistribution;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Clips;
import ch.alpine.tensor.sca.Im;
import test.wrap.DFTConsistency;

class FourierDCTTest {
  @Test
  void test1Simple() {
    Tensor vector = Tensors.vector(2, 1, 3, 5, 9);
    Tensor r2 = FourierDCT._1.transform(vector);
    Tensor r3 = Tensors.vector( //
        10.25304832720494, -4.474873734152917, //
        1.7677669529663682, -0.4748737341529168, 1.7677669529663693);
    Tolerance.CHOP.requireClose(r2, r3);
  }

  @Test
  void test1Complex4() {
    Tensor vector = Tensors.fromString("{1 - I, 3 + 2*I, 5 - 4*I, 2 + 9*I}");
    Tensor r2 = FourierDCT._1.transform(vector);
    Tensor r3 = Tensors.of( // result of Mathematica
        ComplexScalar.of(7.756717518813398, 1.632993161855452), //
        ComplexScalar.of(-1.224744871391589, -1.6329931618554523), //
        ComplexScalar.of(-2.041241452319315, 4.08248290463863), //
        ComplexScalar.of(1.224744871391589, -8.981462390204987));
    // IO.println(r2);
    // IO.println(r3);
    Tolerance.CHOP.requireClose(r2, r3);
  }

  @Test
  void test1Complex5() {
    Tensor vector = Tensors.fromString("{2 + I, 1 - I, 3 + 2*I, 5 - 4*I, 2 + 9*I}");
    Tensor r2 = FourierDCT._1.transform(vector);
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
    Tensor tensor = FourierDCT._2.transform(Tensors.vector(0, 0, 1, 0, 1));
    Tensor vector = Tensors.vector( //
        0.8944271909999159, -0.4253254041760199, -0.08541019662496846, //
        -0.26286555605956674, 0.5854101966249685);
    Tolerance.CHOP.requireClose(tensor, vector);
  }

  @Test
  void testComplex() {
    Tensor tensor = FourierDCT._2.transform(Tensors.fromString("{0.3-2*I, I, 1+2*I, 1}"));
    Tolerance.CHOP.requireClose(tensor.Get(0), ComplexScalar.of(1.15, 0.5));
    Tolerance.CHOP.requireClose(tensor.Get(1), ComplexScalar.of(-0.5146995525614952, -1.1152212486938315));
    Tolerance.CHOP.requireClose(tensor.Get(2), ComplexScalar.of(0.10606601717798216, -1.7677669529663689));
    Tolerance.CHOP.requireClose(tensor.Get(3), ComplexScalar.of(0.32800056492786195, +0.07925633389055353));
  }

  @Test
  void testFirst() {
    Tensor vector = Tensors.vector(0.22204305944782998, 0.779351514647771, 0.298518105256417, 0.9887304754672706);
    Tensor result = FourierDCT._2.transform(vector);
    Tensor expect = Tensors.vector(1.1443215774096442, -0.2621599159963177, 0.04698862977522844, -0.3688153586988667);
    Tolerance.CHOP.requireClose(expect, result);
    Chop.NONE.requireAllZero(result.maps(Im.FUNCTION));
    Tensor raw3 = FourierDCT._3.transform(result);
    Tolerance.CHOP.requireClose(raw3, vector);
    Chop.NONE.requireAllZero(raw3.maps(Im.FUNCTION));
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

  @ParameterizedTest
  @ValueSource(ints = { 1, 2, 3, 4 })
  void testRawRandom(int n) {
    Tensor vector = RandomVariate.of(NormalDistribution.standard(), 1 << n);
    Tensor result = FourierDCT.raw2(vector);
    Tolerance.CHOP.requireAllZero(result.maps(Im.FUNCTION));
    Tensor backto = FourierDCT.raw3(result);
    Tolerance.CHOP.requireClose(vector, backto);
  }

  @ParameterizedTest
  @ValueSource(ints = { 1, 2, 3, 4 })
  void testUnitRandom(int n) {
    Distribution distribution = UniformDistribution.of(Clips.absolute(Quantity.of(3, "m")));
    Tensor vector = RandomVariate.of(distribution, n);
    Tensor result = FourierDCT._2.transform(vector);
    Tolerance.CHOP.requireAllZero(result.maps(Im.FUNCTION));
    Tensor backto = FourierDCT._3.transform(result);
    Tolerance.CHOP.requireClose(vector, backto);
  }

  @ParameterizedTest
  @ValueSource(ints = { 1, 2, 3, 4, 5, 6 })
  void test1Random(int n) {
    Tensor vector = RandomVariate.of(NormalDistribution.standard(), n);
    Tensor result = FourierDCT._1.transform(vector);
    Tensor backto = FourierDCT._1.transform(result);
    Tolerance.CHOP.requireClose(vector, backto);
  }

  @ParameterizedTest
  @ValueSource(ints = { 1, 2, 3, 4, 5, 6 })
  void test4Random(int n) {
    Tensor vector = RandomVariate.of(NormalDistribution.standard(), n);
    Tensor result = FourierDCT._4.transform(vector);
    Tensor backto = FourierDCT._4.transform(result);
    Tolerance.CHOP.requireClose(vector, backto);
  }

  @ParameterizedTest
  @ValueSource(ints = { 1, 2, 3, 4, 5, 6, 7, 8 })
  void test1(int n) {
    Tensor matrix = FourierDCT._1.matrix(n);
    Tolerance.CHOP.requireClose(matrix, Inverse.of(matrix));
  }

  @ParameterizedTest
  @ValueSource(ints = { 1, 2, 3, 4, 5, 6, 7, 8 })
  void test23(int n) {
    Tensor matrix2 = FourierDCT._2.matrix(n);
    Tensor matrix3 = FourierDCT._3.matrix(n);
    Tensor result = matrix2.dot(matrix3);
    Tolerance.CHOP.requireClose(result, IdentityMatrix.of(n));
  }

  @ParameterizedTest
  @ValueSource(ints = { 1, 2, 3, 4, 5, 6, 7, 8 })
  void test4(int n) {
    Tensor matrix = FourierDCT._4.matrix(n);
    Tolerance.CHOP.requireClose(matrix, Inverse.of(matrix));
  }

  @Test
  void testSpecific() {
    Scalar scalar = FourierDCT._2.matrix(7).Get(6, 5);
    Tolerance.CHOP.requireClose(scalar, RealScalar.of(-0.2356569943861637));
  }

  @ParameterizedTest
  @EnumSource
  void testDotR(FourierDCT fourierDCT) {
    if (Set.of(3).contains(fourierDCT.ordinal())) {
      Tensor vector = RandomVariate.of(ComplexUniformDistribution.unit(), 4);
      Tensor r1 = fourierDCT.transform(vector);
      Tensor matrix = fourierDCT.matrix(vector.length());
      Tensor r2 = matrix.dot(vector);
      Tolerance.CHOP.requireClose(r1, r2);
    }
  }

  @ParameterizedTest
  @EnumSource
  void testConsistencyComplex(FourierDCT fourierDCT) {
    DFTConsistency.checkComplex(fourierDCT, true);
  }

  @ParameterizedTest
  @EnumSource
  void testConsistencyReal(FourierDCT fourierDCT) {
    DFTConsistency.checkReal(fourierDCT, true);
  }

  @ParameterizedTest
  @EnumSource
  void testFromResource(FourierDCT fourierDCT) {
    Tensor expect = Transpose.of(Import.of("/ch/alpine/tensor/fft/dctmatrix" + fourierDCT.name() + ".csv"));
    Tensor actual = fourierDCT.matrix(5);
    Tolerance.CHOP.requireClose(expect, actual);
  }

  @RepeatedTest(8)
  void testDCT(RepetitionInfo repetitionInfo) {
    int n = repetitionInfo.getCurrentRepetition();
    for (FourierDCT fourierDCT : FourierDCT.values()) {
      Tensor mat = fourierDCT.matrix(n);
      Tensor inv = fourierDCT.inverse().matrix(n);
      Tolerance.CHOP.requireClose(inv, Inverse.of(mat));
    }
  }

  @RepeatedTest(6)
  void testDCT_vectorDCT(RepetitionInfo repetitionInfo) {
    int n = 1 << repetitionInfo.getCurrentRepetition();
    Tensor vector = RandomVariate.of(ComplexNormalDistribution.STANDARD, n);
    Tensor r1 = FourierDCT._2.transform(vector);
    Tensor r2 = FourierDCT._2.matrix(n).dot(vector);
    Tolerance.CHOP.requireClose(r1, r2);
    Tensor r3 = FourierDCT._3.transform(r1);
    Tolerance.CHOP.requireClose(r3, vector);
    Tensor r4 = FourierDCT._3.matrix(n).dot(r1);
    Tolerance.CHOP.requireClose(r3, r4);
  }

  @RepeatedTest(6)
  void testDCT_vectorDCTUnit(RepetitionInfo repetitionInfo) {
    int n = 1 << repetitionInfo.getCurrentRepetition();
    Tensor vector = RandomVariate.of(ComplexNormalDistribution.STANDARD, n).maps(s -> Quantity.of(s, "m"));
    Tensor r1 = FourierDCT._2.transform(vector);
    Tensor r2 = FourierDCT._2.matrix(n).dot(vector);
    Tolerance.CHOP.requireClose(r1, r2);
    Tensor r3 = FourierDCT._3.transform(r1);
    Tolerance.CHOP.requireClose(r3, vector);
    Tensor r4 = FourierDCT._3.matrix(n).dot(r1);
    Tolerance.CHOP.requireClose(r3, r4);
  }

  @ParameterizedTest
  @EnumSource
  void testFail(FourierDCT fourierDCT) {
    assertThrows(Exception.class, () -> fourierDCT.matrix(0));
  }
}
