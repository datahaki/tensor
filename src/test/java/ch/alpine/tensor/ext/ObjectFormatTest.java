// code by jph
package ch.alpine.tensor.ext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.DataFormatException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.Rational;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.chq.ExactScalarQ;
import ch.alpine.tensor.io.Export;
import ch.alpine.tensor.io.Import;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.qty.Quantity;

class ObjectFormatTest {
  @TempDir
  Path tempDir;

  @Test
  void testSome() throws Exception {
    Tensor inp = Tensors.fromString("{1, {2, 3, {4.3}}, 1}");
    byte[] bytes = ObjectFormat.of(inp);
    Tensor ten = ObjectFormat.parse(bytes);
    assertEquals(inp, ten);
  }

  @Test
  void testNull() throws Exception {
    final Object put = null;
    byte[] bytes = ObjectFormat.of(put);
    Object get = ObjectFormat.parse(bytes);
    assertEquals(put, get);
    assertNull(get);
  }

  @Test
  void testUnderClear() throws ClassNotFoundException, IOException, DataFormatException {
    Scalar q1 = Quantity.of(ComplexScalar.of(Rational.of(2, 7), Rational.HALF.negate()), "m");
    Scalar q2 = Quantity.of(ComplexScalar.of(-1, 7), "m");
    Scalar quc = q1.under(q2);
    Scalar expected = Scalars.fromString("-742/65+294/65*I");
    assertEquals(quc, expected);
    assertEquals(Serialization.copy(quc), expected);
    assertInstanceOf(ComplexScalar.class, quc);
    byte[] bytes = ObjectFormat.of(quc);
    Scalar copy = ObjectFormat.parse(bytes);
    assertEquals(copy, expected);
    Scalar cdq = q2.divide(q1);
    assertInstanceOf(ComplexScalar.class, cdq);
    Scalar qrc = q1.reciprocal().multiply(q2);
    assertInstanceOf(ComplexScalar.class, qrc);
    assertEquals(quc, qrc);
    assertEquals(quc, cdq);
    ExactScalarQ.require(quc);
  }

  @Test
  void testUnderMix() throws ClassNotFoundException, IOException, DataFormatException {
    Scalar q1 = Quantity.of(ComplexScalar.of(Rational.of(2, 7), Rational.HALF.negate()), "CHF");
    Scalar q2 = Quantity.of(ComplexScalar.of(-1, 7), "m");
    Scalar quc = q1.under(q2);
    Scalar expected = Scalars.fromString("-742/65+294/65*I[m*CHF^-1]");
    assertEquals(quc, expected);
    assertEquals(Serialization.copy(quc), expected);
    assertInstanceOf(Quantity.class, quc);
    byte[] bytes = ObjectFormat.of(quc);
    Scalar copy = ObjectFormat.parse(bytes);
    assertEquals(copy, expected);
    Scalar cdq = q2.divide(q1);
    assertInstanceOf(Quantity.class, cdq);
    Scalar qrc = q1.reciprocal().multiply(q2);
    assertInstanceOf(Quantity.class, qrc);
    assertEquals(quc, qrc);
    assertEquals(quc, cdq);
    ExactScalarQ.require(quc);
  }

  @Test
  void testExportImportObject() throws IOException, ClassNotFoundException, DataFormatException {
    Tensor tensor = HilbertMatrix.of(3, 4);
    Path path = tempDir.resolve("file.random");
    Export.object(path, tensor);
    assertTrue(Files.isRegularFile(path));
    assertEquals(Import.object(path), tensor);
  }
}
