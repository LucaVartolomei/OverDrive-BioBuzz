package com.overdrive.oracleftc.math.control;

public class EdgeDetector {

    public enum EdgeType {
        RISING,
        FALLING,
        BOTH
    }

    private final EdgeType type;
    public EdgeDetector(EdgeType type)
    {
        this.type=type;

    }

    private boolean prevInput;
    public boolean calculate(boolean input)
    {
        switch (type)
        {
            case RISING:
                if(input&&!prevInput)
                {
                    prevInput=true;
                    return true;
                }else {
                    prevInput=input;
                    return false;
                }
            case FALLING:
                if(!input&&prevInput)
                {
                    prevInput=false;
                    return true;
                }else {
                    prevInput=input;
                    return false;
                }
            case BOTH:
                if(input!=prevInput)
                {
                    prevInput=input;
                    return true;
                }else {
                    return false;
                }

        }
        return false;
    }

}
