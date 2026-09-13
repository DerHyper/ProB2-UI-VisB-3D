package de.prob2.ui.config.nativecadimport;

import java.io.File;
import java.io.IOException;

public interface NativeCad2BParser {
    public CadJointData parseFromFile(String cadFilePath) throws IOException;

    public File parseIntoBMachine(CadJointData joints) throws IOException;
}
