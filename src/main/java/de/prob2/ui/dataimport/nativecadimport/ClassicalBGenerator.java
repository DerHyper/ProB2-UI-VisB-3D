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

    /**
     * Parses the internal cad data into a classical B machine file string.
     */
    public static String parseIntoBMachine(ParseData joints) throws IOException
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
    private static void constructContent(
            BMachineContent content,
            CadJoint joint,
            ConstrainedParameter parameter) {
        String parameterPrefix = joint.getName() + "_" + parameter.getName();

        constructConstants(content, parameter, parameterPrefix);
    }

    /** 
        EnableMin and enableMax vars are not included in the final machine, because 
        they do not describe a changeable value that controls how a joint moves, 
        but a static configuration values that never changes.
        TODO: Check if there is any case where these constants are needed.
        */
    private static void constructConstants(BMachineContent content, ConstrainedParameter parameter,
            String parameterPrefix) {
        // current value
        String initialConstantName = parameterPrefix.toUpperCase() + SUFFIX_INITIAL;
        content.concreteConstants.add(initialConstantName);

        // min
        if (parameter.getEnableMin() == null
                || parameter.getMin() == null
                || parameter.getEnableMin() == false) {
            // do nothing
        } else {
            String constantName = parameterPrefix.toUpperCase() + SUFFIX_MIN;
            content.concreteConstants.add(constantName);
        }

        // max
        if (parameter.getEnableMax() == null
                || parameter.getMax() == null
                || parameter.getEnableMax() == false) {
            // do nothing
        } else {
            String constantName = parameterPrefix.toUpperCase() + SUFFIX_MAX;
            content.concreteConstants.add(constantName);
        }

        
    }

}
