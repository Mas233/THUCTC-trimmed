package org.thunlp.io;


import java.io.*;

public class TextFileReader {
    BufferedReader br;

    /**
     * 读取当前文件中的所有内容，返回一个字符串。这个函数的性能要比一行一行readline好。
     *
     * @return 当前文件中所有内容，包含回车(\r)或是换行(\n)
     * @throws IOException IO异常
     */
    public String readAll() throws IOException {
        int buffSize = 4096;
        char[] buffer = new char[buffSize];
        int read, fill = 0;
        while (true) {
            read = br.read(buffer, fill, buffer.length - fill);
            if (read == -1) {
                break;
            }
            fill += read;
            if (fill >= buffer.length) {
                char[] newBuffer = new char[buffSize + buffer.length];
                System.arraycopy(buffer, 0, newBuffer, 0, buffer.length);
                buffer = null;
                buffer = newBuffer;
            }
        }

        return new String(buffer, 0, fill);
    }

    public static String readAll(String filename) throws IOException {
        return readAll(filename, "UTF-8");
    }

    public static String readAll(String filename, String encode) throws IOException {
        TextFileReader reader = new TextFileReader(filename, encode);
        String result = reader.readAll();
        reader.close();
        return result;
    }

    public String readLine() throws IOException {
        return br.readLine();
    }


    public TextFileReader(String filename, String encode) throws IOException {
        this(new File(filename), encode);
    }

    public TextFileReader(File file, String encode) throws IOException {
        br = constructReader(file, encode);
    }

    protected BufferedReader constructReader(File file, String encode)
            throws IOException {
        return new BufferedReader(
                new InputStreamReader(new FileInputStream(file), encode));
    }

    public void close() throws IOException {
        br.close();
    }

}
