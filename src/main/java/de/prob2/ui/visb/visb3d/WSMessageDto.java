package de.prob2.ui.visb.visb3d;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class WSMessageDto
{
    public enum WSMessageType
    {
        click,
        none
    }

    public WSMessageType type;
    public String objectId;
    public int pageX;
    public int pageY;
    public boolean altKey;
    public boolean ctrlKey;
    public boolean metaKey;
    public boolean shiftKey;
    public String jsVars;

    public WSMessageDto(
        WSMessageType type,
        String objectId,
        int pageX,
        int pageY,
        boolean altKey,
        boolean ctrlKey,
        boolean metaKey,
        boolean shiftKey,
        String jsVars)
    {
        this.type = type;
        this.objectId = objectId;
        this.pageX = pageX;
        this.pageY = pageY;
        this.altKey = altKey;
        this.ctrlKey = ctrlKey;
        this.metaKey = metaKey;
        this.shiftKey = shiftKey;
        this.jsVars = jsVars;
    }

    public WSMessageDto()
    {
        this.type = WSMessageType.none;
        this.objectId = "";
        this.pageX = 0;
        this.pageY = 0;
        this.altKey = false;
        this.ctrlKey = false;
        this.metaKey = false;
        this.shiftKey = false;
        this.jsVars = "";
    }

    public static WSMessageDto fromJsonString(String str) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS);
        
        WSMessageDto dto = null;
        try {
            dto = mapper.readValue(str, WSMessageDto.class);
        } catch (JsonProcessingException e) {
            System.out.println(e);
            // TODO: Catch
        }
        
        return dto;
    }
}