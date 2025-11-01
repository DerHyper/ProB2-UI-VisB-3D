package de.prob2.ui.visb.visb3d;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.prob.animator.domainobjects.VisBItem.VisBItemKey;

/**
 * Data Transfer Object for VisB3D visualization data.
 */
public class VisB3DDto {
    public List<VisB3DObjectDto> objectStates;

    public VisB3DDto() {
        this.objectStates = new ArrayList<>();
    }

    public void addStateChanges(Map<VisBItemKey, String> stateChanges) {
        for (Map.Entry<VisBItemKey, String> stateChange : stateChanges.entrySet()) {
            addStateChange(
                stateChange.getKey().getId(), 
                stateChange.getKey().getAttribute(),
                stateChange.getValue()
            );
        }
    }

    public void addStateChange(String id, String attribute, String value){
        // Check for new entries
        if (!contains(id)) {
            objectStates.add(new VisB3DObjectDto(id));
        }

        // Add attribute to the corresponding object
        VisB3DObjectDto state = getObjectById(id);
        switch (attribute) {
            case "material-color":
                state.material.color = value;
                break;
            case "material-metallic":
                state.material.metallic = Float.valueOf(value);
            case "material-smoothness":
                state.material.smoothness = Float.valueOf(value);
                break;
            case "is_active":
                state.isActive = Boolean.valueOf(value);
                break;
            case "x":
                state.position.x = Float.valueOf(value);
                break;
            case "y":
                state.position.y = Float.valueOf(value);
                break;
            case "z":
                state.position.z = Float.valueOf(value);
                break;
            case "rotate_x":
                state.rotation.x = Float.valueOf(value);
                break;
            case "rotate_y":
                state.rotation.y = Float.valueOf(value);
                break;
            case "rotate_z":
                state.rotation.z = Float.valueOf(value);
                break;
            case "scale_x":
                state.scale.x = Float.valueOf(value);
                break;
            case "scale_y":
                state.scale.y = Float.valueOf(value);
                break;
            case "scale_z":
                state.scale.z = Float.valueOf(value);
                break;
            case "scale":
                state.scale = new Vector3Dto(Float.parseFloat(value), Float.parseFloat(value), Float.parseFloat(value));
            default:
                throw new IllegalArgumentException("Unknown attribute in VisB3D glue file: " + attribute);
        }
    }

    public boolean contains(String id) {
        for (VisB3DObjectDto obj : objectStates) {
            if (obj.name.equals(id)) {
                return true;
            }
        }
        return false;
    }

    public VisB3DObjectDto getObjectById(String id) {
        for (VisB3DObjectDto obj : objectStates) {
            if (obj.name.equals(id)) {
                return obj;
            }
        }
        return null;
    }
}
