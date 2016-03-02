package br.eti.clairton.uniquevalidator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.util.Date;
import java.util.Set;

import javax.inject.Inject;
import javax.naming.InitialContext;
import javax.persistence.EntityManager;
import javax.transaction.TransactionManager;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.apache.deltaspike.testcontrol.api.junit.CdiTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(CdiTestRunner.class)
public class UniqueValidatorIntegrationTest {
	private final String protocol = "lkasdjfkwej" + new Date().getTime();
	private @Inject Validator validator;
	private @Inject EntityManager manager;
	private Model model;
	private Model2 model2;

	@Before
	public void init() throws Exception {
		final InitialContext context = new InitialContext();
		final TransactionManager tm = (TransactionManager) context.lookup("java:/jboss/TransactionManager");
		tm.begin();
		final Connection connection = manager.unwrap(Connection.class);
		final String sql = "DELETE FROM model; DELETE FROM model2;";
		connection.createStatement().execute(sql);
		model = new Model(protocol);
		manager.persist(model);
		model2 = new Model2(protocol);
		manager.persist(model2);
		manager.persist(model);
		manager.flush();
		manager.clear();
		tm.commit();
	}

	@Test
	public void testIsInValid() {
		final Model model = new Model(protocol);
		final Set<ConstraintViolation<Model>> violations = validator.validate(model);
		assertFalse(violations.isEmpty());
		final ConstraintViolation<Model> violation = violations.iterator().next();
		assertEquals("The value already exists in Data Base.", violation.getMessage());
		assertEquals("protocol", violation.getPropertyPath().toString());
	}

	@Test
	public void testIsValid() {
		final Model model = new Model(protocol + new Date().getTime());
		assertTrue(validator.validate(model).isEmpty());
	}
	
	@Test
	public void testIsInValidWhenUpdate() throws Exception{
		final InitialContext context = new InitialContext();
		final TransactionManager tm = (TransactionManager) context.lookup("java:/jboss/TransactionManager");
		tm.begin();
		manager.joinTransaction();
		final Model model = new Model(protocol + "1466576");
		manager.persist(model);
		manager.flush();
		manager.clear();
		tm.commit();
		assertTrue(validator.validate(model).isEmpty());
		model.setProtocol(protocol);
		assertFalse(validator.validate(model).isEmpty());
	}

	@Test
	public void testIsValidWhenUpdate() {
		assertTrue(validator.validate(model).isEmpty());
	}

	@Test
	public void testTwoColumnsTheSameIsInvalid() {
		final Model2 model2 = new Model2(protocol);
		assertFalse(validator.validate(model2).isEmpty());
	}

	@Test
	public void testTwoColumnsWhenIsValid() {
		final Model2 model2 = new Model2(protocol);
		model2.deactive();
		assertTrue(validator.validate(model2).isEmpty());
	}
}