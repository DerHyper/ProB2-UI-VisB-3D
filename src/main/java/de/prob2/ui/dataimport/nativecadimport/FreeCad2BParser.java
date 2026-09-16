package de.prob2.ui.dataimport.nativecadimport;

import java.io.IOException;
import java.nio.file.Path;

/**
 * This Class is a parser that parses the joint information of FreeCAD files.
 */
public class FreeCad2BParser implements NativeCad2BParser {
    public static final String NATIVE_CAD_FREECAD_EXTENSION = "FCStd";
    @Override
    public CadJointData parseFromFile(Path cadFilePath) throws IOException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'parseFromFile'");
    }
    @Override
    public String parseIntoBMachine(CadJointData joints) throws IOException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'parseIntoBMachine'");
    }
}