/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;


import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.subsystems.Drivebase;
import frc.robot.subsystems.ToggledSolenoid;
import frc.robot.subsystems.VisionTracking;
import frc.robot.subsystems.Winch;

public class Robot extends TimedRobot {
  public static Drivebase m_drivebase;
  public OI m_oi;
  public ToggledSolenoid m_toggledsolenoid; 
  double previous_error;
  public String style;
  AHRS m_gyro = new AHRS(SPI.Port.kMXP);
  // Command m_autonomousCommand;
  // SendableChooser<Command> m_chooser = new SendableChooser<>();

  // Pneumatics
  Compressor c = new Compressor(0);
  public static ToggledSolenoid winchPiston = new ToggledSolenoid(2, 5);
  public static ToggledSolenoid shifters = new ToggledSolenoid(3, 4);
  public static DoubleSolenoid hatchIntake = new DoubleSolenoid(6, 7);
  public static ToggledSolenoid intakeActuator = new ToggledSolenoid(0, 1);


  //public static DoubleSolenoid testPiston = new DoubleSolenoid(6,7);

  // Subsystems
  public static Spark leftWinch = new Spark(4);
  public static Spark rightWinch = new Spark(5);
  public static Winch winch = new Winch(leftWinch, rightWinch, winchPiston);
  public static VisionTracking m_visiontracking = new VisionTracking();

  /* Drivebase
  public static WPI_TalonSRX LeftDrive1 = new WPI_TalonSRX(3);
  public static WPI_TalonSRX RightDrive1 = new WPI_TalonSRX(2);
  public static WPI_TalonSRX LeftDrive2 = new WPI_TalonSRX(4);
  public static WPI_TalonSRX RightDrive2 = new WPI_TalonSRX(1);
  public SpeedControllerGroup m_left = new SpeedControllerGroup(LeftDrive1, LeftDrive2);
  public SpeedControllerGroup m_right = new SpeedControllerGroup(RightDrive1, RightDrive2);
  DifferentialDrive m_drive = new DifferentialDrive(m_left, m_right);*/
  
  public boolean shiftState = true;
  boolean driveStyle = true;
  //public boolean driveStyle = true;
  

  @Override
  public void robotInit() {
    m_oi = new OI();
    m_drivebase = new Drivebase(); 
    CameraServer.getInstance().startAutomaticCapture();
    //m_chooser.setDefaultOption("Default Auto", new ArcadeDrive(m_oi.getDriveJoyY(), m_oi.getDriveJoyX()));
    // chooser.addOption("My Auto", new MyAutoCommand());
    //SmartDashboard.putData("Auto mode", m_chooser);
  }

  void matchPeriodic() {
    
    c.setClosedLoopControl(true);
    
    //vision Tracking and Drivebase
    if (m_oi.driveJoy.getXButton()) {
      m_visiontracking.setTracking(true);
      double steeringAdjust = .038 * m_visiontracking.pidX();
      m_drivebase.arcadeDrive(m_oi.getDriveJoyYL() * m_visiontracking.get("tv"), steeringAdjust);
      //get rid of the YL negative
    } else {
      m_visiontracking.setTracking(false);
      m_drivebase.curvatureDrive(m_oi.getDriveJoyYL(), m_oi.getDriveJoyXR(), m_oi.isQuickTurn());
      //get rid of the YL negative
    }
    SmartDashboard.putString("Drive Mode", m_oi.isQuickTurn() ? "Aracde" : "Curvature");

   /* if (m_oi.opJoy.getAButtonPressed()) {
      new winchDeploy();
    } else {
      winch.setWinch(-m_oi.getOpJoyYL());
    } */

   //Hatch Intake
    if (Math.abs(m_oi.driveJoy.getTriggerAxis(Hand.kLeft)) > 0.1) {
      hatchIntake.set(Value.kForward);
    } else if (Math.abs(m_oi.driveJoy.getTriggerAxis(Hand.kRight)) > .1) {
      hatchIntake.set(Value.kReverse); 
    } else {
      hatchIntake.set(Value.kOff);
    }
    
    //Intake Actuator (Neck)
    if (m_oi.getOpJoyBLPressed()) {
      intakeActuator.togglePiston();
    }
    
    //Shifters  
    if (m_oi.getDriveJoyBLPressed()) {
      shifters.togglePiston();
      shiftState = !shiftState;
    }
    SmartDashboard.putString("Gear", shiftState ? "Low" : "High");
    
    //Winch Piston/Hab Climber Piston
    if (m_oi.opJoy.getBButtonPressed()) {
      winchPiston.togglePiston();
    }
/*
    if (m_oi.getOpJoyBRPressed()) {
      testPiston.set(Value.kForward);
    } else if (m_oi.getOpJoyBLPressed()) {
      testPiston.set(Value.kReverse);
    } else {
      testPiston.set(Value.kOff);
    }*/

  }

  void fodPeriodic(){
    //Field-oriented drive

    c.setClosedLoopControl(true);

    //shifters
    if (m_oi.getDriveJoyBLPressed()) {
      shifters.togglePiston();
      shiftState = !shiftState;
    }
     //Winch Piston/Hab Climber Piston
     if (m_oi.opJoy.getBButtonPressed()) {
      winchPiston.togglePiston();
    }
     //Intake Actuator (Neck)
     if (m_oi.getOpJoyBLPressed()) {
      intakeActuator.togglePiston();
    }
    //Hatch Intake
    if (Math.abs(m_oi.driveJoy.getTriggerAxis(Hand.kLeft)) > 0.1) {
      hatchIntake.set(Value.kForward);
    } else if (Math.abs(m_oi.driveJoy.getTriggerAxis(Hand.kRight)) > .1) {
      hatchIntake.set(Value.kReverse); 
    } else {
      hatchIntake.set(Value.kOff);
    }

    //Field-Oriented Driving
    SmartDashboard.putString("Gear", shiftState ? "Low" : "High");
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

    int negCorrector = m_gyro.getYaw() < 0 ? 1 : 0;
    double gyroCA = m_gyro.getYaw() % 360 + negCorrector * 360;

    double direction = desiredCA < gyroCA ? 1 : -1;
    if (Math.abs(desiredCA - gyroCA) > Math.abs(desiredCA + 360 * direction - gyroCA)) {
      desiredCA += 360 * direction;
    }

    double error = desiredCA - gyroCA;
    double kP;
    double kI;
    double kD;
    double deriv = (error - this.previous_error) / .02;
    previous_error = error;

    if(shiftState == false){
      //high gear PID
      kP = 0.011;//0.006;
      kI = 0.15;//0.2;
      kD = 0.003;//0.0015;
    } else {
      //low gear PID
      kP = 0.02;
      kI = 0.1;
      kD = 0.002;
    }
    
    m_drivebase.arcadeDrive(m_oi.getDriveJoyYL(), m_oi.getMagnitude()*(kP * error + kD * deriv + kI * Math.signum(error)));

    SmartDashboard.putNumber("Desired CA", desiredCA);
    SmartDashboard.putNumber("Current CA", gyroCA);

    if (m_oi.driveJoy.getBackButtonPressed()) {
      m_gyro.reset();
        }
    }
  

  @Override
  public void robotPeriodic() {
  }

  @Override
  public void disabledInit() {
    c.setClosedLoopControl(false);
  }

  @Override
  public void disabledPeriodic() {
    Scheduler.getInstance().run();
  }

  @Override
  public void autonomousInit() {
    /* m_autonomousCommand = m_chooser.getSelected();
    if (m_autonomousCommand != null) {
      m_autonomousCommand.start();*/
  }

  @Override
  public void autonomousPeriodic() {
    matchPeriodic();
    Scheduler.getInstance().run();
  }

  @Override
  public void teleopInit() {
    /*if (m_autonomousCommand != null) {
      m_autonomousCommand.cancel();*/
  }

  @Override
  public void teleopPeriodic() {  
    //matchPeriodic();
    
    if(m_oi.driveJoy.getAButtonPressed()){
      driveStyle = !driveStyle;
        } 
    if(driveStyle == false){
      matchPeriodic();
   } else {
      fodPeriodic();
   } 
    SmartDashboard.putString("Drive Style", driveStyle ? "Field-Oriented" : "Curvature/Arcade");
   
    //SmartDashboard.putString("Drive Style", style);
    
   /* if(m_oi.driveJoy.getAButtonPressed()){
      matchPeriodic();
      //style = "Curvature/Field-Oriented";
    } else {
      fodPeriodic();
     // style = "Field-Oriented";
    }*/
    Scheduler.getInstance().run();
  }

  @Override
  public void testPeriodic() {
  }
  
  /*public static void initTalon(TalonSRX motor) {

    motor.setNeutralMode(NeutralMode.Brake);
    motor.neutralOutput();
    motor.setSensorPhase(false);
    motor.configForwardLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen, 0);
    motor.configReverseLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen, 0);
    motor.configNominalOutputForward(0.0, 0);
    motor.configNominalOutputReverse(0.0, 0);

  }*/
}