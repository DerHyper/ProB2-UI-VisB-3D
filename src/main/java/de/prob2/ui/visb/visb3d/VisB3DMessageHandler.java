package de.prob2.ui.visb.visb3d;

import de.prob2.ui.visb.VisBView.VisBConnector;

public class VisB3DMessageHandler {
    private VisBConnector connector;
    
    public VisB3DMessageHandler(VisBConnector connector) {
        this.connector = connector;
    }

    public void handleMessage(String message) {
        System.out.println("VisB3DMessageHandler: " + message);

        WSMessageDto dto = WSMessageDto.fromJsonString(message);
        connector.click(dto.objectId, dto.pageX, dto.pageY, dto.altKey, dto.ctrlKey, dto.metaKey, dto.shiftKey, dto.jsVars);
    }
}