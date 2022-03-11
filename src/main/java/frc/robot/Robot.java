// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.motorcontrol.Talon;
import edu.wpi.first.wpilibj.motorcontrol.VictorSP;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj.AnalogInput;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import com.revrobotics.CANSparkMax;
import com.revrobotics.ColorSensorV3;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.Joystick; 

/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  private static final String kDefaultAuto = "Default";
  private static final String kCustomAuto = "My Auto";
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();

  private WPI_TalonSRX intakeBar;
  private WPI_VictorSPX intakeExt;
  private WPI_VictorSPX outerRollers;
  private SingleChannelEncoder intakeExtEnc;
  private DigitalInput intakeExtChannel;
  private DigitalInput intakeSensor;
  private Timer intakeTimer;
  private Joystick joystick;
  private DigitalInput armLimit;
  private Intake intake; 

  private CANSparkMax leftFront;
  private CANSparkMax rightFront;
  private CANSparkMax leftBack;
  private CANSparkMax rightBack;
  private Drive drive;

  

  /**
   * This function is run when the robot is first started up and should be used for any
   * initialization code.
   */
  @Override
  public void robotInit() {
    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("My Auto", kCustomAuto);
    SmartDashboard.putData("Auto choices", m_chooser);
    
    intakeBar = new WPI_TalonSRX(3); //get device id 
    intakeBar.setNeutralMode(NeutralMode.Brake);
    intakeExt = new WPI_VictorSPX(1);
    intakeExtChannel = new DigitalInput(5);
    intakeExtEnc = new SingleChannelEncoder(intakeExt, intakeExtChannel);
    outerRollers = new WPI_VictorSPX(0);
    outerRollers.setNeutralMode(NeutralMode.Brake);
    intakeSensor = new DigitalInput(4); // get port for switch
    //colorSensor = new ColorSensorV3(I2C.Port.kOnboard);
    intakeTimer = new Timer();
    joystick = new Joystick(0);
    armLimit = new DigitalInput(6);
    
    //analog = new AnalogInput(0);
    intake = new Intake(intakeBar, intakeExt, outerRollers, intakeExtEnc, intakeSensor, armLimit, intakeTimer); /*colorSensor, intakeTimer, analog*/ 

    leftFront = new CANSparkMax(7, MotorType.kBrushless);
    rightFront = new CANSparkMax(5, MotorType.kBrushless);
    leftBack = new CANSparkMax(8, MotorType.kBrushless);
    rightBack = new CANSparkMax(6, MotorType.kBrushless);

    drive = new Drive(leftFront, leftBack, rightFront, rightBack);
    
  }

  /**
   * This function is called every robot packet, no matter the mode. Use this for items like
   * diagnostics that you want ran during disabled, autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before LiveWindow and
   * SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {}

  /**
   * This autonomous (along with the chooser code above) shows how to select between different
   * autonomous modes using the dashboard. The sendable chooser code works with the Java
   * SmartDashboard. If you prefer the LabVIEW Dashboard, remove all of the chooser code and
   * uncomment the getString line to get the auto name from the text box below the Gyro
   *
   * <p>You can add additional auto modes by adding additional comparisons to the switch structure
   * below with additional strings. If using the SendableChooser make sure to add them to the
   * chooser code above as well.
   */
  @Override
  public void autonomousInit() {
    m_autoSelected = m_chooser.getSelected();
    // m_autoSelected = SmartDashboard.getString("Auto Selector", kDefaultAuto);
    System.out.println("Auto selected: " + m_autoSelected);
  }

  /** This function is called periodically during autonomous. */
  @Override
  public void autonomousPeriodic() {
    switch (m_autoSelected) {
      case kCustomAuto:
        // Put custom auto code here
        break;
      case kDefaultAuto:
      default:
        // Put default auto code here
        break;
    }
  }

  /** This function is called once when teleop is enabled. */
  @Override
  public void teleopInit() {
    //intake.timerReset();
  }

  /** This function is called periodically during operator control. */
  @Override
  public void teleopPeriodic(){
    //intake.displayMethod();
    //drive.driveDisplay();
    //intake.intake(joystick.getY());

    //drive.arcadeDrive(-joystick.getX(), -joystick.getY());
    //SmartDashboard.putNumber("ENC", intakeExtEnc.get());
    if (joystick.getRawAxis(3) > 0 ){
      SmartDashboard.putString("MODE", "METHODS");
      if (joystick.getRawButton(1)){ //get button
        drive.arcadeDrive(joystick.getX()/2, joystick.getY()/2);
        intake.setIntakeMode(); // if button 1 is pressed, motor will intake 

      }
  
      else if(joystick.getRawButton(2)) { // if button 2 is pressed, motor will outtake 
        intake.setOutakeMode();
      }
  
      else if (joystick.getRawButton(3)){ // if button 3 is pressed, motor will be set to feeding mode
        intake.setFeedingMode();
      }
  
      else if (joystick.getRawButton(4)){ // if button 4 is pressed, motor will move forward ot be set into override mode
        intake.setOverrideMode();
      }
  
      else if (joystick.getRawButton(5)){
        intake.setRetract();

      }
  
      else if (joystick.getRawButton(6)){
        intake.setExtend();
      } 

      else if (joystick.getRawButton(7)){
        intake.setMidway();
      }
  
      else{
        intake.setStopMode(); // if no buttons are pressed, the motor will not move 
      }
      
    }
    else if (joystick.getRawAxis(3) < 0){
      SmartDashboard.putString("MODE", "MANUAL");
      if (joystick.getRawButton(1)){
        intake.setTestingMode();
        intake.setIntakeSpeed(joystick.getY(), joystick.getY());
      }
      else if (joystick.getRawButton(2)){
        intake.setTestingMode();
        intake.manualIntakeExt(joystick.getY());
      }
      else{
        intake.setIntakeSpeed(0, 0);
        intake.manualIntakeExt(0);
      }

      if(joystick.getRawButton(3)){
        intakeExtEnc.reset();
      }
      intake.displayMethod();
      intake.run();
    }
  }

  /** This function is called once when the robot is disabled. */
  @Override
  public void disabledInit() {}

  /** This function is called periodically when disabled. */
  @Override
  public void disabledPeriodic() {
    
  }

  /** This function is called once when test mode is enabled. */
  @Override
  public void testInit() {}

  /** This function is called periodically during test mode. */
  @Override
  public void testPeriodic() {}
}
