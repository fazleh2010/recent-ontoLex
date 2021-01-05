/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.correlation.wikipedia.table;

import static citec.correlation.core.analyzer.TextAnalyzer.POS_TAGGER_WORDS;
import citec.correlation.wikipedia.element.DBpediaEntity;
import citec.correlation.wikipedia.element.CurlSparqlQuery;
import citec.correlation.wikipedia.element.DBpediaProperty;
import citec.correlation.wikipedia.element.PropertyNotation;
import citec.correlation.wikipedia.utils.FileFolderUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 *
 * @author elahi
 */
public class EntityTable {

    private List<DBpediaEntity> dbpediaEntities = new ArrayList<DBpediaEntity>();
    private String tableName;
    
    public EntityTable(String tableName,List<DBpediaEntity> dbpediaEntities) throws Exception {
        this.tableName=tableName;
        this.dbpediaEntities=dbpediaEntities;
        FileFolderUtils.convertToJson(dbpediaEntities, tableName);
    }

    public EntityTable(String dbpediaDir,String freqClass, String freqProp,LinkedHashSet<String> entities) throws Exception {
        this.tableName=dbpediaDir+freqClass + "_" + freqProp;
        this.setProperties(entities,freqClass,freqProp);
        FileFolderUtils.convertToJson(dbpediaEntities, tableName);
    }

    private void setProperties(LinkedHashSet<String> entities, String freqClass,String freqProperty) throws Exception {
        Integer index = 0,total=entities.size();
        
        for (String entityString : entities) {
            String entityUrl = DBpediaEntity.getEntityUrl(entityString);
            CurlSparqlQuery curlSparqlQuery = new CurlSparqlQuery(entityUrl,freqProperty);
            DBpediaEntity dbpediaEntity = new DBpediaEntity(freqClass,freqProperty,entityString, curlSparqlQuery.getProperties(),POS_TAGGER_WORDS);
            dbpediaEntities.add(dbpediaEntity);
            System.out.println("entity:" + dbpediaEntity.getEntityUrl()+" count"+index+ " total:"+total); 
            //if (entityString.startsWith("A")||entityString.startsWith("a")) {
                //if (!dbpediaEntity.getProperties().isEmpty()) {
                //    System.out.println("entity:" + dbpediaEntity.getEntityUrl()+" count"+index+ " total:"+total); 
                    /*if(dbpediaEntity.getProperties().containsKey(PropertyNotation.dbo_abstract)){
                       List<String> value=dbpediaEntity.getProperties().get(PropertyNotation.dbo_abstract);
                      System.out.println("entity:" + dbpediaEntity.getEntityUrl()+" property:" +value+" count"+index+ " total:"+total); 
                    }*/
                           
                //}

            //}
           

            index++;

          
        }
    }

    public void setDbpediaEntities(List<DBpediaEntity> dbpediaEntities) {
        this.dbpediaEntities = dbpediaEntities;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<DBpediaEntity> getDbpediaEntities() {
        return dbpediaEntities;
    }

    public String getTableName() {
        return tableName;
    }

    @Override
    public String toString() {
        return "{" + "tableName=" + tableName +  "," +"dbpediaEntities=" + dbpediaEntities +'}';
    }

}
