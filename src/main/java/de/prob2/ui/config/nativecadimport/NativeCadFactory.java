package de.prob2.ui.config.nativecadimport;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.MoreFiles;
import com.google.inject.Inject;
import com.google.inject.Provider;

import de.prob.annotations.Home;
import de.prob.model.classicalb.ClassicalBModel;
import de.prob.scripting.ClassicalBFactory;
import de.prob.scripting.ExtractedModel;
import de.prob.scripting.FactoryProvider;
import de.prob.scripting.ModelFactory;
import de.prob.statespace.StateSpace;

public class NativeCadFactory implements ModelFactory<ClassicalBModel> {

    private static final Map<String, NativeCad2BParser> EXTENSION_TO_PARSER;
    static {
		final Map<String, NativeCad2BParser> map = new HashMap<>();
        map.put(FreeCad2BParser.NATIVE_CAD_FREECAD_EXTENSION, new FreeCad2BParser());

        EXTENSION_TO_PARSER = Map.copyOf(map);
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(NativeCadFactory.class);
	private final Provider<StateSpace> stateSpaceProvider;
	private final Provider<ClassicalBModel> modelCreator;

    @Inject
    NativeCadFactory(Provider<StateSpace> stateSpaceProvider, Provider<ClassicalBModel> modelCreator, @Home Path proBDirectory) {
        this.stateSpaceProvider = stateSpaceProvider;
        this.modelCreator = modelCreator;
    }

    @Override
    public ExtractedModel<ClassicalBModel> extract(String modelPath) throws IOException {
        final File f = new File(modelPath);
        String extension = MoreFiles.getFileExtension(f.toPath());
        ClassicalBFactory bFactory = (ClassicalBFactory) FactoryProvider
                .factoryClassFromExtension(ClassicalBFactory.CLASSICAL_B_MACHINE_EXTENSION)
                .cast(ClassicalBFactory.class);
        
        NativeCad2BParser parser = EXTENSION_TO_PARSER.get(extension);
        
        if (parser == null) {
            throw new IllegalArgumentException(
                "Kein Parser für Dateiendung: " + extension
            );
        }

        CadJointData cadJointData = parser.parseFromFile(modelPath);
        File bMachineFile = parser.parseIntoBMachine(cadJointData);
        
        return bFactory.extract(bMachineFile.toPath().toString());
    }
}
