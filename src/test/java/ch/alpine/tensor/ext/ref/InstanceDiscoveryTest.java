// code by jph
package ch.alpine.tensor.ext.ref;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.fft.DiscreteFourierTransform;
import ch.alpine.tensor.img.ImageResize;

class InstanceDiscoveryTest implements Consumer<DiscreteFourierTransform> {
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

  @TestFactory
  Collection<DynamicTest> dynamicTests() {
    return InstanceDiscovery.of("ch.alpine", DiscreteFourierTransform.class).stream() //
        .map(instance -> DynamicTest.dynamicTest("=" + instance.toString(), () -> accept(instance))) //
        .toList();
  }

  @Override
  public void accept(DiscreteFourierTransform t) {
    t.toString();
  }
}
