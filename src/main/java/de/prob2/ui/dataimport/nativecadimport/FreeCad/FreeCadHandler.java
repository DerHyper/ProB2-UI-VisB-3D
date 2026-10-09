package de.prob2.ui.dataimport.nativecadimport.FreeCad;

import java.util.HashMap;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import de.prob2.ui.dataimport.nativecadimport.CadJoint;
import de.prob2.ui.dataimport.nativecadimport.ConstrainedParameter;
import de.prob2.ui.dataimport.nativecadimport.ConstrainedParameter.BType;
import de.prob2.ui.dataimport.nativecadimport.ParseData;

public class FreeCadHandler extends DefaultHandler {
    // Elements
    private static final String OBJECT = "Object";
    private static final String EXTENTIONS = "Extensions";
    private static final String EXTENTION = "Extension";
    private static final String PROPERTIES = "Properties";
    private static final String PROPERTY = "Property";
    private static final String FLOAT = "Float";
    private static final String BOOL = "Bool";
    private static final String INTEGER = "Integer";
    private static final String CUSTOMENUMLIST = "CustomEnumList";
    private static final String ENUM = "Enum";
    private static final String PROPERTYPLACEMENT = "PropertyPlacement";
    private static final String XLINK = "XLink";
    private static final String SUB = "Sub";

    // Attributes
    private static final String NAME = "name";
    private static final String GROUP = "group";
    private static final String VALUE = "value";

    // FCStd Joint variables (raw XML property names)
    private static final String ANGLE = "Angle";
    private static final String ANGLEMAX = "AngleMax";
    private static final String ANGLEMIN = "AngleMin";
    private static final String DISTANCE = "Distance";
    private static final String DISTANCE2 = "Distance2";
    private static final String LENGTHMIN = "LengthMin";
    private static final String LENGTHMAX = "LengthMax";
    private static final String ENABLEANGLEMAX = "EnableAngleMax";
    private static final String ENABLEANGLEMIN = "EnableAngleMin";
    private static final String ENABLELENGTHMAX = "EnableLengthMax";
    private static final String ENABLELENGTHMIN = "EnableLengthMin";

    // Base names of the resulting ConstrainedParameter instances
    private static final String PARAM_ANGLE = "Angle";
    private static final String PARAM_DISTANCE = "Distance";
    private static final String PARAM_DISTANCE2 = "Distance2";

    // Local variables
    private ParseData parseData = new ParseData();
    private CadJoint currentJoint;
    private StringBuilder elementValue;
    private String currentProperty;
    private Map<String, ConstrainedParameter> parametersInProgress; // Parameters currently being built for the current Object

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (elementValue == null) {
            elementValue = new StringBuilder();
        } else {
            elementValue.append(ch, start, length);
        }
    }

    @Override
    public void startDocument() throws SAXException {
        parseData = new ParseData();
    }

    @Override
    public void startElement(String uri, String lName, String qName, Attributes attr) throws SAXException {
        switch (qName) {
            case OBJECT -> handleStartObject(attr);
            case PROPERTY -> handleStartProperty(attr);
            case FLOAT -> handleFloat(attr);
            case BOOL -> handleBool(attr);
            default -> {}
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        switch (qName) {
            case OBJECT -> handleEndObject();
            default -> {}
        }
    }

    private void handleStartObject(Attributes attr) {
        currentJoint = new CadJoint();
        String name = attr.getValue(NAME);
        currentJoint.setName(name);
        parametersInProgress = new HashMap<>();
    }

    private void handleStartProperty(Attributes attr) {
        currentProperty = attr.getValue(NAME);
    }

    private void handleFloat(Attributes attr) {
        Float value = Float.valueOf(attr.getValue(VALUE));
        switch (currentProperty) {
            case ANGLE       -> getOrCreate(PARAM_ANGLE, BType.FLOAT).setCurrentValue(value);
            case ANGLEMIN    -> getOrCreate(PARAM_ANGLE, BType.FLOAT).setMin(value);
            case ANGLEMAX    -> getOrCreate(PARAM_ANGLE, BType.FLOAT).setMax(value);
            case DISTANCE    -> getOrCreate(PARAM_DISTANCE, BType.FLOAT).setCurrentValue(value);
            case LENGTHMIN   -> getOrCreate(PARAM_DISTANCE, BType.FLOAT).setMin(value);
            case LENGTHMAX   -> getOrCreate(PARAM_DISTANCE, BType.FLOAT).setMax(value);
            case DISTANCE2   -> getOrCreate(PARAM_DISTANCE2, BType.FLOAT).setCurrentValue(value);
            default -> {}
        }
    }

    private void handleBool(Attributes attr) {
        Boolean value = Boolean.valueOf(attr.getValue(VALUE));
        switch (currentProperty) {
            case ENABLEANGLEMIN  -> getOrCreate(PARAM_ANGLE, BType.FLOAT).setEnableMin(value);
            case ENABLEANGLEMAX  -> getOrCreate(PARAM_ANGLE, BType.FLOAT).setEnableMax(value);
            case ENABLELENGTHMIN -> getOrCreate(PARAM_DISTANCE, BType.FLOAT).setEnableMin(value);
            case ENABLELENGTHMAX -> getOrCreate(PARAM_DISTANCE, BType.FLOAT).setEnableMax(value);
            default -> {}
        }
    }

    /**
     * Returns the parameter currently being built for paramName, or creates it 
     * if it does not exist yet. The instance belongs to currentJoint (non-static inner class).
     */
    private ConstrainedParameter getOrCreate(String paramName, BType type) {
        ConstrainedParameter parameter = parametersInProgress.get(paramName);
        if (parameter == null) {
            parameter = new ConstrainedParameter(paramName, type);
            parametersInProgress.put(paramName, parameter);
        }
        return parameter;
    }

    private void handleEndObject() {
        for (ConstrainedParameter parameter : parametersInProgress.values()) {
            currentJoint.addParameter(parameter);
        }
        parseData.addCadJoint(currentJoint);
    }

    public ParseData getParseData() {
        return parseData;
    }
}