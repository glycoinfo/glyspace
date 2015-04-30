package org.glyspace.registry.utils;

import java.io.IOException;


import org.glyspace.registry.database.GlycanEntity;
import org.glyspace.registry.database.MotifEntity;
import org.glyspace.registry.database.MotifSequence;
import org.glyspace.registry.database.MotifTag;
import org.glyspace.registry.database.RoleEntity;
import org.glyspace.registry.database.SettingEntity;
import org.glyspace.registry.database.UserEntity;
import org.glyspace.registry.database.CompositionEntity;
import org.glyspace.registry.database.GlycanComposition;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.tool.hbm2ddl.SchemaExport;

public class HibernateSchemaExport {

	public static void main(String[] args) throws IOException {
		execute(args[0], Boolean.parseBoolean(args[1]), Boolean.parseBoolean(args[2]));
	}

	public static void execute(String destination, boolean create, boolean format) {
		Configuration configuration = new Configuration();
		configuration
		.addAnnotatedClass(UserEntity.class)
		.addAnnotatedClass(GlycanEntity.class)
		.addAnnotatedClass(SettingEntity.class)
		.addAnnotatedClass(RoleEntity.class)
		.addAnnotatedClass(MotifEntity.class)
		.addAnnotatedClass(MotifTag.class)
		.addAnnotatedClass(MotifSequence.class)
		.addAnnotatedClass(CompositionEntity.class)
		.addAnnotatedClass(GlycanComposition.class)
		.setProperty(Environment.DEFAULT_SCHEMA, "glyspace")
		.setProperty(Environment.DIALECT, "org.hibernate.dialect.PostgreSQLDialect")
		.setProperty(Environment.DRIVER, "org.postgresql.Driver")
		.setProperty("hibernate.id.new_generator_mappings", "true");
		
		SchemaExport schemaExport = new SchemaExport(configuration);
		schemaExport.setOutputFile(destination);
		schemaExport.setFormat(format);
		schemaExport.setDelimiter(";").execute(true, false, false, create);
		System.out.println("Schema exported to " + destination);
	}
}
