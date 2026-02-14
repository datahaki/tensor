// code by jph
package test.wrap;

import java.io.Serializable;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.BasisTransform;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.mat.DiagonalMatrix;
import ch.alpine.tensor.mat.MatrixDotTranspose;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.mat.ev.Eigensystem;
import ch.alpine.tensor.mat.ev.RealEigensystem;
import ch.alpine.tensor.sca.Chop;

public record EigensystemQ(Tensor matrix) implements Serializable {
  /** @param eigensystem
   * @param chop */
  public void require(Eigensystem eigensystem, Chop chop) {
    Tensor diagonalMatrix = DiagonalMatrix.sparse(eigensystem.values());
    SerializableQ.require(eigensystem);
    require(diagonalMatrix, eigensystem.vectors(), chop);
  }

  public void require(Eigensystem eigensystem) {
    require(eigensystem, Tolerance.CHOP);
  }

  public void require(RealEigensystem realEigensystem, Chop chop) {
    SerializableQ.require(realEigensystem);
    require(realEigensystem.diagonalMatrix(), realEigensystem.vectors(), chop);
  }

  public void require(RealEigensystem realEigensystem) {
    require(realEigensystem, Tolerance.CHOP);
  }

  public void require(Tensor diagonalMatrix, Tensor vectors, Chop chop) {
    // matrix.Transpose[vectors]==Transpose[vectors].DiagonalMatrix[values]
    Tensor lhs = MatrixDotTranspose.of(matrix, vectors);
    Tensor rhs = Transpose.of(vectors).dot(diagonalMatrix);
    chop.requireClose(lhs, rhs);
    // ---
    Tensor btr = Transpose.of(BasisTransform.ofMatrix(Transpose.of(diagonalMatrix), vectors));
    chop.requireClose(matrix, btr);
  }
}
