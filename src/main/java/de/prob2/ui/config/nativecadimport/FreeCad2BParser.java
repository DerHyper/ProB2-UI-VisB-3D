package de.prob2.ui.config.nativecadimport;

import java.io.File;
import java.io.IOException;

public class FreeCad2BParser implements NativeCad2BParser {
    public static final String NATIVE_CAD_FREECAD_EXTENSION = "FCStd";
    @Override
    public CadJointData parseFromFile(String cadFilePath) throws IOException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'parseFromFile'");
    }
    @Override
    public File parseIntoBMachine(CadJointData joints) throws IOException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'parseIntoBMachine'");
    }
}