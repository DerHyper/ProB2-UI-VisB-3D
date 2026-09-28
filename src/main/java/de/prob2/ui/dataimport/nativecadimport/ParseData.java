package de.prob2.ui.dataimport.nativecadimport;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the joint data parsed from a CAD file.
 */
public class ParseData {

    private List<CadJoint> cadJoints;
    private String machineName;

    public String getMachineName() {
        return machineName;
    }

    public void setMachineName(String machineName) {
        this.machineName = machineName;
    }

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

    @Override 
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getName()).append("{\n");
        for (CadJoint joint : cadJoints) {
            sb.append(joint.toString()).append("\n");
        }
        sb.append("}");
        return sb.toString();
    }
}
