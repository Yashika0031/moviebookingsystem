/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package moviebookingsystem;

public class Movie implements Comparable<Movie> {
    String name, time;
    int silverSeats, goldSeats, platinumSeats;

    public Movie(String name, String time, int silver, int gold, int platinum) {
        this.name = name;
        this.time = time;
        this.silverSeats = silver;
        this.goldSeats = gold;
        this.platinumSeats = platinum;
    }

    @Override
    public int compareTo(Movie m) {
        return this.time.compareTo(m.time);
    }
}
