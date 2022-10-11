// code by jph
package ch.alpine.tensor.tmp;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ext.Serialization;

class BaseResamplingMethodTest {
  @Test
  void testBasic() throws ClassNotFoundException, IOException {
    for (ResamplingMethod resamplingMethod : TestHelper.list())
      Serialization.copy(resamplingMethod);
  }
}
