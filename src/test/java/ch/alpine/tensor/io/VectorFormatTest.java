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

public class VectorFormatTest {
  @Test
  public void testVector() throws IOException {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream(128);
    Tensor tensor = Tensors.fromString("{2, 3, 4.125,\"abc\", 4/3[m*s^-1],xyz\",3+I/7,ethz}");
    ExportHelper.of(Extension.VECTOR, tensor, outputStream);
    byte[] array = outputStream.toByteArray(); // 44 bytes used
    InputStream inputStream = new ByteArrayInputStream(array);
    Tensor result = ImportHelper.of(new Filename("some.vEcToR"), inputStream);
    assertEquals(tensor, result);
  }

  @Test
  public void testMatrix() throws IOException {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream(128);
    Tensor tensor = IdentityMatrix.of(3);
    ExportHelper.of(Extension.VECTOR, tensor, outputStream);
    byte[] array = outputStream.toByteArray();
    assertEquals(new String(array), "{1, 0, 0}\n{0, 1, 0}\n{0, 0, 1}\n");
  }

  @Test
  public void testStrings() {
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
  public void testScalarFail() {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream(128);
    Tensor tensor = RealScalar.ONE;
    assertThrows(Exception.class, () -> ExportHelper.of(Extension.VECTOR, tensor, outputStream));
  }
}
