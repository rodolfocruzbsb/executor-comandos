package br.com.zurcs.executor.business.flyway;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationInfo;

public class FlywayIntegrator {

	private static final Logger logger = LogManager.getLogger(FlywayIntegrator.class.getSimpleName());

	public static void integrate() {

		final Flyway flyway = new Flyway();

		flyway.setDataSource("jdbc:h2:./database/executor", "root", "qwe123");

		flyway.setSchemas("PUBLIC");

		flyway.setEncoding("UTF-8");

		flyway.setBaselineOnMigrate(true);

		flyway.setLocations("db/migration");

		flyway.migrate();

		logMigrate(flyway);

		logger.info("************** Finalizando migrações do Flyway DB");

	}

	private static void logMigrate(Flyway flyway) {

		for (MigrationInfo i : flyway.info().all()) {

			logger.info("Tasks de migração: " + i.getVersion() + " : " + i.getDescription() + " arquivo : " + i.getScript());
		}
	}

	public static void main(String[] args) {

		FlywayIntegrator.integrate();
	}

}
