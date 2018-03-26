package org.hibernate.bugs;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.*;
import java.util.Properties;

/**
 * https://hibernate.atlassian.net/browse/HHH-12433
 */
public class JPAUnitTestCase {

	private EntityManagerFactory entityManagerFactory;

	public static class TestPhysicalNamingStrategy implements PhysicalNamingStrategy {

		private Identifier convertName(Identifier name) {
			if (name == null) {
				return null;
			}
			return new Identifier(name.getText() + "_PHYSICAL", false);
		}

		@Override
		public Identifier toPhysicalCatalogName(Identifier name, JdbcEnvironment jdbcEnvironment) {
			return convertName(name);
		}

		@Override
		public Identifier toPhysicalSchemaName(Identifier name, JdbcEnvironment jdbcEnvironment) {
			return convertName(name);
		}

		@Override
		public Identifier toPhysicalTableName(Identifier name, JdbcEnvironment jdbcEnvironment) {
			return convertName(name);
		}

		@Override
		public Identifier toPhysicalSequenceName(Identifier name, JdbcEnvironment jdbcEnvironment) {
			return convertName(name);
		}

		@Override
		public Identifier toPhysicalColumnName(Identifier name, JdbcEnvironment jdbcEnvironment) {
			return convertName(name);
		}
	}

	@Entity
	static class Test1 {
		@Id
		public Long id;
	}

	@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"col1_id", "col2"}))
//	@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"col1_id_PHYSICAL", "col2"}))
	@Entity
	static class Test2 {
		@Id
		public Long id;

		@JoinColumn(nullable = false)
		@ManyToOne
		public Test1 col1;

		public String col2;
	}

	@Before
	public void init() {
	}

	@After
	public void destroy() {
	}

	// Entities are auto-discovered, so just add them anywhere on class-path
	// Add your tests, using standard JUnit.
	@Test
	public void hhh12433Test() throws Exception {
		Properties props = new Properties();
		props.put(AvailableSettings.HBM2DDL_AUTO, "create");
		try {
			Persistence.generateSchema("templatePU", props);
		} catch (PersistenceException e) {
			// This is actually expected..
			e.printStackTrace();
		}

		props.put(AvailableSettings.PHYSICAL_NAMING_STRATEGY, TestPhysicalNamingStrategy.class.getName());
		Persistence.generateSchema("templatePU", props);

	}
}
