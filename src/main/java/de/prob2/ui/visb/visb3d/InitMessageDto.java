package de.prob2.ui.visb.visb3d;

import java.util.List;

/**
 * A data transfer object that carries information used for initializing
 * the Unity scene. 
 */
public class InitMessageDto {
    public String mainModelLocation;
    public List<String> modelLocations;
    
    public InitMessageDto(String mainModelLocation, List<String> modelLocations) {
        this.mainModelLocation = mainModelLocation;
        this.modelLocations = modelLocations;
    }
}
