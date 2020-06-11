package com.alvarlagerlof.quake2;

public class Timer {
    Integer resetValue;
    Integer currentValue;

    public Timer(Integer resetValue) {
        this.resetValue = resetValue;
        this.currentValue = resetValue;
    }

    public Integer getTime() {
        return currentValue;
    }

    public Integer getResetTime() {
        return resetValue;
    }

    public void decrease() {
        if (currentValue > 0) {
            currentValue--;
        }
    }

    public void reset() {
        currentValue = resetValue;
    }

    public void set(Integer value) {
        currentValue = value;
    }
}
