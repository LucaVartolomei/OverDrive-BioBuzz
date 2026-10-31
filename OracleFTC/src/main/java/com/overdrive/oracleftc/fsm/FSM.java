package com.overdrive.oracleftc.fsm;


import com.overdrive.oracleftc.commands.Command;
import com.overdrive.oracleftc.commands.CommandScheduler;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

@SuppressWarnings({"unused"})
public class FSM<T extends Enum<T>> {

    private T currentState;
    private Transition<T> currentTransition;
    private final CommandScheduler scheduler = new CommandScheduler();
    private final Map<T, List<Transition<T>>> transitions;
    public FSM(T initialState,  Map<T, List<Transition<T>>> transitions)
    {
        currentState=initialState;
        this.transitions=transitions;
    }


    public void update() throws InterruptedException {
        if(currentTransition!=null)
        {
            scheduler.update();
            if(scheduler.finished(currentTransition.getCommand()))
            {
                currentState=currentTransition.getTargetState();
                currentTransition=null;
            }
        }
        else {
            for (Transition<T> transition :
                    Objects.requireNonNull(transitions.getOrDefault(currentState, new ArrayList<>()))) {
                if(transition.getCondition())
                {
                    currentTransition=transition;
                    scheduler.schedule(currentTransition.getCommand());
                    break;
                }
            }
        }
    }

    public T getCurrentState()
    {
        return currentState;
    }

    public Transition<T> getCurrentTransition()
    {
        return currentTransition;
    }

    public static <T extends Enum<T>> FSMBuilder<T> builder()
    {
        return new FSMBuilder<T>();
    }

    public static class FSMBuilder<T extends Enum<T>>
    {
        private final Map<T, List<Transition<T>>> transitions= new HashMap<>();
        private T initialState;
        public FSMBuilder<T> initial(T state)
        {
            initialState=state;
            return this;
        }
        public FSMBuilder<T> transition(T from, T to, Supplier<Boolean> booleanSupplier, Command command)
        {
            List<Transition<T>> elements = transitions.getOrDefault(from, new ArrayList<>());
            Objects.requireNonNull(elements).add(new Transition<T>() {
                @Override
                public boolean getCondition() {
                    return booleanSupplier.get();
                }

                @Override
                public Command getCommand() {
                    return command;
                }

                @Override
                public T getTargetState() {
                    return to;
                }
            });
            transitions.put(from, elements);
            return this;
        }

        public FSM<T> build()
        {
            return new FSM<T>(initialState,transitions);
        }
    }
}
