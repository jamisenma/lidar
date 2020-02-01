/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import frc.robot.commands.ReportTFMini;
import edu.wpi.first.wpilibj.SerialPort;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

import frc.robot.subsystems.TfMini;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import edu.wpi.first.wpilibj.SerialPort.Parity;

import edu.wpi.first.wpilibj.SerialPort.StopBits;




public class TfMini extends SubsystemBase implements Runnable{
	private static SerialPort serialPort;
	private static final int frameHeaderByte = 0x59;

	private static int distancecm = -1;


	

	/**
	 * Creates a new ExampleSubsystem.
	 */
	public TfMini(SerialPort serialPort) {
		TfMini.serialPort = serialPort;

		new Thread(this).start();
	}

	public void initDefaultCommand() {
		setDefaultCommand(new ReportTFMini());
	}

	public void report() {
		serialPort.reset();
		
		int[] frame = new int[9];
		byte[] data = new byte[0];
		int frameIndex = 0;
		int dataIndex = 0;
		
		while (true)
		{
			if (dataIndex >= data.length)
			{
				data = serialPort.read(serialPort.getBytesReceived());
				if (0 == data.length)
				{
					continue;
				}
				dataIndex = 0;
			}
			
			// The next byte to process:
			int b = data[dataIndex++] & 0xff;
			
			// See if we are expecting the frame header:
			if (frameIndex < 2)
			{
				// Need to get a frame header byte:
				if (b == frameHeaderByte)
				{
					frame[frameIndex++] = b;
				}
				else
				{
					frameIndex = 0;
				}
				continue;
			}
			
			// We are past frame header - put current byte in frame:
			frame[frameIndex++] = b;
			if (frameIndex < frame.length)
			{
				continue;
			}
			
			// Reset indices for next frame:
			frameIndex = 0;
			
			// We have a complete frame.
			// Check the checksum:
			if (((frame[0] + frame[1] + frame[2] + frame[3] + frame[4] + frame[5] + frame[6] + frame[7]) & 0xff) != frame[8])
			{
				continue;
			}
			
			// Pull data out of the frame:
      distancecm = frame[3] << 8 + frame[2];
	  SmartDashboard.putNumber("TFMini Distance Cm", distancecm);
		}
		
	
	}
    

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }

	@Override
  public void run()
	{
		// Reset serial port to empty buffers;
		
	}

    
}
