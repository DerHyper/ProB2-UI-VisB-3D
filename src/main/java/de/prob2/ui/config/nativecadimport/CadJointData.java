package de.prob2.ui.config.nativecadimport;

import java.util.List;

/**
 * Represents the joint data parsed from a CAD file.
 */
public class CadJointData {

    private List<CadJoint> cadJoints;

    public CadJointData(List<CadJoint> cadJoints) {
        this.cadJoints = cadJoints;
    }

    public List<CadJoint> getCadJoints() {
        return cadJoints;
    }

    public void setCadJoints(List<CadJoint> cadJoints) {
        this.cadJoints = cadJoints;
    }
}
