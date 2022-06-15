// code by jph
package ch.alpine.tensor.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.VectorQ;
import ch.alpine.tensor.mat.IdentityMatrix;

class VectorFormatTest {
  @Test
  void testVector() throws IOException {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream(128);
    Tensor tensor = Tensors.fromString("{2, 3, 4.125,\"abc\", 4/3[m*s^-1],xyz\",3+I/7,ethz}");
    ExportHelper.of(Extension.VECTOR, tensor, outputStream);
    byte[] array = outputStream.toByteArray(); // 44 bytes used
    InputStream inputStream = new ByteArrayInputStream(array);
    Tensor result = ImportHelper.of(new Filename("some.vEcToR"), inputStream);
    assertEquals(tensor, result);
  }

  @Test
  void testMatrix() throws IOException {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream(64);
    Tensor tensor = IdentityMatrix.of(3);
    ExportHelper.of(Extension.VECTOR, tensor, outputStream);
    byte[] array = outputStream.toByteArray();
    String expect = OperatingSystem.isWindows() //
        ? "{1, 0, 0}\r\n{0, 1, 0}\r\n{0, 0, 1}\r\n"
        : "{1, 0, 0}\n{0, 1, 0}\n{0, 0, 1}\n";
    assertEquals(new String(array), expect);
  }

  @Test
  void testStrings() {
    Tensor tensor = VectorFormat.parse(Stream.of("ethz", "idsc", "tensor library"));
    VectorQ.requireLength(tensor, 3);
    assertEquals(tensor.Get(0), StringScalar.of("ethz"));
    assertEquals(tensor.Get(1), StringScalar.of("idsc"));
    assertEquals(tensor.Get(2), StringScalar.of("tensor library"));
    assertEquals(tensor.Get(0).toString(), "ethz");
    assertEquals(tensor.Get(1).toString(), "idsc");
    assertEquals(tensor.Get(2).toString(), "tensor library");
  }

  @Test
  void testScalarFail() {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream(128);
    Tensor tensor = RealScalar.ONE;
    assertThrows(Exception.class, () -> ExportHelper.of(Extension.VECTOR, tensor, outputStream));
  }
}
