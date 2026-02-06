// code by jph
package ch.alpine.tensor.io;

import java.io.IOException;
import java.nio.file.Path;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ext.HomeDirectory;

class WavReaderTest {
  @Disabled
  @Test
  void test() throws IOException {
    // TODO TENSOR IMPL
    Path file = HomeDirectory.path("USERAUDIO001.WAV");
    // if (file.isFile()) {
    // try (InputStream inputStream = new FileInputStream(file)) {
    // Tensor tensor = WavReader.read(inputStream);
    // tensor.map(Scalar::zero);
    // }
    // }
  }
}
