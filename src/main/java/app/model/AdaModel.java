package app.model;

import app.dataset.CSVLoader;

import com.theokanning.openai.OpenAiHttpException;
import com.theokanning.openai.completion.CompletionChoice;
import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.completion.CompletionResult;
import com.theokanning.openai.service.OpenAiService;

import java.io.*;
import java.util.*;

import static app.net.RedditConnector.PROPERTIES;

/**
 * <h1>AdaModel</h1>
 * @author Dan Ottosson
 */
public class AdaModel {
    private final String credential;
    private final String model;
    private List<String[]> testList;
    private Map<String,String> testMap;
    private List<String[]> testLabelsList;
    public List<String[]> readyList;
    public AdaModel() {
        Properties properties = new Properties();

        try {
            properties.load(new FileInputStream(PROPERTIES));

        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        this.credential = properties.getProperty("model-key");
        this.model = properties.getProperty("model-name");
    }

    private List<String[]> loadTestSet(String testFilePath) {
        CSVLoader loader = new CSVLoader(testFilePath,null);
        return loader.getLines();
    }

    private void loadTestLists(String testCSVFilePath, String testCSVLabelsFilePath) {
        this.testList = loadTestSet(testCSVFilePath);
        this.testLabelsList = loadTestSet(testCSVLabelsFilePath);
    }

    private void filterListToMap() {
        testList.remove(0);
        this.testMap = new HashMap<>();

        for (String[] line : testList) {
            if (line.length == 2) {
                if (line[0].matches("[a-f0-9]{16}")) {
                    if (line[1].matches("[\\x00-\\xFF]*")) {
                        testMap.put(line[0],line[1]);
                    }
                }

            }
        }
    }

    public void filterLabelList() {
        testLabelsList.remove(0);

        List<String[]> data = new ArrayList<>();

        for (String[] line : testLabelsList) {
            if (line.length == 7 && !Objects.equals(line[1], "-1")) {
                String[] dataLine = new String[line.length];
                dataLine[0] = line[0];
                System.arraycopy(line, 0, dataLine, 0, line.length);
                data.add(dataLine);
            }
        }

        testLabelsList = new ArrayList<>(data);
    }

    public void filterNonMatchingLines() {
        List<String[]> filteredList = new ArrayList<>();
        int longest = 0;
        String id = "";

        for (String[] line : testLabelsList) {
            if (testMap.containsKey(line[0])) {

                if (testMap.get(line[0]).length() < 2000) {
                    if (testMap.get(line[0]).length() > longest) {
                        longest = testMap.get(line[0]).length();
                        id = line[0];
                        //System.out.println("Longest string: " + id);
                    }

                    String[] newLine = new String[line.length + 1];
                    newLine[0] = line[0];
                    newLine[1] = testMap.get(line[0]);
                    for (int i = 1; i < line.length; i++) {
                        newLine[i + 1] = line[i].replaceAll("\"","");
                    }
                    filteredList.add(newLine);
                }
            }
        }

        readyList = new ArrayList<>();
        readyList.add(new String[]{"id","comment_text","toxic","severe_toxic","obscene",
                "threat","insult","identity_hate"});

        readyList.addAll(filteredList);


        //System.out.println("TestLabelsList: " + readyList.size() + " lines.");
    }

    public void listStatistics() {
        int none = 0;
        int toxic = 0;
        int severe = 0;
        int obscene = 0;
        int threat = 0;
        int insult = 0;
        int identity = 0;

        for (String[] line : readyList) {
            boolean bullying = false;
            for (int i = 2; i < line.length; i++) {
                switch (i) {
                    case 2 -> {
                        if (line[i].equals("1")) {
                            toxic++;
                            bullying = true;
                        }
                    }
                    case 3 -> {
                        if (line[i].equals("1")) {
                            severe++;
                            bullying = true;
                        }
                    }
                    case 4 -> {
                        if (line[i].equals("1")) {
                            obscene++;
                            bullying = true;
                        }
                    }
                    case 5 -> {
                        if (line[i].equals("1")) {
                            threat++;
                            bullying = true;
                        }
                    }
                    case 6 -> {
                        if (line[i].equals("1")) {
                            insult++;
                            bullying = true;
                        }
                    }
                    case 7 -> {
                        if (line[i].equals("1")) {
                            identity++;
                            bullying = true;
                        }
                    }
                }
            }
            if (!bullying) none++;
        }
        System.out.printf("""

                            Toxic: %d
                            Severe Toxic: %d
                            Obscene: %d
                            Threat: %d
                            Insult: %d
                            Identity Hate: %d
                            Not Bullying: %d
                            """,toxic,severe,obscene,threat,insult,identity,none);
    }

    public void writeToFile(String filePath, List<String[]> list) {
        CSVLoader.writeListToCSVFile(list, filePath);
    }

    private void evaluateModel() {
        OpenAiService service = new OpenAiService(credential);
        List<String[]> completionList = new ArrayList<>();

        completionList.add(new String[]{"id","completion"});

        for (String[] line : readyList) {
            String prompt = line[1];

            try {
                CompletionResult result = service.createCompletion(completionRequest(prompt));
                classifyListEntities(completionList, line, result);

            } catch (OpenAiHttpException e) {
                System.err.println("Id: " + line[0]);
                System.err.println("Prompt: " + prompt);
                System.out.println(e.getMessage());
            }
        }

        writeToFile("src/main/resources/testfiles/completed/completed.csv",
                completionList);
    }

    private void evaluateClassificationTime(int numberOfLinesToClassify) {
        Random random = new Random();
        List<String[]> evaluationList = new ArrayList<>();
        Set<Integer> selectedElementPositions = new HashSet<>();

        while (evaluationList.size() < numberOfLinesToClassify) {

            int next = random.nextInt(0, readyList.size());
            if (!selectedElementPositions.contains(next)) {
                selectedElementPositions.add(next);
                evaluationList.add(readyList.get(next));
            }
        }

        List<String[]> list1 = new ArrayList<>();
        List<String[]> list2 = new ArrayList<>();
        List<String[]> list3 = new ArrayList<>();
        List<String[]> list4 = new ArrayList<>();
        List<String[]> list5 = new ArrayList<>();
        List<String[]> list6 = new ArrayList<>();

        for (int i = 0; i < evaluationList.size(); i++) {
            if (i % 6 == 0) list1.add(evaluationList.get(i));
            else if (i % 6 == 1) list2.add(evaluationList.get(i));
            else if (i % 6 == 2) list3.add(evaluationList.get(i));
            else if (i % 6 == 3) list4.add(evaluationList.get(i));
            else if (i % 6 == 4) list5.add(evaluationList.get(i));
            else list6.add(evaluationList.get(i));
        }

        long beforeStart = new Date().getTime();

        long[] threadTimes = new long[]{0,0,0,0,0,0,0};

        Thread t1 = new Thread(() -> {
            threadTimes[0] = classify(list1);

        },"Thread 1");

        Thread t2 = new Thread(() -> {
            threadTimes[1] = classify(list2);

        },"Thread 2");

        Thread t3 = new Thread(() -> {
            threadTimes[2] = classify(list3);

        },"Thread 3");

        Thread t4 = new Thread(() -> {
            threadTimes[3] = classify(list4);

        },"Thread 4");

        Thread t5 = new Thread(() -> {
            threadTimes[4] = classify(list5);

        },"Thread 3");

        Thread t6 = new Thread(() -> {
            threadTimes[5] = classify(list6);

        },"Thread 3");

        t1.start();
        t2.start();
        t3.start();
        t4.start();
        t5.start();
        t6.start();

        try {
            t1.join();
            t2.join();
            t3.join();
            t4.join();
            t5.join();
            t6.join();

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        long afterStart = new Date().getTime();
        threadTimes[6] = afterStart - beforeStart;

        System.out.println("Threads have returned");
        System.out.println("\nThread 1\nNumber of elements: " + list1.size() + "\nTime:" +
                " " + threadTimes[0]);
        System.out.println("\nThread 2\nNumber of elements: " + list2.size() + "\nTime:" +
                " " + threadTimes[1]);
        System.out.println("\nThread 3\nNumber of elements: " + list3.size() + "\nTime:" +
                " " + threadTimes[2]);
        System.out.println("\nThread 4\nNumber of elements: " + list4.size() + "\nTime:" +
                " " + threadTimes[3]);
        System.out.println("\nThread 5\nNumber of elements: " + list5.size() + "\nTime:" +
                " " + threadTimes[4]);
        System.out.println("\nThread 6\nNumber of elements: " + list6.size() + "\nTime:" +
                " " + threadTimes[5]);
        System.out.println("\nThread MAIN\nNumber of elements: " + evaluationList.size() + "\nTime: " + threadTimes[6]);
    }

    public long classify(List<String[]> lines) {
        OpenAiService service = new OpenAiService(credential);
        List<String[]> completionList = new ArrayList<>();

        long start = new Date().getTime();

        for (String[] line : lines) {
            try {
                CompletionResult result =
                        service.createCompletion(completionRequest(line[1]));
                classifyListEntities(completionList, line, result);

            } catch (OpenAiHttpException e) {
                System.err.println("Id: " + line[0]);
                System.out.println(e.getMessage());
            }
        }
        long end = new Date().getTime();
        return end - start;
    }

    private void classifyListEntities(List<String[]> completionList, String[] line, CompletionResult result) {
        List<String> completions =
                result.getChoices().stream().map(CompletionChoice::getText).toList();

        String[] resultArr = new String[1 + completions.size()];
        resultArr[0] = line[0];

        for (int i = 0; i < completions.size(); i++) {
            resultArr[i+1] = completions.get(i);
        }

        completionList.add(resultArr);
    }

    private CompletionRequest completionRequest(String prompt) {
        final String postfix = " ->";
        return CompletionRequest.builder()
                .model(this.model)
                .prompt(prompt + postfix)
                .maxTokens(1)
                .temperature(0.0)
                //.echo(true)
                .n(1)
                .build();
    }

    private CompletionResult completionResult(CompletionRequest completionRequest) {
        OpenAiService service = new OpenAiService(credential);
        return service.createCompletion(completionRequest);
    }
}
