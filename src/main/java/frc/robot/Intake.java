package frc.robot;

import edu.wpi.first.wpilibj.motorcontrol.MotorController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.DigitalInput; 
import edu.wpi.first.wpilibj.Timer;
import com.revrobotics.ColorSensorV3;
import edu.wpi.first.wpilibj.AnalogInput;

public class Intake {

    private MotorController intakeMotor; // the intake motor
    //private DigitalInput holdSwitch; // limit switch
    private double slowIntakeSpeed = 0.25;
    private double intakeSpeed = 0.5; // the speed of the intake motor
    private double fastIntakeSpeed = 0.75;
    private double outPutSpeed = 0.2;
    private ColorSensorV3 colorSensor; // color sensor
    private Timer runDelay;
    private double farDelay = 12;
    private double closeDelay = 1.3;
    //private int targetRange = 80; // sweeeeet sweeeeet target spot for sensor
    private int farCounter = 0;
    private int closeCounter = 0;
    private int farSpot = 50; // high 50s - low 70s
    private int closeSpot = 280; //  180s - low 200s
    private int farLimit = 80;
    private int closeLimit = 90;
    private AnalogInput analog;
    

    public Intake(MotorController newIntakeMotor, /*DigitalInput newHoldSwitch,*/ ColorSensorV3 newColorSensorV3, Timer newRunDelay, AnalogInput newAnalog){
        intakeMotor = newIntakeMotor;
        //holdSwitch = newHoldSwitch;
        colorSensor = newColorSensorV3;
        runDelay = newRunDelay;
        analog = newAnalog;
    }

    public enum state{ // states of the intake
        INTAKING, OUTTAKING, FEEDING, OVERRIDE, TESTING, STOP
    }

    public state mode = state.STOP; 

    public void setIntakeMode(){ // sets mode to intaking
        mode = state.INTAKING;
    }

    public void setOutakeMode(){ // sets mode to outtake
        mode = state.OUTTAKING;
    }

    public void setFeedingMode(){ // sets mode to feeding mode
        mode = state.FEEDING;
    }

    public void setOverrideMode(){
        mode = state.OVERRIDE;
    }

    public void setTestingMode(){ // sets mode to testing mode
        mode = state.TESTING;
    }

    public void setStopMode(){ // sets mode to stop
        mode = state.STOP;
    }
    
    public int getDistance(){
        return colorSensor.getProximity();
    }

    /*public boolean targetSpot(){ //checks the limit switch if it is being triggered or not
        return getDistance() >= ;
    }
    */

    public boolean ballHasEntered(){
        return getDistance() >= farSpot;
    }

    public boolean farFromTarget(){
        return getDistance() <= farLimit;
    }

    public boolean closeToTarget(){
        return getDistance() >= closeLimit;
    }

    public boolean targetRange(){
        if (getDistance() >= farLimit && getDistance() <= closeLimit){
            return true;
        }
        else{
            return false;
        }
    }
 
    public boolean farRangeCheck(){ // checks if the distance the sensor senses is in the range of the far spot
        if (getDistance() >= farSpot && getDistance() <= farLimit){
            return true;
        }
        else{
            return false;
        }
    }

    public boolean closeRangeCheck(){ // checks if the distance the sensor senses is in the range of the close spot
        if (getDistance() <= closeSpot && getDistance() >= closeLimit){
            return true;
        }
        else{
            return false;
        }
    }

    public void timerReset(){
        runDelay.reset();
        runDelay.stop();
    }
    public void intake(double speed){ //method for the motor intaking
        intakeMotor.set(-speed);
    }

    public void output(double speed){ //output or outtaking
        intakeMotor.set(speed);
    }

    public void stopMotor(){ // stops motor
        intakeMotor.set(0);
    }


    private void motorCheckIntake(){ //intakes cargo and holds it when switch is being triggered
        if(ballHasEntered()){
            if(farRangeCheck()){
                switch(farCounter){
                    case 0:
                        runDelay.start();
                        farCounter++;
                    break;

                    case 1:
                        if(runDelay.get() >= farDelay){
                            runDelay.stop();
                            farCounter++;
                        }

                        else{
                            intake(intakeSpeed);
                        }
                    break;

                    case 2:
                        stopMotor();
                        if(!ballHasEntered()){
                            runDelay.reset();
                            farCounter = 0;
                        }
                    break;
                    }
                }
                else if(closeRangeCheck()){
                    switch(closeCounter){
                        case 0:
                            runDelay.start();
                            closeCounter++;
                        break;

                        case 1:
                            if(runDelay.get() >= closeDelay){
                                runDelay.stop();
                                closeCounter++;
                            }
                            else{
                                intake(intakeSpeed);
                            }
                        break;

                        case 2:
                        stopMotor();
                        if(!ballHasEntered()){
                            runDelay.reset();
                            closeCounter = 0;
                        }
                        break;
                    }
                }
                else{
                    intake(intakeSpeed);
                }
            }        
        }
          

        /*
                if(ballHasEntered [value > 50]){
                    if(its in far range){
                        switch(case){
                            case 0:
                            startTimer
                            case++

                            case 1:
                            if (timer > timerReached){
                                stopTimer;
                                case++
                            }

                            else{
                                runIntake
                            }

                            case 2:
                            stopMotor



                        }
                        act accordingly
                    }

                    else if(its in close range){
                        act accordingly
                    }

                    else [its in the "perfect zone"]{
                        act normally
                    }
                }

        */

    private void feeding(){ // feeds the ball into the shooter
        if (targetRange()){
            intake(intakeSpeed);
        }
        else{
            stopMotor();
        }
    }

    public void displayMethod(){
       // SmartDashboard.putBoolean("Limit switch", cargoCheck()); // displays if the limit switch is being triggered
        SmartDashboard.putString("Mode", mode.toString()); // displays the current state of the intake
        SmartDashboard.putNumber("Cargo Distance", getDistance());
        SmartDashboard.putNumber("Timer", runDelay.get());
        SmartDashboard.putNumber("Close Counter", closeCounter);
        SmartDashboard.putNumber("Far Counter", farCounter);
        SmartDashboard.putNumber("voltage", analog.getVoltage());
        SmartDashboard.putNumber("Accumulator Count", analog.getAccumulatorCount());
        SmartDashboard.putNumber("Accumulator value", analog.getAccumulatorValue());
        SmartDashboard.putNumber("Average bits", analog.getAverageBits());
        SmartDashboard.putNumber("Average value", analog.getAverageValue());
        SmartDashboard.putNumber("Average voltage", analog.getAverageVoltage());
        SmartDashboard.putNumber("Channel", analog.getChannel());
        //tDashboard.putNumber("LSB weight", analog.getLSBWeight());
        SmartDashboard.putNumber("Offset", analog.getOffset());
        SmartDashboard.putNumber("Oversample bits", analog.getOversampleBits());
        SmartDashboard.putNumber("Value", analog.getValue());
    }


    public void run(){

        switch(mode){

            case INTAKING: // sets intake to intaking stage
            motorCheckIntake();
            break; 

            case OUTTAKING: // sets intake to outtaking stage
            output(outPutSpeed);
            break;

            case FEEDING: // sets intake to feeding stage
            feeding();
            break;

            case OVERRIDE: // overrides sensor
            intake(intakeSpeed);
            break;

            case STOP: // sets motor to stop stage
            stopMotor();
            break;

            case TESTING: //sets to testing stage
            break;
        }
    }

}
