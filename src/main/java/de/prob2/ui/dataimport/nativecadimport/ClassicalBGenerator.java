package de.prob2.ui.dataimport.nativecadimport;

import java.io.IOException;
import java.util.List;

public class ClassicalBGenerator {
    /**
     * Intermediate representation of a b machine that contains all
     * the parsed data in a format that is easier to then print into
     * the final b machine.
     */
    private static class BMachineContent {
        public List<String> concreteConstants;
        public List<String> abstractVariables;
        public List<String> properties;
        public List<String> invariants;
        public List<String> assertions;
        public List<String> initialisations;
        public List<String> operations;
    }

    private static final String SUFFIX_INITIAL = "_INITIAL";
    private static final String SUFFIX_MIN = "_MIN";
    private static final String SUFFIX_MAX = "_MAX";
    private static final String SUFFIX_CURRENT = "_current";

    private String constantNameInitial;
    private String constantNameMin;
    private String constantNameMax;
    private String variableNameCurrent;
    private boolean printMin;
    private boolean printMax;

    /**
     * Parses the internal cad data into a classical B machine file string.
     */
    public String parseIntoBMachine(ParseData joints) throws IOException
    {
        StringBuilder sb = new StringBuilder();
        BMachineContent content = new BMachineContent();

        for (CadJoint joint : joints.getCadJoints()) {
            for (ConstrainedParameter parameter : joint.getParameters()) {
                constructContent(content, joint, parameter);
            }
        }
        return sb.toString();
    }

    /**
     * Populates the BMachineContent with content that disc.
     * @param content
     * @param joint
     * @param parameter
     */
    private void constructContent(
            BMachineContent content,
            CadJoint joint,
            ConstrainedParameter parameter) {
        String parameterPrefix = joint.getName() + "_" + parameter.getName();
        constantNameInitial = parameterPrefix.toUpperCase() + SUFFIX_INITIAL;
        constantNameMin = parameterPrefix.toUpperCase() + SUFFIX_MIN;
        constantNameMax = parameterPrefix.toUpperCase() + SUFFIX_MAX;
        variableNameCurrent = parameterPrefix + SUFFIX_CURRENT;
        printMin = !(
            parameter.getEnableMin() == null 
            || parameter.getMin() == null 
            || parameter.getEnableMin() == false);
        printMax = !(
            parameter.getEnableMax() == null
            || parameter.getMax() == null
            || parameter.getEnableMax() == false);
        
        constructConstants(content, parameter, parameterPrefix);
        constructVariables(content, parameter, parameterPrefix);
    }

    /** 
        EnableMin and enableMax vars are not included in the final machine, because 
        they do not describe a changeable value that controls how a joint moves, 
        but a static configuration values that never changes.
        TODO: Check if there is any case where these constants are needed.
        */
    private void constructConstants(BMachineContent content, ConstrainedParameter parameter,
            String parameterPrefix) {
        content.concreteConstants.add(constantNameInitial);

        if (printMin) {
            content.concreteConstants.add(constantNameMin);
        }

        if (printMax) {
            content.concreteConstants.add(constantNameMax);
        }
    }
    
    private void constructVariables(
            BMachineContent content,
            ConstrainedParameter parameter,
            String parameterPrefix) {
        content.abstractVariables.add(variableNameCurrent);
    }
}
