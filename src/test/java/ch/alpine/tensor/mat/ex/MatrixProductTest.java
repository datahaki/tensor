// code by jph
package ch.alpine.tensor.mat.ex;

import java.io.IOException;

import ch.alpine.tensor.ext.Serialization;
import junit.framework.TestCase;

public class MatrixProductTest extends TestCase {
  public void testToString() throws ClassNotFoundException, IOException {
    MatrixProduct matrixProduct = Serialization.copy(MatrixProduct.INSTANCE);
    String string = matrixProduct.toString();
    assertTrue(string.startsWith("MatrixProduct"));
  }
}
