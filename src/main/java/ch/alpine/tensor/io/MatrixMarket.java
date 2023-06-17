// code by jph
package ch.alpine.tensor.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;
import java.util.stream.Stream;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.sca.Conjugate;
import ch.alpine.tensor.spa.SparseArray;

/* package */ enum MatrixMarket {
  ;
  private static enum Symmetry {
    GENERAL,
    SYMMETRIC,
    SKEW_SYMMETRIC,
    HERMITIAN;

    private final String key = name().replace('_', '-').toLowerCase();
  }

  @SuppressWarnings("incomplete-switch")
  public static Tensor of(InputStream inputStream) throws IOException {
    try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StaticHelper.CHARSET))) {
      String format = bufferedReader.readLine();
      Symmetry symmetry = Stream.of(Symmetry.values()).filter(sym -> format.contains(sym.key)).findFirst().orElseThrow();
      String line;
      do {
        line = bufferedReader.readLine();
      } while (line.startsWith("%"));
      Tensor matrix;
      if (format.contains("coordinate")) { // "array" for dense, "coordinate" for sparse
        final int nonzeros;
        {
          StringTokenizer stringTokenizer = new StringTokenizer(line);
          matrix = SparseArray.of(RealScalar.ZERO, //
              Integer.parseInt(stringTokenizer.nextToken()), //
              Integer.parseInt(stringTokenizer.nextToken()));
          nonzeros = Integer.parseInt(stringTokenizer.nextToken());
        }
        for (int count = 0; count < nonzeros; ++count) {
          StringTokenizer stringTokenizer = new StringTokenizer(bufferedReader.readLine());
          int row = Integer.parseInt(stringTokenizer.nextToken()) - 1;
          int col = Integer.parseInt(stringTokenizer.nextToken()) - 1;
          Scalar scalar = extract(stringTokenizer);
          matrix.set(scalar, row, col);
          switch (symmetry) {
          case SYMMETRIC -> matrix.set(scalar, col, row);
          case SKEW_SYMMETRIC -> matrix.set(scalar.negate(), col, row);
          case HERMITIAN -> matrix.set(Conjugate.FUNCTION.apply(scalar), col, row);
          }
        }
      } else {
        StringTokenizer stringTokenizer = new StringTokenizer(line);
        int rows = Integer.parseInt(stringTokenizer.nextToken());
        int cols = Integer.parseInt(stringTokenizer.nextToken());
        matrix = Array.zeros(rows, cols);
        for (int col = 0; col < cols; ++col)
          for (int row = 0; row < rows; ++row)
            matrix.set(extract(new StringTokenizer(bufferedReader.readLine())), row, col);
      }
      return matrix;
    }
  }

  private static Scalar extract(StringTokenizer stringTokenizer) {
    return switch (stringTokenizer.countTokens()) {
    case 1 -> token(stringTokenizer);
    case 2 -> ComplexScalar.of( //
        token(stringTokenizer), //
        token(stringTokenizer));
    default -> throw new IllegalArgumentException();
    };
  }

  private static Scalar token(StringTokenizer stringTokenizer) {
    return Scalars.fromString(stringTokenizer.nextToken().replace('e', 'E'));
  }
}
