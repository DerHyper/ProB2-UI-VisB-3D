package de.prob2.ui.dataimport.nativecadimport;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class ConstrainedParameter {
    private final String name; // e.g. "Angle", "Distance"
    private final BType type;  // FLOAT, BOOL, INTEGER
    private Object currentValue;
    private Object min;
    private Object max;
    private Boolean enableMin;
    private Boolean enableMax;

    public enum BType {
        INTEGER, FLOAT, BOOL
    }

    public ConstrainedParameter(String name, BType type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public BType getType() {
        return type;
    }

    public Object getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(Object currentValue) {
        this.currentValue = currentValue;
    }

    public Object getMin() {
        return min;
    }

    public void setMin(Object min) {
        this.min = min;
    }

    public Object getMax() {
        return max;
    }

    public void setMax(Object max) {
        this.max = max;
    }

    public Boolean getEnableMin() {
        return enableMin;
    }

    public void setEnableMin(Boolean enableMin) {
        this.enableMin = enableMin;
    }

    public Boolean getEnableMax() {
        return enableMax;
    }

    public void setEnableMax(Boolean enableMax) {
        this.enableMax = enableMax;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}