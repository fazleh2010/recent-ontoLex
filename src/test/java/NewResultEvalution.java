
import citec.correlation.wikipedia.dic.lexicon.WordObjectResults;
import citec.correlation.wikipedia.element.DBpediaEntity;
import citec.correlation.wikipedia.element.PropertyNotation;
import citec.correlation.wikipedia.linking.EntityTriple.Triple;
import static citec.correlation.wikipedia.parameters.DirectoryLocation.dbpediaDir;
import citec.correlation.wikipedia.parameters.ExperimentThresold;
import static citec.correlation.wikipedia.parameters.MenuOptions.RESULT_DIR;
import static citec.correlation.wikipedia.parameters.MenuOptions.SELTECTED_WORDS_DIR;
import citec.correlation.wikipedia.results.LineInfo;
import citec.correlation.wikipedia.results.NewResults;
import citec.correlation.wikipedia.results.ObjectWordResults;
import citec.correlation.wikipedia.results.RdfTriple;
import citec.correlation.wikipedia.results.WordResult;
import citec.correlation.wikipedia.utils.FileFolderUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import org.apache.commons.lang3.StringUtils;
import org.javatuples.Pair;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author elahi
 */
public class NewResultEvalution {

    private static String inputDir = dbpediaDir + "results/" + "new/";
    private static Set<String> associationRules = new TreeSet<String>();
    private static Set<String> classNames = new TreeSet<String>();
    private static List<String> predicateRules = new ArrayList<String>();
    private static Map<String,List<String>> rules = new HashMap<String,List<String>>();
    private static Map<String, Map<String, List<File>>> classRuleFiles = new TreeMap<String, Map<String, List<File>>>();
    private static Map<String, List<WordObjectResults>> wordObjectResults = new TreeMap<String, List<WordObjectResults>>();
    private static final String AllConf = "AllConf";
    private static final String MaxConf = "MaxConf";
    private static final String IR = "IR";
    private static final String Kulczynski = "Kulczynski";
    private static final String Cosine = "Cosine";
    private static final String Coherence = "Coherence";
    
    private static final String linguisticRule = "linguisticRule";
    private static final String kbRule = "linguisticRule";


    private static final String predict_l_for_s_given_p = "predict_l_for_s_given_p";
    private static final String predict_l_for_s_given_o = "predict_l_for_s_given_o";
    private static final String predict_l_for_o_given_p = "predict_l_for_o_given_p";
    private static final String predict_l_for_s_given_po = "predict_l_for_s_given_po";

    private static final String predict_p_for_s_given_l = "predict_p_for_s_given_l";
    private static final String predict_o_for_s_given_l = "predict_o_for_s_given_l";
    private static final String predict_p_for_o_given_l = "predict_p_for_o_given_l";
    private static final String predict_po_for_s_given_l = "predict_po_for_s_given_l";

   

    public NewResultEvalution() {
        predicateRules = new ArrayList<String>(Arrays.asList(
                predict_l_for_s_given_p,
                predict_l_for_s_given_o,
                predict_l_for_o_given_p,
                predict_l_for_s_given_po,
                predict_p_for_s_given_l,
                predict_o_for_s_given_l,
                predict_p_for_o_given_l,
                predict_po_for_s_given_l));

        /*predictLinguisticPattern = new ArrayList<String>(Arrays.asList(
                predict_l_for_s_given_p,
                predict_l_for_s_given_o,
                predict_l_for_o_given_p,
                predict_l_for_s_given_po));
        predictObjectPredicate = new ArrayList<String>(Arrays.asList(
                predict_p_for_s_given_l,
                predict_o_for_s_given_l,
                predict_p_for_o_given_l,
                predict_po_for_s_given_l));*/
        classNames = getClassNames(inputDir);
        associationRules = new TreeSet(new ArrayList<String>(Arrays.asList(MaxConf, IR, Kulczynski, Cosine, Coherence)));

    }

    public static void main(String[] args) throws Exception {
        NewResultEvalution newResultEvalution = new NewResultEvalution();
        List<ObjectWordResults> entityResults = new ArrayList<ObjectWordResults>();
       
        
        
        for (String className : classNames) {
            for (String ruleName : associationRules) {
                Pair<Boolean, List<File>> pair = FileFolderUtils.getSpecificFiles(inputDir, className, ruleName, "json");
                if (pair.getValue0()) {
                    List<File> files = pair.getValue1();
                    for (File file : files) {
                        String fileName = file.getName();
                        if (!fileName.contains("Politician")
                                || !fileName.contains(Cosine)
                                || !fileName.contains(predict_l_for_s_given_po)) {
                            continue;
                        }
                        //predict_po_for_s_given_l
                        System.out.println("file:" + file.getName());
                        NewResults result = readFromJsonFile(new File(inputDir + file.getName()));
                       
                        
                        for (String line : result.getDistributions()) {
                            LineInfo LineInfo=new LineInfo(line,1,0);
                            System.out.println(LineInfo);


                        }
                        
                        //ObjectWordResults kbResult = new ObjectWordResults(property, objectOfProperty, numberOfEntitiesFoundInObject, results, this.probabilityT.getProbResultTopWordLimit());

                        /*for(String rules:result.getDistributions()){
                            
                              System.out.println(rules);
                        }*/
                    }
                }

            }

        }

        //String str = entityResultToString(entityResults);
    }

    private static String entityResultToString(List<ObjectWordResults> entityResults) {
        if (entityResults.isEmpty()) {
            return null;
        }
        String str = "";
        for (ObjectWordResults entities : entityResults) {
            String entityLine = "id=" + entities.getObjectIndex() + "  " + "property=" + entities.getProperty() + "  " + "object=" + entities.getObject() + "  " + "NumberOfEntitiesFoundForObject=" + entities.getNumberOfEntitiesFoundInObject() + "\n"; //+" "+"#the data within bracket is different way of counting confidence and lift"+ "\n";
            String wordSum = "";
            for (WordResult wordResults : entities.getDistributions()) {
                String multiply = "multiply=" + wordResults.getMultiple();
                String probabilty = "";
                for (String rule : wordResults.getProbabilities().keySet()) {
                    Double value = wordResults.getProbabilities().get(rule);
                    String line = rule + "=" + String.valueOf(value) + "  ";
                    probabilty += line;
                }
                String liftAndConfidence = "Lift=" + wordResults.getLift() + " " + "{Confidence" + " " + "word=" + wordResults.getConfidenceWord() + " object=" + wordResults.getConfidenceObject() + " =" + wordResults.getConfidenceObjectAndKB() + " " + "Lift=" + wordResults.getOtherLift() + "}";
                liftAndConfidence = "";
                //temporarily lift value made null, since we are not sure about the Lift calculation
                //lift="";
                String wordline = wordResults.getWord() + "  " + wordResults.getPosTag() + "  " + multiply + "  " + probabilty + "  " + liftAndConfidence + "\n";
                wordSum += wordline;
                String key = wordResults.getWord() + "-" + wordResults.getPosTag();

                List<WordObjectResults> propertyObjects = new ArrayList<WordObjectResults>();
                WordObjectResults entityInfo = null;
                if (wordObjectResults.containsKey(key)) {
                    propertyObjects = wordObjectResults.get(key);
                    entityInfo = new WordObjectResults(wordResults.getPosTag(), entities.getProperty(), entities.getObject(), wordResults.getMultipleValue(), wordResults.getProbabilities());
                } else {
                    entityInfo = new WordObjectResults(wordResults.getPosTag(), entities.getProperty(), entities.getObject(), wordResults.getMultipleValue(), wordResults.getProbabilities());

                }
                propertyObjects.add(entityInfo);
                wordObjectResults.put(key, propertyObjects);

            }
            entityLine = entityLine + wordSum + "\n";
            str += entityLine;
        }
        return str;
    }

    private static Set<String> getClassNames(String inputDir) {
        Set<String> classNames = new TreeSet<String>();
        Pair<Boolean, List<File>> filesExistList = FileFolderUtils.getSpecificFiles(inputDir, Cosine, "json");
        if (filesExistList.getValue0()) {
            if (filesExistList.getValue0()) {
                List<File> fileList = filesExistList.getValue1();
                Map<String, List<File>> ruleFiles = new TreeMap<String, List<File>>();
                for (File file : fileList) {
                    classNames.add(getClassName(file.getName()));
                }
            }
        }
        return classNames;
    }

    private static String getClassName(String name) {
        name = name.replace("HR_", "");
        String[] info = name.split("-");
        return info[0].trim().strip();
    }

    public static NewResults readFromJsonFile(File file) throws IOException, Exception {
        ObjectMapper mapper = new ObjectMapper();
        NewResults result = mapper.readValue(file,NewResults.class);
        return result;
    }
}
