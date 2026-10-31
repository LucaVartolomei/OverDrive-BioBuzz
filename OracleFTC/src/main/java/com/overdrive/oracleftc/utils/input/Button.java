package com.overdrive.oracleftc.utils.input;

import java.util.function.Supplier;

@SuppressWarnings("unused")
public class Button {
    private final Supplier<Boolean> internalSupplier;
    public boolean state;
    public boolean lastState;

    public void process() {
        lastState = state;
        state = internalSupplier.get();
    }

    public Button(Supplier<Boolean> internalSupplier) {
        this.internalSupplier=internalSupplier;
    }

    public Supplier<Boolean> pressed() {
        return ()->state&&!lastState;
    }

    public Supplier<Boolean> released() {
        return ()->!state&&lastState;
    }

    public Supplier<Boolean> changed() {
        return ()->state!=lastState;
    }

    public Supplier<Boolean> not() {
        return ()->!state;
    }

    public boolean get() {
        return state;
    }
}
