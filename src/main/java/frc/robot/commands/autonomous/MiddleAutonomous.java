package frc.robot.commands.autonomous;

import java.util.HashMap;

import com.pathplanner.lib.PathConstraints;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.PrintCommand;
import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.robot.Constants.ArmConstants;
import frc.robot.commands.DeliverUpperConeCommand;
import frc.robot.commands.FollowTrajectoryCommand;
import frc.robot.commands.LevelChargingStationCommand;
import frc.robot.common.Odometry;
import frc.robot.subsystems.ElevatorSubsystem;
import frc.robot.subsystems.GrabberSubsystem;
import frc.robot.subsystems.LedSubsystem;
import frc.robot.subsystems.PivotSubsystem;
import frc.robot.subsystems.SwerveDriveSubsystem;
import frc.robot.subsystems.TelescopeSubsystem;

public class MiddleAutonomous extends SequentialCommandGroup {

    GrabberSubsystem grabberSubsystem;
    SwerveDriveSubsystem swerveDriveSubsystem;
    Odometry odometry;
    PivotSubsystem pivotSubsystem;
    TelescopeSubsystem telescopeSubsystem;
    ElevatorSubsystem elevatorSubsystem;
    LedSubsystem ledSubsystem;

    public MiddleAutonomous(GrabberSubsystem grabberSubsystem, PivotSubsystem pivotSubsystem,
        TelescopeSubsystem telescopeSubsystem, ElevatorSubsystem elevatorSubsystem, SwerveDriveSubsystem swerveDriveSubsystem, LedSubsystem ledSubsystem, Odometry odometry, HashMap<String, Command> eventMap) {
            
        this.grabberSubsystem = grabberSubsystem;
        this.swerveDriveSubsystem = swerveDriveSubsystem;
        this.odometry = odometry;
        this.pivotSubsystem = pivotSubsystem;
        this.telescopeSubsystem = telescopeSubsystem;
        this.elevatorSubsystem = elevatorSubsystem;
        this.ledSubsystem = ledSubsystem;

        addRequirements(grabberSubsystem, telescopeSubsystem, pivotSubsystem, swerveDriveSubsystem, elevatorSubsystem);

        addCommands(
            new DeliverUpperConeCommand(pivotSubsystem, telescopeSubsystem, grabberSubsystem),
            new WaitCommand(3.2).deadlineWith(
                new ParallelCommandGroup(
                    new RunCommand(() -> telescopeSubsystem.setTelescopePosition(0.03)),
                    new FollowTrajectoryCommand("middleToChargingStation", odometry, swerveDriveSubsystem, eventMap, new PathConstraints(4, 4))
                )),
            new ParallelCommandGroup(
                new RunCommand(() -> elevatorSubsystem.setElevatorMotor(-0.9)),
                new SequentialCommandGroup(
                    new InstantCommand(() -> swerveDriveSubsystem.setFieldCentric(false)),
                    new PrintCommand("LEVELING"),
                    new LevelChargingStationCommand(odometry, swerveDriveSubsystem, ledSubsystem)
                )));
    }
    
}
