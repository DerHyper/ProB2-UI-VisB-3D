package de.prob2.ui.dataimport.nativecadimport;

/**
 * Internal representation of a joint from a native CAD format.
 * Variables can be null if not used by a CAD format.
 */
public class CadJoint {

    public String name;
    private Float angle;
    private Float angleMax;
    private Float angleMin;
    private Boolean enableAngleMin;
    private Boolean enableAngleMax;
    private Float distance;
    private Float distance2;
    private Float lengthMin;
    private Float lengthMax;
    private Boolean enableLengthMin;
    private Boolean enableLengthMax;

    public CadJoint() {
        this.name = null;
        this.angle = null;
        this.angleMax = null;
        this.angleMin = null;
        this.enableAngleMin = null;
        this.enableAngleMax = null;
        this.distance = null;
        this.distance2 = null;
        this.lengthMin = null;
        this.lengthMax = null;
        this.enableLengthMin = null;
        this.enableLengthMax = null;
    }

    public CadJoint(
            String name, 
            Float angle,
            Float angleMax,
            Float angleMin,
            Boolean enableAngleMin,
            Boolean enableAngleMax,
            Float distance,
            Float distance2,
            Float lengthMin,
            Float lengthMax,
            Boolean enableLengthMin,
            Boolean enableLengthMax) {
        
        this.name = name;
        this.angle = angle;
        this.angleMax = angleMax;
        this.angleMin = angleMin;
        this.enableAngleMin = enableAngleMin;
        this.enableAngleMax = enableAngleMax;
        this.distance = distance;
        this.distance2 = distance2;
        this.lengthMin = lengthMin;
        this.lengthMax = lengthMax;
        this.enableLengthMin = enableLengthMin;
        this.enableLengthMax = enableLengthMax;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Float getAngle() {
        return angle;
    }

    public void setAngle(Float angle) {
        this.angle = angle;
    }

    public Float getAngleMax() {
        return angleMax;
    }

    public void setAngleMax(Float angleMax) {
        this.angleMax = angleMax;
    }

    public Float getAngleMin() {
        return angleMin;
    }

    public void setAngleMin(Float angleMin) {
        this.angleMin = angleMin;
    }

    public Boolean getEnableAngleMin() {
        return enableAngleMin;
    }

    public void setEnableAngleMin(Boolean enableAngleMin) {
        this.enableAngleMin = enableAngleMin;
    }

    public Boolean getEnableAngleMax() {
        return enableAngleMax;
    }

    public void setEnableAngleMax(Boolean enableAngleMax) {
        this.enableAngleMax = enableAngleMax;
    }

    public Float getDistance() {
        return distance;
    }

    public void setDistance(Float distance) {
        this.distance = distance;
    }

    public Float getDistance2() {
        return distance2;
    }

    public void setDistance2(Float distance2) {
        this.distance2 = distance2;
    }

    public Float getLengthMin() {
        return lengthMin;
    }

    public void setLengthMin(Float lengthMin) {
        this.lengthMin = lengthMin;
    }

    public Float getLengthMax() {
        return lengthMax;
    }

    public void setLengthMax(Float lengthMax) {
        this.lengthMax = lengthMax;
    }

    public Boolean getEnableLengthMin() {
        return enableLengthMin;
    }

    public void setEnableLengthMin(Boolean enableLengthMin) {
        this.enableLengthMin = enableLengthMin;
    }

    public Boolean getEnableLengthMax() {
        return enableLengthMax;
    }

    public void setEnableLengthMax(Boolean enableLengthMax) {
        this.enableLengthMax = enableLengthMax;
    }
}

