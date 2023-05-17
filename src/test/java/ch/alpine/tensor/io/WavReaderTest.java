package ch.alpine.tensor.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.ext.HomeDirectory;

class WavReaderTest {
  @Test
  void test() throws IOException {
    File file = HomeDirectory.file("USERAUDIO001.WAV");
    if (file.isFile()) {
      InputStream inputStream = new FileInputStream(file);
      Tensor tensor = WavReader.read(inputStream);
    }
  }
}
