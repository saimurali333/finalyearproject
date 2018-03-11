import java.sql.*;
import java.util.HashSet;
import java.util.Set;
/**haslink,has meaning,has title,has path,hasweight
 *
 * @author HP
 */

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.io.InputStreamHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.semantics.GraphManager;
import com.marklogic.client.semantics.RDFMimeTypes;
import com.marklogic.client.semantics.SPARQLMimeTypes;
import com.marklogic.client.semantics.SPARQLQueryDefinition;
import com.marklogic.client.semantics.SPARQLQueryManager;
public class Mysqlconn extends ExtractMetaData {
	 public Set<String> tableset = new HashSet<>();
     int ct=0;
     int tc=0;

public void insert(String title,String table,Set<String> data1)
{
 String value;
 
 	DatabaseClient client =DatabaseClientFactory.newClient("localhost",8060,"Documents",new DatabaseClientFactory.DigestAuthContext("admin","admin"));
		SPARQLQueryManager sqmgr = client.newSPARQLQueryManager();
		
    // ConceptIdentifier ci=new ConceptIdentifier();
   //  Class.forName("com.mysql.jdbc.Driver");  
     //Connection con=DriverManager.getConnection("jdbc:mysql://localhost:3306/mch_project","root","");  
     //here sonoo is database name, root is username and password  
     //Statement stmt=con.createStatement();  
     //try{
     //System.out.println("T: "+table+"       D:"+data1);
     tableset.clear();
     //Statement stmt=con.createStatement();
     //DatabaseMetaData md = con.getMetaData();
     //ResultSet rs = md.getTables(null, null, "%", null);
     SPARQLQueryDefinition query = sqmgr.newQueryDefinition("SELECT DISTINCT ?word  " +
			    "WHERE {?word <hasmeaning>  ?meaning}");
		InputStreamHandle handle = new InputStreamHandle();
		handle.setMimetype(SPARQLMimeTypes.SPARQL_XML);
		InputStreamHandle results = sqmgr.executeSelect(query, handle);
		String rs=results.toString();
		String str1[] = rs.split("<uri>");
		String d[];
		int count1=0;
		/*for(String s: str1){
      
     	d=s.split("</uri>");
     	d[0]=d[0].replaceAll("_"," ");
          if(count1==1)
         	 System.out.println(d[0]);
          else count1++;
		}*/
     String x;
     Stemmer st=new Stemmer();
     String k[]=new String[10];
     k=st.stripAffixes(table);
     for(int k1=0;k1<k.length;k1++)
         table=k[k1];
     //System.out.println("The different tables in the database are:\n");
     /*while (rs.next()) {
         x=rs.getString(3);
         tableset.add(x);
         //System.out.println(x);
     }*/
     //System.out.println("Contents in tableset are:"+tableset);
     //System.out.println("table="+table);
     if(tableset.contains(table))
     {
         System.out.println("The table "+table+" already exists\n");                    
     }
         else
         {
         	query = sqmgr.newQueryDefinition("SELECT DISTINCT ?meaning  " +
       			    "WHERE {?word <hasmeaning>  ?meaning}");
       		//InputStreamHandle handle = new InputStreamHandle();
       		handle.setMimetype(SPARQLMimeTypes.SPARQL_XML);
       	results = sqmgr.executeSelect(query, handle);
       		rs=results.toString();
       		str1 = rs.split("<uri>");
 		//	String d[];
 		count1=0;
 			int flag=0; 
 			for(String s: str1){
 	         
 	        	d=s.split("</uri>");
 	        	d[0]=d[0].replaceAll("_"," ");
 	             if(count1==1) {
 	            	 
 	            	value=d[0];
 			
             //System.out.println("In else\n\n");
             //ResultSet r = md.getTables(null, null, "%", null);
             
       /***************Checking whetther the word already retrieved from word net or not.We have to check table name and their entries also**********/                    
             
                 flag=0;
                 //x=r.getString(3);
                 //System.out.println("x="+x);
                // try{
                     //System.out.println("inside try box\n");
                   //  ResultSet rs1 = stmt.executeQuery("select * from "+x);
                   //  while(rs1.next()) {
                       //  String value = rs1.getString(1);
                         //System.out.println("value="+value);
                 
                         if(new String(value).equals(table) )
                         {
                             System.out.println("table "+table+" is already present in the database");
                             ct=1;flag=1;break;
                         }
                         else{ct=0;}
                         //System.out.println("when checking words\n");
                 }
                 else count1++;
                     
                     if(ct==1) break;
                 
                 //tableset.add(x);
                 //System.out.println(x);
             
             //System.out.println("out of while loop in mysqlconn.java");
             //String a="abcd";
             if(flag==0){
                 //System.out.println("table="+table+"hii");
                 if(table.contains(" "))
                     table=table.replaceAll(" ", "");
                 
                 //stmt.executeUpdate("CREATE TABLE IF NOT EXISTS "+table+"(data VARCHAR(255));");
                // System.out.println("Table "+table+" created successfully and it has "+l+" meanings");
                 ExtractMetaData emd=new ExtractMetaData();
                 emd.concept(table); 
             	client =DatabaseClientFactory.newClient("localhost",8060,"Documents",new DatabaseClientFactory.DigestAuthContext("admin","admin"));
             	String graphURI="meaningset";
                 for(String p:data1)/* data1 contains sysonyms retrieved from word net*/
                             {
                                     if ((p!= null && !p.equals("")) || p!=" ")
                                     {
                                    	 //p=p.replaceAll(" ", "_");
                                      //   System.out.println("meaning= "+p+" ");
                                         //stmt.executeUpdate("INSERT INTO "+table+" VALUES('"+data1[i1]+"');");
/* doubt*/
                                         if(p.contains(" "))
                                         {
                                        	// System.out.println("meaningsasiodufdzxgapppppppppppppppppppppppppppppppppppppppppp");
                                        	 p=p.replaceAll(" ", "_");
                                             //stmt.executeUpdate("insert into "+table+" values('"+p+"');");
                                       		String tripleStore = "<" + table + ">" + " " + " <hasmeaning>" + " " + "<" +p+ ">" + ".";
                     		        	    GraphManager graphManager = client.newGraphManager();
                     	    graphManager.setDefaultMimetype(RDFMimeTypes.NTRIPLES);
                     	    graphManager.merge(graphURI, new StringHandle(tripleStore));
                     	//  System.out.println(table);
                     	 // System.out.println(p);
                                         }
                                         else{
                                         String[] words=new String[10];
                                        // System.out.println("meaningsasiodufdzxgapppppppppppppppppppppppppppppppppppppppppp123");
                                         words=st.stripAffixes(p);/************stemming p and storing in words************/
                                         for(int j1=0;j1<words.length;j1++){
                                            // System.out.print(words[j1]+"_");
                                             p=p.replaceAll(" ", "_");
                                            // stmt.executeUpdate("INSERT INTO "+table+" VALUES('"+words[j1]+"');");
                                       		String tripleStore = "<" + table + ">" + " " + " <hasmeaning>" + " " + "<" + words[j1] + ">" + ".";
                     		        	    GraphManager graphManager = client.newGraphManager();
                     	   
                                               //  graphManager = client.newGraphManager();
                     	    graphManager.setDefaultMimetype(RDFMimeTypes.NTRIPLES);
                     	    graphManager.merge("meaningset", new StringHandle(tripleStore));
                     //	   System.out.println(table);
                     // 	  System.out.println(p);
                   //
                                         }}
                                     }
                             }//out1.writeBytes(words[i1]+"\n");
                             //stmt.executeUpdate("INSERT INTO "+table+" VALUES('"+data1[i1]+"');");
                             //System.out.println("not at the end");
                             //return table;
                 //System.out.println("The new table "+table+" is created and data is entered into it");
             }
             /*else
             {
                 System.out.println("table "+table+" already exists!!! try again\n");
             }*/
         }
     //con.close();  
     //System.out.println("end of mysqlconn.java");
 }
 //System.out.println("at the end");
//return table;  


}

}
