package oneapp.incture.workbox.pmc.services;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import oneapp.incture.workbox.dbproviders.DatabasePropertyProvider;

public class SingletonEMProvider {

	private static SingletonEMProvider emProvider;
	
	public EntityManager entityManager;
	
	private SingletonEMProvider() {
		EntityManagerFactory emFactory = Persistence.createEntityManagerFactory("workbox_pu", DatabasePropertyProvider.getConnectionProperties("hana"));
//		EntityManagerFactory emFactory = Persistence.createEntityManagerFactory("workbox_pu", DatabasePropertyProvider.getConnectionProperties("oracle"));
//		EntityManagerFactory emFactory = Persistence.createEntityManagerFactory("workbox_pu");
		this.entityManager = emFactory.createEntityManager();
	}
	
	public static SingletonEMProvider getInstance() {
		
		if(emProvider == null) {
			emProvider = new SingletonEMProvider();
			//emProvider.entityManager=emProvider.getEntityManager();
			System.err.println("Entity manager : "+emProvider.entityManager);
		}
		return emProvider;
	}

	/*public EntityManager getEntityManager() {
		return this.entityManager;
	}*/

	/*public void setEntityManager(EntityManager entityManager) {
		entityManager = entityManager;
	}*/
	
	/*public static void main(String[] args) {
		for(int i=0;i<5;i++)
			System.out.println("My EntityManager : "+i+" : "+SingletonEMProvider.getInstance().getEntityManager());
	}*/
}