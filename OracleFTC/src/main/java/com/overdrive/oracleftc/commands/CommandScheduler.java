package com.overdrive.oracleftc.commands;

import com.overdrive.oracleftc.hardware.Subsystem;
import com.overdrive.oracleftc.hardware.SubsystemFlavor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@SuppressWarnings({"unused"})
public class CommandScheduler {

    private final ExecutorService commandExecutor = Executors.newFixedThreadPool(2);
    private final List<Command> controlHubOnlyCommandsToSchedule;
    private final Set<Command> controlHubOnlyActiveCommands;
    private final List<Command> expansionHubOnlyCommandsToSchedule;
    private final Set<Command> expansionHubOnlyActiveCommands;
    private final List<Command> mixedCommandsToSchedule;
    private final Set<Command> mixedActiveCommands;
    private final List<Callable<Void>> hubSpecificCallables;

    public CommandScheduler()
    {
        controlHubOnlyCommandsToSchedule=new ArrayList<>();
        controlHubOnlyActiveCommands=new HashSet<>();
        expansionHubOnlyCommandsToSchedule=new ArrayList<>();
        expansionHubOnlyActiveCommands=new HashSet<>();
        mixedCommandsToSchedule =new ArrayList<>();
        mixedActiveCommands=new HashSet<>();

        hubSpecificCallables =new ArrayList<>();
        hubSpecificCallables.add(()->{

            for (Command command : controlHubOnlyCommandsToSchedule) {
                command.init();
                controlHubOnlyActiveCommands.add(command);
            }
            controlHubOnlyCommandsToSchedule.clear();
            controlHubOnlyActiveCommands.forEach(Command::update);
            controlHubOnlyActiveCommands.removeIf(command->{
                boolean finished = command.finished();
                if(finished) command.end(false);
                return finished;
            });
            return null;
        });
        hubSpecificCallables.add(()->{
            for (Command command : expansionHubOnlyCommandsToSchedule) {
                command.init();
                expansionHubOnlyActiveCommands.add(command);
            }
            expansionHubOnlyCommandsToSchedule.clear();
            expansionHubOnlyActiveCommands.forEach(Command::update);
            expansionHubOnlyActiveCommands.removeIf(command->{
                boolean finished = command.finished();
                if(finished) command.end(false);
                return finished;
            });
            return null;
        });
    }

    public void cancel(Command command)
    {
        controlHubOnlyCommandsToSchedule.remove(command);
        controlHubOnlyActiveCommands.remove(command);
        expansionHubOnlyCommandsToSchedule.remove(command);
        expansionHubOnlyActiveCommands.remove(command);
        mixedCommandsToSchedule.remove(command);
        mixedActiveCommands.remove(command);
    }
    public void cancelAll()
    {
        controlHubOnlyCommandsToSchedule.clear();
        controlHubOnlyActiveCommands.clear();
        expansionHubOnlyCommandsToSchedule.clear();
        expansionHubOnlyActiveCommands.clear();
        mixedCommandsToSchedule.clear();
        mixedActiveCommands.clear();
    }

    public boolean finished(Command command)
    {
        return !controlHubOnlyCommandsToSchedule.contains(command) &&
                !controlHubOnlyActiveCommands.contains(command) &&
                !expansionHubOnlyCommandsToSchedule.contains(command) &&
                !expansionHubOnlyActiveCommands.contains(command) &&
                !mixedCommandsToSchedule.contains(command) &&
                !mixedActiveCommands.contains(command);
    }

    public void schedule(Command command)
    {
        SubsystemFlavor flavor=determineFlavor(command);
        switch (flavor)
        {
            case Mixed:
                mixedCommandsToSchedule.add(command);
                break;
            case ControlHubOnly:
                controlHubOnlyCommandsToSchedule.add(command);
                break;
            case ExpansionHubOnly:
                expansionHubOnlyCommandsToSchedule.add(command);
                break;
        }
    }
    private static SubsystemFlavor determineFlavor(Command command)
    {
        SubsystemFlavor flavor = null;
        if(command.requires()!=null)
            for (Subsystem subsystem:
                    command.requires()) {
                flavor = SubsystemFlavor.merge(flavor, subsystem.flavor());
            }
        if(flavor==null) flavor=SubsystemFlavor.Mixed;
        return flavor;
    }
    public void update() throws InterruptedException {
        for(Future<Void> future:commandExecutor.invokeAll(hubSpecificCallables))
        {
            try {
                future.get();
            } catch (ExecutionException e) {
                e.printStackTrace();
                throw new RuntimeException(e.getCause());
            }
        }

        for (Command command :
                mixedCommandsToSchedule) {
            command.init();
            mixedActiveCommands.add(command);
        }
        mixedCommandsToSchedule.clear();
        mixedActiveCommands.forEach(Command::update);
        mixedActiveCommands.removeIf(command->{
            boolean finished = command.finished();
            if(finished) command.end(false);
            return finished;
        });
        // Reorder commands if their flavor changes
        Set<Command> allCommands = new HashSet<>();
        allCommands.addAll(controlHubOnlyActiveCommands);
        allCommands.addAll(expansionHubOnlyActiveCommands);
        allCommands.addAll(mixedActiveCommands);
        controlHubOnlyActiveCommands.clear();
        expansionHubOnlyActiveCommands.clear();
        mixedActiveCommands.clear();
        for(Command command:allCommands)
        {
            SubsystemFlavor flavor = determineFlavor(command);
            switch (flavor)
            {
                case ControlHubOnly:
                    controlHubOnlyActiveCommands.add(command);
                    break;
                case ExpansionHubOnly:
                    expansionHubOnlyActiveCommands.add(command);
                    break;
                case Mixed:
                    mixedActiveCommands.add(command);
                    break;
            }
        }

    }
}
