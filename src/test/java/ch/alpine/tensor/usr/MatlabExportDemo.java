// code by jph
package ch.alpine.tensor.usr;

import java.io.IOException;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.alg.ArrayReshape;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.alg.Range;
import ch.alpine.tensor.ext.HomeDirectory;
import ch.alpine.tensor.io.Export;
import ch.alpine.tensor.io.Pretty;
import ch.alpine.tensor.io.StringScalar;

/* package */ enum MatlabExportDemo {
  ;
  static void vector1() throws IOException {
    Tensor tensor = Tensors.vectorDouble(3.2, -3, 0.234, 3, 3e-20, 0);
    Export.of(HomeDirectory.file("me_vector1.m"), tensor);
  }

  static void vector2() throws IOException {
    Tensor tensor = Tensors.fromString( //
        "{Infinity, 0, 0, 2.1342134E-300, -Infinity, NaN, 0, 136458123548175/23947236498726349876239876234}");
    boolean status = tensor.stream().anyMatch(s -> s instanceof StringScalar);
    if (status)
      throw Throw.of(tensor);
    Export.of(HomeDirectory.file("me_vector2.m"), tensor);
  }

  static void matrix1() throws IOException {
    Tensor tensor = Tensors.matrix((i, j) -> RationalScalar.of(i * 5 + j, 1), 6, 5);
    System.out.println(Pretty.of(tensor));
    Export.of(HomeDirectory.file("me_matrix1.m"), tensor);
  }

  static void matrix2() throws IOException {
    Tensor tensor = Tensors.fromString("{{1/2, 0, 1.3}, {-0.12, 2+3*I, 0}}");
    Export.of(HomeDirectory.file("me_matrix2.m"), tensor);
  }

  static void form1() throws IOException {
    Tensor tensor = ArrayReshape.of(Range.of(0, 2 * 3 * 5), 2, 3, 5);
    System.out.println(Dimensions.of(tensor));
    Pretty.of(tensor);
    Export.of(HomeDirectory.file("me_form1.m"), tensor);
    // in matlab this is imported as 2x3x5 array
    // with
    // reshape(a(1,:,:),[3 5])
    // gives
    // 0 1 2 3 4
    // 5 6 7 8 9
    // 10 11 12 13 14
  }

  public static void main(String[] args) throws IOException {
    vector1();
    vector2();
    matrix1();
    matrix2();
    form1();
  }
}
