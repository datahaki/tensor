package ch.alpine.tensor.mat.gr;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

class InvolutoryMatrixQTest {
  @Test
  void test() {
    Tensor matrix = Tensors.fromString("{{3,-4},{2,-3}}");
    assertTrue(InvolutoryMatrixQ.INSTANCE.isMember(matrix));
  }
}
