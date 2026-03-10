// code by jph
package ch.alpine.tensor.mat.sv;

import java.io.Serializable;

import ch.alpine.tensor.Tensor;

record SVDRecord(Tensor getU, Tensor values, Tensor getV) implements SingularValueDecomposition, Serializable {
}
