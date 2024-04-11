package org.thunlp.io;

import java.io.*;

public class TextFileWriter {
    BufferedWriter osw;

    public TextFileWriter(String filename) throws IOException {
        this(new File(filename), "UTF-8", false);
    }

    public TextFileWriter(String filename, String charset) throws IOException {
        this(new File(filename), charset, false);
    }

    public TextFileWriter(File file, String charset, boolean append)
            throws IOException {
        osw = constructWriter(file, charset, append);
    }

    protected BufferedWriter constructWriter(
            File file,
            String charset,
            boolean append) throws IOException {
        return new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file, append), charset));
    }

    public void write(String str) throws IOException {
        osw.write(str);
    }

    public void writeLine(String str) throws IOException {
        osw.write(str);
        osw.write(System.lineSeparator());
    }

    public void flush() throws IOException {
        osw.flush();
    }

    public void close() throws IOException {
        osw.close();
    }

    public void append(CharSequence cs) throws IOException {
        osw.append(cs);
    }
}
