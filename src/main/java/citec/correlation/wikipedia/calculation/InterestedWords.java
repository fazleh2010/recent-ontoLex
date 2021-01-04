/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.correlation.wikipedia.calculation;

import citec.correlation.core.analyzer.Analyzer;
import citec.correlation.core.analyzer.TextAnalyzer;
import citec.correlation.wikipedia.element.DBpediaEntity;
import citec.correlation.wikipedia.parameters.WordThresold;
import citec.correlation.wikipedia.utils.FileFolderUtils;
import citec.correlation.wikipedia.utils.SortUtils;
import citec.correlation.wikipedia.table.Tables;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.javatuples.Pair;

/**
 *
 * @author elahi
 */
public class InterestedWords implements WordThresold {

    private Map<String, List<String>> propertyInterestedWords = new HashMap<String, List<String>>();
    private Map<String, String> posTagger = new HashMap<String, String>();
    private Integer numberOfEntitiesToLimitInFile = -1;
    private Integer listSize = -1;
    private List<String> sortFiles = new ArrayList<String>();
    private Tables tables = null;
    private String className = null;
    private String outputLocation = null;
    private Set<String> properties = new HashSet<String>();

    public InterestedWords(String inputDir, String className) throws Exception {
        this.tables = new Tables(inputDir);
        this.className = className;
        this.outputLocation = tables.getEntityTableDir() + SELTECTED_WORDS_DIR;
        this.prepareInterestingWords(inputDir);
        this.getWords();
    }

    private void prepareInterestingWords(String inputDir) throws IOException, Exception {
        List<File> allFiles = FileFolderUtils.getFiles(inputDir, ".json");
        if (allFiles.isEmpty()) {
            throw new Exception("There is no files in " + inputDir + " to generate properties!!");
        }

        for (File file : allFiles) {
            System.out.println(file.getName());
              /* if (!file.getName().contains("dbo:party")) {
                continue;
            }*/
               System.out.println("file.getName():"+file.getName());
            String property = this.getProperty(file);
            ObjectMapper mapper = new ObjectMapper();
            List<DBpediaEntity> dbpediaEntitys = mapper.readValue(file, new TypeReference<List<DBpediaEntity>>() {
            });
            posTagger = new HashMap<String, String>();
            this.prepareInterestingWords(property, dbpediaEntitys);
        }
    }

    public void getWords() throws IOException {
        for (String sortFileName : sortFiles) {
            //System.out.println("sortFileName:"+sortFileName);
            List<String> interestedWords = FileFolderUtils.getSortedList(sortFileName, wordFoundInNumberOfEntities, TopNwords);
            List<String> alphabeticSorted = new ArrayList<String>();
            alphabeticSorted.addAll(interestedWords);
            Collections.sort(alphabeticSorted);
            String tableName = new File(sortFileName).getName().replace(FILE_NOTATION, "");
            if (!alphabeticSorted.isEmpty()) {
                propertyInterestedWords.put(tableName, alphabeticSorted);
                //System.out.println("property:"+tableName);
                //System.out.println("words:"+alphabeticSorted);
            }
        }
        /*for(String property:propertyInterestedWords.keySet()){
            System.out.println("property:"+property);
            System.out.println("words:"+propertyInterestedWords.get(property));

        }*/

    }

    public void prepareInterestingWords(String property, List<DBpediaEntity> dbpediaEntitys) throws Exception {
        String str = this.prepareForAllProperties(dbpediaEntitys, numberOfEntitiesrmSelected);
        if (str != null) {
            String sortFile = outputLocation + className + "_" + property + FILE_NOTATION;
            FileFolderUtils.stringToFiles(str, sortFile);
            this.sortFiles.add(sortFile);
        }
    }

    /*private String prepareForAllProperties(List<DBpediaEntity> dbpediaEntities) throws Exception {
        Map<String, Integer> mostCommonWords = new HashMap<String, Integer>();
        for (DBpediaEntity dbpediaEntity : dbpediaEntities) {
           
            Set<String> words = this.wordHash(dbpediaEntity.getText());
            for (String word : words) {
                word = word.toLowerCase().trim();
                if (TextAnalyzer.ENGLISH_STOPWORDS.contains(word)) {
                    continue;
                }
                if (TextAnalyzer.MONTH.contains(word)) {
                    continue;
                }
                //System.out.println("word"+word);
                Integer count = 0;
                if (mostCommonWords.containsKey(word)) {
                    count = mostCommonWords.get(word);
                    count = count + 1;
                    mostCommonWords.put(word, count);
                } else {
                    count = count + 1;
                    mostCommonWords.put(word, count);
                }
            }
        }
        return SortUtils.sort(mostCommonWords, new TreeMap<String, String>(), numberOfEntitiesToLimitInFile);
    }*/

    private String prepareForAllProperties(List<DBpediaEntity> dbpediaEntities, Integer numberEntitiesSelected) throws Exception {
        Map<String, Integer> mostCommonWords = new HashMap<String, Integer>();
        Integer index = 0;
        Integer total=dbpediaEntities.size(),entitySize=100;
        
        for (DBpediaEntity dbpediaEntity : dbpediaEntities) {
            index = index + 1;
              if (index >entitySize) 
                  break;
        
           Analyzer analyzer = new Analyzer(dbpediaEntity.getText(), TextAnalyzer.POS_TAGGER_WORDS, numberOfSentencesOfAbstract);
           //Set<String> words = this.wordHash(dbpediaEntity.getText());
            Set<String> words = analyzer.getWords();
            Integer wordIndex=0,wordSize=words.size();
            for (String word : words) {
                wordIndex=wordIndex+1;
                String posTag=null;
                if (word.length() < 3) {
                    continue;
                }
                if (!this.isvalid(word)) {
                    continue;
                }
               Pair<Boolean,String>  posTagCheck=this.findPosTag(word,analyzer);
               
               if(!posTagCheck.getValue0())
                   continue;
               else
                   posTag=posTagCheck.getValue1();

                //System.out.println("word:"+word +" postag:"+posTag+" index:"+index+" total:"+entitySize+ " wordIndex:"+wordIndex+" wordSize"+wordSize);
                Integer count = 0;
                if (mostCommonWords.containsKey(word)) {
                    count = mostCommonWords.get(word);
                    count = count + 1;
                    mostCommonWords.put(word, count);
                } else {
                    count = count + 1;
                    mostCommonWords.put(word, count);
                }
               
                posTagger.put(word, posTag);
            }

        }
        if (index < numberEntitiesSelected) {
            return null;
        }

        return SortUtils.sort(mostCommonWords, posTagger, numberOfEntitiesToLimitInFile);
    }
    
     /*private Set< String> wordHash(String text) throws Exception {
        Set<String> words = new HashSet<String>();
        Analyzer analyzer = new Analyzer(text, TextAnalyzer.POS_TAGGER_WORDS, numberOfSentencesOfAbstract);
        for (String word : analyzer.getAdjectives()) {
            word = word.toLowerCase().trim();
            words.add(word);
            posTagger.put(word, TextAnalyzer.ADJECTIVE);
        }
        for (String word : analyzer.getNouns()) {
            word = word.toLowerCase().trim();
            words.add(word);
            posTagger.put(word, TextAnalyzer.NOUN);
        }
        for (String word : analyzer.getVerbs()) {
            word = word.toLowerCase().trim();
            words.add(word);
            posTagger.put(word, TextAnalyzer.VERB);
        }
        return analyzer.g;
    }*/

    /*private Set< String> wordHash(String text) throws Exception {
        Set<String> words = new HashSet<String>();
        Analyzer analyzer = new Analyzer(text, TextAnalyzer.POS_TAGGER_WORDS, numberOfSentencesOfAbstract);
        for (String word : analyzer.getAdjectives()) {
            word = word.toLowerCase().trim();
            words.add(word);
            posTagger.put(word, TextAnalyzer.ADJECTIVE);
        }
        for (String word : analyzer.getNouns()) {
            word = word.toLowerCase().trim();
            words.add(word);
            posTagger.put(word, TextAnalyzer.NOUN);
        }
        for (String word : analyzer.getVerbs()) {
            word = word.toLowerCase().trim();
            words.add(word);
            posTagger.put(word, TextAnalyzer.VERB);
        }
        return words;
    }*/

    private String getProperty(File file) {
        String property = file.getName().replace(className, "");
        property = property.replace("_", "");
        return property;
    }

    private Boolean isvalid(String word) {
        /*if (TextAnalyzer.ENGLISH_STOPWORDS.contains(word)) {
            return false;
        } else*/ 
        if (TextAnalyzer.MONTH.contains(word)) {
            return false;
        }
        return true;
    }

    public Map<String, List<String>> getPropertyInterestedWords() {
        return propertyInterestedWords;
    }

    private Pair<Boolean,String> findPosTag(String word, Analyzer analyzer) {
        if(analyzer.getNouns().contains(word))
            return new Pair<Boolean,String>(Boolean.TRUE,TextAnalyzer.NOUN);
        else if(analyzer.getAdjectives().contains(word))
             return new Pair<Boolean,String>(Boolean.TRUE,TextAnalyzer.ADJECTIVE);
        else if(analyzer.getVerbs().contains(word))
             return new Pair<Boolean,String>(Boolean.TRUE,TextAnalyzer.VERB);
        return  new Pair<Boolean,String>(Boolean.FALSE,word);
    }

}
