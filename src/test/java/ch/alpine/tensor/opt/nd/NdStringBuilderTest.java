// code by jph
package ch.alpine.tensor.opt.nd;

import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.VectorQ;
import junit.framework.TestCase;

public class NdStringBuilderTest extends TestCase {
  public void testSimple() {
    NdMap<String> ndMap = NdTreeMap.of(Tensors.vector(-2, -3), Tensors.vector(8, 9), 2);
    ndMap.add(Tensors.vector(1, 1), "d1");
    ndMap.add(Tensors.vector(1, 0), "d2");
    ndMap.add(Tensors.vector(0, 1), "d3");
    ndMap.add(Tensors.vector(1, 1), "d4");
    ndMap.add(Tensors.vector(0.1, 0.1), "d5");
    ndMap.add(Tensors.vector(6, 7), "d6");
    ndMap.toString();
    NdBinsize<String> ndBinsize = new NdBinsize<>();
    ndMap.visit(ndBinsize);
    VectorQ.require(ndBinsize.bins());
  }
}
