public class Star {
    String name;
    Integer dob;
    int id;

    static int idct = 0;
    public static void setMaxId(int max){
        idct = max;
    }


    public Star(String name, Integer dob){
        this.id = idct;
        this.name = name;
        this.dob = dob;
        idct++;
    }

}
