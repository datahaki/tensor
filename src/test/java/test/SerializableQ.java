// code by jph
package test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import java.io.Serializable;

import ch.alpine.tensor.ext.Serialization;

public enum SerializableQ {
  ;
  public static <T> T require(T object) {
    assertInstanceOf(Serializable.class, object);
    assertDoesNotThrow(() -> Serialization.copy(object));
    return object;
  }
}
