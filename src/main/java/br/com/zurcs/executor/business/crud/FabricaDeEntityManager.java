package br.com.zurcs.executor.business.crud;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import br.com.zurcs.commons.util.validators.IsNullUtil;

/**
 * <p>
 * <b>Title:</b> FabricaDeEntityManager.java
 * </p>
 * 
 * <p>
 * <b>Description:</b> Fabrica de EntityManager
 * </p>
 * 
 * <p>
 * <b>Company: </b> Rodolfo Cruz TI
 * </p>
 * 
 * @author Rodolfo Cruz - rodolfocruz.ti@gmail.com
 * 
 * @version 1.0.0
 */
public class FabricaDeEntityManager {

	public static final String PERSISTENCE_UNIT = "executorAmbienteCsPU";

	private static EntityManagerFactory emf;

	public static EntityManager getEntityManager() {

		if (IsNullUtil.isNull(emf)) {

			emf = Persistence.createEntityManagerFactory(FabricaDeEntityManager.PERSISTENCE_UNIT);
		}

		return emf.createEntityManager();
	}

}
