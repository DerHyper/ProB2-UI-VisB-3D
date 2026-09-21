package de.prob2.ui.dataimport.nativecadimport;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the joint data parsed from a CAD file.
 */
public class ParseData {

    private List<CadJoint> cadJoints;

    public ParseData() {
        this.cadJoints = new ArrayList<>();
    }

    public ParseData(List<CadJoint> cadJoints) {
        this.cadJoints = cadJoints;
    }

    public List<CadJoint> getCadJoints() {
        return cadJoints;
    }

    public void setCadJoints(List<CadJoint> cadJoints) {
        this.cadJoints = cadJoints;
    }

    public void addCadJoint(CadJoint cadJoint)
    {
        cadJoints.add(cadJoint);
    }
}
