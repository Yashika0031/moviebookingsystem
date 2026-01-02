/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package moviebookingsystem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author goyal
 */
import java.util.*;

public class MovieBookingSystem {
    static ArrayList<Movie> movies = new ArrayList<>();
    static LinkedList<Booking> bookings = new LinkedList<>();
    static Stack<String> cancelStack = new Stack<>();
    static Queue<String> waitlist = new LinkedList<>();
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        loadMovies();
        int choice;
        do {
            System.out.println("\n--- Movie Booking System ---");
            System.out.println("1. Display Movies\n2. Search Movie\n3. Book Ticket\n4. Show Bookings\n5. Cancel Last Booking\n6. Show Waitlist\n7. Exit");
            System.out.print("Enter your choice: ");
            choice = sc.nextInt();
            sc.nextLine();  // consume newline
            switch (choice) {
                case 1 -> displayMovies();
                case 2 -> searchMovie();
                case 3 -> bookTicket();
                case 4 -> showBookings();
                case 5 -> cancelLastBooking();
                case 6 -> showWaitlist();
                case 7 -> System.out.println("Exiting...");
                default -> System.out.println("Invalid choice.");
            }
        } while (choice != 7);
    }

    public static void loadMovies() {
        movies.add(new Movie("Avengers", "12:00", 50, 30, 20));
        movies.add(new Movie("Batman", "15:00", 40, 20, 10));
        movies.add(new Movie("Spiderman", "18:00", 60, 35, 25));
        Collections.sort(movies);
    }

    public static void displayMovies() {
        for (int i = 0; i < movies.size(); i++) {
            Movie m = movies.get(i);
            System.out.printf("%d. %s at %s (Silver: %d, Gold: %d, Platinum: %d)\n",
                    i + 1, m.name, m.time, m.silverSeats, m.goldSeats, m.platinumSeats);
        }
    }

    public static void searchMovie() {
        System.out.print("Enter movie name to search: ");
        String name = sc.nextLine();
        for (Movie m : movies) {
            if (m.name.equalsIgnoreCase(name)) {
                System.out.printf("Found: %s at %s (Silver: %d, Gold: %d, Platinum: %d)\n",
                        m.name, m.time, m.silverSeats, m.goldSeats, m.platinumSeats);
                return;
            }
        }
        System.out.println("Movie not found.");
    }

    public static void bookTicket() {
        displayMovies();
        System.out.print("Select movie number: ");
        int movieIndex = sc.nextInt() - 1;
        sc.nextLine();
        if (movieIndex < 0 || movieIndex >= movies.size()) {
            System.out.println("Invalid movie.");
            return;
        }

        Movie selected = movies.get(movieIndex);
        System.out.print("Enter category (Silver/Gold/Platinum): ");
        String category = sc.nextLine();
        System.out.print("Enter number of tickets: ");
        int tickets = sc.nextInt();
        sc.nextLine();

        int available = switch (category) {
            case "Silver" -> selected.silverSeats;
            case "Gold" -> selected.goldSeats;
            case "Platinum" -> selected.platinumSeats;
            default -> -1;
        };

        if (available == -1) {
            System.out.println("Invalid category.");
        } else if (tickets <= available) {
    switch (category) {
        case "Silver" -> selected.silverSeats -= tickets;
        case "Gold" -> selected.goldSeats -= tickets;
        case "Platinum" -> selected.platinumSeats -= tickets;
    }

    bookings.addFirst(new Booking(selected.name, category, tickets));
    cancelStack.push(selected.name);
    System.out.println("Booking confirmed!");

    // 👇 Add this block to save booking into MySQL
    try (Connection conn = DBConnection.getConnection()) {
        String sql = "INSERT INTO bookings (movie_name, category, tickets) VALUES (?, ?, ?)";
        var ps = conn.prepareStatement(sql);
        ps.setString(1, selected.name);
        ps.setString(2, category);
        ps.setInt(3, tickets);
        ps.executeUpdate();
        System.out.println("Booking saved to database.");
    } catch (Exception e) {
        System.out.println("Database error: " + e.getMessage());
    }
}
 else {
System.out.println("Not enough seats. Adding to waitlist.");
System.out.print("Enter your name: ");
String name = sc.nextLine();
waitlist.add(name);

// Save waitlist entry to database
try (Connection conn = DBConnection.getConnection()) {
    String sql = "INSERT INTO waitlist (customer_name, movie_name) VALUES (?, ?)";
    var ps = conn.prepareStatement(sql);
    ps.setString(1, name);
    ps.setString(2, selected.name);
    ps.executeUpdate();
} catch (Exception e) {
    System.out.println("Error saving to waitlist: " + e.getMessage());
}
        }
    }

    public static void showBookings() {
    System.out.println("\nBookings from Database:");

    try (Connection conn = DBConnection.getConnection()) {
        String sql = "SELECT movie_name, category, tickets FROM bookings";
        var ps = conn.prepareStatement(sql);
        var rs = ps.executeQuery();

        boolean hasResults = false;
        while (rs.next()) {
            String movieName = rs.getString("movie_name");
            String category = rs.getString("category");
            int tickets = rs.getInt("tickets");

            System.out.printf("%s | %s | Tickets: %d\n", movieName, category, tickets);
            hasResults = true;
        }

        if (!hasResults) {
            System.out.println("No bookings found in the database.");
        }

    } catch (Exception e) {
        System.out.println("Error reading from database: " + e.getMessage());
    }
}


    public static void cancelLastBooking() {
    if (cancelStack.isEmpty()) {
        System.out.println("No bookings to cancel.");
    } else {
        String cancelled = cancelStack.pop();
        System.out.println("Cancelled last booking for: " + cancelled);

        // Save to database
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO cancellations (movie_name) VALUES (?)";
            var ps = conn.prepareStatement(sql);
            ps.setString(1, cancelled);
            ps.executeUpdate();
        } catch (Exception e) {
            System.out.println("Error saving cancellation: " + e.getMessage());
        }
    }
}


    public static void showWaitlist() {
    System.out.println("\nWaitlist from Database:");

    try (Connection conn = DBConnection.getConnection()) {
        String sql = "SELECT customer_name, movie_name FROM waitlist";
        var ps = conn.prepareStatement(sql);
        var rs = ps.executeQuery();

        boolean hasResults = false;
        while (rs.next()) {
            String name = rs.getString("customer_name");
            String movie = rs.getString("movie_name");
            System.out.printf("%s (Waiting for: %s)\n", name, movie);
            hasResults = true;
        }

        if (!hasResults) {
            System.out.println("Waitlist is empty.");
        }

    } catch (Exception e) {
        System.out.println("Error reading waitlist: " + e.getMessage());
    }
}

}
