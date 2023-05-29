package app.model;

import com.opencsv.CSVReader;

import java.io.FileReader;
import java.util.*;

/**
 * <h1>Classifier</h1>
 * @author Dan Ottosson
 */

enum ConfusionMatrix {
    TP, TN, FP, FN;

    static ConfusionMatrix value(String result, String actual) {
        if (result.equals(actual)) {
            if (result.equals(" none")) {
                return TN;
            }
            else {
                return TP;
            }
        }
        else {
            if (result.equals(" none")) {
                return FN;
            }
            else {
                return FP;
            }
        }
    }

    static ConfusionMatrix bullying(String result, String actual) {
        if (!result.equals(" none")) {
            result = " bullying";
        }

        if (result.equals(actual)) {
            if (result.equals(" none")) {
                return TN;
            }
            else {
                return TP;
            }
        }
        else {
            if (result.equals(" none")) {
                return FN;
            }
            else {
                return FP;
            }
        }
    }
}

record Pair(String id, ConfusionMatrix value){}

public class Classifier {

    private List<String[]> preparedList;
    private List<String[]> resultList;

    public Classifier(String preparedFilePath, String resultFilePath) {
        try {
            CSVReader reader = new CSVReader(new FileReader(preparedFilePath));
            this.preparedList = reader.readAll();

            reader = new CSVReader(new FileReader(resultFilePath));
            this.resultList = reader.readAll();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setUp() {
        createEvaluationEnvironment();
    }

    private Map<String,ConfusionMatrix> populateMatrixMap(Map<String,String> resultMap,
                                                          Map<String,String> bullyingMap,
                                                          boolean bullying) {
        Map<String, ConfusionMatrix> bullyingMatrixMap = new HashMap<>();

        if (!bullying) {
            for (Map.Entry<String,String> line : bullyingMap.entrySet()) {
                String result = resultMap.get(line.getKey());
                ConfusionMatrix matrixValue = ConfusionMatrix.value(
                        result,line.getValue());

                bullyingMatrixMap.put(line.getKey(),matrixValue);

            }
        }
        else {
            for (Map.Entry<String,String> line : bullyingMap.entrySet()) {
                String result = resultMap.get(line.getKey());

                ConfusionMatrix bullyingValue = ConfusionMatrix.bullying(
                        result,line.getValue());

                bullyingMatrixMap.put(line.getKey(),bullyingValue);
            }
        }
        return bullyingMatrixMap;
    }

    private void createEvaluationEnvironment() {
        Map<String,String> toxicMap = new HashMap<>();
        Map<String,String> severeMap = new HashMap<>();
        Map<String,String> obsceneMap = new HashMap<>();
        Map<String,String> threatMap = new HashMap<>();
        Map<String,String> insultMap = new HashMap<>();
        Map<String,String> identityMap = new HashMap<>();
        Map<String,String> noneMap = new HashMap<>();
        Map<String,String> bullyMap = new HashMap<>();

        preparedList.remove(0);
        resultList.remove(0);

        for (String[] line : preparedList) {
            boolean bullying = false;
            for (int i = 2; i < line.length; i++) {
                switch (i) {
                    case 2 -> {
                        if (line[i].equals("1")) {
                            toxicMap.put(line[0]," toxic");
                            bullying = true;
                        }
                    }
                    case 3 -> {
                        if (line[i].equals("1")) {
                            severeMap.put(line[0]," severe");
                            bullying = true;
                        }
                    }
                    case 4 -> {
                        if (line[i].equals("1")) {
                            obsceneMap.put(line[0]," obscene");
                            bullying = true;
                        }
                    }
                    case 5 -> {
                        if (line[i].equals("1")) {
                            threatMap.put(line[0]," threat");
                            bullying = true;
                        }
                    }
                    case 6 -> {
                        if (line[i].equals("1")) {
                            insultMap.put(line[0]," insult");
                            bullying = true;
                        }
                    }
                    case 7 -> {
                        if (line[i].equals("1")) {
                            identityMap.put(line[0]," identity");
                            bullying = true;
                        }
                    }
                }
            }
            if (!bullying) {
                noneMap.put(line[0]," none");
            }
            else {
                bullyMap.put(line[0], " bullying");
            }
        }

        Map<String,String> resultMap = new HashMap<>();

        for (String[] line : resultList) {
            resultMap.put(line[0],line[1]);
        }

        Map<String,ConfusionMatrix> toxicMatrixMap =
                populateMatrixMap(resultMap,toxicMap,false);

        Map<String,ConfusionMatrix> severeMatrixMap =
                populateMatrixMap(resultMap,severeMap,false);

        Map<String,ConfusionMatrix> obsceneMatrixMap =
                populateMatrixMap(resultMap,obsceneMap,false);

        Map<String,ConfusionMatrix> threatMatrixMap =
                populateMatrixMap(resultMap,threatMap,false);

        Map<String,ConfusionMatrix> insultMatrixMap =
                populateMatrixMap(resultMap,insultMap,false);

        Map<String,ConfusionMatrix> identityMatrixMap =
                populateMatrixMap(resultMap,identityMap,false);

        Map<String,ConfusionMatrix> noneMatrixMap =
                populateMatrixMap(resultMap,noneMap,false);

        Map<String,ConfusionMatrix> bullyMatrixMap =
                populateMatrixMap(resultMap,bullyMap,true);

        String id;
        boolean self;
        List<String> removeList = new ArrayList<>();

        Map<String,ConfusionMatrix> bullyingMatrixMap = new HashMap<>();

        for (Map.Entry<String,ConfusionMatrix> entry : severeMatrixMap.entrySet()) {
            id = entry.getKey();

            boolean TP = entry.getValue().equals(ConfusionMatrix.TP);
            self = TP;

            if (TP) {
                bullyingMatrixMap.put(id,ConfusionMatrix.TP);
            }

            if (threatMatrixMap.containsKey(id)) {
                if (TP) {
                    threatMatrixMap.remove(id);
                }
                else {
                    if (threatMatrixMap.get(id).equals(ConfusionMatrix.TP)) {
                        TP = true;
                    }
                }
            }
            if (identityMatrixMap.containsKey(id)) {
                if (TP) {
                    identityMatrixMap.remove(id);
                }
                else {
                    if (identityMatrixMap.get(id).equals(ConfusionMatrix.TP)) {
                        TP = true;
                    }
                }
            }
            if (insultMatrixMap.containsKey(id)) {
                if (TP) {
                    insultMatrixMap.remove(id);
                }
                else {
                    if (insultMatrixMap.get(id).equals(ConfusionMatrix.TP)) {
                        TP = true;
                    }
                }
            }
            if (obsceneMatrixMap.containsKey(id)) {
                if (TP) {
                    obsceneMatrixMap.remove(id);
                }
                else {
                    if (obsceneMatrixMap.get(id).equals(ConfusionMatrix.TP)) {
                        TP = true;
                    }
                }
            }
            if (toxicMatrixMap.containsKey(id)) {
                if (TP) {
                    toxicMatrixMap.remove(id);
                }
                else {
                    if (toxicMatrixMap.get(id).equals(ConfusionMatrix.TP)) {
                        TP = true;
                    }
                }
            }

            if (TP && !self) {
                removeList.add(id);
            }
        }

        for (String mapID : removeList) {
            severeMatrixMap.remove(mapID);
        }

        removeList = new ArrayList<>();

        for (Map.Entry<String,ConfusionMatrix> entry : threatMatrixMap.entrySet()) {

            boolean TP = entry.getValue().equals(ConfusionMatrix.TP);
            self = TP;
            id = entry.getKey();

            if (TP) {
                bullyingMatrixMap.put(id,ConfusionMatrix.TP);
            }

            if (identityMatrixMap.containsKey(id)) {
                if (TP) {
                    identityMatrixMap.remove(id);
                }
                else {
                    if (identityMatrixMap.get(id).equals(ConfusionMatrix.TP)) {
                        TP = true;
                    }
                }
            }
            if (insultMatrixMap.containsKey(id)) {
                if (TP) {
                    insultMatrixMap.remove(id);
                }
                else {
                    if (insultMatrixMap.get(id).equals(ConfusionMatrix.TP)) {
                        TP = true;
                    }
                }
            }
            if (obsceneMatrixMap.containsKey(id)) {
                if (TP) {
                    obsceneMatrixMap.remove(id);
                }
                else {
                    if (obsceneMatrixMap.get(id).equals(ConfusionMatrix.TP)) {
                        TP = true;
                    }
                }
            }
            if (toxicMatrixMap.containsKey(id)) {
                if (TP) {
                    toxicMatrixMap.remove(id);
                }
                else {
                    if (toxicMatrixMap.get(id).equals(ConfusionMatrix.TP)) {
                        TP = true;
                    }
                }
            }

            if (TP && !self) {
                removeList.add(id);
            }
        }

        for (String mapID : removeList) {
            threatMatrixMap.remove(mapID);
        }

        removeList = new ArrayList<>();

        for (Map.Entry<String,ConfusionMatrix> entry : identityMatrixMap.entrySet()) {
            boolean TP = entry.getValue().equals(ConfusionMatrix.TP);
            self = TP;
            id = entry.getKey();

            if (TP) {
                bullyingMatrixMap.put(id,ConfusionMatrix.TP);
            }

            if (insultMatrixMap.containsKey(id)) {
                if (TP) {
                    insultMatrixMap.remove(id);
                }
                else {
                    if (insultMatrixMap.get(id).equals(ConfusionMatrix.TP)) {
                        TP = true;
                    }
                }
            }
            if (obsceneMatrixMap.containsKey(id)) {
                if (TP) {
                    obsceneMatrixMap.remove(id);
                }
                else {
                    if (obsceneMatrixMap.get(id).equals(ConfusionMatrix.TP)) {
                        TP = true;
                    }
                }
            }
            if (toxicMatrixMap.containsKey(id)) {
                if (TP) {
                    toxicMatrixMap.remove(id);
                }
                else {
                    if (toxicMatrixMap.get(id).equals(ConfusionMatrix.TP)) {
                        TP = true;
                    }
                }
            }

            if (TP && !self) {
                removeList.add(id);
            }
        }

        for (String mapID : removeList) {
            identityMatrixMap.remove(mapID);
        }

        removeList = new ArrayList<>();

        for (Map.Entry<String,ConfusionMatrix> entry : insultMatrixMap.entrySet()) {
            boolean TP = entry.getValue().equals(ConfusionMatrix.TP);
            self = TP;
            id = entry.getKey();

            if (TP) {
                bullyingMatrixMap.put(id,ConfusionMatrix.TP);
            }

            if (obsceneMatrixMap.containsKey(id)) {
                if (TP) {
                    obsceneMatrixMap.remove(id);
                }
                else {
                    if (obsceneMatrixMap.get(id).equals(ConfusionMatrix.TP)) {
                        TP = true;
                    }
                }
            }
            if (toxicMatrixMap.containsKey(id)) {
                if (TP) {
                    toxicMatrixMap.remove(id);
                }
                else {
                    if (toxicMatrixMap.get(id).equals(ConfusionMatrix.TP)) {
                        TP = true;
                    }
                }
            }

            if (TP && !self) {
                removeList.add(id);
            }
        }

        for (String mapID : removeList) {
            insultMatrixMap.remove(mapID);
        }

        removeList = new ArrayList<>();

        for (Map.Entry<String,ConfusionMatrix> entry : obsceneMatrixMap.entrySet()) {
            boolean TP = entry.getValue().equals(ConfusionMatrix.TP);
            self = TP;
            id = entry.getKey();

            if (TP) {
                bullyingMatrixMap.put(id,ConfusionMatrix.TP);
            }

            if (toxicMatrixMap.containsKey(id)) {
                if (TP) {
                    toxicMatrixMap.remove(id);
                }
                else {
                    if (toxicMatrixMap.get(id).equals(ConfusionMatrix.TP)) {
                        TP = true;
                    }
                }
            }

            if (TP && !self) {
                removeList.add(id);
            }
        }

        for (String mapID : removeList) {
            obsceneMatrixMap.remove(mapID);
        }

        for (Map.Entry<String,ConfusionMatrix> entry : toxicMatrixMap.entrySet()) {
            boolean TP = entry.getValue().equals(ConfusionMatrix.TP);
            id = entry.getKey();

            if (TP) {
                bullyingMatrixMap.put(id,ConfusionMatrix.TP);
            }
        }

        System.out.printf(
                """
                        Toxic
                        %sSevere
                        %sObscene
                        %sThreat
                        %sInsult
                        %sIdentity
                        %sNone
                        %sBullying
                        %s""",
                matrixToString(getMatrixValues(toxicMatrixMap,false)),
                matrixToString(getMatrixValues(severeMatrixMap,false)),
                matrixToString(getMatrixValues(obsceneMatrixMap,false)),
                matrixToString(getMatrixValues(threatMatrixMap,false)),
                matrixToString(getMatrixValues(insultMatrixMap,false)),
                matrixToString(getMatrixValues(identityMatrixMap,false)),
                matrixToString(getMatrixValues(noneMatrixMap,true)),
                matrixToString(getMatrixValues(bullyMatrixMap,false))
        );

        System.out.println(bullyingMatrixMap.size());
    }

    private int[][] getMatrixValues(Map<String,ConfusionMatrix> summaryMap,
                                    boolean none) {
        int[][] matrixValues = new int[][]{{0,0},{0,0}};

        if (none) {
            for (ConfusionMatrix value : summaryMap.values()) {
                if (value.equals(ConfusionMatrix.TN)) {
                    matrixValues[0][0]++;
                }
                else if (value.equals(ConfusionMatrix.FN)) {
                    matrixValues[0][1]++;
                }
                else if (value.equals(ConfusionMatrix.FP)) {
                    matrixValues[1][0]++;
                }
                else {
                    matrixValues[1][1]++;
                }
            }
        }
        else {
            for (ConfusionMatrix value : summaryMap.values()) {
                if (value.equals(ConfusionMatrix.TP)) {
                    matrixValues[0][0]++;
                }
                else if (value.equals(ConfusionMatrix.FP)) {
                    matrixValues[0][1]++;
                }
                else if (value.equals(ConfusionMatrix.FN)) {
                    matrixValues[1][0]++;
                }
                else {
                    matrixValues[1][1]++;
                }
            }
        }
        return matrixValues;
    }

    private String matrixToString(int[][] matrixValues) {
        return String.format(
                """
                %d || %d
                == == ==
                %d || %d
                %s
                """, matrixValues[0][0],matrixValues[0][1],matrixValues[1][0],
                matrixValues[1][1],matrixMetricsToString(matrixValues));
    }

    private String matrixMetricsToString(int[][] matrixValues) {
        int tp = matrixValues[0][0];
        int fp = matrixValues[0][1];
        int fn = matrixValues[1][0];
        int tn = matrixValues[1][1];

        double accuracy = (double) (tp + tn) / (tp + tn + fp + fn);
        double precision = (double) tp / (tp + fp);
        double recall = (double) tp / (tp + fn);
        double f1_score = (double) 2 * ((precision * recall) / (precision + recall));

        return String.format(
                """
                Accuracy: %f
                Precision: %f
                Recall: %f
                F1-score: %f
                
                """,accuracy,precision,recall,f1_score);
    }
}
