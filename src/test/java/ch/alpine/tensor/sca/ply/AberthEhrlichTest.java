// code by jph
package ch.alpine.tensor.sca.ply;

import java.util.Random;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Sort;
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
    Tolerance.CHOP.requireAllZero(result.maps(polynomial));
  }

  @Test
  void testSimple() {
    Polynomial polynomial = Polynomial.of(Tensors.vector(4, -5, 0, 1));
    Tensor result = AberthEhrlich.of(polynomial);
    Tolerance.CHOP.requireAllZero(result.maps(polynomial));
  }

  @Test
  void testQuantity() {
    Polynomial polynomial = Polynomial.of(Tensors.fromString("{-13[bar], 0.27[K^-1*bar]}")).antiderivative();
    Tensor v1 = polynomial.roots();
    Tensor v2 = Sort.of(AberthEhrlich.of(polynomial).maps(Tolerance.CHOP));
    Tolerance.CHOP.requireClose(v1, v2);
  }

  @Test
  void testQuantity1() {
    Tensor coeffs = Tensors.fromString("{3[m], 2[m*s^-1], 3[m*s^-2], -4[m*s^-3]}");
    Polynomial polynomial = Polynomial.of(coeffs);
    Tensor roots = AberthEhrlich.of(polynomial);
    Tolerance.CHOP.requireAllZero(roots.maps(polynomial));
  }

  @RepeatedTest(8)
  void testComplex(RepetitionInfo repetitionInfo) {
    int degree = 2 + repetitionInfo.getCurrentRepetition();
    Random random = new Random(degree);
    Distribution distribution = ComplexNormalDistribution.STANDARD;
    Tensor zeros1 = RandomVariate.of(distribution, random, degree);
    Polynomial polynomial = Polynomial.fromRoots(zeros1);
    Tensor zeros = AberthEhrlich.of(polynomial, random);
    Tolerance.CHOP.requireAllZero(zeros.maps(polynomial));
  }

  @RepeatedTest(6)
  void testComplexDisk(RepetitionInfo repetitionInfo) {
    int degree = 2 + repetitionInfo.getCurrentRepetition();
    Random random = new Random(degree);
    Distribution distribution = ComplexDiskUniformDistribution.of(3);
    Tensor zeros1 = RandomVariate.of(distribution, random, degree);
    Polynomial polynomial = Polynomial.fromRoots(zeros1);
    Tensor zeros = AberthEhrlich.of(polynomial, random);
    Tolerance.CHOP.requireAllZero(zeros.maps(polynomial));
  }

  @Test
  void testMultiplicity2() {
    Polynomial polynomial = Polynomial.fromRoots(Tensors.fromString("{3,3,1+I}"));
    Tensor zeros = AberthEhrlich.of(polynomial);
    Tolerance.CHOP.requireAllZero(zeros.maps(polynomial));
  }

  @Test
  void testMultiplicity3() {
    Polynomial polynomial = Polynomial.fromRoots(Tensors.fromString("{3,3,3,1+I,-2-I}"));
    Tensor zeros = AberthEhrlich.of(polynomial);
    Tolerance.CHOP.requireAllZero(zeros.maps(polynomial));
  }
}
