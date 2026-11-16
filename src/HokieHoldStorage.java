package src;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Saves and loads HokieHold waiting-list information.
 *
 * Each saved student contains a HokiePID, name, category,
 * and requested group size.
 *
 * @author Zachary
 * @version 11/16/2026
 */
public class HokieHoldStorage {

    private File saveFile;

    /**
     * Creates storage using the specified file.
     *
     * @param fileName name of the save file
     */
    public HokieHoldStorage(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "File name cannot be blank.");
        }

        saveFile = new File(fileName);
    }

    /**
     * Saves the supplied waiting-list records.
     *
     * @param records records to save
     * @throws IOException if the file cannot be written
     */
    public void save(List<String> records) throws IOException {
        if (records == null) {
            throw new IllegalArgumentException(
                "Records cannot be null.");
        }

        try (BufferedWriter writer =
                new BufferedWriter(new FileWriter(saveFile))) {

            for (String record : records) {
                if (record == null) {
                    throw new IllegalArgumentException(
                        "A record cannot be null.");
                }

                writer.write(record);
                writer.newLine();
            }
        }
    }

    /**
     * Loads all saved waiting-list records.
     *
     * @return saved records, or an empty list if no file exists
     * @throws IOException if the file cannot be read
     */
    public List<String> load() throws IOException {
        List<String> records = new ArrayList<String>();

        if (!saveFile.exists()) {
            return records;
        }

        try (BufferedReader reader =
                new BufferedReader(new FileReader(saveFile))) {

            String record;

            while ((record = reader.readLine()) != null) {
                if (!record.trim().isEmpty()) {
                    records.add(record);
                }
            }
        }

        return records;
    }
}