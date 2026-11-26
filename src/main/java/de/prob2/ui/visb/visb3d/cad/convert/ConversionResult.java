package de.prob2.ui.visb.visb3d.cad.convert;

import java.nio.file.Path;

/**
 * This class allows converters to state their where, how and what information
 * is stored was converted.
 */
public class ConversionResult {
    /**
     * Represents to what level hierarchical information is stored in a file. 
     */
    public enum FileHierarchyPersistenceLevel {
        // order matters: lowest first
        NONE ("visb.conversion.alert.fileHierarchyPersistenceLevel.none"), // No hierarchy info is saved
        SOME ("visb.conversion.alert.fileHierarchyPersistenceLevel.some"), // Some hierarchy info is saved e.g. flat groups or objects
        MOST ("visb.conversion.alert.fileHierarchyPersistenceLevel.most"), // Most hierarchy info is saved e.g. nodes that can reference other nodes to some level
        ALL("visb.conversion.alert.fileHierarchyPersistenceLevel.all"); // All hierarchy info is saved e.g. nodes that can reference multiple other nodes 
        

        public final String warning;
        
        FileHierarchyPersistenceLevel(String string) {
            this.warning = string;
        }
    }

    public Path outputPath;
    public FileHierarchyPersistenceLevel persistenceLevel;

    public ConversionResult(Path outputPath, FileHierarchyPersistenceLevel persistenceLevel) {
        this.outputPath = outputPath;
        this.persistenceLevel = persistenceLevel;
    }
}
