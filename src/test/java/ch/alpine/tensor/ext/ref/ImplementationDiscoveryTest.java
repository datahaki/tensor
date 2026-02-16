// code by jph
package ch.alpine.tensor.ext.ref;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.img.ImageResize;

class ImplementationDiscoveryTest {
  @Test
  void testWinProv() {
    ImplementationDiscovery<ScalarUnaryOperator> classDiscUtils = new ImplementationDiscovery<>(ScalarUnaryOperator.class);
    List<ScalarUnaryOperator> list = classDiscUtils.getInstances("ch.alpine");
    assertTrue(150 <= list.size());
  }

  @Test
  void testDateTimeInterval() {
    ImplementationDiscovery<ImageResize> classDiscUtils = new ImplementationDiscovery<>(ImageResize.class);
    List<ImageResize> list = classDiscUtils.getInstances("ch.alpine");
    assertEquals(list.size(), ImageResize.values().length);
  }
}
