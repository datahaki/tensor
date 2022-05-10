// code by jph
package ch.alpine.tensor.mat.pd;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.sca.pow.Sqrt;

class PolarDecompositionBaseTest {
  @Test
  public void testStrang() throws ClassNotFoundException, IOException {
    Tensor matrix = Tensors.fromString("{{3, 0}, {4, 5}}");
    PolarDecomposition polarDecomposition = Serialization.copy(PolarDecomposition.pu(matrix));
    Tensor s = polarDecomposition.getPositiveSemidefinite().multiply(Sqrt.FUNCTION.apply(RealScalar.of(5)));
    Tensor expect = Tensors.fromString("{{6, 3}, {3, 14}}");
    Tolerance.CHOP.requireClose(s, expect);
  }
}
