// code by jph
package ch.alpine.tensor.fft;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Set;

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
import ch.alpine.tensor.io.Import;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.mat.SymmetricMatrixQ;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.mat.re.Inverse;
import ch.alpine.tensor.pdf.ComplexNormalDistribution;
import ch.alpine.tensor.pdf.ComplexUniformDistribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.qty.Quantity;

class FourierDSTTest {
  private static Tensor _consistent1(Tensor vector) {
    Tensor r1 = FourierDST._1.transform(vector);
    Tensor matrix = FourierDST._1.matrix(vector.length());
    SymmetricMatrixQ.require(matrix);
    Tensor r2 = matrix.dot(vector);
    Tolerance.CHOP.requireClose(r1, r2);
    Tolerance.CHOP.requireClose(vector, FourierDST._1.transform(r1));
    Tolerance.CHOP.requireClose(matrix, Inverse.of(matrix));
    return r1;
  }

  @Test
  void test1() {
    Tensor vector = Tensors.vector(1, 2, 3);
    _consistent1(vector);
  }

  @Test
  void test1M3() {
    Tensor vector = Tensors.vector(2, 3, 6);
    Tensor r1 = _consistent1(vector);
    Tensor r3 = Tensors.vector(6.121320343559642, -2.82842712474619, 1.8786796564403576); // Mathematica
    Tolerance.CHOP.requireClose(r1, r3);
  }

  @Test
  void test1M4() {
    Tensor vector = Tensors.vector(2, 3, 6, 1);
    Tensor r1 = _consistent1(vector);
    Tensor r3 = Tensors.vector( //
        6.528752698448464, -0.513743148373008, //
        -1.5412294451190243, 2.1762508994828216); // Mathematica
    Tolerance.CHOP.requireClose(r1, r3);
  }

  @Test
  void test1Complex() {
    Tensor vector = Tensors.of(ComplexScalar.of(1, 2), ComplexScalar.of(0, 0.3), RealScalar.ONE);
    _consistent1(vector);
  }

  @Test
  void test1Complex3() {
    Tensor vector = Tensors.fromString("{2 + 7*I, -1+3*I, 2 + I}");
    Tensor r1 = _consistent1(vector);
    String string = "{1.2928932188134525+6.121320343559643*I, 4.242640687119285*I, 2.707106781186548+1.878679656440358*I}";
    Tolerance.CHOP.requireClose(Tensors.fromString(string), r1); // confirmed with Mathematica
  }

  @Test
  void test1Complex4() {
    Tensor vector = Tensors.fromString("{2 + 2*I, -1+3*I, 2 + I, 1-2*I}");
    Tensor r1 = _consistent1(vector);
    String string = "{1.7167450583880997+2.4060038200301825*I, -0.5137431483730079+3.149499888950552*I, 1.4327548305624522-1.4869921378407378*I, 2.176250899482822+0.283990227825646*I}";
    Tolerance.CHOP.requireClose(Tensors.fromString(string), r1); // confirmed with Mathematica
  }

  @RepeatedTest(8)
  void test1(RepetitionInfo repetitionInfo) {
    int n = repetitionInfo.getCurrentRepetition();
    Tensor matrix = FourierDST._1.matrix(n);
    Tolerance.CHOP.requireClose(matrix, Inverse.of(matrix));
    SymmetricMatrixQ.require(matrix);
  }

  @RepeatedTest(6)
  void test_vectorDSTUnit(RepetitionInfo repetitionInfo) {
    int n = repetitionInfo.getCurrentRepetition();
    Tensor vector = RandomVariate.of(ComplexNormalDistribution.STANDARD, n).map(s -> Quantity.of(s, "m"));
    Tensor r1 = FourierDST._1.transform(vector);
    Tensor r2 = vector.dot(FourierDST._1.matrix(n));
    Tolerance.CHOP.requireClose(r1, r2);
  }

  // ---
  @RepeatedTest(8)
  void test23(RepetitionInfo repetitionInfo) {
    int n = repetitionInfo.getCurrentRepetition();
    Tensor matrix2 = FourierDST._2.matrix(n);
    Tensor matrix3 = FourierDST._3.matrix(n);
    Tensor result = matrix2.dot(matrix3);
    Tolerance.CHOP.requireClose(result, IdentityMatrix.of(n));
  }

  @RepeatedTest(8)
  void test4(RepetitionInfo repetitionInfo) {
    int n = repetitionInfo.getCurrentRepetition();
    Tensor matrix = FourierDST._4.matrix(n);
    Tolerance.CHOP.requireClose(matrix, Inverse.of(matrix));
  }

  @Test
  void testSpecific() {
    Scalar scalar = FourierDST._2.matrix(7).Get(5, 6);
    Tolerance.CHOP.requireClose(scalar, RealScalar.of(-0.3779644730092272));
  }

  @ParameterizedTest
  @EnumSource
  void testDotR(FourierDST fourierDST) {
    if (Set.of(0, 3).contains(fourierDST.ordinal())) {
      Tensor vector = RandomVariate.of(ComplexUniformDistribution.unit(), 4);
      Tensor r1 = fourierDST.transform(vector);
      Tensor matrix = fourierDST.matrix(vector.length());
      Tensor r2 = matrix.dot(vector);
      Tolerance.CHOP.requireClose(r1, r2);
    }
  }

  @ParameterizedTest
  @EnumSource
  void testDotL(FourierDST fourierDST) {
    if (Set.of(0, 1, 2, 3).contains(fourierDST.ordinal())) {
      Tensor vector = RandomVariate.of(ComplexUniformDistribution.unit(), 4);
      Tensor r1 = fourierDST.transform(vector);
      Tensor matrix = fourierDST.matrix(vector.length());
      Tensor r2 = vector.dot(matrix);
      Tolerance.CHOP.requireClose(r1, r2);
    }
  }

  @ParameterizedTest
  @EnumSource
  void testFromResource(FourierDST fourierDST) {
    Tensor expect = Import.of("/ch/alpine/tensor/fft/dstmatrix" + fourierDST + ".csv");
    Tensor actual = fourierDST.matrix(5);
    Tolerance.CHOP.requireClose(expect, actual);
  }

  @ParameterizedTest
  @EnumSource
  void testFail(FourierDST fourierDST) {
    assertThrows(Exception.class, () -> fourierDST.matrix(0));
  }
}
