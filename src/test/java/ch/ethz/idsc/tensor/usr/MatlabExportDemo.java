// code by jph
package ch.ethz.idsc.tensor.usr;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Partition;
import ch.ethz.idsc.tensor.io.MatlabExport;
import ch.ethz.idsc.tensor.io.Pretty;

enum MatlabExportDemo {
  ;
  static void export3D() {
    Tensor tensor = Partition.of(Tensors.matrix((i, j) -> RationalScalar.of(i * 5 + j, 1), 6, 5), 3);
    Pretty.of(tensor);
    // System.out.println(Pretty.of(tensor));
    // System.out.println(Dimensions.of(tensor));
    try {
      // Files.write(Paths.get("exported.m"), (Iterable<String>) MatlabExport.of(tensor)::iterator);
    } catch (Exception exception) {
      exception.printStackTrace();
    }
    // in matlab this is imported as 2x3x5 array
    // with
    // reshape(a(1,:,:),[3 5])
    // gives
    // 0 1 2 3 4
    // 5 6 7 8 9
    // 10 11 12 13 14
  }

  static void _matrix() throws IOException {
    Tensor tensor = Tensors.matrix((i, j) -> RationalScalar.of(i * 5 + j, 1), 6, 5);
    System.out.println(Pretty.of(tensor));
    Files.write(Paths.get(UserHome.file("me_matrix.m").toString()), (Iterable<String>) MatlabExport.of(tensor)::iterator);
  }

  static void _vector() throws IOException {
    Tensor tensor = Tensors.vectorDouble(3.2, -3, .234, 3, 3e-20, 0);
    Files.write(Paths.get(UserHome.file("me_vector.m").toString()), (Iterable<String>) MatlabExport.of(tensor)::iterator);
  }

  public static void main(String[] args) throws IOException {
    _vector();
    _matrix();
  }
}