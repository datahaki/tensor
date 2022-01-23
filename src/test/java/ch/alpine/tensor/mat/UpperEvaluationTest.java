// code by jph
package ch.alpine.tensor.mat;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Range;
import junit.framework.TestCase;

public class UpperEvaluationTest extends TestCase {
  public void testSimple() {
    Tensor result = UpperEvaluation.of(Range.of(1, 4), Range.of(3, 6), (p, q) -> (Scalar) p.add(q), Scalar::one);
    assertEquals(result, Tensors.fromString("{{4, 5, 6}, {1, 6, 7}, {1, 1, 8}}"));
  }
}
