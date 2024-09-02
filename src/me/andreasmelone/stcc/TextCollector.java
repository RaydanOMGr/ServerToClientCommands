package me.andreasmelone.stcc;

import java.io.*;
import java.util.*;
import java.util.function.*;

public class TextCollector extends OutputStream {
    private final List<String> lines = new ArrayList<>();
    private final List<Consumer<String>> listeners = new ArrayList();
    private final PrintStream original;
    private StringBuilder buffer = new StringBuilder();

    public TextCollector(PrintStream original) {
        this.original = original;
    }

    @Override
    public void write(int b) throws IOException {
        if (b == '\n') {
            lines.add(buffer.toString());
            listeners.forEach(l -> l.accept(buffer.toString()));
            buffer = new StringBuilder();
        } else {
            buffer.append((char) b);
        }
        original.write(b);
    }

    public void onPrint(Consumer<String> listener) {
        listeners.add(listener);
    }

    public List<String> getLines() {
        return lines;
    }
}
