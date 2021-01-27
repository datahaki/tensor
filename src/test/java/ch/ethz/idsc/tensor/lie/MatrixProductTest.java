// code by jph
package ch.ethz.idsc.tensor.lie;

import java.io.IOException;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.ext.Serialization;
import junit.framework.TestCase;

public class MatrixProductTest extends TestCase {
  public void testToString() throws ClassNotFoundException, IOException {
    MatrixProduct matrixProduct = Serialization.copy(new MatrixProduct(3, RealScalar.ONE));
    String string = matrixProduct.toString();
    assertTrue(string.startsWith("MatrixProduct"));
  }
}
