package db;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.query.ResultSet;
import org.apache.jena.tdb.TDBFactory;
import org.apache.jena.update.GraphStore;
import org.apache.jena.update.GraphStoreFactory;
import org.apache.jena.update.UpdateExecutionFactory;
import org.apache.jena.update.UpdateFactory;
import org.apache.jena.update.UpdateProcessor;
import org.apache.jena.update.UpdateRequest;

@SuppressWarnings("deprecation")
public class PupilDB {
	String directory = "pupilDB";
	Dataset dataset;

	public PupilDB(String directory){
		dataset = TDBFactory.createDataset(directory) ;
	}
	public PupilDB(){
		dataset = TDBFactory.createDataset(directory) ;
	}
	
	public void addPupil(int id, int score){
		dataset.begin(ReadWrite.WRITE) ;
		GraphStore graphStore = GraphStoreFactory.create(dataset);
		String insertString = 
        		"PREFIX dc: <http://zukunftsdiplom/pupils/elements/> " +
        		"INSERT DATA { " +
        				"<http://pupil/"+id+"> <http://zukunftsdiplom/pupils/elements/score> \""+score+"\" ; " +
        		"} ";
		UpdateRequest request = UpdateFactory.create(insertString) ;
	    UpdateProcessor proc = UpdateExecutionFactory.create(request, graphStore) ;
	    proc.execute() ;
	    dataset.commit();
	    
	}
	
	public int getScore(int id){
		dataset.begin(ReadWrite.READ) ;
		String qs1=
				"SELECT ?x "+
				"WHERE {<http://pupil/"+id+"> <http://zukunftsdiplom/pupils/elements/score>  ?x }";	
		try(QueryExecution qExec = QueryExecutionFactory.create(qs1, dataset)) {
			ResultSet rs = qExec.execSelect() ;
		    //ResultSetFormatter.out(rs) ;
		    //System.out.println(rs.getRowNumber());
		    //List<QuerySolution> ls;
			if(rs.hasNext()){
				QuerySolution qs = rs.next();
			    String x = qs.get("x").toString();
			    return Integer.parseInt(x);
			}	
			return 0;
			//System.out.println(rs.hasNext());
		    //String ls=ResultSetFormatter.asText(rs);
		    //System.out.println(ls);
		} finally { dataset.end(); }		
	    /*System.out.println(dataset.containsNamedModel("http://zukunftsdiplom/pupils"));
	    Model model = dataset.getNamedModel("http://zukunftsdiplom/pupils");	   
	 	Resource vcard = model.getResource("http://pupil/2");*/	 	
	}
	public boolean isID(int id){
		dataset.begin(ReadWrite.READ) ;
		String qs1=
				"SELECT ?x "+
				"WHERE {<http://pupil/"+id+"> <http://zukunftsdiplom/pupils/elements/score>  ?x }";	
		try(QueryExecution qExec = QueryExecutionFactory.create(qs1, dataset)) {
			ResultSet rs = qExec.execSelect() ;
			if(rs.getRowNumber()>0){
				return true;
			}
		} finally { dataset.end(); }
		return false;
	}
}
