// code by jph
package ch.alpine.tensor.mat.ex;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ext.Serialization;

class MatrixProductTest {
  @Test
  public void testToString() throws ClassNotFoundException, IOException {
    MatrixProduct matrixProduct = Serialization.copy(MatrixProduct.INSTANCE);
    String string = matrixProduct.toString();
    assertTrue(string.startsWith("MatrixProduct"));
  }
}
