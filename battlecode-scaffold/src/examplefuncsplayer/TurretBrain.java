package examplefuncsplayer;

import java.util.Random;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.Team;

public class TurretBrain implements Brain{

	@Override
	public void run(RobotController rc) {
		Direction[] directions = {Direction.NORTH, Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST,
                Direction.SOUTH, Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST};
        RobotType[] robotTypes = {RobotType.SCOUT, RobotType.SOLDIER, RobotType.SOLDIER, RobotType.SOLDIER,
                RobotType.GUARD, RobotType.GUARD, RobotType.VIPER, RobotType.TURRET};
        Random rand = new Random(rc.getID());
        int myAttackRange = 0;
        Team myTeam = rc.getTeam();
        Team enemyTeam = myTeam.opponent();
        
        try {
            myAttackRange = rc.getType().attackRadiusSquared;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

        while (true) {
            // This is a loop to prevent the run() method from returning. Because of the Clock.yield()
            // at the end of it, the loop will iterate once per game round.
            try {
                // If this robot type can attack, check for enemies within range and attack one
                if (rc.isWeaponReady()) {
                    RobotInfo[] enemiesWithinRange = rc.senseNearbyRobots(myAttackRange, enemyTeam);
                    RobotInfo[] zombiesWithinRange = rc.senseNearbyRobots(myAttackRange, Team.ZOMBIE);
                    if (enemiesWithinRange.length > 0) {
                        for (RobotInfo enemy : enemiesWithinRange) {
                            // Check whether the enemy is in a valid attack range (turrets have a minimum range)
                            if (rc.canAttackLocation(enemy.location)) {
                                rc.attackLocation(enemy.location);
                                break;
                            }
                        }
                    } else if (zombiesWithinRange.length > 0) {
                        for (RobotInfo zombie : zombiesWithinRange) {
                            if (rc.canAttackLocation(zombie.location)) {
                                rc.attackLocation(zombie.location);
                                break;
                            }
                        }
                    }
                }

                Clock.yield();
            } catch (Exception e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }
	}

}
