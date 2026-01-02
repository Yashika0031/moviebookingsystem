/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package moviebookingsystem;

/**
 *
 * @author goyal
 */
public class Booking {
    String movieName, category;
    int tickets;

    public Booking(String movieName, String category, int tickets) {
        this.movieName = movieName;
        this.category = category;
        this.tickets = tickets;
    }
}

