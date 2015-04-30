/**
 * 
 */
package org.glyspace.registry.controller;

import java.io.File;
import java.io.FileInputStream;
import java.security.Principal;

import javax.ws.rs.core.Response.Status;

import org.glyspace.registry.database.UserEntity;
import org.glyspace.registry.service.UserManager;
import org.glyspace.registry.view.Glycan;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithSecurityContextTestExcecutionListener;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.context.web.ServletTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.boolex.Matcher;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;

/**
 * @author aoki
 *
 */

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = "classpath:springmvc-servlet.xml")
@TransactionConfiguration(defaultRollback = true, transactionManager = "transactionManager")
@TestExecutionListeners(listeners = { ServletTestExecutionListener.class,
		DependencyInjectionTestExecutionListener.class,
		DirtiesContextTestExecutionListener.class,
		TransactionalTestExecutionListener.class,
		WithSecurityContextTestExcecutionListener.class })
// @Transactional
public class GlycanControllerTest {

	private static Logger logger = (Logger) LoggerFactory
			.getLogger("org.glyspace.registry.controller.GlycanControllerTest");
	UserEntity user = null;
	Principal principal = null;

	@Autowired
	GlycanController glycanController;

	@Autowired
	UserManager userManager;

	private static String SEC_CONTEXT_ATTR = HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;

	@Autowired
	private FilterChainProxy springSecurityFilterChain;

	@Autowired
	private WebApplicationContext wac;
	private MockMvc mockMvc;

	@Before
	public void setup() {
		user = userManager.getUserByLoginId("testtest", true, true); // only
																		// validated
																		// and
																		// active
																		// users
																		// can
																		// use
																		// the
																		// system
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac)
				.addFilters(this.springSecurityFilterChain).build();
		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String hashedPassword = passwordEncoder.encode(user.getPassword());
		user.setPassword(hashedPassword);
		principal = new UsernamePasswordAuthenticationToken("testtest",
				user.getPassword());
	}

	@Test
	public void requiresAuthentication() throws Exception {
		mockMvc.perform(get("/service/users/get/test").header("Accept", "json"))
				.andExpect(status().is(401));
	}

	/**
	 * Test method for
	 * {@link org.glyspace.registry.controller.GlycanController#batchGlycanSubmit(java.lang.String, org.springframework.web.multipart.MultipartFile, java.security.Principal)}
	 * .
	 */
	@Test
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	public void testBatchGlycanSubmitDir() throws Exception {
		File dir = new File(
				"./data/usr/local/GlycomeDB/export/GlycoCT_condenced/");
		File[] directoryListing = dir.listFiles();
		if (directoryListing != null) {
			for (File child : directoryListing) {
				FileInputStream fis = new FileInputStream(child);
				MockMultipartFile glycanFile = new MockMultipartFile("data",
						fis);

				glycanController.batchGlycanSubmit("glycoct", glycanFile,
						principal);
			}
		}
	}

	/**
	 * Test method for
	 * {@link org.glyspace.registry.controller.GlycanController#batchGlycanSubmit(java.lang.String, org.springframework.web.multipart.MultipartFile, java.security.Principal)}
	 * .
	 */
	@Test
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	public void testBatchGlycanSubmitFile() throws Exception {

		File file = new File("./data/GlycoCTList.txt");
		FileInputStream fis = new FileInputStream(file);
		MockMultipartFile glycanFile = new MockMultipartFile("data", fis);

		glycanController.batchGlycanSubmit("glycoct", glycanFile, principal);
	}

	/**
	 * Test method for
	 * {@link org.glyspace.registry.controller.GlycanController#batchGlycanSubmit(java.lang.String, org.springframework.web.multipart.MultipartFile, java.security.Principal)}
	 * .
	 */
	@Test
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	public void testBatchGlycanSubmit() throws Exception {
		MockMultipartFile glycanFile = new MockMultipartFile(
				"data",
				"filename.txt",
				"text/plain",
				"RES\n1b:x-dgro-dgal-NON-2:6|1:a|2:keto|3:d\n2s:n-glycolyl\nLIN\n1:1d(5+1)2n\n///"
						.getBytes());
		glycanController.batchGlycanSubmit("glycoct", glycanFile, principal);
	}

	/**
	 * Test method for
	 * {@link org.glyspace.registry.controller.GlycanController#glycanListAdd(org.glyspace.registry.view.GlycanInputList, java.security.Principal)}
	 * .
	 */
	@Test
	public void testSubmitStructure() throws Exception {
		Glycan glycan = new Glycan();
		glycan.setEncoding("glycoct");
		glycan.setStructure("RES\n1b:x-dgro-dgal-NON-2:6|1:a|2:keto|3:d\n2s:n-glycolyl\nLIN\n1:1d(5+1)2n\n");
		glycanController.submitStructure(glycan, principal);
	}
}