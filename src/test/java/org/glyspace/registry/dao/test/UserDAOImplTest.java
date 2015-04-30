/** 
 * Test UserDAO 
 * 
 * in order for the tests to pass, you need to have a sample entry in the database
 * 
 * @author sena
 *
 */
package org.glyspace.registry.dao.test;

import static org.junit.Assert.*;

import java.util.List;

import org.glyspace.registry.dao.UserDAO;
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
public class UserDAOImplTest extends AbstractTransactionalJUnit4SpringContextTests {
	
	@Autowired
	private UserDAO userDAO;
	 
	@Test
	public void testGetUsers() {
		 List<UserEntity> users = userDAO.getAllUsers();
		 assertFalse(users.isEmpty());
	}
}
