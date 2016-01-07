package examplefuncsplayer;

import java.util.ArrayList;
import java.util.Random;

import battlecode.common.*;

public class SoldierBrain implements Brain {

	static RobotController rc;
    static ArrayList<MapLocation> past = new ArrayList<MapLocation>();
    static int sameLoc = 0;
    
	@Override
	public void run(RobotController rcI) {
		rc = rcI;
		int myAttackRange = 0;
		try {
			// Any code here gets executed exactly once at the beginning of the
			// game.
			myAttackRange = rc.getType().attackRadiusSquared;
		} catch (Exception e) {
			// Throwing an uncaught exception makes the robot die, so we need to
			// catch exceptions.
			// Caught exceptions will result in a bytecode penalty.
			System.out.println(e.getMessage());
			e.printStackTrace();
		}

		while (true) {
			// This is a loop to prevent the run() method from returning.
			// Because of the Clock.yield()
			// at the end of it, the loop will iterate once per game round.
			try {
				runTurn();
				Clock.yield();
			} catch (Exception e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
			}
		}
	}

	public static void runTurn() throws GameActionException {
		Direction[] directions = { Direction.NORTH, Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST,
				Direction.SOUTH, Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST };
		RobotType[] robotTypes = { RobotType.SCOUT, RobotType.SOLDIER, RobotType.SOLDIER, RobotType.SOLDIER,
				RobotType.GUARD, RobotType.GUARD, RobotType.VIPER, RobotType.TURRET };
		int myAttackRange = 0;
		Team myTeam = rc.getTeam();
		Team enemyTeam = myTeam.opponent();
		MapLocation init = rc.getLocation();
		boolean shouldAttack = false;
		// If this robot type can attack, check for enemies within range and
		// attack one
		if (rc.getType().canAttack() && myAttackRange > 0) {
			RobotInfo[] enemiesWithinRange = rc.senseNearbyRobots(myAttackRange, enemyTeam);
			RobotInfo[] zombiesWithinRange = rc.senseNearbyRobots(myAttackRange, Team.ZOMBIE);
			if (enemiesWithinRange.length > 0) {
				shouldAttack = true;
				// Check if weapon is ready
				if (rc.isWeaponReady()) {
					rc.attackLocation(enemiesWithinRange[0].location);
				}
			} else if (zombiesWithinRange.length > 0) {
				shouldAttack = true;
				// Check if weapon is ready
				if (rc.isWeaponReady()) {
					rc.attackLocation(zombiesWithinRange[0].location);
				}
			}
		}
		if (!shouldAttack) {
			if (rc.isCoreReady()) {
				Boolean move = false;
				Direction lowest = directions[0];
				double L = rc.senseRubble(rc.getLocation().add(directions[0]));
				for (int i = 0; i < 8; i++) {
					Direction d = directions[i];
					if (rc.canMove(d)&&rc.senseRubble(rc.getLocation().add(d)) < GameConstants.RUBBLE_OBSTRUCTION_THRESH) {
						if (!past.contains(rc.getLocation().add(d))) {
							move = true;
							rc.move(d);
							past.add(rc.getLocation());
							if(past.size()>50)
							{
								past.remove(0);
							}
							break;
						}
						else{
							sameLoc++;
							if(sameLoc>15)
							{
								for(int j=0;j<past.size()/5;j++)
									past.remove(past.size()-1);
							}
						}
					} else {
						if (rc.senseRubble(rc.getLocation().add(d)) < L) {
							lowest = d;
						}
					}
				}
				if (!move) {
					rc.clearRubble(lowest);
				}
			}
		}
	}

}
