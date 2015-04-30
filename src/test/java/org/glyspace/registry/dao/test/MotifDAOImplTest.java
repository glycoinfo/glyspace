package org.glyspace.registry.dao.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.glyspace.registry.dao.MotifDAO;
import org.glyspace.registry.database.MotifEntity;
import org.glyspace.registry.database.MotifSequence;
import org.glyspace.registry.database.MotifTag;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath:springmvc-servlet.xml")
@TransactionConfiguration(defaultRollback=true,transactionManager="transactionManager")
public class MotifDAOImplTest extends AbstractTransactionalJUnit4SpringContextTests {

	@Autowired
	MotifDAO motifDAO;
	
	@Test
	public void testAddMotif() {
		MotifEntity motif = new MotifEntity();
		motif.setName ("Something not meaningful");
		
		Set<MotifTag> tags = new HashSet<MotifTag>();
		
		MotifTag tag;
		
		if ((tag=motifDAO.getTag("fuzzy")) == null) {
			tag = new MotifTag();
			tag.setTag("fuzzy");
			motifDAO.addTag(tag);
		}
		tags.add(tag);
		
		MotifTag tag2;
		if ((tag2=motifDAO.getTag("o-glycan")) == null) {
			tag2 = new MotifTag();
			tag2.setTag("o-glycan");
			motifDAO.addTag(tag2);
		}
		tags.add(tag2);
		
		motif.setTags(tags);
		
		MotifSequence seq = new MotifSequence();
		String structure="RES\n" +
			"1b:a-dgal-HEX-1:5\n" +
			"2s:n-acetyl\n" +
			"3b:b-dglc-HEX-1:5\n" +
			"4s:n-acetyl\n" +
			"LIN\n" +
			"1:1d(2+1)2n\n" +
			"2:1o(3+1)3d\n" +
			"3:3d(2+1)4n";
		seq.setSequence(structure);
		seq.setReducing(null);
		seq.setMotif(motif);
		
		Set<MotifSequence> sequences = new HashSet<>();
		sequences.add(seq);
		motif.setSequences(sequences);
		
		motifDAO.addMotif(motif);
	}
	
	@Test
	public void testGetMotif() {
		testAddMotif();
		MotifEntity motif2 = motifDAO.getMotifByName("Something not meaningful");
		assertEquals (1, motif2.getSequences().size());
		assertEquals(2, motif2.getTags().size());
	}
	
	@Test
	public void testGetMotifsByTags () {
		testAddMotif();
		List<String> tags = new ArrayList<>();
		tags.add("fuzzy");
		tags.add("o-glycan");
		List<MotifEntity> motifs = motifDAO.getMotifsByTags(tags, true);
		assertTrue(motifs.size() > 1);
	}
	
	@Test
	public void testGetMotifTags() {
		testAddMotif();
		List<MotifTag> tags = motifDAO.getMotifTags("Something not meaningful");
		assertEquals(2, tags.size());
	}
	
	@Test
	public void testGetMotifSequences() {
		testAddMotif();
		List<MotifSequence> sequences = motifDAO.getMotifSequences("Something not meaningful");
		assertEquals(1, sequences.size());
	}
}
