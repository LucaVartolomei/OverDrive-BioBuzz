package com.overdrive.oracleftc.hardware.motor;

import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.DcMotorImplEx;
import com.qualcomm.robotcore.hardware.configuration.typecontainers.MotorConfigurationType;

public class OracleDcMotor extends DcMotorImplEx {

    public OracleDcMotor(DcMotorController controller, int portNumber) {
        super(controller, portNumber);

    }

    public OracleDcMotor(DcMotorController controller, int portNumber, Direction direction) {
        super(controller, portNumber, direction);
    }

    public OracleDcMotor(DcMotorController controller, int portNumber, Direction direction, MotorConfigurationType motorType) {
        super(controller, portNumber, direction, motorType);
    }


}
