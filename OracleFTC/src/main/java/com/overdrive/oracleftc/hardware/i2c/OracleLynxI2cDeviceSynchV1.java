package com.overdrive.oracleftc.hardware.i2c;


import com.qualcomm.hardware.lynx.LynxI2cDeviceSynchV1;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.hardware.lynx.LynxNackException;
import com.qualcomm.hardware.lynx.Supplier;
import com.qualcomm.hardware.lynx.commands.LynxCommand;
import com.qualcomm.hardware.lynx.commands.LynxMessage;
import com.qualcomm.hardware.lynx.commands.core.LynxI2cReadMultipleBytesCommand;
import com.qualcomm.hardware.lynx.commands.core.LynxI2cReadSingleByteCommand;
import com.qualcomm.hardware.lynx.commands.core.LynxI2cWriteSingleByteCommand;
import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.hardware.I2cWaitControl;
import com.qualcomm.robotcore.hardware.I2cWarningManager;
import com.qualcomm.robotcore.hardware.TimestampedData;
import com.qualcomm.robotcore.hardware.TimestampedI2cData;
import com.overdrive.oracleftc.hardware.OracleLynxModule;

public class OracleLynxI2cDeviceSynchV1 extends LynxI2cDeviceSynchV1 implements OracleI2cDeviceSynch {
    public OracleLynxI2cDeviceSynchV1(LynxModule module, int bus) {
        super(null, module, bus);
    }
    private Runnable interleavedRunnable = null;
    @Override
    public void scheduleInterleavedRunnable(Runnable runnable) {
        interleavedRunnable=runnable;
    }


    @Override
    public synchronized TimestampedData readTimeStamped(int ireg, int creg) {
        try {
            final Supplier<LynxI2cWriteSingleByteCommand> writeTxSupplier = () -> new LynxI2cWriteSingleByteCommand(getModule(), bus, i2cAddr, ireg);

            final Supplier<LynxCommand<?>> readTxSupplier = () -> {
                /*
                 * LynxI2cReadMultipleBytesCommand does not support a
                 * byte count of one, so we manually differentiate here.
                 */
                return creg==1
                        ? new LynxI2cReadSingleByteCommand(getModule(), bus, i2cAddr)
                        : new LynxI2cReadMultipleBytesCommand(getModule(), bus, i2cAddr, creg);
            };

            return acquireI2cLockWhile(() -> {
                sendI2cTransaction(writeTxSupplier);
                internalWaitForWriteCompletions(I2cWaitControl.ATOMIC);
                sendI2cTransaction(readTxSupplier);
                if(interleavedRunnable!=null)
                {
                    interleavedRunnable.run();
                    interleavedRunnable=null;
                }
                readTimeStampedPlaceholder.reset();
                return pollForReadResult(i2cAddr, ireg, creg);
            });
        } catch (InterruptedException|RobotCoreException|RuntimeException e) {
            handleException(e);
        } catch (LynxNackException e) {
            /*
             * This is a possible device problem, go ahead and tell I2cWarningManager to warn.
             */
            I2cWarningManager.notifyProblemI2cDevice(this);
            handleException(e);
        }
        return readTimeStampedPlaceholder.log(TimestampedI2cData.makeFakeData(getI2cAddress(), ireg, creg));
    }

}

