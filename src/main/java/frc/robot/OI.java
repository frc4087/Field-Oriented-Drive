/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.XboxController;

/**
 * This class is the glue that binds the controls on the physical operator
 * interface to the commands and command groups that allow control of the robot.
 */
public class OI {
  //// CREATING BUTTONS
  // One type of button is a joystick button which is any button on a
  //// joystick.
  // You create one by telling it which joystick it's on and which button
  // number it is.
  // Joystick stick = new Joystick(port);
  // Button button = new JoystickButton(stick, buttonNumber);

  // There are a few additional built in buttons you can use. Additionally,
  // by subclassing Button you can create custom triggers and bind those to
  // commands the same as any other Button.

  //// TRIGGERING COMMANDS WITH BUTTONS
  // Once you have a button, it's trivial to bind it to a button in one of
  // three ways:

  // Start the command when the button is pressed and let it run the command
  // until it is finished as determined by it's isFinished method.
  // button.whenPressed(new ExampleCommand());

  // Run the command while the button is being held down and interrupt it once
  // the button is released.
  // button.whileHeld(new ExampleCommand());

  // Start the command when the button is released and let it run the command
  // until it is finished as determined by it's isFinished method.
  // button.whenReleased(new ExampleCommand());

  // Joystick deadband; default is 0.1
  public static final double JOY_DEADZONE = 0.1;

  // Initialize joysticks
  public final Joystick DRIVE_JOY = new Joystick(0);
  // public final XboxController DRIVE_JOY = new
  // XboxController(RobotMap.DRIVE_JOYSTICK.value);
  public final XboxController CONTROL_JOY = new XboxController(RobotMap.CONTROL_JOYSTICK.value);

  // get Joystick axis values
  // public double getDriveJoyXL() {
  // double raw = DRIVE_JOY.getRawAxis(0);
  // return Math.abs(raw) < JOY_DEADZONE ? 0.0 : raw;
  // }

  // public double getDriveJoyYL() {
  // double raw = DRIVE_JOY.getRawAxis(1);
  // return Math.abs(raw) < JOY_DEADZONE ? 0.0 : raw;
  // }

  // public double getDriveJoyXR() {
  // double raw = DRIVE_JOY.getRawAxis(4);
  // return Math.abs(raw) < JOY_DEADZONE ? 0.0 : raw;
  // }

  // public double getDriveJoyYR() {
  // double raw = DRIVE_JOY.getRawAxis(5);
  // return Math.abs(raw) < JOY_DEADZONE ? 0.0 : raw;
  // }

  public double getDriveJoyX() {
    return DRIVE_JOY.getX();
  }
  
  public double getDriveJoyY() {
    return DRIVE_JOY.getY();
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

  public double getControlJoyYR() {
    double raw = CONTROL_JOY.getRawAxis(5);
    return Math.abs(raw) < JOY_DEADZONE ? 0.0 : raw;
  }

  public OI() {

  }
}
