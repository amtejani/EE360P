import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Scanner;


public class Inventory {
    private HashMap<String, Integer> storage;
    public Inventory(String file) {
        storage = new HashMap<>();
        // TODO read file
        try {
            Scanner fileReader = new Scanner(new FileReader(file));
            String line;
            while((line = fileReader.nextLine()) != null) {
                String item = line.split(" ")[0];
                int count = Integer.parseInt(line.split(" ")[1]);
                storage.put(item, count);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public String getCommand(String command) {
        String[] tokens = command.split(" ");
        switch (tokens[0]) {
            case "purchase": return purchase(tokens[1],tokens[2], Integer.parseInt(tokens[3]));
            case "cancel": return cancel(tokens[1]);
            case "search": return search(tokens[1]);
            case "list": return list();
        }
        return "ERROR: No such command";
    }

    private String purchase(String username, String productName, int quantity) {
        return "";
    }

    private String cancel(String orderID) {
        return "";
    }

    private String search(String username) {
        return "";
    }

    private String list() {
        return "";
    }
}
