package ch.alpine.tensor.io;

import java.io.IOException;
import java.io.InputStream;

import ch.alpine.tensor.Tensor;

enum WavReader {
  ;
  public static Tensor read(InputStream inputStream) throws IOException {
    byte[] d4 = new byte[4];
    inputStream.read(d4);
    String string = new String(d4);
    System.out.println(string);
    // ByteBuffer.wrap(d4).;
    return null;
  }
}
