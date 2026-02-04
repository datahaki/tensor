// code by jph
package ch.alpine.tensor.mat.ex;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import ch.alpine.tensor.ext.Serialization;

class MatrixGroupsTest {
  @ParameterizedTest
  @EnumSource(MatrixGroups.class)
  void testToString(MatrixGroups matrixGroups) throws ClassNotFoundException, IOException {
    MatrixGroups copy = Serialization.copy(matrixGroups);
    String string = copy.toString();
    assertTrue(string.startsWith("MatrixGroups["));
  }
}
