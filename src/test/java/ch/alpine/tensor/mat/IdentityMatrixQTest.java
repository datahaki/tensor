// code by jph
package ch.alpine.tensor.mat;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import ch.alpine.tensor.mat.pi.LinearSubspace;

class IdentityMatrixQTest {
  @ParameterizedTest
  @ValueSource(ints = { 2, 3, 4 })
  void testSimple(int n) {
    IdentityMatrixQ.INSTANCE.requireMember(IdentityMatrix.of(n));
    assertThrows(Exception.class, () -> LinearSubspace.of(IdentityMatrixQ.INSTANCE::defect, n, n));
    // assertEquals(.dimensions(), 3);
    assertFalse(IdentityMatrixQ.INSTANCE.isMember(HilbertMatrix.of(n)));
  }

  @Test
  void testOne() {
    IdentityMatrixQ.INSTANCE.requireMember(IdentityMatrix.of(1));
    LinearSubspace linearSubspace = LinearSubspace.of(IdentityMatrixQ.INSTANCE::defect, 1, 1);
    // FIXME this is wrong
    IO.println(linearSubspace.basis());
  }
}
