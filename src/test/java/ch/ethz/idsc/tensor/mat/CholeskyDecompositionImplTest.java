// code by jph
package ch.ethz.idsc.tensor.mat;

import java.io.IOException;
import java.lang.reflect.Modifier;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.ext.Serialization;
import junit.framework.TestCase;

public class CholeskyDecompositionImplTest extends TestCase {
  public void testPackageVisibility() {
    assertTrue(Modifier.isPublic(CholeskyDecomposition.class.getModifiers()));
    assertFalse(Modifier.isPublic(CholeskyDecompositionImpl.class.getModifiers()));
  }

  public void testSolveQuantity() throws ClassNotFoundException, IOException {
    Tensor matrix = Tensors.fromString( //
        "{{60[m^2], 30[m*rad], 20[kg*m]}, {30[m*rad], 20[rad^2], 15[kg*rad]}, {20[kg*m], 15[kg*rad], 12[kg^2]}}");
    CholeskyDecomposition choleskyDecomposition = //
        Serialization.copy(CholeskyDecomposition.of(matrix));
    assertEquals( //
        choleskyDecomposition.solve(IdentityMatrix.of(3)), //
        Inverse.of(matrix));
    assertTrue(choleskyDecomposition.toString().startsWith("CholeskyDecomposition"));
  }
}
