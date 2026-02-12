// code by jph
package ch.alpine.tensor.mat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import ch.alpine.tensor.mat.pi.LinearSubspace;

class IdentityMatrixQTest {
  @ParameterizedTest
  @ValueSource(ints = { 2, 3, 4 })
  void testSimple(int n) {
    IdentityMatrixQ.INSTANCE.require(IdentityMatrix.of(n));
    LinearSubspace linearSubspace = LinearSubspace.of(IdentityMatrixQ.INSTANCE::defect, n, n);
    assertEquals(linearSubspace.dimensions(), 0);
    assertFalse(IdentityMatrixQ.INSTANCE.test(HilbertMatrix.of(n)));
  }
}
