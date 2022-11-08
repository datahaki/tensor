// code by jph
package ch.alpine.tensor.mat.re;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import java.util.Random;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.mat.pi.LeastSquares;
import ch.alpine.tensor.num.RandomPermutation;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.UniformDistribution;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Clips;
import ch.alpine.tensor.spa.SparseArray;

class KaczmarzIterationTest {
  @Test
  void test2x2() {
    Tensor matrix = Tensors.fromString("{{3,2},{2,4}}");
    Tensor b = Tensors.vector(4, 7);
    Tensor sol = LinearSolve.of(matrix, b);
    sol.copy();
    // System.out.println(sol.map(N.DOUBLE));
    KaczmarzIteration kaczmarzIteration = new KaczmarzIteration(matrix, b);
    for (int i = 0; i < 10; ++i) {
      Tensor x = kaczmarzIteration.refine();
      x.copy();
      // System.out.println("x1=" + x.map(N.DOUBLE));
      // System.out.println("err="+Vector2Norm.between(x, sol));
    }
  }

  @Test
  void test2x5() {
    Distribution distribution = UniformDistribution.of(-2, 2);
    int n = 3;
    Tensor matrix = RandomVariate.of(distribution, n, 5);
    Tensor b = RandomVariate.of(distribution, n);
    Tensor sol = LeastSquares.of(matrix, b);
    sol.copy();
    // System.out.println(sol.map(N.DOUBLE));
    KaczmarzIteration kaczmarzIteration = new KaczmarzIteration(matrix, b);
    // Tensor x = null;
    for (int i = 0; i < 30; ++i) {
      kaczmarzIteration.refine();
      // System.out.println("x1=" + x.map(N.DOUBLE));
      // System.out.println("err="+Vector2Norm.between(x, sol));
    }
    Tensor x = kaczmarzIteration.refine();
    x.copy();
    // System.out.println("err="+Vector2Norm.between(x, sol));
  }

  @Test
  void testSimple() {
    Random random = new Random(10);
    int n = 3;
    Distribution distribution = UniformDistribution.of(-1, 1);
    Tensor matrix = RandomVariate.of(distribution, random, n, n);
    Tensor b = RandomVariate.of(distribution, random, n);
    KaczmarzIteration kaczmarzIteration = new KaczmarzIteration(matrix, b);
    for (int count = 0; count < 30; ++count)
      kaczmarzIteration.refine(random);
    Tensor actual = kaczmarzIteration.refine(random);
    Tensor expect = LinearSolve.of(matrix, b);
    Chop._03.requireClose(actual, expect);
  }

  @Test
  void testQuantity() {
    Random random = new Random(10);
    int n = 3;
    Distribution distribution = UniformDistribution.of(Clips.absolute(Quantity.of(3, "m")));
    Tensor matrix = RandomVariate.of(distribution, random, n, n);
    Tensor b = RandomVariate.of(distribution, random, n);
    KaczmarzIteration kaczmarzIteration = new KaczmarzIteration(matrix, b);
    for (int count = 0; count < 30; ++count)
      kaczmarzIteration.refine(random);
    Tensor actual = kaczmarzIteration.refine(random);
    Tensor expect = LinearSolve.of(matrix, b);
    Chop._03.requireClose(actual, expect);
  }

  @Test
  void testQuantity2() {
    Random random = new Random(10);
    int n = 3;
    Distribution dA = UniformDistribution.of(Clips.absolute(Quantity.of(3, "m")));
    Tensor matrix = RandomVariate.of(dA, random, n, n);
    Distribution db = UniformDistribution.of(Clips.absolute(Quantity.of(3, "")));
    Tensor b = RandomVariate.of(db, random, n);
    KaczmarzIteration kaczmarzIteration = new KaczmarzIteration(matrix, b);
    for (int count = 0; count < 30; ++count)
      kaczmarzIteration.refine(random);
    Tensor actual = kaczmarzIteration.refine(random);
    Tensor expect = LinearSolve.of(matrix, b);
    Chop._03.requireClose(actual, expect);
  }

  @Test
  void testSparse() {
    Random random = new Random(10);
    int n = 100;
    Distribution distribution = UniformDistribution.of(-1, 1);
    int[] sigma = RandomPermutation.of(n, random);
    Tensor matrix = SparseArray.of(RealScalar.ZERO, n, n);
    IntStream.range(0, n).forEach(i -> matrix.set(RealScalar.ONE, i, sigma[i]));
    assertInstanceOf(SparseArray.class, matrix);
    Tensor b = RandomVariate.of(distribution, random, n);
    KaczmarzIteration kaczmarzIteration = new KaczmarzIteration(matrix, b);
    for (int i = 0; i < n; ++i)
      kaczmarzIteration.refine(i);
    Tensor actual = kaczmarzIteration.refine(random);
    Tensor expect = LinearSolve.of(matrix, b);
    Tolerance.CHOP.requireClose(actual, expect);
  }
}
