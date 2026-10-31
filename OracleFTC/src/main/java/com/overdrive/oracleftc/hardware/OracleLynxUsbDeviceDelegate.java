package com.overdrive.oracleftc.hardware;

import com.qualcomm.hardware.lynx.LynxUsbDeviceDelegate;
import com.qualcomm.hardware.lynx.LynxUsbDeviceImpl;

public class OracleLynxUsbDeviceDelegate extends LynxUsbDeviceDelegate {
    public OracleLynxUsbDeviceDelegate(LynxUsbDeviceImpl lynxUsbDevice) {
        super(lynxUsbDevice);
    }

}
