// code by gemini, jph
package ch.alpine.tensor.ext;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.UncheckedIOException;

/** Used in: Export, Import, ObjectFormat */
public enum Serialization {
  ;
  private static final class Buffer extends ByteArrayOutputStream {
    private static final int INITIAL_BUFFER_SIZE = 8192; // 8kB pre-allocated buffer

    public Buffer() {
      super(INITIAL_BUFFER_SIZE);
    }

    public byte[] buf() {
      return buf;
    }

    public int count() {
      return count;
    }
  }

  // Thread-local instances avoid thread contention and heavy GC allocations
  private static final ThreadLocal<Buffer> THREAD_LOCAL = //
      ThreadLocal.withInitial(Buffer::new);

  /** encodes {@link Serializable} input {@link Object} as array of bytes.
   * 
   * <p>In order to store the object <b>uncompressed</b> to a file, use
   * <code>Files.write(Paths.get("filePath"), bytes)</code>
   * 
   * @param object may be null
   * @return serialization of object
   * @throws IOException */
  public static byte[] of(Object object) throws IOException {
    Buffer buffer = THREAD_LOCAL.get();
    buffer.reset();
    try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(buffer)) {
      objectOutputStream.writeObject(object);
      objectOutputStream.flush();
      // Note: toByteArray() creates a copy here, kept for public of(Object) API compatibility
      return buffer.toByteArray();
    }
  }

  /** decodes {@link Serializable} object from array of bytes,
   * deserialization of object
   * 
   * In order to retrieve the object from an <b>uncompressed</b> file, use
   * <code>Files.readAllBytes(Paths.get("filePath"))</code>
   * 
   * @param bytes
   * @return {@link Serializable} object encoded in input bytes
   * @throws ClassNotFoundException
   * @throws IOException */
  @SuppressWarnings("unchecked")
  public static <T> T parse(byte[] bytes) throws ClassNotFoundException, IOException {
    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
    try (ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream)) {
      return (T) objectInputStream.readObject();
    }
  }

  /** Creates a high-performance deep copy of a {@link Serializable} object. */
  @SuppressWarnings("unchecked")
  public static <T> T copy(T object) {
    Buffer buffer = THREAD_LOCAL.get();
    try {
      buffer.reset();
      // 1. Write to the buffer
      try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(buffer)) {
        objectOutputStream.writeObject(object);
        objectOutputStream.flush();
      }
      // 2. Zero-copy read directly from the underlying buffer array
      try (ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(buffer.buf(), 0, buffer.count()))) {
        return (T) objectInputStream.readObject();
      }
    } catch (IOException ioException) {
      throw new UncheckedIOException(ioException);
    } catch (ClassNotFoundException e) {
      throw new IllegalStateException(e);
    }
  }
}
