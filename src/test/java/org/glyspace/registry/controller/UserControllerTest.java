package org.glyspace.registry.controller;

import org.glyspace.registry.view.User;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.web.bind.annotation.PathVariable;

import com.wordnik.swagger.annotations.ApiParam;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:springmvc-servlet.xml")
@TransactionConfiguration(defaultRollback = true, transactionManager = "transactionManager")
public class UserControllerTest extends
		AbstractTransactionalJUnit4SpringContextTests {

	@Autowired
	UserController userController;

	/**
	 * Test method for
	 * {@link org.glyspace.registry.controller.UserController#signin()}.
	 */
	@Test
	public void testSignin() throws Exception {
		userController.signin();
	}

	/**
	 * Test method for
	 * {@link org.glyspace.registry.controller.UserController#addUser(org.glyspace.registry.view.User)}
	 * .
	 */
	@Test
	public void testAddDuplicateUser() throws Exception {
		User user = new User();
		user.setAffiliation("test affiliation");
		user.setEmail("test@bluetree.jp");
		user.setFullName("test fullname");
		user.setLoginId("test");
		user.setPassword("test");
		try {
			userController.addUser(user);
			userController.addUser(user);
		} catch (ConstraintViolationException e) {
			System.out.println(e.getMessage());
			assert (e.getMessage().equals("could not execute statement"));
			return;
		}
	}

	/**
	 * Test method for
	 * {@link org.glyspace.registry.controller.UserController#addUser(org.glyspace.registry.view.User)}
	 * .
	 */
	@Test
	public void testAddUser() throws Exception {
		User user = new User();
		user.setAffiliation("test affiliation");
		user.setEmail("test@bluetree.jp");
		user.setFullName("test fullname");
		user.setLoginId("testNew");
		user.setPassword("testNew");
		userController.addUser(user);
	}

	/**
	 * Test method for {@link org.glyspace.registry.controller.UserController#recoverPassword(java.lang.String)}.
	 */
	@Test
	public void testRecoverPassword() throws Exception {
		userController.recoverPassword("aoki");
	}
	
	
}