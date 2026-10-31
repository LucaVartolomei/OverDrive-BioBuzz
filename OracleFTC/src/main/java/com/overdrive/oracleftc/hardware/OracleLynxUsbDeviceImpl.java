package com.overdrive.oracleftc.hardware;

import android.content.Context;
import android.graphics.text.TextRunShaper;

import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.hardware.lynx.LynxUsbDevice;
import com.qualcomm.hardware.lynx.LynxUsbDeviceDelegate;
import com.qualcomm.hardware.lynx.LynxUsbDeviceImpl;
import com.qualcomm.robotcore.eventloop.SyncdDevice;
import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.hardware.LynxModuleDescription;
import com.qualcomm.robotcore.hardware.usb.RobotUsbManager;
import com.qualcomm.robotcore.util.RobotLog;
import com.qualcomm.robotcore.util.SerialNumber;

public class OracleLynxUsbDeviceImpl extends LynxUsbDeviceImpl {
    private static final String TAG="OracleLynxUsb";
    /**
     * Use {@link #findOrCreateAndArm} instead
     *
     * @param context
     * @param serialNumber
     * @param manager
     * @param usbManager
     */
    protected OracleLynxUsbDeviceImpl(Context context, SerialNumber serialNumber, Manager manager, RobotUsbManager usbManager) {
        super(context, serialNumber, manager, usbManager);
        extantDevices.clear();
        extantDevices.add(this);
    }
    public static LynxUsbDevice findOrCreateAndArm(final Context context,
                                                   final SerialNumber serialNumber,
                                                   SyncdDevice.Manager manager,
                                                   RobotUsbManager robotUsbManager) throws RobotCoreException, InterruptedException
    {
        synchronized (extantDevices)
        {
            for (LynxUsbDeviceImpl device : extantDevices)
            {
                if (device.getSerialNumber().equals(serialNumber) && /*paranoia*/device.getArmingState() != ARMINGSTATE.CLOSED)
                {
                    device.addRef(); // new delegate must own another reference
                    RobotLog.vv(TAG, "using existing [%s]: 0x%08x", serialNumber, device.hashCode());
                    return new OracleLynxUsbDeviceDelegate(device);
                }
            }

            OracleLynxUsbDeviceImpl newDevice = new OracleLynxUsbDeviceImpl(context, serialNumber, manager, robotUsbManager); // has ref count of one
            RobotLog.vv(TAG, "creating new [%s]: 0x%08x", serialNumber, newDevice.hashCode());
            newDevice.armOrPretend();
            return new OracleLynxUsbDeviceDelegate(newDevice);
        }
    }

    @Override
    public LynxModule getOrAddModule(LynxModuleDescription moduleDescription) throws InterruptedException, RobotCoreException {
        synchronized (sysOpStartStopLock)
        {
            int moduleAddress = moduleDescription.address;
            RobotLog.vv(TAG, "addConfiguredModule() module#=%d", moduleAddress);
            boolean added = false;
            LynxModule module;

            synchronized (this.knownModules)
            {
                if (!this.knownModules.containsKey(moduleAddress))
                {
                    module = new OracleLynxModule(this, moduleAddress, moduleDescription.isParent, moduleDescription.isUserModule);
                    if (moduleDescription.isSystemSynthetic) { module.setSystemSynthetic(true); }
                    this.knownModules.put(moduleAddress, module);
                    added = true;
                }
                else
                {
                    module = knownModules.get(moduleAddress);
                    RobotLog.vv(TAG, "addConfiguredModule() module#=%d: already exists", moduleAddress);

                    //noinspection ConstantConditions
                    if (moduleDescription.isUserModule && !module.isUserModule())
                    {
                        // The caller of this method is trying to set up a user module, but the
                        // currently-registered module is a non-user module, so we convert the
                        // registered module into a user module.
                        RobotLog.vv(TAG, "Converting module #%d to a user module", module.getModuleAddress());
                        module.setUserModule(true);
                    }

                    //noinspection ConstantConditions
                    if (moduleDescription.isUserModule && moduleDescription.isSystemSynthetic && !module.isSystemSynthetic())
                    {
                        // The caller of this method is trying to set up a user module that was added
                        // implicitly, rather than explicitly stated in the XML configuration. Add that
                        // property to the registered module.
                        module.setSystemSynthetic(true);
                    }

                    //noinspection ConstantConditions
                    if (moduleDescription.isParent != module.isParent())
                    {
                        RobotLog.ww(TAG, "addConfiguredModule(): The active configuration file may be incorrect about whether Expansion Hub %d is the parent", module.getModuleAddress());
                    }
                }
            }

            if (added)
            {
                try {
                    module.pingAndQueryKnownInterfacesAndEtc();
                }
                catch (RobotCoreException|InterruptedException|RuntimeException e)
                {
                    // If we don't full add, then we don't add at all
                    RobotLog.logExceptionHeader(TAG, e, "addConfiguredModule() module#=%d", module.getModuleAddress());
                    RobotLog.ee(TAG, "Unable to communicate with REV Hub #%d at robot startup. A Robot Restart will be required to use this hub.", module.getModuleAddress());
                    module.close();
                    synchronized (this.knownModules)
                    {
                        this.knownModules.remove(module.getModuleAddress());
                    }
                    throw e;
                }
            }

            return module;
        }
    }
}
