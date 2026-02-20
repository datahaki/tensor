// code by jph
package ch.alpine.tensor.ext.ref;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/** implementation of class visitor to extract implementation of cls */
public record InstanceDiscovery<T>(String basePackage, Class<T> cls, Consumer<Supplier<T>> consumer) implements ClassVisitor {
  /** function to discover instances of a certain class nested in basePackage
   * 
   * Example use:
   * generate dynamic test from the instances in the returned list
   * 
   * @param basePackage
   * @param cls
   * @return */
  public static <T> List<Supplier<T>> of(String basePackage, Class<T> cls) {
    List<Supplier<T>> list = new ArrayList<>();
    ClassDiscovery.execute(ClassPaths.getDefault(), new InstanceDiscovery<>(basePackage, cls, list::add));
    return list;
  }

  @Override // from ClassVisitor
  public void accept(String jarfile, Class<?> subcls) {
    if (StaticHelper.isInSubpackageOf(subcls, basePackage) && //
        cls.isAssignableFrom(subcls)) { // this narrow is deliberate
      for (Field field : subcls.getDeclaredFields())
        if (Modifier.isStatic(field.getModifiers())) {
          try {
            field.setAccessible(true); // mandatory
            Object object = field.get(null);
            if (cls.isInstance(object)) {
              T cast = cls.cast(object);
              consumer.accept(() -> cast);
            }
          } catch (Exception exception) {
            System.err.println("error " + exception);
          }
        }
      // ---
      if (subcls.isEnum()) {
        // enum constants are handled as fields above
      } else //
      if (subcls.isInterface()) {
        // ---
      } else //
      if (subcls.isRecord()) {
        // ---
      } else //
      if (subcls.isAnonymousClass()) {
        // ---
      } else //
        try {
          {
            Constructor<?> constructor = subcls.getDeclaredConstructor();
            constructor.setAccessible(true);
            Object object = constructor.newInstance();
            cls.cast(object);
          }
          consumer.accept(() -> {
            try {
              Constructor<?> constructor = subcls.getDeclaredConstructor();
              constructor.setAccessible(true);
              Object object = constructor.newInstance();
              return cls.cast(object);
            } catch (Exception exception) {
              throw new RuntimeException(exception);
            }
          });
        } catch (Exception exception) {
          // default constructor may not exist
        }
    }
  }
}
