// code by jph
package ch.ethz.idsc.tensor.alg;

import java.util.function.UnaryOperator;
import java.util.stream.IntStream;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;
import ch.ethz.idsc.tensor.pdf.DiscreteUniformDistribution;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class ReverseTest extends TestCase {
  public void testRev() {
    Tensor tensor = Tensors.vector(3, 2, 6, 5);
    Tensor rev = Reverse.of(tensor);
    Tensor res = Tensors.vector(5, 6, 2, 3);
    assertEquals(rev, res);
  }

  public void testReverse() {
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

  public void testEmpty() {
    assertEquals(Tensors.empty(), Reverse.of(Tensors.empty()));
  }

  public void testFail() {
    AssertFail.of(() -> Reverse.of(RealScalar.ONE));
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
