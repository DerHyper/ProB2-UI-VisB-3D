package de.prob2.ui.visb.visb3d.cad.convert;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * A interface for all converters that transform files into .glb files.
 * <p>
 * New formats can be added by implementing this interface like in 
 * {@link MeshFileToGlbConverter}.
 */
public interface CadToGlbConverter {

	/**
	 * @param fileExtensionLowerCase Filename suffix in lower letters (e.g. "stl", "obj", "step")
	 * @return true, if converter can use this file format
	 */
	boolean supports(String fileExtensionLowerCase);

	/**
     * Converts the input file to .glb.
     *
     * @param inputFile Path to the input file
     * @param outputDirectory Directory, the .glb file is generated in
     * @return Path to the generated .glb file
     * @throws IOException When errors occur while reading/writing, or a file in incorrect 
     */
    Path convert(Path inputFile, Path outputDirectory) throws IOException;
     
    /**
    * @return List of file extensions that can be converted by the converter
    */
    List<String> supportedFileExtensions();
}