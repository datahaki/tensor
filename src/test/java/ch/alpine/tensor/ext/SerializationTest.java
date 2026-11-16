// code by jph
package ch.alpine.tensor.ext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

class NonSerializable {
  int value;
}

class SerializationTest {
  @Test
  void testCopy() {
    String s1 = "abc";
    String s2 = Serialization.copy(s1);
    assertEquals(s1, s2);
    assertNotSame(s1, s2);
  }

  @Test
  void testCopy2() {
    Tensor t1 = Tensors.vector(2, 3, 4, 5);
    Tensor t2 = Serialization.copy(t1);
    assertEquals(t1, t2);
    assertNotSame(t1, t2);
  }

  @Test
  void testParseNull() throws ClassNotFoundException, IOException {
    Object object = Serialization.parse(Serialization.of(null));
    assertNull(object);
  }

  @Test
  void testCopyNull() {
    Serialization.copy(null);
  }

  @Test
  void testOfFail() {
    NonSerializable nonSerializable = new NonSerializable();
    assertThrows(Exception.class, () -> Serialization.of(nonSerializable));
  }

  @Test
  void testParseFail() {
    assertThrows(Exception.class, () -> Serialization.parse(null));
  }

  @Test
  void testParseFail2() {
    byte[] bytes = new byte[100];
    ThreadLocalRandom.current().nextBytes(bytes);
    assertThrows(Exception.class, () -> Serialization.parse(bytes));
  }
}
