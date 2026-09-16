package de.prob2.ui.dataimport;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import de.prob2.ui.config.FileChooserManager;
import de.prob2.ui.dataimport.nativecadimport.NativeCadParserFacade;
import de.prob2.ui.internal.I18n;
import de.prob2.ui.internal.StageManager;
import de.prob2.ui.project.ProjectManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;

@Singleton
public final class CADDataImportDialog extends DataImportDialog {
    
    private final ProjectManager projectManager;
    
    @Inject
    public CADDataImportDialog(FileChooserManager fileChooserManager, I18n i18n, StageManager stageManager,
            ProjectManager projectManager) {
        super(fileChooserManager, i18n, stageManager, ImportType.CAD);
        this.projectManager = projectManager;
        stageManager.loadFXML(this, "data_import_dialog.fxml");
    }
    
    @FXML
    @Override
    public void initialize() {
        super.initialize();
        this.version.setText("LibraryCAD");
    }
    
    
    @Override
    void importImplementation() {
        try {
            String importMch = NativeCadParserFacade.parseToClassicalB(file.get().toPath());
            Path generated = directory.get().resolve(machineName.get() + ".mch");
            Files.write(generated, importMch.getBytes());
            Platform.runLater(() -> {
                projectManager.openFile(generated);
                this.close();
            });
        } catch (IOException ex) {
            Platform.runLater(() -> {
                Alert alert = stageManager.makeExceptionAlert(ex, "dataimport.dialog.error.failed",
                        "dataimport.dialog.error.failed.content");
                alert.initOwner(this);
                alert.showAndWait();
            });
        }
    }
    
        @FXML
        @Override
        public void selectFile() {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle(i18n.translate("dataimport.dialog.fileChooser.title"));
            for (String ext : NativeCadParserFacade.getSupportedFileExtensions())
            {
                fileChooser.getExtensionFilters().add(fileChooserManager.getExtensionFilter("common.fileChooser.fileTypes." + ext, ext));
            }
            Path path = fileChooserManager.showOpenFileChooser(fileChooser, FileChooserManager.Kind.DATA_IMPORT, stageManager.getCurrent());
            if (path != null) {
                file.set(path.toFile());
            }
        }
}
