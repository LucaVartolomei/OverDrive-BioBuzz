package com.overdrive.oracleftc.utils.input;

import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class Trigger {
    private final Supplier<Double> internalSupplier;
    public double state = 0.0;
    public double lastState =0.0;

    public Trigger(Supplier<Double> internalSupplier) {
        this.internalSupplier=internalSupplier;
    }

    public Supplier<Boolean> when(Function<Double, Boolean> eval) {
        return () -> eval.apply(internalSupplier.get());
    }

    public void process() {
        lastState=state;
        state = internalSupplier.get();
    }

    public double get() {
        return state;
    }
}
