// code by jph
package ch.alpine.tensor.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Modifier;
import java.nio.file.Path;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Unprotect;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.alg.Flatten;
import ch.alpine.tensor.alg.OrderedQ;
import ch.alpine.tensor.red.Mean;
import ch.alpine.tensor.sca.Unitize;

class ImportHelperTest {
  @Test
  void testGif() throws Exception {
    String string = "/ch/alpine/tensor/img/rgba7x3.gif"; // file consist of a single line break character
    Tensor tempor = Import.of(string);
    Path path = Unprotect.path(string);
    Tensor tensor = Import.of(path);
    assertEquals(tensor, tempor);
    assertEquals(Dimensions.of(tensor), Arrays.asList(3, 7, 4));
    assertEquals(tensor.get(0, 0), Tensors.vector(0, 0, 0, 255));
    assertEquals(tensor.get(0, 1, 3), RealScalar.ZERO);
    assertEquals(tensor.get(0, 3), Tensors.vector(255, 255, 255, 255));
    assertEquals(tensor.get(2, 5), Tensors.vector(145, 74, 198, 255));
    Tensor tensor2 = tensor.get(Tensor.ALL, Tensor.ALL, 3);
    Tensor units = tensor2.map(Unitize.FUNCTION);
    assertEquals(Flatten.scalars(units).reduce(Scalar::add).orElseThrow(), RealScalar.of(9));
  }

  @Test
  void testTiff() {
    Tensor tensor = Import.of("/ch/alpine/tensor/img/rgb14x11.tiff");
    assertEquals(Dimensions.of(tensor), Arrays.asList(11, 14, 4));
    Tensor rgba = Flatten.of(tensor, 1);
    assertEquals(Dimensions.of(rgba), Arrays.asList(11 * 14, 4));
    Tensor mean = Mean.of(rgba);
    OrderedQ.require(mean);
  }

  @Test
  void testExtensionMFail() {
    InputStream inputStream = new ByteArrayInputStream(new byte[128]);
    assertThrows(Exception.class, () -> ImportHelper.of(new Filename("some.m"), inputStream));
  }

  @SuppressWarnings("null")
  @Test
  void testSwitch() {
    Extension extension = null;
    try {
      switch (extension) {
      default:
      }
      fail();
    } catch (NullPointerException exception) {
      // ---
    }
  }

  @Test
  void testVisibility() {
    assertFalse(Modifier.isPublic(ImportHelper.class.getModifiers()));
  }
}
