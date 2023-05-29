package app.dataset;

import com.opencsv.CSVReader;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * <h1>LeftOverLapping</h1>
 * @author Dan Ottosson
 */
public class LeftOverLapping {
    public List<String[]> lines;

    public LeftOverLapping(String filepath) {
        lines = new ArrayList<>();
        try {
            CSVReader reader = new CSVReader(new FileReader(filepath));

            lines = reader.readAll();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void lapp() {
        Pattern start = Pattern.compile("^[a-f0-9]{16}$");
        Pattern end = Pattern.compile("[0-1,]*$");
        for (String[] line : lines) {

            if (start.matcher(line[0]).matches()) {
                System.out.println(line[0]);
            }
            int lastElement = line.length - 1;
            if (line[lastElement].equals("0") || line[lastElement].equals("1")) {
                System.out.println(line[lastElement-5]+line[lastElement-4]+line[lastElement-3]+line[lastElement-2]+line[lastElement-1]+line[lastElement]);
            }
        }
    }
}
