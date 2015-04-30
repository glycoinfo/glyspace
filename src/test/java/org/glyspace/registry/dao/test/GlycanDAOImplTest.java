package org.glyspace.registry.dao.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.glyspace.registry.dao.GlycanDAO;
import org.glyspace.registry.dao.UserDAO;
import org.glyspace.registry.database.GlycanEntity;
import org.glyspace.registry.database.UserEntity;
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
public class GlycanDAOImplTest extends
		AbstractTransactionalJUnit4SpringContextTests {
	
	@Autowired
	GlycanDAO glycanDAO;

	@Autowired 
	UserDAO userDAO;
	
	@Test
	public void testAddGlycan() {
		GlycanEntity glycan = new GlycanEntity();
		UserEntity user = userDAO.getUser(1); // our test user ---- needs to be in the database for the tests to run
		assertFalse(user == null);
		glycan.setContributor(user);
		String structure="RES\n" +
		"1b:b-dglc-HEX-1:5\n" +
		"2s:n-acetyl\n" +
		"3b:b-dglc-HEX-1:5\n" +
		"4s:n-acetyl\n" +
		"5b:b-dman-HEX-1:5\n" +
		"6b:a-dman-HEX-1:5\n" +
		"7b:b-dglc-HEX-1:5\n" +
		"8s:n-acetyl\n" +
		"9b:b-dgal-HEX-1:5\n" +
		"10b:a-dgro-dgal-NON-2:6|1:a|2:keto|3:d\n" +
		"11b:a-dman-HEX-1:5\n" +
		"12b:b-dglc-HEX-1:5\n" +
		"13s:n-acetyl\n" +
		"14b:b-dgal-HEX-1:5\n" +
		"15b:a-dgro-dgal-NON-2:6|1:a|2:keto|3:d\n" +
		"16s:n-acetyl\n" +
		"LIN\n" +
		"1:1d(2+1)2n\n" +
		"2:1o(4+1)3d\n" +
		"3:3d(2+1)4n\n" +
		"4:3o(4+1)5d\n" +
		"5:5o(3+1)6d\n" +
		"6:6o(2+1)7d\n" +
		"7:7d(2+1)8n\n" +
		"8:7o(4+1)9d\n" +
		"9:9o(6+2)10d\n" +
		"10:5o(6+1)11d\n" +
		"11:11o(2+1)12d\n" +
		"12:12d(2+1)13n\n" +
		"13:12o(4+1)14d\n" +
		"14:14o(3+2)15d\n" + 
		"15:15d(5+1)16n";
		
		glycan.setStructure(structure);
		glycan.setStructureLength(structure.length());
		
		try {
			glycanDAO.addGlycan(glycan);
			assertTrue("added glycan with id " + glycan.getGlycanId(), true);
			System.out.println ("added glycan with id " + glycan.getGlycanId());
			System.out.println (glycan.getAccessionNumber());
			System.out.println (glycan.getDateEntered());
		} catch (Exception e) {
			e.printStackTrace();
			assertFalse("Exception occurred while adding a glycan", true);
		}
	}
}
