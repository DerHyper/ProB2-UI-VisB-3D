package de.prob2.ui.visb.visb3d;

public class MaterialDto {
    /// <summary>
    /// Can either be a Hex color string (e.g., "#RRGGBB" or "#RRGGBBAA")
    /// or a name with a matching unity material located at "\Assets\Resources\Materials".
    /// </summary>
    public String color;
    public Float metallic;
    public Float smoothness;
}
