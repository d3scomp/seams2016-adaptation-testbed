package cz.cuni.mff.d3s.deeco.ros.seams2016.garbagecollection;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import cz.cuni.mff.d3s.deeco.ros.seams2016.garbagecollection.PositionGenerator.Area;
import cz.cuni.mff.d3s.deeco.runners.DEECoSimulation;
import cz.cuni.mff.d3s.deeco.runtime.DEECoNode;
import cz.cuni.mff.d3s.jdeeco.network.Network;
import cz.cuni.mff.d3s.jdeeco.network.l2.strategy.KnowledgeInsertingStrategy;
import cz.cuni.mff.d3s.jdeeco.position.Position;
import cz.cuni.mff.d3s.jdeeco.position.PositionPlugin;
import cz.cuni.mff.d3s.jdeeco.publishing.DefaultKnowledgePublisher;
import cz.cuni.mff.d3s.jdeeco.ros.BeeClick;
import cz.cuni.mff.d3s.jdeeco.ros.Positioning;
import cz.cuni.mff.d3s.jdeeco.ros.sim.ROSSimulation;

/**
 * Example of vehicles traveling across the map
 * 
 * @author Vladimir Matena <matena@d3s.mff.cuni.cz>
 *
 */
public class GarbageCollectDemo {
	private static int TOTAL_GARBAGE = 20;
	
	private static final List<Position> garbage = new LinkedList<>();
	private static final List<Position> garbage1 = new LinkedList<>();
	private static final List<Position> garbage2 = new LinkedList<>();
	
	static {
		PositionGenerator generator = new PositionGenerator(new Random(42));
		generator.addArea(new Area(00.05, 01.05, 11.1, 13.10)); // Kitchen
		generator.addArea(new Area(12.00, 13.75, 1.00, 8.00)); // Office
		
		generator.addArea(new Area(02.25, 04.75, 10.50, 13.75)); // Corridor left
		generator.addArea(new Area(06.25, 14.25, 10.50, 13.75)); // Corridor center left
		generator.addArea(new Area(15.75, 23.75, 10.50, 13.75)); // Corridor center right
		generator.addArea(new Area(25.25, 28.75, 10.50, 13.75)); // Corridor right
		
		
		
		
		// Garbage positions
		for(int i = 0; i < TOTAL_GARBAGE; ++i) {
			garbage.add(generator.getRandomPosition());
		}
		/*garbage.add(new Position(13, 2, 0));
		garbage.add(new Position(3, 14, 0));
		garbage.add(new Position(27, 14, 0));*/
		
		Collections.shuffle(garbage, new Random(42));
		
		Random r = new Random(42);
		for(Position p: garbage) {
			if(r.nextBoolean()) {
				garbage1.add(p);
			} else {
				garbage2.add(p);
			}
		}
	}
	
	public static void main(String[] args) throws Exception {
	
		ROSSimulation rosSim = new ROSSimulation("192.168.56.101", 11311, "192.168.56.1", "corridor", 0.02, 100);

		// Create main application container
		DEECoSimulation realm = new DEECoSimulation(rosSim.getTimer());
		
		// Configure loop-back networking for all nodes
		realm.addPlugin(Network.class);
		realm.addPlugin(DefaultKnowledgePublisher.class);
		realm.addPlugin(KnowledgeInsertingStrategy.class);
		realm.addPlugin(BeeClick.class);
				
		Positioning robot0Pos = new Positioning();
		DEECoNode robot0 = realm.createNode(0, robot0Pos, rosSim.createROSServices("red"), new PositionPlugin(12, 13.5));
		robot0.deployComponent(new CollectorRobot("Collector0", robot0Pos, rosSim.getTimer(), garbage1));
//		robot0.deployEnsemble(LeaderFollowerEnsemble.class);
		
		Positioning robot1Pos = new Positioning();
		DEECoNode robot1 = realm.createNode(1, robot1Pos, rosSim.createROSServices("blue"), new PositionPlugin(26, 11));
		robot1.deployComponent(new CollectorRobot("Collector1", robot1Pos, rosSim.getTimer(), garbage2));
//		robot1.deployEnsemble(LeaderFollowerEnsemble.class);
		
		// Simulate for specified time
		realm.start(180_000);
			
		System.out.println("!#!@!#!@!#@!@#!@#!@#!#!@!#!@!#@!@#!@#!@#!#!@!#!@!#@!@#!@#!@#!#!@!#!@!#@!@#!@#!@#!#!@!#!@!#@!@#!@#!@#!#!@!#!@!#@!@#");
		System.out.println("!@!#!@!#@!@#!@#!@# As we cannot make ROS exit nicely we are now going to terminate the whole JVM !@#!@#!@#!@#!@#!@#");
		System.out.println("!#!@!#!@!#@!@#!@#!@#!#!@!#!@!#@!@#!@#!@#!#!@!#!@!#@!@#!@#!@#!#!@!#!@!#@!@#!@#!@#!#!@!#!@!#@!@#!@#!@#!#!@!#!@!#@!@#");
		System.exit(0);
	}
}
