import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

public class XMLParser {

    Document dom;
    private String username;
    private String passwd;
    DocumentBuilderFactory dbf;
    FileWriter parseFile;
    dataQuery dq;

    HashMap<String, List<Movie>> directorsMap;

    // {"star name" -> [ STAR1, STAR2, ...] }
    // each ele in list is star with same name but diff dob
    HashMap<String, List<Star>> starMap;

    public XMLParser() {
        this.dbf = DocumentBuilderFactory.newInstance();
        this.directorsMap = new HashMap<>();
    }

    private void getContext(){
        try{
            dom =  dbf.newDocumentBuilder().parse("../fablix/web/META-INF/context.xml");
            Element root = dom.getDocumentElement();
            Element resource = (Element) root.getElementsByTagName("Resource").item(0);
            username = resource.getAttribute("username");
            passwd = resource.getAttribute("password");
//            System.out.println("username: " + username + " password: " + passwd);
        } catch (SAXException | ParserConfigurationException | IOException e) {
            System.out.println("error in get Context");
            e.printStackTrace();
        }
    }

    public void run() {
        try{
            System.out.println("begin parsing.");
            dq = new dataQuery(username, passwd);
            dq.init();

            // set id max ct
            Movie.setMaxId(dq.movieMax);
            Star.setMaxId(dq.starMax);

            System.out.println("parse-log in parse-log.txt.");
            this.parseFile = new FileWriter("parse-log.txt");

            parseMain();
            parseActor();
            parseCast();

            System.out.println("Done parsing!");
            this.parseFile.close();
        } catch (Exception e) {
            System.out.println("error in run()");
            e.printStackTrace();
        }
    }

    // pre-check:   all login movies are all new, no need checking
    private void parseMain() {
        try {
            //Using factory get an instance of document builder
            DocumentBuilder db = dbf.newDocumentBuilder();

            //parse using builder to get DOM representation of the XML file
            this.dom = db.parse("xmls/mains243.xml");
            this.dom.normalize();

            Element movies = dom.getDocumentElement();

            this.parseFile.write("####################\n");
            this.parseFile.write("       mains243     \n");
            this.parseFile.write("####################\n\n");

            NodeList directorfilms = movies.getElementsByTagName("directorfilms");

            for (int i = 0; i < directorfilms.getLength(); i++) {
                Element directorfilm = (Element) directorfilms.item(i);
                Element director = (Element) directorfilm.getElementsByTagName("director").item(0);

                String dirname = null;

                // director name
                try {
                    dirname = director.getElementsByTagName("dirname").item(0).getFirstChild().getNodeValue();
                } catch (Exception e) {
                    // no dirname
                    String nodeInfo = director.getNodeName();
                    Node dirid = director.getElementsByTagName("dirid").item(0);
                    nodeInfo += (dirid != null ? " (sid " + dirid.getFirstChild().getNodeValue() + ")" : "");
                    this.parseFile.write("no dirname while " + nodeInfo + "\n");
                }
                String dirid = director.getElementsByTagName("dirid").item(0).getFirstChild().getNodeValue();

                // only care movies with un-null dirname
                if (dirname != null && dirid != null) {
                    this.directorsMap.put(dirid, new ArrayList<>());

                    NodeList films = ((Element) directorfilm.getElementsByTagName("films").item(0))
                            .getElementsByTagName("film");

                    for (int j = 0; j < films.getLength(); j++) {
                        Element film = (Element) films.item(j);

                        String t = null;
                        Integer yearInt = null;
                        Integer cat = null;

                        // movie title
                        try {
                            t = film.getElementsByTagName("t").item(0).getFirstChild().getNodeValue();
                        } catch (Exception e) {
                            String nodeInfo = film.getNodeName();
                            Node fid = film.getElementsByTagName("fid").item(0);
                            nodeInfo += (fid != null ? " (fid " + fid.getFirstChild().getNodeValue() + ")" : "");
                            this.parseFile.write("no film title in " + nodeInfo + "\n");
                        }

                        // movie year
                        try {
                            String year = film.getElementsByTagName("year").item(0).getFirstChild().getNodeValue();
                            yearInt = Integer.parseInt(year);
                        } catch (Exception e) {
                            String nodeInfo = film.getNodeName();
                            Node fid = film.getElementsByTagName("fid").item(0);
                            nodeInfo += (fid != null ? " (fid " + fid.getFirstChild().getNodeValue() + ")" : "");
                            this.parseFile.write("no film year in " + nodeInfo + "\n");
                        }

                        String catCode = null;
                        // category
                        Element cats = (Element) film.getElementsByTagName("cats").item(0);
                        // some movie has no <cats> tag
                        if (cats == null) {
                            String nodeInfo = film.getNodeName();
                            Node fid = film.getElementsByTagName("fid").item(0);
                            nodeInfo += (fid != null ? " (fid " + fid.getFirstChild().getNodeValue() + ")" : "");
                            this.parseFile.write("no film cat tag in " + nodeInfo + "\n");
                            continue;
                        }

                        NodeList listOfcats = cats.getElementsByTagName("cat");
                        if (listOfcats.getLength() == 0){
                            String nodeInfo = film.getNodeName();
                            Node fid = film.getElementsByTagName("fid").item(0);
                            nodeInfo += (fid != null ? " (fid " + fid.getFirstChild().getNodeValue() + ")" : "");
                            this.parseFile.write("no cat in " + nodeInfo + "\n");
                            continue;
                        }

                        List<Integer> catCodes = new ArrayList<>();
                        try{
                            for (int c = 0; c<listOfcats.getLength(); c++){
                                catCode = listOfcats.item(c).getFirstChild().getNodeValue();

                                // try decode, else inconsistent report
                                try{
                                    cat = Integer.parseInt(decodeCat(catCode));
                                    catCodes.add(cat);
                                } catch (Exception e){
                                    this.parseFile.write("unrecognizable cat " + catCode + "\n");
                                }
                            }
                        } catch (Exception e) {
                            String nodeInfo = film.getNodeName();
                            Node fid = film.getElementsByTagName("fid").item(0);
                            nodeInfo += (fid != null ? " (fid " + fid.getFirstChild().getNodeValue() + ")" : "");
                            this.parseFile.write("no cat in " + nodeInfo + "\n");
                        }


                        if (t != null && yearInt != null && catCodes.size()>0){
//                            if (this.dq.dir.containsKey(dirname)){
//                                System.out.println("dir in db");
//                                System.out.println(dirname);
//                                System.out.println(t);
//                                System.out.println(this.dq.dir.get(dirname));
//                            }
                            this.directorsMap.get(dirid).add(new Movie(t, yearInt, dirname, catCodes));
                        }
                    }
                }
            }
        } catch (ParserConfigurationException | SAXException | IOException pce) {
            System.out.println("error in parse Main");
            pce.printStackTrace();
        }
    }

    private String decodeCat(String catCode){
        String cat;
        switch (catCode){
            case "Susp":
                cat = "21";
                break;
            case "Fant ":
            case "Fant":
                cat = "11";
                break;
            case "CnR":
            case "CnRb":
                cat = "7";
                break;
            case "DRam":
            case "Dram":
                cat = "9";
                break;
            case "West":
                cat = "23";
                break;
            case "Myst":
                cat = "16";
                break;
            case "Actn":
                cat = "1";
                break;
            case "S.F.":
            case "SciF":
            case "ScFi":
                cat = "19";
                break;
            case "Advt":
                cat = "3";
                break;
            case "Horr":
                cat = "13";
                break;
            case "Hist":
                cat = "31";
                break;
            case "Romt":
                cat = "18";
                break;
            case "Comd":
            case "Comd ":
                cat = "6";
                break;
            case "Musc":
                cat = "14";
                break;
            case "Docu":
                cat = "8";
                break;
            case "BioP":
                cat = "5";
                break;
            case "TV":
                cat = "17";
                break;
            case "Porn":
                cat = "24";
                break;
            case "Noir":
                cat = "26";
                break;
            case "TVs":
                cat = "25";
                break;
            case "TVm":
                cat = "27";
                break;
            case "Romt. Comd":
            case "Romt Comd":
                cat = "28";
                break;
            case "Epic":
                cat = "29";
                break;
            case "Cart":
                cat = "30";
                break;
            default:
                // Porn, Noir, TVs, TVm, Romt Comd
                cat = catCode;
                break;
        }
        return cat;
    }

    private void parseActor(){
        try {

            //Using factory get an instance of document builder
            DocumentBuilder db = dbf.newDocumentBuilder();

            //parse using builder to get DOM representation of the XML file
            dom = db.parse("xmls/actors63.xml");
            dom.normalize();
            this.starMap = new HashMap<>();

            Element actors= dom.getDocumentElement();

            this.parseFile.write("\n####################\n");
            this.parseFile.write("       actors63     \n");
            this.parseFile.write("####################\n\n");

            NodeList actorList = actors.getElementsByTagName("actor");

            String stagename;
            Integer dob;

            for (int i = 0; i < actorList.getLength(); i++) {
                Element actor = (Element) actorList.item(i);
                stagename = actor.getElementsByTagName("stagename").item(0).getFirstChild().getNodeValue();

                try {
                    dob = Integer.parseInt(
                            actor.getElementsByTagName("dob").item(0).getFirstChild().getNodeValue()
                    );
                } catch (Exception e) {
                    dob = null;
                }


                // already exist in db
                // db.stars map null to 0
                List<Integer> dqStarDob = this.dq.stars.get(stagename);
                boolean starExist = false;
                if (dqStarDob != null){
                    int dobToCheck = dob == null ? 0: dob;
                    for (int dqdob: dqStarDob){
                        if (dqdob == dobToCheck){
                            this.parseFile.write("found same actor with dob " + dobToCheck + " ," + stagename + "\n");
                            starExist = true;
                            break;
                        }
                    }

                }
                // this star not exist in db
                if (!starExist){
                    List<Star> listOfStars = this.starMap.get(stagename);
                    if (listOfStars == null){
                        // if not put yet now put
                        List<Star> l = new ArrayList<>();
                        l.add(new Star(stagename, dob));
                        starMap.put(stagename, l);
                    }
                    else{
                        // if put already then add
                        boolean add = true;
                        for (Star s: listOfStars){
                            if (s.dob == null && dob == null) {
                                add = false;
                                break;
                            }
                            else if (s.dob != null && s.dob.equals(dob)){
                                add = false;
                                break;
                            }
                        }
                        if (add) listOfStars.add(new Star(stagename, dob));
                    }
                }

            }
        } catch (ParserConfigurationException | SAXException | IOException pce) {
            System.out.println("error in parse Actor");
            pce.printStackTrace();
        }
    }

    // pre-check: all movies here are new and not in moviedb
    private void parseCast(){
        try {
            //Using factory get an instance of document builder
            DocumentBuilder db = dbf.newDocumentBuilder();

            //parse using builder to get DOM representation of the XML file
            this.dom = db.parse("xmls/casts124.xml");
            this.dom.normalize();

            Element casts = dom.getDocumentElement();

            this.parseFile.write("\n####################\n");
            this.parseFile.write("       casts124     \n");
            this.parseFile.write("####################\n\n");

            NodeList dirfilms = casts.getElementsByTagName("dirfilms");

            // for each director
            for (int i = 0; i < dirfilms.getLength(); i++){
                Element dirfilm = (Element) dirfilms.item(i);

                // director name
                String is = null;
                String dirid = null;
                try {
                    Element director = (Element) dirfilm.getElementsByTagName("is").item(0);
                    is = director.getFirstChild().getNodeValue();
                    dirid = dirfilm.getElementsByTagName("dirid").item(0).getFirstChild().getNodeValue();
                } catch (Exception e){
                    this.parseFile.write("no is/ dirid\n");
                }


                // some director appears here but not in login discard
                List<Movie> movies = this.directorsMap.get(dirid);
                if (movies == null){
                    this.parseFile.write("directors with no movie info (id = " + dirid + " " + is + ")\n");
                    continue;
                }

                // for each films by director dirid
                NodeList filmc = dirfilm.getElementsByTagName("filmc");
                for (int j = 0; j < filmc.getLength(); j++){
                    // a filmc tag
                    Element film = (Element) filmc.item(j);
                    NodeList ms = film.getElementsByTagName("m");

                    // for each star in this movie (film)
                    for (int k = 0; k < ms.getLength(); k++){
                        // a movie-star entity
                        Element m = (Element) ms.item(k);
                        // title
                        String t = m.getElementsByTagName("t").item(0).getFirstChild().getNodeValue();
//                        if (this.dq.dir.get(is) != null){
//                            System.out.println(is + "     " + t);
//                            System.out.println(this.dq.dir.get(is));
//                        }


                        // star stagename
                        String star = null;
                        try{
                            star = m.getElementsByTagName("a").item(0).getFirstChild().getNodeValue();
                        } catch (Exception e){
                            this.parseFile.write("no star name in movie title " + t + "\n");
                        }
                        if (star != null){
                            // ignore s a (some actors)
                            if (star.equals("s a") || star.equals("sa") ||
                                    star.equals("sa s a") || star.equals("sa sa") ||
                                    star.equals("sa s") || star.equals("s.a.") ||
                                    star.equals("\nsa")){
                                this.parseFile.write("no clear star name (" + star + ") in movie title "
                                        + t + "\n");
                                continue;
                            }

                            // update starMap
                            List<Star> stars  = starMap.get(star);
                            Star starToAdd = null;

                            // already exist
                            if (stars != null){
                                starToAdd = stars.get(0);
//                                boolean add = true;
//                                for (Star s: stars){
//                                    if (s.dob == null){
//                                        starToAdd = s;
//                                        add = false;
//                                        break;
//                                    }
//                                }
//                                if (add) {
//                                    starToAdd = new Star(star, null);
//                                    stars.add(starToAdd);
//                                }
                            }
                            else{
                            // not exist -> add
//                            if (stars == null){
                                List<Star> l = new ArrayList<>();
                                starToAdd = new Star(star, null);
                                l.add(starToAdd);
                                starMap.put(star, l);
                            }

                            // update movie
                            for (Movie movie: movies){
                                if (movie.title.equals(t)){
                                    movie.stars.add(starToAdd);
                                }
                            }

                        }
                    }
                }
            }
        } catch (ParserConfigurationException | SAXException | IOException pce) {
            System.out.println("error in parse Casts");
            pce.printStackTrace();
        }
    }

    // update moviedb
    public void update() throws Exception {
        System.out.println("start updating");

        File gim = new File("csvs/gim.csv");
        gim.getParentFile().mkdirs();
        FileWriter genres_in_movies = new FileWriter(gim);
        FileWriter movies = new FileWriter("csvs/movie.csv");
        FileWriter stars = new FileWriter("csvs/star.csv");
        FileWriter stars_in_movies = new FileWriter("csvs/sim.csv");


        for (Map.Entry<String, List<Movie>> kv : directorsMap.entrySet()){
            List<Movie> listOfMovies = kv.getValue();
            for (Movie m: listOfMovies){
                if (m.stars.size() != 0) {
                    for (Integer gid: m.genre){
                        genres_in_movies.write(gid + "|tt0" + m.id + "\n");
                    }
                    movies.write("tt0" + m.id + "|" + m.title + "|" + m.year + "|" + m.director + "\n");
                    for (Star s : m.stars) {
                        stars_in_movies.write("nm" + s.id + "|tt0" + m.id + "\n");
                    }
                }
            }
        }

        // stars
        for (Map.Entry<String, List<Star>> kv: starMap.entrySet()){
            List<Star> listOfStars = kv.getValue();
            for (Star s: listOfStars){
                String dobinput = s.dob == null ? "\\N" : String.valueOf(s.dob);
                stars.write("nm" + s.id+ "|" + s.name + "|" + dobinput + "\n");
                if (s.id == 9436026){
                    System.out.println(s.name);
                }

            }
        }

        System.out.println("done updating!");
        genres_in_movies.close();
        movies.close();
        stars.close();
        stars_in_movies.close();

        dataUpdater du = new dataUpdater(username, passwd);
        du.update();
    }

    public static void main(String[] args) {
        XMLParser parser = new XMLParser();
        parser.getContext();
        parser.run();
        try{
            parser.update();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}