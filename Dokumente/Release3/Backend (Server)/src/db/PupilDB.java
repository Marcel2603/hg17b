package db;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.tdb.TDBFactory;
import org.apache.jena.tdb2.DatabaseMgr;
import org.apache.jena.tdb2.TDB2Factory;
import org.apache.jena.update.UpdateExecutionFactory;
import org.apache.jena.update.UpdateFactory;
import org.apache.jena.update.UpdateProcessor;
import org.apache.jena.update.UpdateRequest;


/**
 * Die PupilDB.
 * @author Korti
 *
 */
public class PupilDB {
    /**
     * DatasetGraph object.
     */
    private DatasetGraph graphStore;
    /**
     * Default directory.
     */
    private String directory = "pupilDB";
    /**
     * The dataset Object.
     */
    private Dataset dataset;

    /**
     * Initializes the database.
     */
    public PupilDB() {
        dataset = TDB2Factory.connectDataset(directory);
        graphStore = DatabaseMgr.connectDatasetGraph(directory);
    }


    /**
     * Adds a student and students score.
     * @param id The students identification number.
     * @param score The students score.
     */
    public void addPupil(final int id, final int score) {
        graphStore.begin(ReadWrite.WRITE);
        String insertString =
                "INSERT DATA {"
                        + "GRAPH <http://zukunftsdiplom/pupils/>{"
                        + "<http://pupil/" + id + "> "
                        + "<http://zukunftsdiplom/pupils/elements/score>"
                        + " " + score + "; "
                        + "} "
                        + "} ";
        UpdateRequest request = UpdateFactory.create(insertString);
        UpdateProcessor proc = UpdateExecutionFactory.create(request,
                graphStore);
        proc.execute();
        graphStore.commit();
    }


    /**
     * Returns the students score.
     * @param id ID of student
     * @return Score of student
     */
    public int getScore(final int id) {
        dataset.begin(ReadWrite.READ);
        String qs1 =
                "SELECT ?x "
                        + "FROM <http://zukunftsdiplom/pupils/>"
                        + "WHERE {<http://pupil/" + id + "> "
                        + "<http://zukunftsdiplom/pupils/elements/score>  ?x }";

        try (QueryExecution qExec = QueryExecutionFactory.create(qs1,
                dataset)) {
            ResultSet rs = qExec.execSelect();
            //ResultSetFormatter.out(rs) ;
            //System.out.println(rs.getRowNumber());
            //List<QuerySolution> ls;

            if (rs.hasNext()) {
                QuerySolution qs = rs.next();
                Literal y = (Literal) qs.get("x");
                return y.getInt();
            }
            return 0;
            //System.out.println(rs.hasNext());
            //String ls=ResultSetFormatter.asText(rs);
            //System.out.println(ls);
        } finally {
            dataset.end();
        }
        /*System.out.println(dataset.containsNamedModel(
         * "http://zukunftsdiplom/pupils"));
        Model model = dataset.getNamedModel(
        "http://zukunftsdiplom/pupils");
        Resource vcard = model.getResource("http://pupil/2");*/
    }


    /**
     * Returns true if ID is in Database, false if not.
     * @param id students ID to be checked.
     * @return true if ID is in Database, false if not.
     */
    public boolean isID(final int id) {
        dataset.begin(ReadWrite.READ);
        String qs1 =
                "SELECT ?x "
                        + "FROM <http://zukunftsdiplom/pupils/>"
                        + "WHERE {<http://pupil/" + id + ">"
                        + " <http://zukunftsdiplom/pupils/elements/score> ?x }";
        try (QueryExecution qEx = QueryExecutionFactory.create(qs1, dataset)) {
            ResultSet rs = qEx.execSelect();
            if (rs.hasNext()) {
                return true;
            }
        } finally {
            dataset.end();
        }
        return false;
    }


    /**
     * Makes pupils ranks based on score.
     *
     */
    public void makeRank() {
        ArrayList<String> ranking = new ArrayList<>();
        deleteRanks();
        dataset.begin(ReadWrite.READ);
        String qs1 =
                "SELECT ?student ?score "
                        + "FROM <http://zukunftsdiplom/pupils/>"
                        + "WHERE {?student "
                        + "<http://zukunftsdiplom/pupils/elements/score>  ?score } "
                        + "ORDER BY DESC(?score)";
        try (QueryExecution qExec = QueryExecutionFactory.create(qs1,
                dataset)) {
            ResultSet rs = qExec.execSelect();
            //ResultSetFormatter.out(rs);
            while (rs.hasNext()) {
                QuerySolution qs = rs.next();
                String student = "<" + qs.get("student").toString() + ">";
                ranking.add(student);
            }
        } finally {
            dataset.end();
        }
        for (int i = 0; i < ranking.size(); i++) {
            graphStore.begin(ReadWrite.WRITE);
            String insertString =
                    "INSERT DATA {"
                            + "GRAPH <http://zukunftsdiplom/pupils/> {"
                            + ranking.get(i) + " "
                            + "<http://zukunftsdiplom/pupils/elements/rank>"
                            + " " + (i + 1) + "; "
                            + "} "
                            + "} ";
            UpdateRequest request = UpdateFactory.create(insertString);
            UpdateProcessor proc = UpdateExecutionFactory.create(request,
                    graphStore);
            proc.execute();
            graphStore.commit();
        }
    }


    /**
     * Gets the toplist as Hash-Map.
     * @return Hashmap with rank as key, score as value
     */
    public HashMap<Integer, Integer> getToplist() {
        HashMap<Integer, Integer> ranking = new HashMap<>();
        dataset.begin(ReadWrite.READ);
        String qs1 =
                "SELECT ?student ?rank ?score "
                        + "FROM <http://zukunftsdiplom/pupils/> "
                        + "WHERE {?student "
                        + "<http://zukunftsdiplom/pupils/elements/rank> ?rank . "
                        + "?student "
                        + "<http://zukunftsdiplom/pupils/elements/score> ?score} "
                        + "ORDER BY ASC(?rank) LIMIT 10";
        try (QueryExecution qExec = QueryExecutionFactory.create(qs1,
                dataset)) {
            ResultSet rs = qExec.execSelect();
            //ResultSetFormatter.out(rs);
            while (rs.hasNext()) {
                QuerySolution qs = rs.next();
                Literal rank = (Literal) qs.get("rank");
                Literal score = (Literal) qs.get("score");
                ranking.put(rank.getInt(), score.getInt());
            }
        } finally {
            dataset.end();
        }
        return ranking;
    }


    /**
     * Retrieves the students rank.
     * @param id Id of student.
     * @return Rank of student.
     */
    public int getRank(final int id) {
        String student = "<http://pupil/" + id + ">";
        int ranking = 0;
        dataset.begin(ReadWrite.READ);
        String qs1 =
                "SELECT ?rank "
                        + "FROM <http://zukunftsdiplom/pupils/> "
                        + "WHERE {" + student + " "
                        + "<http://zukunftsdiplom/pupils/elements/rank> ?rank  "
                        + "}";
        try (QueryExecution qExec = QueryExecutionFactory.create(qs1,
                dataset)) {
            ResultSet rs = qExec.execSelect();
            //ResultSetFormatter.out(rs);
            if (rs.hasNext()) {
                QuerySolution qs = rs.next();
                Literal rankLiteral = (Literal) qs.get("rank");
                ranking = rankLiteral.getInt();
            }
        } finally {
            dataset.end();
        }
        return ranking;
    }


    /**
     * Removes Ranks prior to calculating and adding them them.
     */
    private void deleteRanks() {
        graphStore.begin(ReadWrite.WRITE);
        String delString =
                "WITH <http://zukunftsdiplom/pupils/> "
                        + "DELETE {"
                        + "?student "
                        + "<http://zukunftsdiplom/pupils/elements/rank>"
                        + "?rank "
                        + "} "
                        + " WHERE {"
                        + "?student"
                        + "<http://zukunftsdiplom/pupils/elements/rank>"
                        + "?rank"
                        + "}";
        UpdateRequest request = UpdateFactory.create(delString);
        UpdateProcessor proc = UpdateExecutionFactory.create(request,
                graphStore);
        proc.execute();
        graphStore.commit();
    }

    /**
     * Checks if email address belongs to an Organizer.
     * @param email The Organizers email address.
     * @return True if email belongs to Organizer in Database. False if not.
     */
    public boolean isOrganizer(final String email) {
        dataset.begin(ReadWrite.READ);
        String qs1 =
                "prefix foaf: <http://xmlns.com/foaf/0.1/> "
                        + "SELECT ?x WHERE {?x foaf:mbox \"" + email + "\"}";
        try (QueryExecution qExec = QueryExecutionFactory.create(qs1,
                dataset)) {
            ResultSet rs = qExec.execSelect();
            // ResultSetFormatter.out(rs);
            if (rs.hasNext()) {
                return true;
            }
        } finally {
            dataset.end();
        }
        return false;
    }


    /**
     * Retrieves the address corresponding to an address ID as String.
     * @param addressId The id of the address to be retrieved.
     * @return The actual address.
     */
    private String getAddress(final String addressId) {
        dataset.begin(ReadWrite.READ);
        String adress = "";
        String qs1 = "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
                + "SELECT ?label "
                + "WHERE "
                +    "{" + addressId + " rdfs:label ?label } ";

        /*Code mit anderer Adressen.ttl:
         *
         *String qs1 = "prefix ld: <http://leipzig-data.de/Data/Model/> "
                + "SELECT ?street ?houseNumber ?postCode ?city ?district "
                + "WHERE "
                +    "{" + addressId + " ld:hasStreet ?street . "
                +     addressId + " ld:hasHouseNumber ?houseNumber . "
                +     addressId + " ld:hasPostCode ?postCode . "
                +     addressId + " ld:hasCity ?city . "
                +     addressId + " ld:inOrtsteil ?district}";
         */
        try (QueryExecution qExec = QueryExecutionFactory.create(qs1,
                dataset)) {
            ResultSet rs = qExec.execSelect();
            if (rs.hasNext()) {
                QuerySolution qs = rs.next();
                Literal x = (Literal) qs.get("label");
                adress = x.toString();

                /* Code mit anderer Adressen.ttl
                 * QuerySolution qs = rs.next();
                Literal x = (Literal) qs.get("street");
                adress = x.toString();
                x = (Literal) qs.get("houseNumber");
                adress = adress + " " + x.toString();
                x = (Literal) qs.get("postCode");
                adress = adress + "; " + x.toString();
                x = (Literal) qs.get("city");
                adress = adress + " " + x.toString();
                Resource y = (Resource) qs.get("district");
                adress = adress + " (" + y.toString() + ")";
                System.out.println(adress);*/
            }
        } finally {
            dataset.end();
        }
        return adress;
    }

    /**
     * Retrieves past or future Events of an Organizer in ArrayList.
     * Entries are HashMaps with an Events attributes.
     * @param email The Organizers email address.
     * @param past Boolean to check for past or future events. <br>
     * true - returns past events. <br>
     * false - returns future events.
     * @return ArrayList of HashMaps with keys as follows <br>
     * "label" - Eventname <br>
     * "address" - Adress of Event <br>
     * "url" - URL to Events homepage <br>
     * "description" - Event description
     * "start"- DateTime of Start
     * "end" - DateTime of Ending.
     */
    public ArrayList<HashMap<String, String>> getEventsOrganizer(
            final String email, final boolean past) {
        ArrayList<String> akteure = new ArrayList<String>();
        ArrayList<HashMap<String, String>> events =
                new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> newEvents =
                new ArrayList<HashMap<String, String>>();
        dataset.begin(ReadWrite.READ);
        String qs1 =
                "prefix foaf: <http://xmlns.com/foaf/0.1/> "
                        + "SELECT ?email WHERE {?email foaf:mbox \"" + email + "\"}";
        try (QueryExecution qExec = QueryExecutionFactory.create(qs1,
                dataset)) {
            ResultSet rs = qExec.execSelect();
            //ResultSetFormatter.out(rs);
            while (rs.hasNext()) {
                QuerySolution qs = rs.next();
                Resource x = (Resource) qs.get("email");
                if (x.toString().contains("/Data/Akteur")) {
                    akteure.add(x.toString());
                }

                //System.out.println("x");
                //Literal score = (Literal) qs.get("score");
                //ranking.put(rank.getInt(), score.getInt());
            }
        } finally {
            dataset.end();
        }
        final DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String now = sdf.format(date).toString().replace(" ", "T");
        for (String akteur : akteure) {
            qs1 = "PREFIX le: <http://leipziger-ecken.de/Data/Model#> "
                    + "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
                    + "prefix ical: <http://www.w3.org/2002/12/cal/ical#> "
                    + ""
                    + "SELECT ?label ?address ?url ?start ?end ?description "
                    + "WHERE{"
                    + "?x le:hatAkteur + <" + akteur + "> . "
                    + "?x rdfs:label ?label . "
                    + "?x ical:url ?url . "
                    + "?x ical:dtstart ?start . ";
            if (past) {
                qs1 = qs1 + "FILTER (?start < \"" + now + "\" ) ";
            } else {
                qs1 = qs1 + "FILTER (?start > \"" + now + "\" ) ";
            }
            qs1 = qs1
                    + "?x ical:dtend ?end . "
                    + "?x ical:location ?address . "
                    + "?x ical:summary ?description} "
                    + ""
                    + "ORDER BY ASC(?start)";
            dataset.begin(ReadWrite.READ);
            try (QueryExecution qExec = QueryExecutionFactory.create(qs1,
                    dataset)) {
                ResultSet rs = qExec.execSelect();
                //ResultSetFormatter.out(rs);

                while (rs.hasNext()) {
                    QuerySolution qs = rs.next();
                    HashMap<String, String> temp =
                            new HashMap<String, String>();
                    Literal x = (Literal) qs.get("label");
                    temp.put("label", x.toString());
                    Resource y = (Resource) qs.get("address");
                    temp.put("address", y.toString());
                    y = (Resource) qs.get("url");
                    temp.put("url", y.toString());
                    x = (Literal) qs.get("start");
                    temp.put("start", x.toString());
                    x = (Literal) qs.get("end");
                    temp.put("end", x.toString());
                    x = (Literal) qs.get("description");
                    temp.put("description", x.toString());
                    events.add(temp);
                }
            } finally {
                dataset.end();
            }
            for (HashMap<String, String> event:events) {
                HashMap<String, String> newEvent =
                        new HashMap<String, String>();
                newEvent.put("label", event.get("label"));
                newEvent.put("address",
                        getAddress("<" + event.get("address") + ">"));
                newEvent.put("url", event.get("url"));
                newEvent.put("start", event.get("start"));
                newEvent.put("end", event.get("stop"));
                newEvent.put("description", event.get("description"));
                newEvents.add(newEvent);
            }
        }
        return newEvents;
    }


    /**
     * Retrieves past or future Events of an Organizer in ArrayList.
     * Entries are HashMaps with an Events attributes.
     * @param past Boolean to check for past or future events. <br>
     * true - returns past events. <br>
     * false - returns future events.
     * @return ArrayList of HashMaps with keys as follows <br>
     * "label" - Eventname <br>
     * "address" - Adress of Event <br>
     * "url" - URL to Events homepage <br>
     * "description" - Event description
     * "start"- DateTime of Start
     * "end" - DateTime of Ending.
     */
    public ArrayList<HashMap<String, String>> getEventsStudents(
            final boolean past) {

        ArrayList<HashMap<String, String>> events =
                new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> newEvents =
                new ArrayList<HashMap<String, String>>();
        final DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String now = sdf.format(date).toString().replace(" ", "T");
        String qs1;
        qs1 = "PREFIX le: <http://leipziger-ecken.de/Data/Model#> "
                + "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
                + "prefix ical: <http://www.w3.org/2002/12/cal/ical#> "
                + ""
                + "SELECT ?label ?address ?url ?start ?end ?description "
                + "WHERE{"
                + "?x rdfs:label ?label . "
                + "?x ical:url ?url . "
                + "?x ical:dtstart ?start . ";
        if (past) {
            qs1 = qs1 + "FILTER (?start < \"" + now + "\" ) ";
        } else {
            qs1 = qs1 + "FILTER (?start > \"" + now + "\" ) ";
        }
        qs1 = qs1
                + "?x ical:dtend ?end . "
                + "?x ical:location ?address . "
                + "?x ical:summary ?description} "
                + ""
                + "ORDER BY ASC(?start)";
        dataset.begin(ReadWrite.READ);
        try (QueryExecution qExec = QueryExecutionFactory.create(qs1,
                dataset)) {
            ResultSet rs = qExec.execSelect();
            //ResultSetFormatter.out(rs);

            while (rs.hasNext()) {
                QuerySolution qs = rs.next();
                HashMap<String, String> temp =
                        new HashMap<String, String>();
                Literal x = (Literal) qs.get("label");
                temp.put("label", x.toString());
                Resource y = (Resource) qs.get("address");
                temp.put("address", y.toString());
                y = (Resource) qs.get("url");
                temp.put("url", y.toString());
                x = (Literal) qs.get("start");
                temp.put("start", x.toString());
                x = (Literal) qs.get("end");
                temp.put("end", x.toString());
                x = (Literal) qs.get("description");
                temp.put("description", x.toString());
                events.add(temp);
            }
        } finally {
            dataset.end();
        }
        for (HashMap<String, String> event:events) {
            HashMap<String, String> newEvent =
                    new HashMap<String, String>();
            newEvent.put("label", event.get("label"));
            newEvent.put("address",
                    getAddress("<" + event.get("address") + ">"));
            newEvent.put("url", event.get("url"));
            newEvent.put("start", event.get("start"));
            newEvent.put("end", event.get("stop"));
            newEvent.put("description", event.get("description"));
            newEvents.add(newEvent);
        }
        return newEvents;
    }


}
