package com.develogical.camera;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import static org.jmock.internal.Cardinality.exactly;

public class CameraTest {

    @Rule
    public JUnitRuleMockery context = new JUnitRuleMockery();

    Sensor sensor = context.mock(Sensor.class);
    MemoryCard memCard = context.mock(MemoryCard.class);

    Camera camera = new Camera(sensor, memCard);

    private void powerOnCameraAndPressShutter() {
        context.checking(new Expectations() {{
            exactly(1).of(sensor).powerUp();

            byte[] data = exactly(1).of(sensor).readData();
            exactly(1).of(memCard).write(data);
        }});

        camera.powerOn();
        camera.pressShutter();
    }

    @Test
    public void switchingTheCameraOnPowersUpTheSensor() {

        context.checking(new Expectations() {{
            exactly(1).of(sensor).powerUp();
        }});

        camera.powerOn();
    }

    @Test
    public void switchingTheCameraOffPowersUpTheSensor() {

        context.checking(new Expectations() {{
            exactly(1).of(sensor).powerDown();
        }});

        camera.powerOff();
    }

    @Test
    public void pressingTheShutterWhileOffDoesNothing() {

        context.checking(new Expectations() {{
            never(sensor).readData();
        }});

        camera.pressShutter();
    }

    @Test
    public void pressingTheShutterWhileOnCopiesData() {
        powerOnCameraAndPressShutter();
    }

    @Test
    public void switchingCameraOffDoesNotPowerOffBusySensor() {
        powerOnCameraAndPressShutter();
        camera.powerOff();
    }


    @Test
    public void switchingCameraOffPowersOffBusySensorAfterWriteCompleted() {

        powerOnCameraAndPressShutter();

        context.checking(new Expectations() {{
            exactly(1).of(sensor).powerDown();
        }});

        camera.powerOff();
        camera.writeComplete();
    }
}
