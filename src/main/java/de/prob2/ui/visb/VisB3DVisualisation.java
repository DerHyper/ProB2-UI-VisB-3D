package de.prob2.ui.visb;

import java.nio.file.Path;
import java.util.List;

import de.prob.animator.domainobjects.VisBEvent;
import de.prob.animator.domainobjects.VisBItem;
import de.prob.animator.domainobjects.VisBSVGObject;

/**
 * The VisB3DVisualisation Object is a subclass of VisBVisualisation specifically for 3D visualizations using GLB files.
 */
public class VisB3DVisualisation extends VisBVisualisation {
    public VisB3DVisualisation(Path svgPath, List<VisBItem> items, List<VisBEvent> events,
            List<VisBSVGObject> svgObjects) {
        super(svgPath, "", items, events, svgObjects);
    }
}
