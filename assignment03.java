import java.util.Scanner;
import java.util.ArrayList;
import java.io.*;

class Product {
    private int id;
    private String name;
    private int quantity;

    Product(int id, String name, int quantity) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}

public class InventorySystem {

    static ArrayList<Product> products = new ArrayList<>();
    static boolean isSorted = false;
    static final String FILE_NAME = "products.csv";

    // Load products from CSV file
    static void loadProducts() {
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            boolean isHeader = true;
            while ((line = br.readLine()) != null) {
                if (isHeader) {
                    isHeader = false; // Skip header
                    continue;
                }
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    int id = Integer.parseInt(parts[0].trim());
                    String name = parts[1].trim();
                    int quantity = Integer.parseInt(parts[2].trim());
                    products.add(new Product(id, name, quantity));
                }
            }
            System.out.println("Products loaded from CSV file.");
        } catch (FileNotFoundException e) {
            System.out.println("No previous data found. Starting fresh.");
        } catch (Exception e) {
            System.out.println("Error loading products: " + e.getMessage());
        }
    }

    // Save products to CSV file
    static void saveProducts() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_NAME))) {
            pw.println("ID,Name,Quantity"); // Header
            for (Product p : products) {
                pw.println(p.getId() + "," + p.getName() + "," + p.getQuantity());
            }
            System.out.println("Products saved to CSV file.");
        } catch (Exception e) {
            System.out.println("Error saving products: " + e.getMessage());
        }
    }

    // Insert Data
    static void insertData(Scanner sc) {
        System.out.print("Enter number of products: ");
        int num = sc.nextInt();
        sc.nextLine(); // clear buffer

        for (int i = 0; i < num; i++) {
            System.out.println("\nProduct " + (i + 1));

            System.out.print("Enter Product ID: ");
            int id = sc.nextInt();
            sc.nextLine();

            // Check for duplicate ID
            boolean duplicate = false;
            for (Product p : products) {
                if (p.getId() == id) {
                    duplicate = true;
                    break;
                }
            }
            if (duplicate) {
                System.out.println("ID already exists! Skipping.");
                continue;
            }

            System.out.print("Enter Product Name: ");
            String name = sc.nextLine();

            System.out.print("Enter Quantity: ");
            int qty = sc.nextInt();
            sc.nextLine(); // clear buffer

            products.add(new Product(id, name, qty));
        }
        isSorted = false; // New data added, may not be sorted
    }

    // Display Data
    static void display() {
        if (products.isEmpty()) {
            System.out.println("No products to display.");
            return;
        }
        System.out.println("\nProduct List:");
        System.out.printf("%-5s %-20s %-10s\n", "ID", "Name", "Quantity");
        System.out.println("-----------------------------------------");

        for (Product p : products) {
            System.out.printf("%-5d %-20s %-10d\n", p.getId(), p.getName(), p.getQuantity());
        }
    }

    // Insertion Sort (by Product ID)
    static void sortData() {
        if (products.isEmpty()) {
            System.out.println("No products to sort.");
            return;
        }
        for (int i = 1; i < products.size(); i++) {
            Product key = products.get(i);
            int j = i - 1;

            while (j >= 0 && products.get(j).getId() > key.getId()) {
                products.set(j + 1, products.get(j));
                j--;
            }
            products.set(j + 1, key);
        }

        isSorted = true;
        System.out.println("\nSorted successfully using Insertion Sort!");
    }

    // Search by Product ID
    static void searchData(Scanner sc) {
        if (products.isEmpty()) {
            System.out.println("No products to search.");
            return;
        }
        System.out.print("Enter Product ID to search: ");
        int key = sc.nextInt();
        sc.nextLine();

        int index = -1;
        if (isSorted) {
            // Binary search
            int low = 0, high = products.size() - 1;
            while (low <= high) {
                int mid = (low + high) / 2;
                if (products.get(mid).getId() == key) {
                    index = mid;
                    break;
                } else if (products.get(mid).getId() < key) {
                    low = mid + 1;
                } else {
                    high = mid - 1;
                }
            }
        } else {
            // Linear search
            for (int i = 0; i < products.size(); i++) {
                if (products.get(i).getId() == key) {
                    index = i;
                    break;
                }
            }
        }

        if (index != -1) {
            Product p = products.get(index);
            System.out.println("\nProduct Found!");
            System.out.println("ID: " + p.getId());
            System.out.println("Name: " + p.getName());
            System.out.println("Quantity: " + p.getQuantity());
        } else {
            System.out.println("Product not found!");
        }
    }

    // Update Quantity by Product ID
    static void updateQuantity(Scanner sc) {
        if (products.isEmpty()) {
            System.out.println("No products to update.");
            return;
        }
        System.out.print("Enter Product ID to update: ");
        int key = sc.nextInt();
        sc.nextLine();

        for (Product p : products) {
            if (p.getId() == key) {
                System.out.print("Enter new quantity: ");
                int newQty = sc.nextInt();
                sc.nextLine();
                p.setQuantity(newQty);
                System.out.println("Quantity updated successfully!");
                return;
            }
        }
        System.out.println("Product not found!");
    }

    // Delete Product by ID
    static void deleteProduct(Scanner sc) {
        if (products.isEmpty()) {
            System.out.println("No products to delete.");
            return;
        }
        System.out.print("Enter Product ID to delete: ");
        int key = sc.nextInt();
        sc.nextLine();

        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getId() == key) {
                products.remove(i);
                System.out.println("Product deleted successfully!");
                isSorted = false; // List may not be sorted after removal
                return;
            }
        }
        System.out.println("Product not found!");
    }

    // Main Menu
    public static void main(String[] args) {
        loadProducts(); // Load previous data
        Scanner sc = new Scanner(System.in);
        int choice;

        while (true) {
            System.out.println("\n--- Inventory System ---");
            System.out.println("1. Insert Data");
            System.out.println("2. Display");
            System.out.println("3. Sort (Insertion Sort)");
            System.out.println("4. Search");
            System.out.println("5. Update Quantity");
            System.out.println("6. Delete Product");
            System.out.println("7. Exit");
            System.out.print("Enter choice: ");

            choice = sc.nextInt();
            sc.nextLine(); // clear buffer

            switch (choice) {
                case 1:
                    insertData(sc);
                    break;
                case 2:
                    display();
                    break;
                case 3:
                    sortData();
                    break;
                case 4:
                    searchData(sc);
                    break;
                case 5:
                    updateQuantity(sc);
                    break;
                case 6:
                    deleteProduct(sc);
                    break;
                case 7:
                    saveProducts(); // Save data before exit
                    System.out.println("Exiting...");
                    sc.close();
                    return;
                default:
                    System.out.println("Invalid choice!");
            }
        }
    }
}