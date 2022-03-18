// code by jph
package ch.alpine.tensor.opt.nd;

import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.VectorQ;

public class NdStringBuilderTest {
  @Test
  public void testSimple() {
    NdMap<String> ndMap = NdTreeMap.of(CoordinateBounds.of(Tensors.vector(-2, -3), Tensors.vector(8, 9)), 2);
    ndMap.insert(Tensors.vector(1, 1), "d1");
    ndMap.insert(Tensors.vector(1, 0), "d2");
    ndMap.insert(Tensors.vector(0, 1), "d3");
    ndMap.insert(Tensors.vector(1, 1), "d4");
    ndMap.insert(Tensors.vector(0.1, 0.1), "d5");
    ndMap.insert(Tensors.vector(6, 7), "d6");
    ndMap.insert(Tensors.vector(1, 1), "d7");
    ndMap.insert(Tensors.vector(1, 1), "d8");
    String string = ndMap.toString();
    assertFalse(string.isEmpty());
    NdBinsize<String> ndBinsize = new NdBinsize<>();
    ndMap.visit(ndBinsize);
    VectorQ.require(ndBinsize.bins());
  }
}
