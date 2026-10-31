package com.overdrive.oracleftc.hardware.servo;

import com.qualcomm.robotcore.hardware.ServoControllerEx;
import com.qualcomm.robotcore.hardware.ServoImplEx;
import com.qualcomm.robotcore.hardware.configuration.typecontainers.ServoConfigurationType;

public class OracleServo extends ServoImplEx {
    public OracleServo(ServoControllerEx controller, int portNumber, ServoConfigurationType servoType) {
        super(controller, portNumber, servoType);
    }

    public OracleServo(ServoControllerEx controller, int portNumber, Direction direction, ServoConfigurationType servoType) {
        super(controller, portNumber, direction, servoType);
    }
}
