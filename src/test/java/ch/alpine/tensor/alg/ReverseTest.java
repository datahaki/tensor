// code by jph
package ch.alpine.tensor.alg;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.function.UnaryOperator;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.d.DiscreteUniformDistribution;

class ReverseTest {
  @Test
  void testRev() {
    Tensor tensor = Tensors.vector(3, 2, 6, 5);
    Tensor rev = Reverse.of(tensor);
    Tensor res = Tensors.vector(5, 6, 2, 3);
    assertEquals(rev, res);
  }

  @Test
  void testReverse() {
    Distribution distribution = DiscreteUniformDistribution.of(0, 100);
    int n = 5;
    Tensor m = RandomVariate.of(distribution, n, n, n, n);
    Tensor v = Reverse.of(IdentityMatrix.of(n));
    Tensor t1 = BasisTransform.ofForm(m, v);
    Tensor t2 = nestRank(m, Reverse::of);
    Tensor t3 = Reverse.all(m);
    assertEquals(t1, t2);
    assertEquals(t1, t3);
  }

  @Test
  void testEmpty() {
    assertEquals(Tensors.empty(), Reverse.of(Tensors.empty()));
  }

  @ParameterizedTest
  @ValueSource(ints = { 1, 2, 3, 4, 5 })
  void testIdentity(int n) {
    Tensor eye = IdentityMatrix.of(n);
    Tensor rev = Reverse.of(eye);
    assertEquals(eye, rev.dot(rev));
  }

  @Test
  void testFail() {
    assertThrows(Throw.class, () -> Reverse.of(RealScalar.ONE));
  }

  private static Tensor nestRank(Tensor tensor, UnaryOperator<Tensor> operator) {
    int rank = TensorRank.of(tensor);
    int[] sigma = IntStream.range(1, rank + 1) //
        .map(index -> index % rank) //
        .toArray();
    for (int index = 0; index < sigma.length; ++index)
      tensor = Transpose.of(operator.apply(tensor), sigma); // [1, 2, ..., r, 0]
    return tensor;
  }
}
