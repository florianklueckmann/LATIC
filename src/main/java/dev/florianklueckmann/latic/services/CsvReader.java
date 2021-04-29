package dev.florianklueckmann.latic.services;

import dev.florianklueckmann.latic.App;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class CsvReader {
    private static final CsvReader csvReader = new CsvReader();
    public static CsvReader getInstance() {
        return csvReader;
    }


    public List<String> readFile(String fileName) {
        System.out.println(fileName);
        List<String> lines = new ArrayList<>();
        try (Scanner scanner = new Scanner(new File(Objects.requireNonNull(
                App.class.getResource(fileName), "fileName must not be null").getPath()))) {
            while (scanner.hasNextLine()) {
                lines.add(scanner.nextLine());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return lines;
    }
}
