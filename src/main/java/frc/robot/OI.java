/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.GenericHID.Hand;

/**
 * This class is the glue that binds the controls on the physical operator
 * interface to the commands and command groups that allow control of the robot.
 */
public class OI {

  // Joystick deadband; default is 0.1
  public static final double JOY_DEADZONE = 0.1;

  // Initialize joysticks
  //public final Joystick DRIVE_JOY = new Joystick(0);
  // public final XboxController DRIVE_JOY = new
  // XboxController(RobotMap.DRIVE_JOYSTICK.value);
  //public final Joystick CONTROL_JOY = new Joystick(1);
  
  public final XboxController DRIVE_JOY = new XboxController(0);
  public final XboxController CONTROL_JOY = new XboxController(1);

  // get Joystick axis values
  public double getDriveJoyXL() {
  double raw = DRIVE_JOY.getRawAxis(0);
  return Math.abs(raw) < JOY_DEADZONE ? 0.0 : raw;
   }
   //this needs to return a negative value???
  public double getDriveJoyYL() {
  double raw = DRIVE_JOY.getRawAxis(1);
  return Math.abs(raw) < JOY_DEADZONE ? 0.0 : -raw;
   }

   public double getDriveJoyXR() {
  double raw = DRIVE_JOY.getRawAxis(4);
  return Math.abs(raw) < JOY_DEADZONE ? 0.0 : raw;
   }

  //this needs to return negative value???
  public double getDriveJoyYR() {
  double raw = DRIVE_JOY.getRawAxis(5);
  return Math.abs(raw) < JOY_DEADZONE ? 0.0 : -raw;
   }

  public double getDriveJoyX() {
    double raw = DRIVE_JOY.getX();
    return Math.abs(raw) < JOY_DEADZONE ? 0.0 : raw;
  }
  
  public double getDriveJoyY() {
    double raw = DRIVE_JOY.getY();
    return Math.abs(raw) < JOY_DEADZONE ? 0.0 : raw;
  }

  public boolean getBackButtonPressed() {
    return DRIVE_JOY.getBackButtonPressed();
  }

  /*public double getDriveJoyMag() {
    return DRIVE_JOY.getMagnitude();
  }*/
  public double getMagnitude() {
    return Math.sqrt(Math.pow(getDriveJoyX(), 2) + Math.pow(getDriveJoyY(), 2));
  }

  public double getControlJoyXL() {
    double raw = CONTROL_JOY.getRawAxis(0);
    return Math.abs(raw) < JOY_DEADZONE ? 0.0 : raw;
  }

  public double getControlJoyYL() {
    double raw = CONTROL_JOY.getRawAxis(1);
    return Math.abs(raw) < JOY_DEADZONE ? 0.0 : raw;
  }

  public double getControlJoyXR() {
    double raw = CONTROL_JOY.getRawAxis(4);
    return Math.abs(raw) < JOY_DEADZONE ? 0.0 : raw;
  }

  public double getControlJoyY() {
    return CONTROL_JOY.getY();
  }

  public double getControlJoyYR() {
    double raw = CONTROL_JOY.getRawAxis(5);
    return Math.abs(raw) < JOY_DEADZONE ? 0.0 : raw;
  }
  public boolean getDriveJoyBLPressed() {
    return DRIVE_JOY.getBumperPressed(Hand.kLeft);
  }



  public OI() {

  }
}
