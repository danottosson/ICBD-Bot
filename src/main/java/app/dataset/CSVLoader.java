package app.dataset;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.*;
import java.util.*;

/**
 * <h1>CSVLoader</h1>
 * @author Dan Ottosson
 */
public class CSVLoader {
    private String testFilePath;
    public List<String[]> lines;
    public List<String[]> data;
    public List<String[]> leftOvers;

    public CSVLoader(String datasetFilePath, String testFilePath) {
        lines = new ArrayList<>();
        this.testFilePath = testFilePath;
        try {
            CSVReader reader = new CSVReader(new FileReader(datasetFilePath));

            lines = reader.readAll();

        } catch (Exception e) {
            e.printStackTrace();
        }

        prepareTrainDataset();
    }

    public CSVLoader(String textFilePath) {
        try {
            File file = new File(textFilePath.replaceAll(".txt",".csv"));
            BufferedReader br =
                    new BufferedReader(new InputStreamReader(new FileInputStream(textFilePath)));
            FileWriter outputFile = new FileWriter(file);
            CSVWriter writer = new CSVWriter(outputFile);

            String line;

            while ((line = br.readLine()) != null) {
                String[] lineArr = line.replaceAll(",",", ").split(",");

                for (int i = 0; i < lineArr.length; i++) {
                    if (lineArr[i].equals(" "))
                        lineArr[i] = "null";
                    else
                        lineArr[i] = lineArr[i].replaceAll(" ", "");
                }
                writer.writeNext(lineArr);
            }
            writer.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<String[]> getLines() { return lines; }

    public void prepareTrainDataset() {
        filterBadLines();
        preProcessLinesAndWriteToFile(testFilePath);
    }

    public void filterBadLines() {
        lines.remove(0);

        data = new ArrayList<>();
        leftOvers = new ArrayList<>();

        for (String[] line : lines) {
            if (line.length == 8) {
                String[] dataLine = new String[line.length];
                System.arraycopy(line, 0, dataLine, 0, line.length);
                data.add(dataLine);
            }
            else {
                String[] leftOver = new String[line.length];
                System.arraycopy(line, 0, leftOver, 0, line.length);
                leftOvers.add(leftOver);
            }
        }
    }

    public void preProcessLinesAndWriteToFile(String filePath) {
        List<String[]> fullList = new ArrayList<>();
        fullList.add(new String[]{"prompt","completion"});

        boolean bullying;

        for (String[] line : data) {
            bullying = false;

            if (line.length == 8) {
                for (int i = 1; i < line.length; i++) {
                    switch (i) {
                        case 1 -> {
                            if (line[i].contains("->")) {
                                line[i] = line[i].replaceAll("->","");
                            }
                        }
                        case 2 -> {
                            if (line[i].equals("1") || line[i].equals(" 1")) {
                                fullList.add(new String[]{line[1] + " ->", " toxic"});
                                bullying = true;
                            }
                        }
                        case 3 -> {
                            if (line[i].equals("1") || line[i].equals(" 1")) {
                                fullList.add(new String[]{line[1] + " ->", " severe"});
                                bullying = true;
                            }
                        }
                        case 4 -> {
                            if (line[i].equals("1") || line[i].equals(" 1")) {
                                fullList.add(new String[]{line[1] + " ->", " obscene"});
                                bullying = true;
                            }
                        }
                        case 5 -> {
                            if (line[i].equals("1") || line[i].equals(" 1")) {
                                fullList.add(new String[]{line[1] + " ->", " threat"});
                                bullying = true;
                            }
                        }
                        case 6 -> {
                            if (line[i].equals("1") || line[i].equals(" 1")) {
                                fullList.add(new String[]{line[1] + " ->", " insult"});
                                bullying = true;
                            }
                        }
                        case 7 -> {
                            if (line[i].equals("1") || line[i].equals(" 1")) {
                                fullList.add(new String[]{line[1] + " ->", " identity"});
                                bullying = true;
                            }
                        }
                    }
                }

                if (!bullying) {
                    fullList.add(new String[]{line[1] + " ->", " none"});
                }
            }
        }

        writeListToCSVFile(fullList, filePath);
    }

    public static void writeListToCSVFile(List<String[]> data, String filePath) {
        File file = new File(filePath);
        try {
            FileWriter outputFile = new FileWriter(file);
            CSVWriter writer = new CSVWriter(outputFile);

            writer.writeAll(data);
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeFineTuneDataColumnsToSeparateFiles(int[] columns, String baseFilePath) {
        for (int col : columns) {
            List<String> columnData = new ArrayList<>();

            for (String[] line : lines) {
                columnData.add(line[0] + ": " + line[col]);
            }

            try {
                File file = new File(baseFilePath + col + ".txt");
                BufferedWriter bw =
                        new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));

                for (String line : columnData) {
                    bw.write(line + "\n");
                }

                bw.close();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }
}
