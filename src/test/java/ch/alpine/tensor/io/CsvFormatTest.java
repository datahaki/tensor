// code by jph
package ch.alpine.tensor.io;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.ext.ReadLine;
import ch.alpine.tensor.ext.Serialization;

class CsvFormatTest {
  @Test
  void testNonRect() throws Exception {
    Tensor s = Tensors.empty();
    s.append(Tensors.of(StringScalar.of("ksah   g d fkhjg")));
    s.append(Tensors.vector(1, 2, 3));
    s.append(Tensors.vector(7));
    s.append(Tensors.of(StringScalar.of("kddd")));
    s.append(Tensors.vector(5, 6));
    for (XsvFormat xsvFormat : XsvFormat.values()) {
      Stream<String> stream = xsvFormat.of(s);
      Tensor r = xsvFormat.parse(stream);
      assertEquals(s, r);
      Tensor p = Tensors.fromString(s.toString());
      assertEquals(r, p);
      Tensor ten = Serialization.parse(Serialization.of(s));
      assertEquals(s, ten);
      assertEquals(s, Serialization.copy(s));
    }
  }

  @Test
  void testParse() {
    Tensor t = XsvFormat.CSV.parse(Arrays.asList("10, 200, 3", "", "78", "-3, 2.3").stream());
    Tensor r = Tensors.fromString("{{10, 200, 3}, {}, {78}, {-3, 2.3}}");
    assertEquals(t, r);
    assertEquals(t.toString(), r.toString());
  }

  @Test
  void testParse2() {
    Tensor t = XsvFormat.CSV.parse(Arrays.asList("10, {200, 3}", "78", "-3, 2.3").stream());
    Tensor r = Tensors.fromString("{{10, {200, 3}}, {78}, {-3, 2.3}}");
    assertEquals(t, r);
    assertEquals(t.toString(), r.toString());
  }

  @Test
  void testSpacing() {
    Tensor r = Tensors.fromString("{{10, {200, 3}}, {},  {78}, {-3, 2.3, 1-3*I}}");
    List<String> list = XsvFormat.CSV.of(r).collect(Collectors.toList());
    assertEquals(list.get(0), "10,{200, 3}");
    assertEquals(list.get(1), "");
    assertEquals(list.get(2), "78");
    assertEquals(list.get(3), "-3,2.3,1-3*I");
  }

  @Test
  void testCount2() throws IOException {
    try (InputStream inputStream = getClass().getResource("/io/libreoffice_calc.csv").openStream()) {
      try (Stream<String> stream = ReadLine.of(inputStream)) {
        Tensor table = XsvFormat.CSV.parse(stream);
        assertEquals(Dimensions.of(table), Arrays.asList(4, 2));
      }
      assertEquals(inputStream.available(), 0);
    }
  }

  @Test
  void testLibreofficeCalcFile() throws Exception {
    try (InputStream inputStream = getClass().getResource("/io/libreoffice_calc.csv").openStream()) {
      Stream<String> stream = ReadLine.of(inputStream);
      Tensor table = XsvFormat.CSV.parse(stream);
      assertEquals(Dimensions.of(table), Arrays.asList(4, 2));
      assertEquals(inputStream.available(), 0);
    }
  }

  @Test
  void testMatlabFile() throws Exception {
    try (InputStream inputStream = getClass().getResource("/io/matlab_3x5.csv").openStream()) {
      Stream<String> stream = ReadLine.of(inputStream);
      Tensor table = XsvFormat.CSV.parse(stream);
      assertEquals(Dimensions.of(table), Arrays.asList(3, 5));
    }
  }

  @Test
  void testGeditFile() throws Exception {
    try (InputStream inputStream = getClass().getResource("/io/gedit_mixed.csv").openStream()) {
      Stream<String> stream = ReadLine.of(inputStream);
      Tensor table = XsvFormat.CSV.parse(stream);
      assertEquals(table, Tensors.fromString("{{hello, blub}, {1, 4.22}, {-3, 0.323, asdf}, {}, {2, 1.223}, {3+8*I, 12, 33}}"));
    }
  }

  @Test
  void testStrict() {
    Tensor matrix = Tensors.of(Tensors.of( //
        StringScalar.of("PUT"), //
        RationalScalar.of(1, 2), //
        RationalScalar.of(5, 1), //
        DoubleScalar.of(1.25)));
    Tensor strict = matrix.map(CsvFormat.strict());
    assertEquals(strict.toString(), "{{\"PUT\", 0.5, 5, 1.25}}");
  }

  @Test
  void testStringWithComma() {
    Tensor row = Tensors.of(StringTensor.vector("123", "[ , ]", "a"));
    Stream<String> stream = XsvFormat.CSV.of(row);
    List<String> list = stream.collect(Collectors.toList());
    assertEquals(list.size(), 1); // only 1 row
    assertEquals(list.get(0), "123,[ , ],a");
  }

  @Test
  void testStringStrict() {
    Tensor row = Tensors.of(StringTensor.vector("123", "[ , ]", "a"));
    Stream<String> stream = XsvFormat.CSV.of(row.map(CsvFormat.strict()));
    List<String> list = stream.collect(Collectors.toList());
    assertEquals(list.size(), 1); // only 1 row
    assertEquals(list.get(0), "\"123\",\"[ , ]\",\"a\"");
  }

  @Test
  void testVectorWithComma() {
    Tensor row = StringTensor.vector(" 2  ,  3 ", "[ , ]", "` ;  ;  ,   ;`");
    Stream<String> stream = XsvFormat.CSV.of(row);
    List<String> list = stream.collect(Collectors.toList());
    assertEquals(list.size(), 3); // 3 rows
    assertEquals(list.get(0), " 2  ,  3 ");
    assertEquals(list.get(1), "[ , ]");
    assertEquals(list.get(2), "` ;  ;  ,   ;`");
  }
}
