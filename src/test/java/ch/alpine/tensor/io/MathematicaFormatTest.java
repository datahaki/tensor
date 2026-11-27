// code by jph
package ch.alpine.tensor.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.random.RandomGenerator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import ch.alpine.tensor.DecimalScalar;
import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.Rational;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.Unprotect;
import ch.alpine.tensor.alg.Flatten;
import ch.alpine.tensor.alg.VectorQ;
import ch.alpine.tensor.lie.LeviCivitaTensor;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.sca.Chop;

class MathematicaFormatTest {
  @Test
  void testMathematica() {
    int n = 20;
    int m = 10;
    RandomGenerator randomGenerator = ThreadLocalRandom.current();
    Tensor a = Tensors.matrix((_, _) -> //
    randomGenerator.nextInt(3) == 0 ? //
        DoubleScalar.of(randomGenerator.nextDouble()) : //
        Rational.of(randomGenerator.nextLong(), randomGenerator.nextLong()), n, m);
    assertEquals(MathematicaFormat.of(a).count(), n); // count rows
    assertEquals(a, MathematicaFormat.parse(MathematicaFormat.of(a))); // full circle
  }

  private static void checkNonString(Tensor tensor) {
    Optional<Scalar> optional = Flatten.scalars(tensor) //
        .filter(scalar -> scalar instanceof StringScalar) //
        .findAny();
    boolean containsStringScalar = optional.isPresent();
    if (containsStringScalar)
      fail("" + optional.orElseThrow());
    assertFalse(containsStringScalar);
    assertFalse(StringScalarQ.any(tensor));
  }

  @Test
  void testStrings() {
    String[] strings = new String[] { //
        "{{3 + 2*I}, I,-I,-1.0348772853950305 + 0.042973906265653894*I, ", //
        " -1.0348772853950305 - 0.042973906265653894*I, {}, ", //
        " {3.141592653589793, {3, 1.4142135623730951}, 23846238476583465873465/", //
        "   234625348762534876523847652837645223864521}}" };
    Tensor tensor = MathematicaFormat.parse(Stream.of(strings));
    checkNonString(tensor);
  }

  @Test
  void testComplex() {
    String[] strings = new String[] { //
        "{{3 + I}, -1.0348772853950305 - 0.042973906265653894*I, {}, ", //
        " 0.1 + I, 0.1 - I, ", // <- these were manually added
        " 0. + 0.123*I, 0. - 123233.323123*I, {0. + 1982.6716245387552*I,", //
        "  {(81263581726538*I)/42921390881, 0. + 892.5158065769785*I}} }" };
    Tensor tensor = MathematicaFormat.parse(Stream.of(strings));
    checkNonString(tensor);
    assertEquals(tensor.length(), 8);
  }

  @Test
  void testBasic() throws IOException {
    Path path = Unprotect.resourcePath("ch/alpine/tensor/io/basic.mathematica");
    Tensor tensor = MathematicaFormat.Get.of(path);
    checkNonString(tensor);
  }

  @Test
  void testBasicResource() {
    Tensor tensor = Import.of("ch/alpine/tensor/io/basic.mathematica");
    checkNonString(tensor);
  }

  @Test
  void testExponent() throws IOException {
    Path path = Unprotect.resourcePath("ch/alpine/tensor/io/exponent.mathematica");
    Tensor tensor = MathematicaFormat.Get.of(path);
    checkNonString(tensor);
    assertEquals(tensor, Import.of("ch/alpine/tensor/io/exponent.mathematica"));
  }

  @Test
  void testExponent2() {
    Tensor tensor = MathematicaFormat.parse(Stream.of("{1*^-10, 1*^10}"));
    checkNonString(tensor);
    String put = MathematicaFormat.Put.string(tensor);
    Tensor recon = MathematicaFormat.parse(Stream.of(put));
    assertEquals(tensor, recon);
  }

  @Test
  void testPrime() throws IOException {
    Path path = Unprotect.resourcePath("ch/alpine/tensor/io/decimals.mathematica");
    Tensor tensor = MathematicaFormat.Get.of(path);
    assertTrue(tensor.stream().anyMatch(scalar -> scalar instanceof DecimalScalar));
    checkNonString(tensor);
    assertEquals(tensor.toString(), MathematicaFormat.Put.string(tensor));
  }

  @Test
  void testSparse() {
    Tensor sparse = LeviCivitaTensor.of(3);
    List<String> list = MathematicaFormat.of(sparse).collect(Collectors.toList());
    assertEquals(list.get(0), "SparseArray[{{1, 2, 3}->1,");
  }

  @Test
  void testSparseNested() {
    Tensor tensor = Tensors.fromString("{{2, 3.123+3*I, 34.1231}, {556, 3/456, -323/2, {3, 8.45`}}}");
    tensor.set(IdentityMatrix.sparse(3), 0, 2);
    List<String> list = MathematicaFormat.of(tensor).collect(Collectors.toList());
    assertFalse(list.isEmpty());
    // list.forEach(System.out::println);
  }

  @Test
  void testOf() {
    String string = MathematicaFormat.concise("Function", 12);
    assertEquals(string, "Function[12]");
  }

  @Test
  void testFail() {
    assertThrows(Exception.class, () -> MathematicaFormat.of(null));
    assertThrows(Exception.class, () -> MathematicaFormat.concise(null, 12));
  }

  @Test
  void testSmallMatrix() {
    String string = new Throw(RealScalar.of(3), 12).getMessage();
    assertEquals(string, "Throw[3, 12]");
  }

  @Test
  void testResource() throws IOException {
    Path path = Unprotect.resourcePath("ch/alpine/tensor/io/basic.mathematica");
    Tensor tensor = MathematicaFormat.Get.of(path);
    assertNotNull(tensor);
    assertFalse(tensor instanceof Scalar);
    assertEquals(tensor.length(), 13);
    assertEquals(tensor, MathematicaFormat.Get.of(path));
  }

  @Test
  void testBinary() throws IOException { // this use is not as intended
    Path path = Unprotect.resourcePath("ch/alpine/tensor/img/rgb7x11.bmp");
    Tensor tensor = MathematicaFormat.Get.of(path);
    assertInstanceOf(StringScalar.class, tensor);
  }

  @Test
  void testMissing() {
    Path path = Path.of("ch/alpine/tensor/io/doesnotexist");
    assertThrows(Exception.class, () -> MathematicaFormat.Get.of(path));
  }

  @Test
  void testHanzi() throws IOException {
    Path path = Unprotect.resourcePath("ch/alpine/tensor/io/hanzi.mathematica");
    Tensor tensor = MathematicaFormat.Get.of(path);
    String string = tensor.Get(2).toString();
    assertEquals(string.charAt(0), '\u6C49');
    assertEquals(string.charAt(1), '\u5B57');
    VectorQ.requireLength(tensor, 5);
    Scalar pi = tensor.Get(4);
    assertTrue(pi instanceof DecimalScalar);
    Chop._40.requireClose(pi, Pi.in(100));
  }

  @TempDir
  Path tempDir;

  @Test
  void testUnstructured() throws IOException {
    Path path = tempDir.resolve("file.put");
    Tensor tensor = Tensors.fromString("{{2, 3.123+3*I, 34.1231}, {556, 3/456, -323/2, {3, 8.45`}}}");
    MathematicaFormat.Put.of(path, tensor.unmodifiable());
    Tensor readin = MathematicaFormat.Get.of(path);
    assertEquals(tensor, readin);
  }

  @Test
  void testNullFail() {
    Path path = tempDir.resolve("fileNullFail.put");
    assertThrows(Exception.class, () -> MathematicaFormat.Put.of(path, null));
  }
}
