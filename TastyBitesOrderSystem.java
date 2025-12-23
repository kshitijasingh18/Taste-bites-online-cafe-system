import java.io.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

class MenuItem {
    private final String name;
    private final double price;
    private int availableQuantity;

    public MenuItem(String name, double price, int availableQuantity) {
        this.name = name;
        this.price = price;
        this.availableQuantity = availableQuantity;
    }

    public String getName() { return name; }
    public double getPrice() { return price; }
    public int getAvailableQuantity() { return availableQuantity; }

    public boolean reduceQuantity(int qty) {
        if (qty <= availableQuantity) {
            availableQuantity -= qty;
            return true;
        }
        return false;
    }
}

class OrderItem {
    private final MenuItem item;
    private final int quantity;

    public OrderItem(MenuItem item, int quantity) {
        this.item = item;
        this.quantity = quantity;
    }

    public MenuItem getItem() { return item; }
    public int getQuantity() { return quantity; }
    public double getTotal() { return item.getPrice() * quantity; }
}

public class TastyBitesOrderSystem {
    private static final List<MenuItem> menu = new ArrayList<>();
    private static final List<OrderItem> orderList = new ArrayList<>();
    private static final Scanner sc = new Scanner(System.in);
    private static final LocalTime OPEN_TIME = LocalTime.of(8, 0);
    private static final LocalTime CLOSE_TIME = LocalTime.of(22, 0);

    public static void main(String[] args) {
        initializeMenu();

        System.out.println("🍔 Welcome to TastyBites Online Ordering 🍟");
        System.out.println("🕗 Café Timings: " + OPEN_TIME + " - " + CLOSE_TIME);

        LocalTime now = LocalTime.now();
        if (now.isBefore(OPEN_TIME) || now.isAfter(CLOSE_TIME)) {
            System.out.println("\n⚠️ Sorry! TastyBites is currently closed.");
            System.out.println("Please visit us between " + OPEN_TIME + " and " + CLOSE_TIME + ".");
            return;
        }

        while (true) {
            showMenu();
            System.out.print("Enter item numbers to order (e.g. 1 2 3, or 0 to finish): ");
            String input = sc.nextLine().trim(); // ✅ FIXED: removed extra nextLine()

            if (input.equals("0")) {
                break;
            }

            String[] choices = input.split("\\s+");
            for (String ch : choices) {
                try {
                    int choice = Integer.parseInt(ch);
                    if (choice > 0 && choice <= menu.size()) {
                        MenuItem selected = menu.get(choice - 1);
                        System.out.print("Enter quantity for " + selected.getName() + ": ");
                        int qty = sc.nextInt();
                        sc.nextLine(); // consume leftover newline

                        if (qty <= 0) {
                            System.out.println("❌ Invalid quantity.\n");
                            continue;
                        }

                        if (selected.reduceQuantity(qty)) {
                            orderList.add(new OrderItem(selected, qty));
                            System.out.println("✅ " + selected.getName() + " added!\n");
                        } else {
                            System.out.println("⚠️ Only " + selected.getAvailableQuantity() + " available.\n");
                        }
                    } else {
                        System.out.println("❌ Invalid item number: " + ch);
                    }
                } catch (NumberFormatException e) {
                    System.out.println("⚠️ '" + ch + "' is not a valid number.");
                }
            }
        }

        if (orderList.isEmpty()) {
            System.out.println("🕒 No items ordered. Goodbye!");
        } else {
            printBill();
        }
    }

    private static void initializeMenu() {
        menu.add(new MenuItem("Espresso", 3.50, 10));
        menu.add(new MenuItem("Cappuccino", 4.00, 8));
        menu.add(new MenuItem("Latte", 4.50, 6));
        menu.add(new MenuItem("Mocha", 5.00, 5));
        menu.add(new MenuItem("Croissant", 2.75, 15));
        menu.add(new MenuItem("Muffin", 2.50, 12));
        menu.add(new MenuItem("Sandwich", 5.50, 7));
    }

    private static void showMenu() {
        System.out.println("\n------ MENU ------");
        for (int i = 0; i < menu.size(); i++) {
            MenuItem m = menu.get(i);
            System.out.printf("%d. %-15s $%.2f (Available: %d)%n",
                    i + 1, m.getName(), m.getPrice(), m.getAvailableQuantity());
        }
        System.out.println("------------------");
    }

    private static void printBill() {
        LocalDateTime orderTime = LocalDateTime.now();
        double total = 0;
        int totalQty = 0;

        StringBuilder receipt = new StringBuilder();

        receipt.append("\n========== TASTY BITES ONLINE BILL ==========\n");
        receipt.append("🕒 Order Accepted Time: ").append(orderTime).append("\n");
        receipt.append("-------------------------------------------\n");

        for (OrderItem o : orderList) {
            double itemTotal = o.getTotal();
            total += itemTotal;
            totalQty += o.getQuantity();
            receipt.append(String.format("%-15s x %2d = $%.2f%n",
                    o.getItem().getName(), o.getQuantity(), itemTotal));
        }

        receipt.append("-------------------------------------------\n");
        receipt.append(String.format("Total Items Ordered: %d%n", orderList.size()));
        receipt.append(String.format("Total Quantity:      %d%n", totalQty));
        receipt.append(String.format("Grand Total:        $%.2f%n", total));
        receipt.append("-------------------------------------------\n");
        receipt.append("✅ Your order has been accepted. Please collect it soon!\n");
        receipt.append("Thank you for ordering at TastyBites! 🍔\n");
        receipt.append("===========================================\n");

        System.out.println(receipt);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HHmmss");
        String timestamp = orderTime.format(formatter);
        String filename = "TastyBites_Receipt_" + timestamp + ".txt";

        try (FileWriter writer = new FileWriter(filename)) {
            writer.write(receipt.toString());
            System.out.println("🧾 Receipt saved as '" + filename + "'.");
        } catch (IOException e) {
            System.out.println("⚠️ Error saving receipt: " + e.getMessage());
        }
    }
}
