package com.hpe.adm.nga.sdk.tests.crud;

import com.hpe.adm.nga.sdk.Query;
import com.hpe.adm.nga.sdk.model.*;
import com.hpe.adm.nga.sdk.tests.base.TestBase;
import com.hpe.adm.nga.sdk.utils.CommonUtils;
import com.hpe.adm.nga.sdk.utils.QueryUtils;
import com.hpe.adm.nga.sdk.utils.generator.DataGenerator;
import org.junit.Assert;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Guy Guetta on 21/04/2016.
 */
public class UpdateEntity extends TestBase {

    public UpdateEntity() {
        entityName = "releases";
    }

    @Test
    public void testUpdateEntityById() throws Exception{

        String updatedNameValue = "updatedName" + UUID.randomUUID();
        Set<FieldModel> fields = new HashSet<>();
        Collection<EntityModel> generatedEntity = DataGenerator.generateEntityModel(nga, entityName, fields);
        Collection<EntityModel> entityModels = entityList.create().entities(generatedEntity).execute();
        EntityModel entityModel = entityModels.iterator().next();
        int entityId = CommonUtils.getIdFromEntityModel(entityModel);

        StringFieldModel nameField = new StringFieldModel("name", updatedNameValue);
        fields.add(nameField);
        EntityModel updatedEntity = new EntityModel(fields);

        entityList.at(entityId).update().entity(updatedEntity).execute();

        EntityModel getEntity = entityList.at(entityId).get().execute();

        Assert.assertTrue(CommonUtils.isEntityAInEntityB(updatedEntity, getEntity));
    }

    @Test
    public void testUpdateEntityCollectionIdInBody() throws Exception {

        List<String> updatedNameValues =  DataGenerator.generateNamesForUpdate();
        Set<FieldModel> fields = new HashSet<>();
        Collection<EntityModel> generatedEntity = DataGenerator.generateEntityModelCollection(nga, entityName);
        Collection<EntityModel> entityModels = entityList.create().entities(generatedEntity).execute();
        List<Integer> entityIds = CommonUtils.getIdFromEntityModelCollection(entityModels);

        Collection<EntityModel> updatedEntityCollection = new ArrayList<>();
        for(int i = 0; i < entityIds.size(); i++) {
            fields.clear();
            StringFieldModel nameField = new StringFieldModel("name", updatedNameValues.get(i));
            LongFieldModel id = new LongFieldModel("id", entityIds.get(i).longValue());
            fields.add(nameField);
            fields.add(id);
            EntityModel updatedEntity = new EntityModel(fields);
            updatedEntityCollection.add(updatedEntity);
        }

        Query query = QueryUtils.getQueryForIds(entityIds);

        entityList.update().entities(updatedEntityCollection).execute();

        Collection<EntityModel> getEntity = entityList.get().query(query).execute();

        Assert.assertTrue(CommonUtils.isCollectionAInCollectionB(updatedEntityCollection, getEntity));

    }

    @Test // for release entity only
    public void testUpdateEntityCollectionWithQuery() throws Exception {

        String updatedEndDateValue =  "2026-03-14T12:00:00Z";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        final Date date = simpleDateFormat.parse(updatedEndDateValue);

        Collection<EntityModel> generatedDefect = DataGenerator.generateEntityModelCollection(nga, entityName);
        Collection<EntityModel> entityModels = entityList.create().entities(generatedDefect).execute();
        List<Integer> entityIds = CommonUtils.getIdFromEntityModelCollection(entityModels);

        Collection<EntityModel> updatedEntityCollection = new ArrayList<>();

//        StringFieldModel nameField = new StringFieldModel("end_date", updatedEndDateValue);
        DateFieldModel nameField = new DateFieldModel("end_date", date);
        Set<FieldModel> fields = new HashSet<>();

        fields.add(nameField);
        EntityModel updatedEntity = new EntityModel(fields);
        updatedEntityCollection.add(updatedEntity);

        Query query = QueryUtils.getQueryForIds(entityIds);

        entityList.update().entities(updatedEntityCollection).query(query).execute();

        Collection<EntityModel> getEntity = entityList.get().query(query).execute();

        Assert.assertTrue(CommonUtils.isCollectionAInCollectionB(updatedEntityCollection, getEntity));
    }
}
