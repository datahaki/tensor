// code by jph
package ch.alpine.tensor.ext;

import java.util.function.Function;
import java.util.stream.IntStream;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class CacheTest extends TestCase {
  public void testSimple() {
    Function<Object, Double> function = Cache.of(k -> Math.random(), 3);
    double double1 = function.apply("eth");
    double double2 = function.apply("eth");
    assertEquals(double1, double2);
  }

  public void testInception() {
    Function<Object, Double> memo1 = Cache.of(k -> Math.random(), 32);
    AssertFail.of(() -> Cache.of(memo1, 32));
  }

  public void testMap() {
    Function<String, Integer> function = Cache.of(k -> 1, 768);
    IntStream.range(0, 26).parallel().forEach(c1 -> {
      char chr1 = (char) (65 + c1);
      for (int c2 = 0; c2 < 26; ++c2) {
        char chr2 = (char) (65 + c2);
        for (int c3 = 0; c3 < 13; ++c3) {
          char chr3 = (char) (65 + c3);
          function.apply(chr1 + "" + chr2 + "" + chr3);
        }
      }
    });
    Cache<String, Integer> cache = (Cache<String, Integer>) function;
    assertEquals(cache.size(), 768);
    cache.clear();
    assertEquals(cache.size(), 0);
  }

  private static class ScalarStringFunc implements Function<Scalar, String> {
    public int count = 0;

    @Override
    public String apply(Scalar t) {
      ++count;
      return t.toString();
    }
  }

  public void testSingle() {
    ScalarStringFunc scalarStringFunc = new ScalarStringFunc();
    Cache<Scalar, String> cache = Cache.of(scalarStringFunc, 1);
    assertEquals(cache.size(), 0);
    assertEquals(scalarStringFunc.count, 0);
    cache.apply(Pi.VALUE);
    assertEquals(cache.size(), 1);
    assertEquals(scalarStringFunc.count, 1);
    cache.apply(Pi.VALUE);
    assertEquals(cache.size(), 1);
    assertEquals(scalarStringFunc.count, 1);
    cache.apply(RealScalar.ONE);
    assertEquals(cache.size(), 1);
    assertEquals(scalarStringFunc.count, 2);
    cache.apply(RealScalar.ONE);
    assertEquals(cache.size(), 1);
    assertEquals(scalarStringFunc.count, 2);
  }

  private static class TensorStringFunc implements Function<Tensor, String> {
    public int count = 0;

    @Override
    public String apply(Tensor t) {
      ++count;
      return t.toString();
    }
  }

  public void testTensor() {
    TensorStringFunc tensorStringFunc = new TensorStringFunc();
    Tensor tensor = Tensors.fromString( //
        "{{-0.32499999999999907, 0.4708333333333343, 0.7853981633974483}, {1, 0, 1}, {-1, 1, 0}, {-0.5, -1, 0}, {0.4, 1, 0}}");
    Cache<Tensor, String> cache = Cache.of(tensorStringFunc, 2);
    assertEquals(cache.size(), 0);
    assertEquals(tensorStringFunc.count, 0);
    cache.apply(tensor.copy());
    assertEquals(cache.size(), 1);
    assertEquals(tensorStringFunc.count, 1);
    cache.apply(tensor.copy());
    assertEquals(cache.size(), 1);
    assertEquals(tensorStringFunc.count, 1);
  }

  public void testTensor2() {
    TensorStringFunc tensorStringFunc = new TensorStringFunc();
    Tensor tensor = Tensors.fromString( //
        "{{-0.32499999999999907, 0.4708333333333343, 0.7853981633974483}, {+Infinity}, {-Infinity, abc, 1[m*K^1/2]}, {-0.5, -1, 0}}");
    Cache<Tensor, String> cache = Cache.of(tensorStringFunc, 2);
    assertEquals(cache.size(), 0);
    assertEquals(tensorStringFunc.count, 0);
    cache.apply(tensor.copy().unmodifiable());
    assertEquals(cache.size(), 1);
    assertEquals(tensorStringFunc.count, 1);
    cache.apply(tensor.unmodifiable());
    assertEquals(cache.size(), 1);
    assertEquals(tensorStringFunc.count, 1);
    cache.apply(tensor.unmodifiable().copy());
    assertEquals(cache.size(), 1);
    assertEquals(tensorStringFunc.count, 1);
    cache.apply(tensor.unmodifiable());
    assertEquals(cache.size(), 1);
    assertEquals(tensorStringFunc.count, 1);
    IntStream.range(0, 26).parallel().forEach(c1 -> cache.apply(tensor));
    assertEquals(tensorStringFunc.count, 1);
  }

  private static class DelayedStringFunc implements Function<Tensor, String> {
    public int count = 0;

    @Override
    public String apply(Tensor t) {
      try {
        Thread.sleep(1);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      ++count;
      return t.toString();
    }
  }

  public void testDelayed() {
    DelayedStringFunc delayedStringFunc = new DelayedStringFunc();
    Tensor tensor = Tensors.fromString( //
        "{{-0.32499999999999907, 0.4708333333333343, 0.7853981633974483}, {+Infinity, 0, 1/3}, {-Infinity, abc, 1[m*K^1/2]}}");
    Cache<Tensor, String> cache = Cache.of(delayedStringFunc, 2);
    assertEquals(cache.size(), 0);
    IntStream.range(0, 26).parallel().forEach(c1 -> cache.apply(tensor));
    assertEquals(cache.size(), 1);
    // the function is typically called more than once
    assertTrue(0 <= delayedStringFunc.count);
  }

  public void testFailNull() {
    AssertFail.of(() -> Cache.of(null, 32));
  }

  public void testFailNegative() {
    AssertFail.of(() -> Cache.of(Function.identity(), -1));
  }
}
