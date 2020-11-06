import java.util.ArrayList;
import java.util.List;

public class Movie {
    public int year;
    public List<Integer> genre;
    public List<Star> stars;
    public String title;
    public String director;
    public static int idct = 0;
    public int id;

    public static void setMaxId(int max){
        idct = max;
    }

    public Movie(String title, int year, String director, List<Integer> genre){
        this.id = idct;
        this.title = title;
        this.year = year;
        this.director = director;
        this.genre = genre;
        this.stars = new ArrayList<>();
        idct++;
    }

}
