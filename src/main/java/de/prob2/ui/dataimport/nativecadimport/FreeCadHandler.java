package de.prob2.ui.dataimport.nativecadimport;

import org.xml.sax.helpers.DefaultHandler;

public class FreeCadHandler extends DefaultHandler {
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
    

    private ParseData parseData;
    private CadJoint currentJoint;
    private StringBuilder elementValue;
}
