package com.overdrive.oracleftc.utils.input;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class Joystick {

    private final Supplier<JoystickData> internalSupplier;
    private JoystickData state = new JoystickData(0,0,false);
    private JoystickData lastState;
    public Joystick(Supplier<JoystickData> internalSupplier) {
        this.internalSupplier=internalSupplier;
    }

    public void process() {

        lastState=state;
        state = internalSupplier.get();
    }

    public JoystickData get() {
        return state;
    }

    public BooleanSupplier moved(double epsilon) {
        return () -> Math.abs(state.x) > epsilon || Math.abs(state.y) > epsilon;
    }

    public Button button() {
        return new Button(() -> state.pressed);
    }


}
