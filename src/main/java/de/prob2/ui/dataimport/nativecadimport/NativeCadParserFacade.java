package de.prob2.ui.dataimport.nativecadimport;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.google.common.io.MoreFiles;


/**
 * NativeCadParserWrapper is a facade that helps when parsing from native CAD files (e.g. FreeCAD files) 
 * into formal model machines (like classical B .mch).
 */
public class NativeCadParserFacade {

    public static final Map<String, NativeCad2BParser> EXTENSION_TO_PARSER;
    static {
		final Map<String, NativeCad2BParser> map = new HashMap<>();
        map.put(FreeCad2BParser.NATIVE_CAD_FREECAD_EXTENSION, new FreeCad2BParser());

        EXTENSION_TO_PARSER = Map.copyOf(map);
    }

    public static String parseToClassicalB(Path modelPath) throws IOException {
        String extension = MoreFiles.getFileExtension(modelPath);
        NativeCad2BParser parser = EXTENSION_TO_PARSER.get(extension);

        if (parser == null) {
            throw new IllegalArgumentException(
                    "No Parser for file extension found: " + extension);
        }

        ParseData cadJointData = parser.parseFromFile(modelPath);
        String bMachine = parser.parseIntoBMachine(cadJointData);

        return bMachine;
    }
    
    public static Set<String> getSupportedFileExtensions()
    {
        return EXTENSION_TO_PARSER.keySet();
    }
}
