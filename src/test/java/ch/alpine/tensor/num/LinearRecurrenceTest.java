// code by jph
package ch.alpine.tensor.num;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.Serialization;

class LinearRecurrenceTest {
  @Test
  void testPadovan() throws ClassNotFoundException, IOException {
    LinearRecurrence linearRecurrence = Serialization.copy(new LinearRecurrence(Tensors.vector(0, 1, 1), Tensors.vector(1, 1, 1)));
    assertEquals(Tensors.vector(1, 1, 1, 2, 2, 3, 4, 5, 7, 9), linearRecurrence.until(10));
    assertEquals(Tensors.vector(1, 1), linearRecurrence.until(2));
  }

  @Test
  void testPell() {
    LinearRecurrence linearRecurrence = new LinearRecurrence(Tensors.vector(2, 1), Tensors.vector(0, 1));
    assertEquals(Tensors.fromString("{{0, 1}, {1, 2}}"), linearRecurrence.matrix());
    Tensor expect = Tensors.vector(0, 1, 2, 5, 12, 29, 70, 169, 408, 985);
    assertEquals(expect, linearRecurrence.until(10));
    assertEquals(expect, Tensors.vector(linearRecurrence::at, 10));
  }

  @Test
  void testPellLucas() {
    LinearRecurrence linearRecurrence = new LinearRecurrence(Tensors.vector(2, 1), Tensors.vector(2, 2));
    assertEquals(Tensors.vector(2, 2, 6, 14, 34, 82, 198, 478, 1154, 2786), linearRecurrence.until(10));
  }

  @Test
  void testPerrin() {
    LinearRecurrence linearRecurrence = new LinearRecurrence(Tensors.vector(0, 1, 1), Tensors.vector(3, 0, 2));
    Tensor expect = Tensors.vector(3, 0, 2, 3, 2, 5, 5, 7, 10, 12);
    assertEquals(expect, linearRecurrence.until(10));
    assertEquals(expect, Tensors.vector(linearRecurrence::at, 10));
    assertThrows(Exception.class, () -> linearRecurrence.at(-1));
  }

  @Test
  void testLarge() {
    LinearRecurrence linearRecurrence = new LinearRecurrence(Tensors.vector(3, 2, 1, 4), Tensors.vector(1, -36, 9, 80));
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("1622574397278918171666437845244003440460572109718562001906919385780504");
    stringBuilder.append("5184109913520469261114740568360934138889831708784973834793219762647082");
    stringBuilder.append("6776262757718290308714228170590405395226687583684613139784505059153310");
    stringBuilder.append("6546903233147844287759022961462160417119924435002722691333477902889457");
    stringBuilder.append("6485");
    Scalar scalar = linearRecurrence.at(499);
    assertEquals(stringBuilder.toString(), scalar.toString());
  }

  @Test
  void testInputFail() {
    assertThrows(Exception.class, () -> new LinearRecurrence(Tensors.vector(3, 2, 1, 4), Tensors.vector(1, -36, 9)));
    assertThrows(Exception.class, () -> new LinearRecurrence(Tensors.vector(3, 2, 1), Tensors.vector(1, -36, 9, 10)));
    assertThrows(Exception.class, () -> new LinearRecurrence(Tensors.empty(), Tensors.vector()));
  }
}
