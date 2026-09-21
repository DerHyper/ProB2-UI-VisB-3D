package de.prob2.ui.dataimport.nativecadimport;

import java.util.Map;
import java.util.function.Consumer;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
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
    private String currentProperty;

    private final Map<String, Consumer<Object>> handlers =
        Map.<String, Consumer<Object>>of(
            "Angle",   value -> currentJoint.setAngle((Float) value)
        );

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
    }

    private void handleStartProperty(Attributes attr) {
        currentProperty = attr.getValue(NAME);
    }

    private void handleFloat(Attributes attr) {
        Float value = Float.valueOf(attr.getValue(VALUE));
        Consumer<Object> handler = handlers.get(currentProperty);
        if (handler != null)
        {
            handler.accept(value);
        }
    }

    private void handleEndObject() {
        parseData.addCadJoint(currentJoint);
    }

    public ParseData getParseData() {
        return parseData;
    }
}
