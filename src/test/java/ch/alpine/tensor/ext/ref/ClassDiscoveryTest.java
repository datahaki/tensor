// code by jph
package ch.alpine.tensor.ext.ref;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

class ClassDiscoveryTest {
  @Test
  void testSimple() {
    AtomicInteger count = new AtomicInteger();
    ClassVisitor classVisitor = new ClassVisitor() {
      @Override
      public void accept(String jarfile, Class<?> cls) {
        count.getAndIncrement();
      }
    };
    ClassDiscovery.execute(ClassPaths.getDefault(), classVisitor);
    assertTrue(1000 < count.intValue());
  }
}
