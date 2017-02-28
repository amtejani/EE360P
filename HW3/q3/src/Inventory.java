/**
 * Keeps track of inventory and executes client requests
 * Ali Tejani, amt3639
 * Travis McClure, tam2983
 */

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;


public class Inventory {
    class Order{
        int orderID, quantity;
        String username, productName;
    }

    private HashMap<String, Integer> storage;
    private HashMap<Integer, Order> orders;
    private int numOrders;
    public Inventory(String file) {
        storage = new HashMap<>();
        orders = new HashMap<>();
        numOrders = 1;
        // read file
        try {
            Scanner fileReader = new Scanner(new FileReader(file));
            String line;
            while((line = fileReader.nextLine()) != null) {
                if (line.split(" ").length != 2) break;
                String item = line.split(" ")[0];
                int count = Integer.parseInt(line.split(" ")[1]);
                storage.put(item, count);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * parse command
     * @param command string to be parsed
     * @return appropriate response for command
     */
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

    /**
     * Creates order for client
     * @param username username of buyer
     * @param productName product being purchased
     * @param quantity number of product being purchased
     * @return appropriate response for command
     */
    private synchronized String purchase(String username, String productName, int quantity) {
        Integer i = storage.get(productName);
        if(i == null) {
            return "Not Available - We do not sell this product";
        } else if (i < quantity) {
            return "Not Available - Not enough items";
        } else {
            storage.put(productName, i - quantity);
            Order order = new Order();
            order.orderID = numOrders++;
            order.productName = productName;
            order.username = username;
            order.quantity = quantity;
            orders.put(order.orderID, order);
            return "You order has been placed, " + order.orderID + " "
                    + order.username + " "
                    + order.productName + " "
                    + order.quantity;
        }
    }

    /**
     * cancels an order
     * @param orderID order to be canceled
     * @return appropriate response for command
     */
    private synchronized String cancel(String orderID) {
        Order order = orders.remove(Integer.parseInt(orderID));
        if(order == null) {
            return orderID + " not found, no such order";
        }
        storage.put(order.productName, storage.get(order.productName) + order.quantity);
        return "Order " + orderID + " is canceled";
    }

    /**
     * finds all orders for user
     * @param username user to search for
     * @return appropriate response for command
     */
    private synchronized String search(String username) {
        String newLine = "";
        String retValue = "";
        for(Order order: orders.values()) {
            if(order.username.equals(username)) {
                retValue += newLine + order.orderID + ", "
                        + order.productName + ", "
                        + order.quantity;
                newLine = "\n";
            }
        }
        if (retValue.equals("")) {
            return "No order found for " + username;
        }
        return retValue;
    }

    private synchronized String list() {
        String newLine = "";
        String retValue = "";
        for(Map.Entry<String, Integer> entry: storage.entrySet()) {
            retValue += newLine + entry.getKey() + " " + entry.getValue();
            newLine = "\n";
        }
        return retValue;
    }
}
