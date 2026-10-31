package com.overdrive.oracleftc.fsm;

import com.overdrive.oracleftc.commands.Command;

import java.util.function.Supplier;

public interface Transition<T> {
    boolean getCondition();
    Command getCommand();
    T getTargetState();
}
