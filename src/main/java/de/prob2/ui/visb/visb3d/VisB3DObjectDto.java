package de.prob2.ui.visb.visb3d;

public class VisB3DObjectDto {
    public enum MaterialDto {
        standard,
        red,
        organge,
        yellow,
        green,
        cyan,
        blue,
        purple,
        black,
        grey,
        metallic,
        glassy,
        transparent
    }

    public String name;
    public MaterialDto material;
    public Boolean isActive;
    public Vector3Dto position;
    public Vector3Dto rotation;
    public Vector3Dto scale;

    public VisB3DObjectDto(String name) {
        this.name = name;
        this.material = null;
        this.isActive = null;
        this.position = null;
        this.rotation = null;
        this.scale = null;
    }
}
