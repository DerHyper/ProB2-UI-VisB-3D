package de.prob2.ui.dataimport.nativecadimport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javafx.beans.binding.StringBinding;

public class ClassicalBGenerator {
    /**
     * Intermediate representation of a b machine that contains all
     * the parsed data in a format that is easier to then print into
     * the final b machine.
     */
    private class BMachineContent {
        public List<String> concreteConstants = new ArrayList<>();
        public List<String> abstractVariables = new ArrayList<>();
        public List<String> properties = new ArrayList<>();
        public List<String> invariants = new ArrayList<>();
        public List<String> assertions = new ArrayList<>();
        public List<String> initialisations = new ArrayList<>();
        public List<BMachineOperation> operations = new ArrayList<>();

        public class BMachineOperation {
            public String head;
            public List<String> guards = new ArrayList<>();
            public List<String> effect = new ArrayList<>();
        }
    }

    private static final String SUFFIX_INITIAL = "_INITIAL";
    private static final String SUFFIX_MIN = "_MIN";
    private static final String SUFFIX_MAX = "_MAX";
    private static final String SUFFIX_CURRENT = "_current";
    private static final String OPERATIONS_VAR_NAME = "value";

    private String currentConstantNameInitial;
    private String currentConstantNameMin;
    private String currentConstantNameMax;
    private String currentVariableName;
    private boolean printMin;
    private boolean printMax;

    /**
     * Parses the internal cad data into a classical B machine file string.
     */
    public String parseIntoBMachine(ParseData joints) throws IOException
    {
        BMachineContent content = new BMachineContent();
        for (CadJoint joint : joints.getCadJoints()) {
            for (ConstrainedParameter parameter : joint.getParameters()) {
                constructContent(content, joint, parameter);
            }
        }

        return "";
    }

    /**
     * Populates the BMachineContent with content that describes the individual lines inside of each 
     * machine section, without any formatting.
     * @param content
     * @param joint
     * @param parameter
     */
    private void constructContent(
            BMachineContent content,
            CadJoint joint,
            ConstrainedParameter parameter) {
        String parameterPrefix = joint.getName() + "_" + parameter.getName();
        currentConstantNameInitial = parameterPrefix.toUpperCase() + SUFFIX_INITIAL;
        currentConstantNameMin = parameterPrefix.toUpperCase() + SUFFIX_MIN;
        currentConstantNameMax = parameterPrefix.toUpperCase() + SUFFIX_MAX;
        currentVariableName = parameterPrefix + SUFFIX_CURRENT;
        printMin = !(
            parameter.getEnableMin() == null 
            || parameter.getMin() == null 
            || parameter.getEnableMin() == false);
        printMax = !(
            parameter.getEnableMax() == null
            || parameter.getMax() == null
            || parameter.getEnableMax() == false);
        
        constructConstants(content);
        constructVariables(content);
        constructProperties(content, parameter);
        constructInvariants(content);
        constructAssertions(content);
        constructInitialisation(content);
        constructOperations(content);
    }

    /** 
     * Populates the machines constants.
    */
    private void constructConstants(BMachineContent content) {
        content.concreteConstants.add(currentConstantNameInitial);

        // EnableMin and enableMax vars are not included in the final machine, because 
        // they do not describe a changeable value that controls how a joint moves, 
        // but a static configuration values that never changes.
        // TODO: Check if there is any case where these constants are needed.
        if (printMin) {
            content.concreteConstants.add(currentConstantNameMin);
        }

        if (printMax) {
            content.concreteConstants.add(currentConstantNameMax);
        }
    }
    
    /**
     * Populates the machines variables.
     * @param content
     */
    private void constructVariables(BMachineContent content) {
        content.abstractVariables.add(currentVariableName);
    }
    
    /**
     * Populates the machines properties with a constants type and value
     */
    private void constructProperties(BMachineContent content, ConstrainedParameter parameter) {
        constructTypeProperty(content, parameter, currentConstantNameInitial);
        constructValueProperty(content, parameter, currentConstantNameInitial, parameter.getCurrentValue());

        if (printMin) {
            constructTypeProperty(content, parameter, currentConstantNameMin);
            constructValueProperty(content, parameter, currentConstantNameMin, parameter.getMin());
        }

        if (printMax) {
            constructTypeProperty(content, parameter, currentConstantNameMax);
            constructValueProperty(content, parameter, currentConstantNameMax, parameter.getMax());
        }
    }

    private void constructTypeProperty(BMachineContent content, ConstrainedParameter parameter, String constantName) {
        StringBuilder sb = new StringBuilder();
        sb.append(constantName);
        sb.append(" : ");
        switch (parameter.getType()) {
            case FLOAT -> sb.append("FLOAT");
            case BOOL -> sb.append("BOOL");
            case INTEGER -> sb.append("INTEGER");
            default -> sb.append("UNKNOWN_TYPE"); // This should never happen
        }

        content.properties.add(sb.toString());
    }

    private void constructValueProperty(BMachineContent content, ConstrainedParameter parameter, String constantName,
            Object currentValue) {
        StringBuilder sb = new StringBuilder();
        sb.append(constantName);
        sb.append(" = ");
        switch (parameter.getType()) {
            case FLOAT -> sb.append(Float.toString((Float) currentValue));
            case BOOL -> sb.append(Boolean.toString((Boolean) currentValue));
            case INTEGER -> sb.append(Integer.toString((Integer) currentValue));
            default -> sb.append("UNKNOWN_TYPE"); // This should never happen
        }

        content.properties.add(sb.toString());
    }
    
    private void constructAssertions(BMachineContent content) {
        // Currently no Assertions are created
    }
    
    private void constructInvariants(BMachineContent content) {
        if (printMin) {
            StringBuilder sb = new StringBuilder();
            sb.append(currentVariableName);
            sb.append(" >= ");
            sb.append(currentConstantNameMin);
            content.invariants.add(sb.toString());
        }

        if (printMax) {
            StringBuilder sb = new StringBuilder();
            sb.append(currentVariableName);
            sb.append(" <= ");
            sb.append(currentConstantNameMax);
            content.invariants.add(sb.toString());
        }
    }

    private void constructInitialisation(BMachineContent content) {
        StringBuilder sb = new StringBuilder();
        sb.append(currentVariableName);
        sb.append(" := ");
        sb.append(currentConstantNameInitial);
        content.initialisations.add(sb.toString());
    }
    
    private void constructOperations(BMachineContent content) {
        BMachineContent.BMachineOperation operation = content.new BMachineOperation();

        // Head
        StringBuilder headBuilder = new StringBuilder();
        headBuilder.append("set_");
        headBuilder.append(currentVariableName);
        headBuilder.append("(");
        headBuilder.append(OPERATIONS_VAR_NAME);
        headBuilder.append(")\n");
        operation.head = headBuilder.toString();

        // Guards
        List<String> guards = new ArrayList<>();
        if (printMin) {
            StringBuilder selectRulesBuilder = new StringBuilder();
            selectRulesBuilder.append(OPERATIONS_VAR_NAME);
            selectRulesBuilder.append(" >= ");
            selectRulesBuilder.append(currentConstantNameMin);
            guards.add(selectRulesBuilder.toString());
        }
        if (printMax) {
            StringBuilder selectRulesBuilder = new StringBuilder();
            selectRulesBuilder.append(currentVariableName);
            selectRulesBuilder.append(" <= ");
            selectRulesBuilder.append(currentConstantNameMax);
            guards.add(selectRulesBuilder.toString());
        }
        operation.guards = guards;

        // Effects
        StringBuilder effectBuilder = new StringBuilder();
        effectBuilder.append(currentVariableName);
        effectBuilder.append(" := ");
        effectBuilder.append(OPERATIONS_VAR_NAME);
        operation.effect.add(effectBuilder.toString());

        content.operations.add(operation);
    }
}
