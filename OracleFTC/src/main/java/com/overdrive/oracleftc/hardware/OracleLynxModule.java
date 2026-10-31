package com.overdrive.oracleftc.hardware;

import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.hardware.lynx.LynxUnsupportedCommandException;
import com.qualcomm.hardware.lynx.LynxUsbDevice;
import com.qualcomm.hardware.lynx.commands.LynxCommand;
import com.qualcomm.hardware.lynx.commands.LynxMessage;
import com.qualcomm.hardware.lynx.commands.core.LynxDekaInterfaceCommand;
import com.qualcomm.hardware.lynx.commands.core.LynxSetMotorConstantPowerCommand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OracleLynxModule extends LynxModule {
    public OracleLynxModule(LynxUsbDevice lynxUsbDevice, int moduleAddress, boolean isParent, boolean isUserModule) {
        super(lynxUsbDevice, moduleAddress, isParent, isUserModule);

    }


}
