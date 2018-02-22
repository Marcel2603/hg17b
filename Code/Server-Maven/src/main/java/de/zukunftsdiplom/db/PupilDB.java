package de.zukunftsdiplom.db;

import java.util.ArrayList;
import java.util.HashMap;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.sparql.core.DatasetGraph;
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
}
