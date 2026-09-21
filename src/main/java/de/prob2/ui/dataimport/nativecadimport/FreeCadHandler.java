package de.prob2.ui.dataimport.nativecadimport;

import org.xml.sax.helpers.DefaultHandler;

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

    // FCStd Joint variables
    private static final String ANGLE = "Angle";
    private static final String ANGLEMAX = "AngleMax";
    private static final String ANGLEMIN = "AngleMin";
    private static final String DISTANCE = "Distance";
    private static final String DISTANCE2 = "Distance2";
    private static final String ENABLEANGLEMAX = "EnableAngleMax";
    private static final String ENABLEANGLEMIN = "EnableAngleMin";
    private static final String ENABLELENGTHMAX = "EnableLengthMax";
    private static final String ENABLELENGTHMIN = "EnableLengthMin";
    
    // Local variables
    private ParseData parseData = new ParseData();
    private CadJoint currentJoint;
    private StringBuilder elementValue;
}
