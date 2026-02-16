// code by jph
package ch.alpine.tensor.ext.ref;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import org.junit.jupiter.api.Test;

class ClassHierarchyTest {
  @Test
  void testOrder() {
    Deque<Class<?>> deque = ClassHierarchy.of(ArrayList.class);
    List<String> list = deque.stream().map(Class::getSimpleName).toList();
    assertEquals(list.toString(), "[Object, AbstractCollection, AbstractList, ArrayList]");
  }

  @Test
  void testNull() {
    Deque<Class<?>> deque = ClassHierarchy.of(null);
    assertTrue(deque.isEmpty());
  }
}
