package de.prob2.ui.config.nativecadimport;

import java.util.List;

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
