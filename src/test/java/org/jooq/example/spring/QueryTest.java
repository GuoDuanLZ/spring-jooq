package org.jooq.example.spring;


import org.jooq.DSLContext;
import org.jooq.example.spring.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author Lukas Eder
 * @author Thomas Darimont
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class QueryTest {

	@Autowired DSLContext dsl;

	@Autowired
	UserService userService;

	@Test
	public void getUserInfo(){

    }
}
