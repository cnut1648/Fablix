package edu.uci.ics.fabflixmobile;

import java.io.Serializable;
import java.util.ArrayList;

public class Movie implements Serializable {
    private String name;
    private short year;
    private String genre;
    private String star;
    private String director;
    private String rating;

    public Movie(String name, short year,String genre,String star,String director, String rating) {
        this.name = name;
        this.year = year;
        this.genre=genre;
        this.star=star;
        this.director=director;
        this.rating = rating;
    }

    public String getName() {
        return name;
    }

    public short getYear()
    {
        return year;
    }

    public String getGenre() {
        return genre;
    }
    public String getStar() {
        return star;
    }
    public String getRating() {return rating;}

    public String getThreeStar() {
        String newstar="";
        int count=0;

        for (String single : star.split(",")) {
                newstar = newstar + single;
                newstar = newstar + ", ";
                count+=1;
                if(count==3)
                {
                    break;
                }
            }
        return newstar.substring(0, newstar.length()-1);
    }
    public String getDirector() {
        return director;
    }

    public String getAllStar(){
        String r= "";
        for (String s: star.split(",")){
            r += s + "\n";
        }
        return r.substring(0, r.length()-1);
    }

    public String getAllGenre(){
        String r = "";
        for (String s: genre.split(",")){
            r += s + "\n";
        }
        return r.substring(0, r.length()-1);
    }
}