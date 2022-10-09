// code by jph
package ch.alpine.tensor.sca.ply;

import java.util.Random;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.pdf.ComplexNormalDistribution;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.ComplexDiskUniformDistribution;

class AberthEhrlichTest {
  @Test
  void testDegree1() {
    Polynomial polynomial = Polynomial.of(Tensors.vector(4, -5, 1));
    Tensor result = AberthEhrlich.of(polynomial);
    Tolerance.CHOP.requireAllZero(result.map(polynomial));
  }

  @Test
  void testSimple() {
    Polynomial polynomial = Polynomial.of(Tensors.vector(4, -5, 0, 1));
    Tensor result = AberthEhrlich.of(polynomial);
    Tolerance.CHOP.requireAllZero(result.map(polynomial));
  }

  @Test
  @Disabled
  void testQuantity() {
    Random random = new Random(2);
    Polynomial polynomial = Polynomial.of(Tensors.fromString("{-13[bar], 0.27[K^-1*bar]}")).antiderivative();
    Tolerance.CHOP.requireClose( //
        polynomial.roots(), //
        AberthEhrlich.of(polynomial, random).map(Tolerance.CHOP));
  }

  @RepeatedTest(8)
  void testComplex(RepetitionInfo repetitionInfo) {
    int degree = 2 + repetitionInfo.getCurrentRepetition();
    Random random = new Random(degree);
    Distribution distribution = ComplexNormalDistribution.STANDARD;
    Polynomial polynomial = RandomVariate.of(distribution, random, degree).stream() //
        .map(Scalar.class::cast) //
        .map(zero -> Tensors.of(zero.negate(), zero.one())) //
        .map(Polynomial::of) //
        .reduce(Polynomial::times) //
        .orElseThrow();
    Tensor zeros = AberthEhrlich.of(polynomial, random);
    Tolerance.CHOP.requireAllZero(zeros.map(polynomial));
  }

  @RepeatedTest(6)
  void testComplexDisk(RepetitionInfo repetitionInfo) {
    int degree = 2 + repetitionInfo.getCurrentRepetition();
    Random random = new Random(degree);
    Distribution distribution = ComplexDiskUniformDistribution.of(3);
    Polynomial polynomial = RandomVariate.of(distribution, random, degree).stream() //
        .map(Scalar.class::cast) //
        .map(zero -> Tensors.of(zero.negate(), zero.one())) //
        .map(Polynomial::of) //
        .reduce(Polynomial::times) //
        .orElseThrow();
    Tensor zeros = AberthEhrlich.of(polynomial, random);
    Tolerance.CHOP.requireAllZero(zeros.map(polynomial));
  }
}
