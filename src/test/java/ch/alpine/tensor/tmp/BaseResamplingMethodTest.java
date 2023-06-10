// code by jph
package ch.alpine.tensor.tmp;

import java.io.IOException;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import ch.alpine.tensor.ext.Serialization;

class BaseResamplingMethodTest {
  @ParameterizedTest
  @EnumSource
  void testBasic(ResamplingMethods resamplingMethods) throws ClassNotFoundException, IOException {
    ResamplingMethod resamplingMethod = resamplingMethods.get();
    Serialization.copy(resamplingMethod);
  }
}
