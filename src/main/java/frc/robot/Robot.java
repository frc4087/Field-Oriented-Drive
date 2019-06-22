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
import frc.robot.commands.ArcadeDrive;
import frc.robot.subsystems.Drivebase;
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
  AHRS m_gyro = new AHRS(SPI.Port.kMXP);
  Command m_autonomousCommand;
  SendableChooser<Command> m_chooser = new SendableChooser<>();

  @Override
  public void robotInit() {
    m_oi = new OI();
    m_drivebase = new Drivebase();
    m_chooser.setDefaultOption("Default Auto", new ArcadeDrive(m_oi.getDriveJoyY(), m_oi.getDriveJoyX()));
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
    double desired;
    if (m_oi.getDriveJoyY() != 0) {
      desired = Math.toDegrees(Math.atan(m_oi.getDriveJoyX() / m_oi.getDriveJoyY()));
    } else {
      desired = 90;
    }

    double desiredCA;
    if (m_oi.getDriveJoyX() >= 0) {
      if (m_oi.getDriveJoyY() >= 0) {
        desiredCA = desired;
      } else {
        desiredCA = desired + 180;
      }

    } else {
      if (m_oi.getDriveJoyY() <= 0) {
        desiredCA = desired + 180;
      } else {
        desiredCA = desired + 360;
      }
    }

    int negCorrector = m_gyro.getYaw() < 0 ? 1 : 0;
    double gyroCA = m_gyro.getYaw() % 360 + negCorrector * 360;

    double direction = desiredCA < gyroCA ? 1 : -1;
    if (Math.abs(desiredCA - gyroCA) > Math.abs(desiredCA + 360 * direction - gyroCA)) {
      desiredCA += 360 * direction;
    }

    double error = desiredCA - gyroCA;
    double kP = 0.004; // .038;
    double deriv = (error - this.previous_error) / .02;
    double kD = .002;
    previous_error = error;

    m_drivebase.arcadeDrive(m_oi.getControlJoyY(), m_oi.getDriveJoyMag()*(kP * error + kD * deriv + .25 * Math.signum(error)));

    SmartDashboard.putNumber("Desired CA", desiredCA);
    SmartDashboard.putNumber("Current CA", gyroCA);

    if (m_oi.DRIVE_JOY.getRawButtonPressed(3)) {
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
