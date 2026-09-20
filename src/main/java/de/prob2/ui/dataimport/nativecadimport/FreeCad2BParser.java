package de.prob2.ui.dataimport.nativecadimport;

import java.io.IOException;
import java.nio.file.Path;

/**
 * This class is a parser that extracts data (mainly joint information) from FreeCAD files.
 */
public class FreeCad2BParser implements NativeCad2BParser {
    public static final String NATIVE_CAD_FREECAD_EXTENSION = "FCStd";
    @Override
    public ParseData parseFromFile(Path cadFilePath) throws IOException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'parseFromFile'");
    }
    @Override
    public String parseIntoBMachine(ParseData joints) throws IOException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'parseIntoBMachine'");
    }
}