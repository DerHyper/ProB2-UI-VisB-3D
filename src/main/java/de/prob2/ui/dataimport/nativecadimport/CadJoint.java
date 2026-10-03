package de.prob2.ui.dataimport.nativecadimport;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

/**
 * Internal representation of a joint from a native CAD format.
 * Variables can be null if not used by a CAD format.
 */
public class CadJoint {

    private String name;
    private String type;
    private List<ConstrainedParameter> parameters;

    public CadJoint() {
        this.name = null;
        this.type = null;
        this.parameters = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<ConstrainedParameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<ConstrainedParameter> parameters) {
        this.parameters = parameters;
    }

    public void addParameter(ConstrainedParameter parameter) {
        parameters.add(parameter);
    }

    @Override 
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}

