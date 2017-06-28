package com.github.dsaouda.fiap.devops.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.dsaouda.fiap.devops.model.Image;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.KeyFactory;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.Transaction;
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;

@Component
public class ImageDao {

	private final KeyFactory keyFactory;
	private Datastore datastore;

	@Autowired
	public ImageDao(Datastore datastore) {
		this.datastore = datastore;
		keyFactory = datastore.newKeyFactory().setKind("Image");
	}

	public Entity put(Image image) {
		Key key = datastore.allocateId(keyFactory.newKey());		

		Entity entity = Entity.newBuilder(key)
				.set("name", image.getName())				
				.set("created", image.getCreated())
				.set("updated", image.getUpdated())
				.set("jsonLabel", image.getJsonLabel())
				.set("jsonFace", image.getJsonFace())
				.set("jsonCropHint", image.getJsonCropHint())
				.set("jsonText", image.getJsonText())				
				.build();

		return datastore.put(entity);
	}
	
	
	public Entity findByName(String name) {
		Query<Entity> query = Query.newEntityQueryBuilder()
				.setKind("Image")
				.setFilter(PropertyFilter.eq("name", name))
				.build();
		
		QueryResults<Entity> queryResults = datastore.run(query);
		if (queryResults.hasNext()) {
			return queryResults.next();
		}
		
		throw new RuntimeException("Não foi encontrado registro com o nome " + name);
	}
	
	/**
	 * Exemplo transacao
	 * Transaction transaction = datastore.newTransaction();
	 *   try {
	 *     Entity task = transaction.get(keyFactory.newKey(id));
	 *     System.out.println(task);
	 *     
	 *     if (task != null) {
	 *       transaction.put(Entity.newBuilder(task).set("done", true).build());
	 *     }
	 *     transaction.commit();
	 *     
	 *   } finally {
	 *     
	 *   	if (transaction.isActive()) {
	 *   		transaction.rollback();
	 *   	}
	 *   }
	 * @param entity
	 * @return
	 */
	public Entity update(Entity entity) {		
		Transaction transaction = datastore.newTransaction();		
		Entity entityResult = transaction.put(entity);
		transaction.commit();		
		return entityResult;
	}
	

}
