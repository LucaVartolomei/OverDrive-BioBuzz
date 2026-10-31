package com.overdrive.oracleftc.commands;

import com.overdrive.oracleftc.hardware.Subsystem;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Base class for commands
 */
@SuppressWarnings({"unused"})
public abstract class Command {
    public void init() {}
    public void update() {}
    public boolean finished() { return false; }
    public void end(boolean interrupted) {}
    public boolean interruptable() { return true; }
    public Set<Subsystem> requires() { return new HashSet<>();}




    public static CommandBuilder builder() { return new CommandBuilder(); }
    public static void run(Command command)
    {
        command.init();
        while(!command.finished())
            command.update();
        command.end(false);
    }
    public static class CommandBuilder {
        private Runnable init = () -> {
        };
        private Runnable update = () -> {
        };
        private Supplier<Boolean> finished = () -> false;
        private Consumer<Boolean> end = (finished) -> {
        };
        private Supplier<Boolean> interruptable = () -> true;
        private Supplier<Set<Subsystem>> requires = () -> null;

        public CommandBuilder init(Runnable init) {
            this.init = init;
            return this;
        }

        public CommandBuilder update(Runnable update) {
            this.update = update;
            return this;
        }

        public CommandBuilder finished(Supplier<Boolean> finished) {
            this.finished = finished;
            return this;
        }

        public CommandBuilder end(Consumer<Boolean> end) {
            this.end = end;
            return this;
        }

        public CommandBuilder interruptable(boolean interruptable) {
            this.interruptable = () -> interruptable;
            return this;
        }

        public CommandBuilder interruptable(Supplier<Boolean> interruptable) {
            this.interruptable = interruptable;
            return this;
        }

        public CommandBuilder requires(Subsystem subsystem)
        {
            this.requires=()-> {
                Set<Subsystem> set= new HashSet<>();
                set.add(subsystem);
                return set;
            };
            return this;
        }
        public CommandBuilder requires(Set<Subsystem> subsystems)
        {
            this.requires=()->subsystems;
            return this;
        }
        public CommandBuilder requires(Supplier<Set<Subsystem>> subsystemsSupplier)
        {
            this.requires=subsystemsSupplier;
            return this;
        }

        public Command build() {
            return new Command() {
                @Override
                public void init() {
                    CommandBuilder.this.init.run();
                }

                @Override
                public void update() {
                    CommandBuilder.this.update.run();
                }

                @Override
                public boolean finished() {
                    return CommandBuilder.this.finished.get();
                }

                @Override
                public void end(boolean interrupted) {
                    CommandBuilder.this.end.accept(interrupted);
                }

                @Override
                public boolean interruptable() {
                    return CommandBuilder.this.interruptable.get();
                }
                @Override
                public Set<Subsystem> requires() {
                    return requires.get();
                }
            };
        }
    }
}
