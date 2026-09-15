package de.prob2.ui.dataimport.nativecadimport;

import java.io.File;
import java.io.IOException;

/**
 * This interface defines functions that are used to parse a native CAD file 
 * (e.g. FCStd) into a temporary classical B machine file.
 * Native CAD files are used because they contain joint information (e.g. maximum angles) 
 * that are not accessible in export formats like STEP.
*/
public interface NativeCad2BParser {
    /**
     * Parses a native CAD file (e.g. ".FCStd" files from FreeCAD) joint information into an 
     * internal representation.
     * Native files are used because most export formats are incapable of storing joint information 
     * (like STL or OBJ) or are capable of storing joint information but are generally not implemented 
     * to do so (like STEP).
     */
    public CadJointData parseFromFile(String cadFilePath) throws IOException;
    
    /**
     * Parses the internal representation of joint information into a temporary classical B machine file.
     */
    public File parseIntoBMachine(CadJointData joints) throws IOException;
}
