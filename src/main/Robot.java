// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.drive.MecanumDrive;

public class Robot extends TimedRobot {
  private static final int kFrontLeftChannel = 1;
  private static final int kRearLeftChannel = 2;
  private static final int kFrontRightChannel = 3;
  private static final int kRearRightChannel = 4;

  private static final int kJoystickChannel = 0;

  ADXRS450_Gyro gyro = new ADXRS450_Gyro();

  private MecanumDrive robotDrive;
  private Joystick stick;

  @Override
  public void robotInit() {

    WPI_TalonFX frontLeft = new WPI_TalonFX(kFrontLeftChannel);
    WPI_TalonFX rearLeft = new WPI_TalonFX(kRearLeftChannel);
    WPI_TalonFX frontRight = new WPI_TalonFX(kFrontRightChannel);
    WPI_TalonFX rearRight = new WPI_TalonFX(kRearRightChannel);

    // Invert the right side motors.
    // You may need to change or remove this to match your robot.
    frontLeft.setInverted(true);
    rearLeft.setInverted(true);

    frontLeft.setNeutralMode(NeutralMode.Coast);
    frontRight.setNeutralMode(NeutralMode.Coast);
    rearLeft.setNeutralMode(NeutralMode.Coast);
    rearRight.setNeutralMode(NeutralMode.Coast);

    robotDrive = new MecanumDrive(frontLeft, rearLeft, frontRight, rearRight);

    stick = new Joystick(kJoystickChannel);
  }

  private static double smooth(double val, double deadzone, double max){

    if(Math.abs((val)) < deadzone){
      return 0;
    } else if (val > max) {
      return max;
    } else {
      return Math.abs(val) * Math.abs(val) * (val / Math.abs(val));

    }
  }

  private static double safety(double cmdVal, double prevVal, double maxChange) {
		double diff = cmdVal - prevVal;
		if (Math.abs(diff) < maxChange) {
			return cmdVal;
		} else {
			if (diff > 0) {
				return prevVal + maxChange;
			} else {
				return prevVal - maxChange;
			}
		
        }
    }


  double prevx = 0;
  double prevy = 0;
  double prevz = 0;

  @Override
  public void teleopPeriodic() {
    // Use the joystick X axis for lateral movement, Y axis for forward
    // movement, and Z axis for rotation.

    double x = -stick.getRawAxis(0);
    double y = stick.getRawAxis(1);
    double z = stick.getRawAxis(2);

    x = smooth(x, .05, .9);
    y = smooth(y, .05, .9);
    z = smooth(z, .05, .7);

    x = safety(x, prevx, .3);
    y = safety(y, prevy, .3);
    z = safety(z, prevz, .3);

    if(stick.getPOV() == -1){
      robotDrive.driveCartesian(y, x, z);
    } else {
      robotDrive.drivePolar(.2, stick.getPOV(), 0.0);
    }

    prevx = x;
    prevy = y;
    prevz = z;
  }
  
}