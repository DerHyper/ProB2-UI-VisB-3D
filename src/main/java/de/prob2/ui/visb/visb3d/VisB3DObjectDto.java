package de.prob2.ui.visb.visb3d;

public class VisB3DObjectDto {
    public String name;
    public MaterialDto material;
    public Boolean isActive;
    public Vector3Dto position;
    public Vector3Dto rotation;
    public Vector3Dto scale;

    public VisB3DObjectDto(String name) {
        this.name = name;
        this.material = new MaterialDto();
        this.isActive = null;
        this.position = new Vector3Dto();
        this.rotation = new Vector3Dto();
        this.scale = new Vector3Dto();
    }
}
