package de.prob2.ui.dataimport.nativecadimport.FreeCad;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.io.FilenameUtils;
import org.xml.sax.SAXException;

import de.prob2.ui.dataimport.nativecadimport.NativeCad2BParser;
import de.prob2.ui.dataimport.nativecadimport.ParseData;

/**
 * This class is a parser that extracts data (mainly joint information) from FreeCAD files.
 */
public class FreeCad2BParser implements NativeCad2BParser {
    public static final String NATIVE_CAD_FREECAD_EXTENSION = "FCStd";
    public static final String DOCUMENT_XML = "Document.xml";

    @Override
    public ParseData parseFromFile(Path cadFilePath) throws IOException {
        // Get input
        InputStream documentXml = zipToDocXmlStream(cadFilePath);

        // Parse
        FreeCadHandler freeCadHandler = new FreeCadHandler();
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser;
        try {
            parser = factory.newSAXParser();
            parser.parse(documentXml, freeCadHandler);
        } catch (ParserConfigurationException | SAXException e) {
            throw new IOException("Error while parsing Document.xml in " + cadFilePath, e);
        }

        ParseData parseData = freeCadHandler.getParseData();
        String machineName = FilenameUtils.removeExtension(cadFilePath.toFile().getName());
        parseData.setMachineName(machineName);
        return parseData;
    }
    
    private InputStream zipToDocXmlStream(Path cadFilePath) throws ZipException, IOException {
        ZipFile file = new ZipFile(cadFilePath.toFile());
        ZipEntry entry = file.getEntry(DOCUMENT_XML);
        if (entry == null) {
            throw new IOException("Could not open Document.xml in " + cadFilePath);
        }

        return file.getInputStream(entry);
    }
}