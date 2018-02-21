package db;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;
import static org.junit.Assert.*;

import org.junit.Test;

public class PupilDBTest {

	@Test
	public void testaddPupilgetScore(){
		int randomID=ThreadLocalRandom.current().nextInt(1, 10000);
		int randomScore=ThreadLocalRandom.current().nextInt(1,1000);
		PupilDB db= new PupilDB();
		db.addPupil(randomID,randomScore);
		int scoreDB=db.getScore(randomID);
		assertTrue("Inserted Pupil ID is not detected", db.isID(randomID));
		assertEquals("Inserted Score does not equal received Score.", scoreDB, randomScore);
	}
	
	
	@Test
	public void testToplist(){
		PupilDB db = new PupilDB();
		HashMap<Integer, Integer> toplist = db.getToplist(); 
		int lastScore=Integer.MAX_VALUE;
		for (int i = 2; i<11; i++){
			assertTrue("Rank " + i + "is not in toplist. Does DB contain more than 10 entries?"
					+ " Multiple entries with same rank?", toplist.containsKey(i));
			System.out.println(i + " " + lastScore + " >= " + toplist.get(i));
			assertTrue("Ranking and Score Error " + i, (toplist.get(i)<=lastScore));
			lastScore = toplist.get(i);
		}
		
	}
}
