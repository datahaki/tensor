// code by jph
package ch.ethz.idsc.tensor.lie;

import junit.framework.TestCase;

public class MatrixProductTest extends TestCase {
  public void testToString() {
    MatrixProduct matrixProduct = new MatrixProduct(3);
    String string = matrixProduct.toString();
    assertEquals(string, "MatrixProduct[3]");
  }
}
