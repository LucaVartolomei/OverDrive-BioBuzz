package com.overdrive.oracleftc.hardware.servo;

import com.qualcomm.hardware.bosch.BHI260IMU;
import com.qualcomm.robotcore.hardware.CRServoImpl;
import com.qualcomm.robotcore.hardware.CRServoImplEx;
import com.qualcomm.robotcore.hardware.ServoController;
import com.qualcomm.robotcore.hardware.ServoControllerEx;
import com.qualcomm.robotcore.hardware.configuration.typecontainers.ServoConfigurationType;


public class OracleCRServo extends CRServoImplEx {

    public OracleCRServo(ServoControllerEx controller, int portNumber, ServoConfigurationType servoType) {
        super(controller, portNumber, servoType);
    }

    public OracleCRServo(ServoControllerEx controller, int portNumber, Direction direction, ServoConfigurationType servoType) {
        super(controller, portNumber, direction, servoType);
    }
}
