// code by jph
package ch.alpine.tensor.ext.ref;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.img.ImageResize;

class InstanceDiscoveryTest {
  @Test
  void testWinProv() {
    List<ScalarUnaryOperator> list = InstanceDiscovery.of("ch.alpine", ScalarUnaryOperator.class);
    assertTrue(150 <= list.size());
  }

  @Test
  void testDateTimeInterval() {
    List<ImageResize> list = InstanceDiscovery.of("ch.alpine", ImageResize.class);
    assertEquals(list.size(), ImageResize.values().length);
  }
}
