package de.prob2.ui.config.nativecadimport;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Provider;

import de.prob.annotations.Home;
import de.prob.scripting.ExtractedModel;
import de.prob.scripting.ModelFactory;
import de.prob.statespace.StateSpace;

public class NativeCadFactory implements ModelFactory<NativeCadModel>  {
    public static final String NATIVE_CAD_FREECAD_EXTENSION = "FCStd";

    private static final Logger LOGGER = LoggerFactory.getLogger(NativeCadFactory.class);

    private final Provider<StateSpace> stateSpaceProvider;
    private final Provider<NativeCadModel> modelCreator;

    @Inject
    NativeCadFactory(Provider<StateSpace> stateSpaceProvider, Provider<NativeCadModel> modelCreator, @Home Path proBDirectory) {
        this.stateSpaceProvider = stateSpaceProvider;
        this.modelCreator = modelCreator;
    }

    @Override
    public ExtractedModel<NativeCadModel> extract(String modelPath) throws IOException {
        final File f = new File(modelPath);
        NativeCadModel nativeCadModel = modelCreator.get();
        try {
            nativeCadModel = new FreeCad2BParser().parseFromFile(f.getAbsolutePath());
            return new ExtractedModel<>(stateSpaceProvider, nativeCadModel);
        } catch (final IOException e) {
            throw new IOException();
        }
    }

}
