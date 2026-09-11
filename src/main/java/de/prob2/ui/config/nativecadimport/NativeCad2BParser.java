package de.prob2.ui.config.nativecadimport;

import java.io.IOException;

public interface NativeCad2BParser {
    public NativeCadModel parseFromFile(String cadFilePath) throws IOException;
}
