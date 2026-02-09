// code by jph
package ch.alpine.tensor.mat.ev;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
import java.util.stream.IntStream;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.Integers;
import ch.alpine.tensor.mat.DiagonalMatrix;
import ch.alpine.tensor.mat.NullSpace;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.mat.re.Det;

/** Careful: only for use with small matrices */
/* package */ enum SimpleEigensystem {
  ;
  private static final Scalar XN = Rapoly.of(Tensors.vector(0, -1));

  public static Eigensystem of(Tensor matrix) {
    int n = matrix.length();
    Tensor res = matrix.add(DiagonalMatrix.of(n, XN));
    Rapoly rapoly = (Rapoly) Det.withoutDivision(res);
    Tensor values = Tensors.reserve(n);
    Tensor vectors = Tensors.reserve(n);
    List<Scalar> list = new ArrayList<>();
    for (Tensor _r : rapoly.polynomial().roots().maps(Tolerance.CHOP)) {
      Scalar root = (Scalar) _r;
      OptionalInt optionalInt = IntStream.range(0, list.size()).filter(i -> Tolerance.CHOP.isClose(list.get(i), root)).findFirst();
      if (optionalInt.isPresent())
        list.remove(optionalInt.orElseThrow());
      else {
        Tensor diff = matrix.add(DiagonalMatrix.of(n, root.negate()));
        Tensor ns = NullSpace.of(diff);
        ns.forEach(vectors::append);
        ns.forEach(_ -> values.append(root));
        for (int index = 1; index < ns.length(); ++index)
          list.add(root);
      }
    }
    Integers.requireEquals(values.length(), n);
    return new Eigensystem(values, vectors);
  }
}
