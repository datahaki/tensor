package ch.alpine.tensor.sca;

import java.util.Random;
import java.util.TreeMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

class NBaseTest {
  public static int hashCode2(int field, int a, int b) {
    return (field << 6 + (a << 4) + (b << 2));
  }

  public static int hashCode3(int field, int a, int b) {
    return (field << 6) + (a << 4) + (b << 2);
  }

  @Test
  void test() {
    Random random = ThreadLocalRandom.current();
    TreeMap<Integer, Long> map = IntStream.generate(() -> hashCode3(random.nextInt(), random.nextInt(), random.nextInt())) //
        .limit(10000).boxed() //
        .collect(Collectors.groupingBy( //
            Function.identity(), TreeMap::new, Collectors.counting()));
    map.entrySet().stream().filter(e -> 6 < e.getValue()).forEach(e -> System.out.printf("%08x %d\n", e.getKey(), e.getValue()));
  }
}
