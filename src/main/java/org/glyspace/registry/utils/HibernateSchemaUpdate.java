package org.glyspace.registry.utils;

import java.io.IOException;

import org.apache.commons.dbcp.BasicDataSource;
import org.glyspace.registry.database.GlycanEntity;
import org.glyspace.registry.database.MotifEntity;
import org.glyspace.registry.database.MotifSequence;
import org.glyspace.registry.database.MotifTag;
import org.glyspace.registry.database.RoleEntity;
import org.glyspace.registry.database.SettingEntity;
import org.glyspace.registry.database.UserEntity;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.tool.hbm2ddl.SchemaUpdate;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class HibernateSchemaUpdate {
	public static void main(String[] args) throws IOException {
		
		execute(args[0], Boolean.parseBoolean(args[1]));
	}

	public static void execute(String destination, boolean format) {
		
		ApplicationContext applicationContext =
                new ClassPathXmlApplicationContext("springmvc-servlet.xml");
    	BasicDataSource dataSource = (BasicDataSource) applicationContext.getBean("dataSource");
    	
		Configuration configuration = new Configuration();
		configuration
		.addAnnotatedClass(UserEntity.class)
		.addAnnotatedClass(GlycanEntity.class)
		.addAnnotatedClass(SettingEntity.class)
		.addAnnotatedClass(RoleEntity.class)
		.addAnnotatedClass(MotifEntity.class)
		.addAnnotatedClass(MotifTag.class)
		.addAnnotatedClass(MotifSequence.class)
		.setProperty(Environment.DEFAULT_SCHEMA, "glyspace")
		.setProperty(Environment.DIALECT, "org.hibernate.dialect.PostgreSQLDialect")
		.setProperty(Environment.DRIVER, "org.postgresql.Driver")
		.setProperty("hibernate.id.new_generator_mappings", "true")
		.setProperty(Environment.URL, dataSource.getUrl())
		.setProperty(Environment.USER, dataSource.getUsername())
		.setProperty(Environment.PASS, dataSource.getPassword());
		
		SchemaUpdate schemaUpdate = new SchemaUpdate(configuration);
		schemaUpdate.setOutputFile(destination);
		schemaUpdate.setFormat(format);
		schemaUpdate.setDelimiter(";");
		schemaUpdate.execute(true, false);
		System.out.println("Schema update created in " + destination);
	}
}
