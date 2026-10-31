package com.overdrive.oracleftc.commands.helpers;

import com.overdrive.oracleftc.commands.Command;

@SuppressWarnings("unused")
public class InstantCommand extends Command {
    private final Runnable runnable;

    public InstantCommand(Runnable runnable) {
        this.runnable = runnable;
    }

    @Override
    public void update() {
        runnable.run();
    }

    @Override
    public boolean finished() {
        return true;
    }
}
