// code by jph
package ch.alpine.tensor.lie;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import ch.alpine.tensor.chq.ExactTensorQ;
import ch.alpine.tensor.mat.pi.LinearSubspace;

class RiemannCurvatureQTest {
  @ParameterizedTest
  @CsvSource({ "1,0", "2,1", "3,6" }) // , "4,20"
  void test(int n, int expect) {
    List<Integer> list = Collections.nCopies(4, n);
    LinearSubspace linearSubspace = LinearSubspace.of(RiemannCurvatureQ.INSTANCE::defect, list);
    assertEquals(linearSubspace.dimensions(), expect);
    ExactTensorQ.of(linearSubspace.basis());
  }
}
