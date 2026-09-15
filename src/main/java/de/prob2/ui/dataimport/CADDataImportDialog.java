package de.prob2.ui.dataimport;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import de.prob2.ui.config.FileChooserManager;
import de.prob2.ui.internal.I18n;
import de.prob2.ui.internal.StageManager;
import de.prob2.ui.project.ProjectManager;
import javafx.fxml.FXML;

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
        this.version.setText("LibraryJSON");
    }
    
}
