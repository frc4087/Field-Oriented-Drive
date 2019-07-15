/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import frc.robot.commands.ArcadeDrive;
import frc.robot.subsystems.Drivebase;
import frc.robot.subsystems.ToggledSolenoid;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.SerialPort;

import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;
import com.ctre.phoenix.motorcontrol.LimitSwitchSource;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.kauailabs.navx.frc.*;

public class Robot extends TimedRobot {
  public static Drivebase m_drivebase;
  public static OI m_oi;
  double previous_error;
  boolean shiftState = false; 
  String x;
  AHRS m_gyro = new AHRS(SPI.Port.kMXP);
  Command m_autonomousCommand;
  SendableChooser<Command> m_chooser = new SendableChooser<>();

  // Pneumatics
  Compressor c = new Compressor(0);
  public static ToggledSolenoid winchPiston = new ToggledSolenoid(2, 5);
  public static ToggledSolenoid shifters = new ToggledSolenoid(3, 4);
  public static DoubleSolenoid hatchIntake = new DoubleSolenoid(6, 7);
  public static ToggledSolenoid intakeActuator = new ToggledSolenoid(0, 1);



  @Override
  public void robotInit() {
    m_oi = new OI();
    m_drivebase = new Drivebase();
    m_chooser.setDefaultOption("Default Auto", new ArcadeDrive(m_oi.getDriveJoyYL(), m_oi.getDriveJoyXR()));
    // chooser.addOption("My Auto", new MyAutoCommand());
    SmartDashboard.putData("Auto mode", m_chooser);
  }

  @Override
  public void robotPeriodic() {
  }

  @Override
  public void disabledInit() {
  }

  @Override
  public void disabledPeriodic() {
    Scheduler.getInstance().run();
  }

  @Override
  public void autonomousInit() {
    m_autonomousCommand = m_chooser.getSelected();
    if (m_autonomousCommand != null) {
      m_autonomousCommand.start();
    }
  }

  @Override
  public void autonomousPeriodic() {
    Scheduler.getInstance().run();
  }

  @Override
  public void teleopInit() {
    if (m_autonomousCommand != null) {
      m_autonomousCommand.cancel();
    }
  }

  @Override
  public void teleopPeriodic() {
    matchPeriodic();
    Scheduler.getInstance().run();
  }

  @Override
  public void testPeriodic() {
  }

  void matchPeriodic() {
    //shifters
    if (m_oi.getDriveJoyBLPressed()) {
      shifters.togglePiston();
      shiftState = !shiftState;
    }
    SmartDashboard.putString("Gear", shiftState ? "Low" : "High");

    //field-Oriented Drive
    
    
    double desired;
    if (m_oi.getDriveJoyYR() != 0) {
      desired = Math.toDegrees(Math.atan(m_oi.getDriveJoyXR() / m_oi.getDriveJoyYR()));
    } else {
      desired = 90;
    }

    double desiredCA;
    if (m_oi.getDriveJoyXR() >= 0) {
      if (m_oi.getDriveJoyYR() >= 0) {
        desiredCA = desired;
      } else {
        desiredCA = desired + 180;
      }

    } else {
      if (m_oi.getDriveJoyYR() <= 0) {
        desiredCA = desired + 180;
      } else {
        desiredCA = desired + 360;
      }
    }

    //try removing this section
    int negCorrector = m_gyro.getYaw() < 0 ? 1 : 0;
    double gyroCA = m_gyro.getYaw() % 360 + negCorrector * 360;

    double direction = desiredCA < gyroCA ? 1 : -1;
    if (Math.abs(desiredCA - gyroCA) > Math.abs(desiredCA + 360 * direction - gyroCA)) {
      desiredCA += 360 * direction;
    }

   
    double error = desiredCA - gyroCA;
    //double kP;  // = 0.004; // .038;
    //double kI; // = 0.25;
    double deriv = (error - this.previous_error) / .02;
    //double kD; // = .002;
    previous_error = error;

    double kP = 0.004;
    double kI = 0.25;
    double kD = 0.002;
  
   /* if(error < 90){
      kP = 0.004;
      kI = 0.25;
      kD = 0.002; //needs tuning
    } else {
      kP = 0.038;
      kI = 0.25;
      kD = 0.002;  //needs tuning
    }*/
   

    m_drivebase.arcadeDrive(m_oi.getDriveJoyYL(), m_oi.getMagnitude()*(kP * error + kD * deriv + kI * Math.signum(error)));


    SmartDashboard.putNumber("Desired CA", desiredCA);
    SmartDashboard.putNumber("Current CA", gyroCA);

    if (m_oi.getBackButtonPressed()) {
      m_gyro.reset();
    }
  }

  public static void initTalon(TalonSRX motor) {

    motor.setNeutralMode(NeutralMode.Brake);
    motor.neutralOutput();
    motor.setSensorPhase(false);
    motor.configForwardLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen, 0);
    motor.configReverseLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen, 0);
    motor.configNominalOutputForward(0.0, 0);
    motor.configNominalOutputReverse(0.0, 0);

  }
}
