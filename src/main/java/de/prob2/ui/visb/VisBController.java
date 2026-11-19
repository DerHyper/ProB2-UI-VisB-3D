package de.prob2.ui.visb;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.MoreFiles;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;

import de.be4.classicalb.core.parser.analysis.prolog.RecursiveMachineLoader;
import de.be4.classicalb.core.parser.exceptions.BCompoundException;
import de.prob.animator.command.AbstractCommand;
import de.prob.animator.command.GetVisBDefaultSVGCommand;
import de.prob.animator.command.GetVisBSVGObjectsCommand;
import de.prob.animator.command.LoadVisBCommand;
import de.prob.animator.command.LoadVisBFromDefinitionsCommand;
import de.prob.animator.command.ReadVisBEventsHoversCommand;
import de.prob.animator.command.ReadVisBItemsCommand;
import de.prob.animator.command.ReadVisBSvgPathCommand;
import de.prob.animator.command.VisBPerformClickCommand;
import de.prob.animator.domainobjects.VisBClickMetaInfos;
import de.prob.animator.domainobjects.VisBItem;
import de.prob.statespace.StateSpace;
import de.prob.statespace.Trace;
import de.prob.statespace.Transition;
import de.prob2.ui.internal.executor.CliTaskExecutor;
import de.prob2.ui.internal.executor.FxThreadExecutor;
import de.prob2.ui.prob2fx.CurrentProject;
import de.prob2.ui.prob2fx.CurrentTrace;
import de.prob2.ui.simulation.interactive.UIInteractionHandler;
import de.prob2.ui.simulation.simulators.RealTimeSimulator;
import de.prob2.ui.visb.visb3d.cad.convert.CadConverterRegistry;
import de.prob2.ui.visb.visb3d.cad.convert.ConversionUtils;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

/**
 * The VisBController controls the {@link VisBView}.
 * Everything that can be done in Java only and uses interaction with ProB2-UI should be in here, not in the other classes.
 */
@Singleton
public final class VisBController {
	private static final Logger LOGGER = LoggerFactory.getLogger(VisBController.class);

	public static final Path NO_PATH = Paths.get("");

	/**
	 * Optional key in a VisB glue file that lists additional 3D files, e.g. <code>"models": ["Stem.stl", "Complex.stl"]</code>.
	 * ProB ignores this key, it is only read on the Java side.
	 */
	private static final String MODELS_KEY = "models";
	private static final ObjectMapper JSON_MAPPER = new ObjectMapper();

	private final CurrentProject currentProject;
	private final CurrentTrace currentTrace;
	private final CliTaskExecutor cliExecutor;
	private final FxThreadExecutor fxExecutor;
	private final Injector injector;

	private final ObjectProperty<Path> absoluteVisBPath;
	private final ObjectProperty<Path> relativeVisBPath;
	private final ObjectProperty<VisBVisualisation> visBVisualisation;
	private final ObservableMap<VisBItem.VisBItemKey, String> attributeValues;
	private final BooleanProperty executingEvent;

	@Inject
	public VisBController(Injector injector, CurrentProject currentProject, CurrentTrace currentTrace, CliTaskExecutor cliExecutor, FxThreadExecutor fxExecutor) {
		this.injector = injector;
		this.currentProject = currentProject;
		this.currentTrace = currentTrace;
		this.cliExecutor = cliExecutor;
		this.fxExecutor = fxExecutor;

		this.absoluteVisBPath = new SimpleObjectProperty<>(this, "absoluteVisBPath", null);
		this.relativeVisBPath = new SimpleObjectProperty<>(this, "relativeVisBPath", null);
		this.visBVisualisation = new SimpleObjectProperty<>(this, "visBVisualisation", null);
		this.attributeValues = FXCollections.observableHashMap();
		this.executingEvent = new SimpleBooleanProperty(this, "executingEvent", false);
	}

	public static Path resolveVisBPath(Path projectLocation, Path relativeVisBPath) {
		if (NO_PATH.equals(relativeVisBPath)) {
			return VisBController.NO_PATH;
		} else if (relativeVisBPath.isAbsolute()) {
			throw new IllegalArgumentException("Tried to resolve an already absolute VisB path: " + relativeVisBPath);
		} else {
			return projectLocation.resolve(relativeVisBPath);
		}
	}

	public static Path relativizeVisBPath(Path projectLocation, Path absoluteVisBPath) {
		if (NO_PATH.equals(absoluteVisBPath)) {
			return VisBController.NO_PATH;
		} else if (!absoluteVisBPath.isAbsolute()) {
			throw new IllegalArgumentException("Tried to relativize an already relative VisB path: " + absoluteVisBPath);
		} else {
			return projectLocation.relativize(absoluteVisBPath);
		}
	}

	public ReadOnlyObjectProperty<Path> absoluteVisBPathProperty() {
		return this.absoluteVisBPath;
	}

	public Path getAbsoluteVisBPath() {
		return this.absoluteVisBPathProperty().get();
	}

	public ReadOnlyObjectProperty<Path> relativeVisBPathProperty() {
		return this.relativeVisBPath;
	}

	public Path getRelativeVisBPath() {
		return this.relativeVisBPathProperty().get();
	}

	/**
	 * Hide the currently loaded visualisation, but remember its path (if any) so that the user can reload it.
	 * This should be called after visualisation errors that can possibly be fixed by a reload.
	 */
	public void hideVisualisation() {
		this.visBVisualisation.set(null);
	}

	/**
	 * Close any currently loaded visualisation.
	 * Unlike {@link #hideVisualisation()}, this also unsets the visualisation file path,
	 * so the user cannot reload the visualisation unless they explicitly re-select it.
	 */
	public void closeVisualisation() {
		this.hideVisualisation();
		this.absoluteVisBPath.set(null);
		this.relativeVisBPath.set(null);
	}

	public CompletableFuture<VisBVisualisation> loadFromAbsolutePath(Path path) {
		Objects.requireNonNull(path, "path");
		Path relativePath = relativizeVisBPath(currentProject.getLocation(), path);
		this.absoluteVisBPath.set(path);
		this.relativeVisBPath.set(relativePath);
		return this.reloadVisualisation();
	}

	public CompletableFuture<VisBVisualisation> loadFromRelativePath(Path path) {
		Objects.requireNonNull(path, "path");
		Path absolutePath = resolveVisBPath(currentProject.getLocation(), path);
		this.absoluteVisBPath.set(absolutePath);
		this.relativeVisBPath.set(path);
		return this.reloadVisualisation();
	}

	public ReadOnlyObjectProperty<VisBVisualisation> visBVisualisationProperty() {
		return this.visBVisualisation;
	}

	public VisBVisualisation getVisBVisualisation() {
		return this.visBVisualisationProperty().get();
	}

	public ObservableMap<VisBItem.VisBItemKey, String> getAttributeValues() {
		return this.attributeValues;
	}

	public ReadOnlyBooleanProperty executingEventProperty() {
		return this.executingEvent;
	}

	public boolean isExecutingEvent() {
		return this.executingEventProperty().get();
	}

	/**
	 * This method is used by the {@link VisBView} to execute an event, whenever an svg item was clicked. Only one event per svg item is allowed.
	 * @param id of the svg item that was clicked
	 */
	public CompletableFuture<Trace> executeEvent(String id, VisBClickMetaInfos metaInfos) {
		if (this.isExecutingEvent()) {
			throw new IllegalStateException("Cannot execute an event while another event is already being executed");
		}

		Trace trace = currentTrace.get();
		LOGGER.debug("Finding event for id: {}", id);

		this.executingEvent.set(true);
		return cliExecutor.submit(() -> {
			VisBPerformClickCommand performClickCommand = new VisBPerformClickCommand(trace.getStateSpace(), id, metaInfos, trace.getCurrentState().getId());
			trace.getStateSpace().execute(performClickCommand);
			List<Transition> transitions = performClickCommand.getTransitions();

			if (transitions.isEmpty()) {
				LOGGER.debug("No events found for id: {}", id);
				return null;
			} else {
				LOGGER.debug("Executing event for id: {}", id);
				Trace newTrace = trace.addTransitions(transitions);
				LOGGER.debug("Finished executed event for id: {}", id);
				currentTrace.set(newTrace);
				RealTimeSimulator realTimeSimulator = injector.getInstance(RealTimeSimulator.class);
				for (Transition transition : transitions) {
					UIInteractionHandler uiInteraction = injector.getInstance(UIInteractionHandler.class);
					uiInteraction.addUserInteraction(realTimeSimulator, transition);
				}
				return newTrace;
			}
		}).whenCompleteAsync((res, exc) -> this.executingEvent.set(false), fxExecutor);
	}

	CompletableFuture<Trace> executeBeforeInitialisation() {
		if (this.isExecutingEvent()) {
			throw new IllegalStateException("Cannot perform initialisation while another event is already being executed");
		}

		Trace trace = currentTrace.get();
		Set<Transition> nextTransitions = trace.getNextTransitions();
		if (nextTransitions.size() != 1) {
			throw new IllegalStateException("Cannot perform non-deterministic initialization from VisB");
		}

		this.executingEvent.set(true);
		return cliExecutor.submit(() -> {
			Transition transition = nextTransitions.iterator().next();
			Trace newTrace = trace.add(transition);
			currentTrace.set(newTrace);
			RealTimeSimulator realTimeSimulator = injector.getInstance(RealTimeSimulator.class);
			UIInteractionHandler uiInteraction = injector.getInstance(UIInteractionHandler.class);
			uiInteraction.addUserInteraction(realTimeSimulator, transition);
			return newTrace;
		}).whenCompleteAsync((res, exc) -> this.executingEvent.set(false), fxExecutor);
	}

	/**
	 * Reads the optional {@link #MODELS_KEY} array from a VisB .json file.
	 * @return the listed paths (as written in the file), or an empty list if there is no such entry
	 */
	private static List<String> readModelNames(Path visBPath) throws IOException {
		JsonNode models = JSON_MAPPER.readTree(visBPath.toFile()).get(MODELS_KEY);
		if (models == null) {
			return new ArrayList<>();
		}
		if (!models.isArray()) {
			throw new IOException("\"" + MODELS_KEY + "\" must be an array of file paths in " + visBPath);
		}
		List<String> result = new ArrayList<>();
		for (JsonNode entry : models) {
			if (!entry.isTextual() || entry.asText().isBlank()) {
				throw new IOException(
						"Every entry of \"" + MODELS_KEY + "\" must be a non-empty string in " + visBPath);
			}
			result.add(entry.asText().strip());
		}
		return result;
	}
	
	/**
	 * Resolves a path from the model entry of a VisB file (relative to the VisB file) 
	 * and checks that it is a non-empty regular file.
	 */
	private static Path modelNameToPath(Path visBPath, String modelName) throws IOException {
		Path path = Paths.get(visBPath.getParent().toString(), modelName);
		if (!Files.isRegularFile(path) || Files.size(path) <= 0) {
			throw new IOException("Given visualisation path is not a non-empty regular file: " + path);
		}
		return path;
	}

	/**
	 * Reads the names from the model entry in the VisB glue file and resolves the paths. 
	 */
	private static Path[] readModelPaths(Path visBPath) throws IOException {
		List<String> modelNames = readModelNames(visBPath);
		Path[] paths = new Path[modelNames.size()];
		for (int i = 0; i < paths.length; i++) {
			paths[i] = modelNameToPath(visBPath, modelNames.get(i));
		}
		return paths;
	}

	/**
	 * This method takes a VisB file as input and returns a {@link VisBVisualisation} object.
	 * @param stateSpace the ProB animator instance using which to load the VisB file
	 * @param visBPath path to the .json or .def file for the VisB visualisation
	 * @return VisBVisualisation object
	 */
	private static VisBVisualisation constructVisualisationFromFile(StateSpace stateSpace, Path visBPath) throws BCompoundException, IOException {
		if (!visBPath.equals(NO_PATH)) {
			visBPath = visBPath.toRealPath();
			if (!Files.isRegularFile(visBPath)) {
				throw new IOException("Given VisB file is not a regular file: " + visBPath);
			}
		}

		AbstractCommand loadCmd;
		if (visBPath.equals(NO_PATH)) {
			// Visualization defined in B definitions in the machine itself.
			// This is represented by passing an empty file path to probcli.
			loadCmd = new LoadVisBCommand("");
		} else if ("def".equals(MoreFiles.getFileExtension(visBPath))) {
			// Visualization defined in a separate B definition file.
			var rml = RecursiveMachineLoader.loadFile(visBPath.toFile());
			loadCmd = new LoadVisBFromDefinitionsCommand(visBPath.toFile(), rml);
		} else {
			// Visualization defined in a traditional VisB .json file.
			loadCmd = new LoadVisBCommand(visBPath.toString());
		}

		var svgCmd = new ReadVisBSvgPathCommand();
		var itemsCmd = new ReadVisBItemsCommand();
		var eventsCmd = new ReadVisBEventsHoversCommand();
		var svgObjectsCmd = new GetVisBSVGObjectsCommand();
		var defaultSVGCmd = new GetVisBDefaultSVGCommand();
		if(stateSpace == null) {
			return null;
		}
		stateSpace.execute(loadCmd, svgCmd, itemsCmd, eventsCmd, svgObjectsCmd, defaultSVGCmd);

		String svgPathString = svgCmd.getSvgPath();
		final Path svgPath;
		final String svgContent;
		if (svgPathString.isEmpty()) {
			svgPath = NO_PATH;
			svgContent = defaultSVGCmd.getSVGFileContents();
		} else {
			if (visBPath.equals(NO_PATH)) {
				svgPath = Paths.get(svgPathString).toRealPath();
			} else {
				svgPath = visBPath.resolveSibling(svgPathString).toRealPath();
			}
			if (!Files.isRegularFile(svgPath) || Files.size(svgPath) <= 0) {
				throw new IOException("Given svg path is not a non-empty regular file: " + svgPath);
			}
			String fileFormat = ConversionUtils.extractExtension(svgPath);
			if (fileFormat.equals("glb") || new CadConverterRegistry().getSupportedFileExtensions().contains(fileFormat)) {
				// GLB file specified, no SVG content to read
				Path[] models = readModelPaths(visBPath);
				return new VisB3DVisualisation(svgPath, models, itemsCmd.getItems(), eventsCmd.getEvents(), svgObjectsCmd.getSvgObjects());
			} else {
				svgContent = Files.readString(svgPath);
			}
		}
		
		return new VisBVisualisation(svgPath, svgContent, itemsCmd.getItems(), eventsCmd.getEvents(), svgObjectsCmd.getSvgObjects());
	}

	CompletableFuture<VisBVisualisation> reloadVisualisation() {
		// Hide the previous visualisation before loading a new one.
		// This ensures that listeners on visBVisualisation are always called
		// and prevents an old visualisation remaining visible after an error.
		this.hideVisualisation();

		Path visBPath = this.getAbsoluteVisBPath();
		if (visBPath == null) {
			return CompletableFuture.completedFuture(null);
		}

		StateSpace stateSpace = currentTrace.getStateSpace();
		return cliExecutor.submit(() -> constructVisualisationFromFile(stateSpace, visBPath)).thenApplyAsync(vis -> {
			this.visBVisualisation.set(vis);
			return vis;
		}, fxExecutor);
	}
}