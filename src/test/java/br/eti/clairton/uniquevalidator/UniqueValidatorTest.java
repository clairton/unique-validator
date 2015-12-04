package br.eti.clairton.uniquevalidator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.util.Date;

import javax.inject.Inject;
import javax.naming.InitialContext;
import javax.persistence.EntityManager;
import javax.transaction.TransactionManager;
import javax.validation.Validator;

import org.apache.deltaspike.testcontrol.api.junit.CdiTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(CdiTestRunner.class)
public class UniqueValidatorTest {
	private final String protocol = "lkasdjfkwej" + new Date().getTime();
	private @Inject Validator validator;
	private @Inject EntityManager manager;

	@Before
	public void init() throws Exception {
		final InitialContext context = new InitialContext();
		final TransactionManager tm = (TransactionManager) context.lookup("java:/jboss/TransactionManager");
		tm.begin();
		final Connection connection = manager.unwrap(Connection.class);
		final String sql = "DELETE FROM model;";
		connection.createStatement().execute(sql);
		final Model model = new Model(protocol);
		manager.persist(model);
		manager.flush();
		manager.clear();
		tm.commit();
	}

	@Test
	public void testIsInValid() {
		final Model model = new Model(protocol);
		assertFalse(validator.validateProperty(model, "protocol").isEmpty());
	}

	@Test
	public void testIsValid() {
		final Model model = new Model(protocol + new Date().getTime());
		assertTrue(validator.validateProperty(model, "protocol").isEmpty());
	}
}