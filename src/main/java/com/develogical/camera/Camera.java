package com.develogical.camera;

import com.sun.org.apache.xpath.internal.operations.Bool;

public class Camera implements WriteListener {

    private final Sensor sensor;
    private final MemoryCard memCard;

    private Boolean isOff = true;
    private Boolean isWriting = false;
    private Boolean isPendingPowerOff = false;

    public Camera(Sensor sensor, MemoryCard memCard) {
        this.sensor = sensor;
        this.memCard = memCard;
    }

    public void pressShutter() {
        if (!this.isOff) {
            byte[] data = this.sensor.readData();
            this.isWriting = true;
            this.memCard.write(data);
        }
    }

    public void powerOn() {
        this.sensor.powerUp();
        this.isOff = false;
    }

    public void powerOff() {
        this.isOff = true;

        if (!this.isWriting) {
            this.sensor.powerDown();
        } else {
            this.isPendingPowerOff = true;
        }
    }

    @Override
    public void writeComplete() {
        this.isWriting = false;

        if (this.isPendingPowerOff) {
            this.isPendingPowerOff = false;
            powerOff();
        }
    }
}

