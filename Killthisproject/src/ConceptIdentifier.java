import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.io.InputStreamHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.semantics.GraphManager;
import com.marklogic.client.semantics.RDFMimeTypes;
import com.marklogic.client.semantics.SPARQLMimeTypes;
import com.marklogic.client.semantics.SPARQLQueryDefinition;
import com.marklogic.client.semantics.SPARQLQueryManager;


public class ConceptIdentifier extends TwoWord  {
    public void minsert(String word,Set<String> data1)
    {
    
            ConceptIdentifier ci=new ConceptIdentifier();
            DatabaseClient client =DatabaseClientFactory.newClient("localhost",8060,"Documents",new DatabaseClientFactory.DigestAuthContext("admin","admin"));
    		SPARQLQueryManager sqmgr = client.newSPARQLQueryManager();
            //int ct=0;
            SPARQLQueryDefinition query = sqmgr.newQueryDefinition("SELECT ?meaning  " +
    			    "WHERE {<"+word+"> <hasmeaning>  ?meaning}");//databse:mch_project->tables are titlestore and concept under words and meanings 
    		InputStreamHandle handle = new InputStreamHandle();
    		handle.setMimetype(SPARQLMimeTypes.SPARQL_XML);
    		InputStreamHandle results = sqmgr.executeSelect(query, handle);
    		String rs1=results.toString();
            for(String p:data1)
            {
                if(!(rs1.contains(p)))
                {
            		String graphURI = "meaningset";	
            		String tripleStore = "<" + word + ">" + " " + " <hasmeaning>" + " " + "<" + p + ">" + ".";
            		GraphManager graphManager = client.newGraphManager();
            	    graphManager.setDefaultMimetype(RDFMimeTypes.NTRIPLES);
            	    graphManager.merge(graphURI, new StringHandle(tripleStore));
                }
                //int l=data1.size();
                System.out.println("not at the end");   
                //con.close();  
            }
        
        System.out.println("at the end");  
    }
    public void title_concept(Map<String, Float> hash1,String title,String word_title) throws IOException {
              System.out.println("actual title is:"+word_title);
        Set<String> keywords=hash1.keySet();
        
        //int j;
        Map<String, Float> conceptfamilystore = new HashMap<>();
         Map<String, Float> preconceptfamilystore = new HashMap<>();
        // Stemmer stem =new Stemmer();
        conceptfamilystore.clear();
        preconceptfamilystore.clear();
        //int i=0,x1=1;
        Stemmer st1=new Stemmer();
      String[] words=word_title.split(" ");
      word_title="";
      String temp;
      for(int k=0;k<words.length;k++)
      {
          temp=words[k];
          if(!stopWordsSet.contains(temp)&& temp.isEmpty()==false){
             temp=st1.stripAffixes(temp)[0];
             word_title+=temp+" ";}
      }
       // StringTokenizer st;
        DatabaseClient client =DatabaseClientFactory.newClient("localhost",8060,"Documents",new DatabaseClientFactory.DigestAuthContext("admin","admin"));
 		//String wikipedia[100];
 		SPARQLQueryManager sqmgr = client.newSPARQLQueryManager();
 		SPARQLQueryDefinition query = sqmgr.newQueryDefinition("SELECT ?person " +
 			    "WHERE {?s <haslink>  ?person}"
 );//"SELECT * WHERE { ?s ?p ?o } LIMIT 10");//.withBinding("o", "http://example.org/object1");
 		InputStreamHandle handle = new InputStreamHandle();
 		handle.setMimetype(SPARQLMimeTypes.SPARQL_XML);
 		InputStreamHandle results = sqmgr.executeSelect(query, handle);
 		String strings=results.toString();
                String wikipedia[];
 String str[] = strings.split("<uri>");
 int count1=0;
         for(String s: str){
          wikipedia=s.split("</uri>");
        if(count1==1){
                    //if the string matches with one stop word(stored in names[] array), the we can remove that string from
                    //the list of tokens and break the loop because it is no use to traverse through the remaining stop word.
           wikipedia[0]=wikipedia[0].replaceAll("_", " ");        
        	if(word_title==wikipedia[0])
                    {
        		//System.out.println("preconceptfamilystore::::::::"+wikipedia[0]);
                        preconceptfamilystore.put(wikipedia[0],(float)1.0);
                    }
                    else if(hash1.containsKey(wikipedia[0]))
                    {
                      preconceptfamilystore.put(wikipedia[0],hash1.get(wikipedia[0]));
                      //break;
                    }
        }else count1++;
                }      
        // }
            //end the else part here
        String concept="";
        String concept1="";
        Float a=0.0f;
        Float b=0.0f;
        Map<String, Float> sortedMap = sortByValue(preconceptfamilystore);//sorted in ascending order
        System.out.print("the entries in the preconceptfamilystore are:   ");
        for(Map.Entry<String,Float> entry1:sortedMap.entrySet()) 
            System.out.print(entry1.getKey()+" : "+entry1.getValue()+"     ");
        int l=sortedMap.size();
        int p=0;
            for(Map.Entry<String,Float> entry:sortedMap.entrySet()){  
                 if((l-2)==p){
                    concept1=entry.getKey(); 
                    b=entry.getValue();
                }
                if((l-1)==p){
                    concept=entry.getKey();
                    a=entry.getValue();
                }
                p++;
            }
        String e;
        System.out.print("concept = "+concept+" concept1 = "+concept1);
      float d=((a-b)/a)*100; 
      System.out.println("  d="+d);
      System.out.println("stemmed title is:"+word_title+" concept="+concept+" concept1="+concept1);
      
      //System.out.println(word_title.contains(concept)+" why "+word_title.contains(concept1));
      client =DatabaseClientFactory.newClient("localhost",8060,"Documents",new DatabaseClientFactory.DigestAuthContext("admin","admin"));
      sqmgr = client.newSPARQLQueryManager();
     
      if(word_title.contains(concept) && concept!="")
      {
    	  System.out.println("kldjhghgskjl"+concept);
    	  System.out.println("the concept of title decided directly");
          //  float value;
            for(String s1:keywords) {
                e=concept+"^"+s1;
                    //System.out.println("The concept of "+title+"is = "+concept);
                int ct=0;
               // System.out.println(concept);
                if(concept!=""){
                	//System.out.println(concept);
            		  try{
                          //System.out.println("kjhgrtytuy"+concept1);
                                concept=concept.replaceAll("[:,.-]", "_");
                                if(concept.contains(" ")||concept.contains(":"))
                                    concept=concept.replaceAll(" ", "_");
                               
                        		query = sqmgr.newQueryDefinition("SELECT  ?word " +
                        			    "WHERE {?concept <haswordindocumentdata2>  ?word} "
                        );//"SELECT * WHERE { ?s ?p ?o } LIMIT 10");//.withBinding("o", "http://example.org/object1");
                        		//InputStreamHandle handle = new InputStreamHandle();
                        		handle.setMimetype(SPARQLMimeTypes.SPARQL_XML);
                        		results = sqmgr.executeSelect(query, handle);
                        		String r=results.toString();
                        		s1=s1.replaceAll(" ", "_");
                        		//System.out.println(s1);
                        		if(!(r.contains(s1))) {
                        			System.out.println("ewlirkusljk"+s1);
                        		String graphURI = "concept_word_weight";
                        		System.out.println("concept is:::9"+concept);
                        		String tripleStore = "<" + concept + ">" + " " + " <haswordindocumentdata2>" + " " + "<" + s1 + ">" + ".";
                        		        	    GraphManager graphManager = client.newGraphManager();
                        		
                        	    graphManager.setDefaultMimetype(RDFMimeTypes.NTRIPLES);
                        	    graphManager.merge(graphURI, new StringHandle(tripleStore));
                        	    float weight=hash1.get(s1);
                        	    tripleStore = "<" + concept + ">" + " " + " <hasweightindocumentdata2>" + " " + "<" + weight + ">" + ".";
        		        	    graphManager = client.newGraphManager();
        		
        	    graphManager.setDefaultMimetype(RDFMimeTypes.NTRIPLES);
        	    graphManager.merge(graphURI, new StringHandle(tripleStore));
                        		
                                //System.out.println("The table is created for the concept"+concept);
                             /*  stmt.executeUpdate("CREATE TABLE IF NOT EXISTS "+concept+"(term VARCHAR(255), weight VARCHAR(255));");
                                 ResultSet rs2 = stmt.executeQuery("select * from "+concept);
                                while(rs2.next()) {
                                    String value1 = rs2.getString(1);
                                    String value2 = rs2.getString(2);
                                    //System.out.println("value="+value);
                                    if(value1.equals(s1))
                                    {
                                        ct=1;break;
                                    }
                                }
                                if(ct==0)
                                {
                                    //System.out.println("near concept insert concept_store");
                                    stmt.executeUpdate("INSERT INTO "+concept
                                            +" VALUES('"+s1+"','"+hash1.get(s1)+"');");
                                }*/
                            
                           // con.close();
                   
                    ct=0;
                //insert this number title into the database mch_project under the table name: "title_store"
                    	//SPARQLQueryManager sqmgr = client.newSPARQLQueryManager();

                            
                                System.out.println("kjhgrtytuy1"+concept);
                                concept=concept.replaceAll("[:,.-]", "_");
                                if(concept.contains(" "))
                                    concept=concept.replaceAll(" ", "_");
                                
                                //SPARQLQueryManager sqmgr = client.newSPARQLQueryManager();
                        		//InputStreamHandle handle = new InputStreamHandle();
                        		handle.setMimetype(SPARQLMimeTypes.SPARQL_XML);
                        		results = sqmgr.executeSelect(query, handle);
                        		//String r=results.toString();
                        		//s1=s1.replaceAll(" ", "_");
                        	
                        		//System.out.println(s1);
                        			System.out.println("concept is:::1"+concept);
                                graphURI = "concept_title";
                                System.out.println(".d.kljhghjkl;kjhgf"+concept);
                        		tripleStore = "<" + concept + ">" + " " + " <hastitle>" + " " + "<" + title + ">" + ".";
                        		  //      	    GraphManager graphManager = client.newGraphManager();
                        	    graphManager.setDefaultMimetype(RDFMimeTypes.NTRIPLES);
                        	    graphManager.merge(graphURI, new StringHandle(tripleStore));
                        	    
                            }
                            }
                            catch(Exception e2)
                            {
                                System.out.println("error:"+e2);
                            }
                            //con.close();
                 
                //System.out.println(e);
                /*if(conceptfamilystore.containsKey(e)){
                    value=conceptfamilystore.get(e);
                    
                }else*/
             conceptfamilystore.put(e,hash1.get(s1));
            }}
            String[] str_a=new String[10];
            int s=0;
            Mysqlconn m=new Mysqlconn();
            int p1=m.term_concept.size();
            if(p1>0){
            System.out.println("The number of terms that are not having the meaning are:"+p1+"\n and its entries are as follows:");
            for(String st11:m.term_concept)
            {
                System.out.println("st11==="+st11);
                if(st11 != null || !st11.equals("")){
                str_a[s]=st1.stripAffixes(st11)[0];
                System.out.println(str_a[s]);}
                else{str_a[s]=" ";}
                s++;
            }
            if(concept!=""){
                System.out.println("It belongs to the concept of "+concept+"length="+str_a.length+"s="+s);
                minsert(concept, term_concept);
            }}
      //} 
      else if(concept1!="" && word_title.contains(concept1))
      {
          System.out.println("the concept1 of title decided directly");
                     for(String s1:keywords) {
                e=concept+"^"+s1;
                //int ct=0,i2=0;
                    //System.out.println("The concept of "+title+"is = "+concept1+" and ");
                //System.out.println("concept="+concept);
                if(concept1!=""){
                    
                    	    //here title_store is database name, root is username and xampp has no password by default  
                           
                            try{
                                //concept=concept1.replaceAll("[:,.-]", "_");
                                if(concept1.contains(" "))
                                    concept1=concept1.replaceAll(" ", "_");
                                System.out.println("kjhgrtytuy"+concept1);
                                //SPARQLQueryManager sqmgr = client.newSPARQLQueryManager();
                                query = sqmgr.newQueryDefinition("SELECT  ?word " +
                        			    "WHERE {?concept <haswordindocumentdata2>  ?word} "
                        );//"SELECT * WHERE { ?s ?p ?o } LIMIT 10");//.withBinding("o", "http://example.org/object1");
                        		//InputStreamHandle handle = new InputStreamHandle();
                        		handle.setMimetype(SPARQLMimeTypes.SPARQL_XML);
                                    results = sqmgr.executeSelect(query, handle);
                                   String r=results.toString();
                                   s1=s1.replaceAll(" ","_");
                        		if(!(r.contains(s1))) {
                        		String graphURI = "concept_word_weight";
                        		System.out.println("concept is:::2"+concept1);
                        		String tripleStore = "<" + concept1 + ">" + " " + " <haswordindocumentdata2>" + " " + "<" + s1 + ">" + ".";
                        		        	    GraphManager graphManager = client.newGraphManager();
                        		
                        	    graphManager.setDefaultMimetype(RDFMimeTypes.NTRIPLES);
                        	    graphManager.merge(graphURI, new StringHandle(tripleStore));
                        	    float weight=hash1.get(s1);
                        	    tripleStore = "<" + concept1 + ">" + " " + " <hasweightindocumentdata2>" + " " + "<" + weight + ">" + ".";
        		        	    graphManager = client.newGraphManager();
        		
        	    graphManager.setDefaultMimetype(RDFMimeTypes.NTRIPLES);
        	    graphManager.merge(graphURI, new StringHandle(tripleStore));
                        	
                            //con.close();
                   
                    ct=0;
                //insert this number title into the database mch_project under the table name: "title_store"
                       
                    	client =DatabaseClientFactory.newClient("localhost",8060,"Documents",new DatabaseClientFactory.DigestAuthContext("admin","admin"));
                    	sqmgr = client.newSPARQLQueryManager();
                    
                                concept=concept.replaceAll("[:,.-]", "_");
                                if(concept1.contains(" "))
                                    concept1=concept1.replaceAll(" ", "_");
                                
                                //SPARQLQueryManager sqmgr = client.newSPARQLQueryManager();
                        	query = sqmgr.newQueryDefinition("SELECT  ?word " +
                        			    "WHERE {?concept <haswordindocumentdata2>  ?word} "
                        );//"SELECT * WHERE { ?s ?p ?o } LIMIT 10");//.withBinding("o", "http://example.org/object1");
                        		handle = new InputStreamHandle();
                        		handle.setMimetype(SPARQLMimeTypes.SPARQL_XML);
                        		results = sqmgr.executeSelect(query, handle);
                        		//String r=results.toString();
                        		//s1=s1.replaceAll(" ","_");
                        		
                        		//graphURI = "concept_word_weight";
                        	    
                        		tripleStore = "<" + concept1 + ">" + " " + " <haswordindocumentdata2>" + " " + "<" + s1 + ">" + ".";
                        		//        	    GraphManager graphManager = client.newGraphManager();
                        		
                        	    graphManager.setDefaultMimetype(RDFMimeTypes.NTRIPLES);
                        	    graphManager.merge(graphURI, new StringHandle(tripleStore));
                        	    weight=hash1.get(s1);
                        	    System.out.println("concept is:::3"+concept1);
                        	    tripleStore = "<" + concept1 + ">" + " " + " <hasweightindocumentdata2>" + " " + "<" + weight + ">" + ".";
        		        	    graphManager = client.newGraphManager();
        		
        	    graphManager.setDefaultMimetype(RDFMimeTypes.NTRIPLES);
        	    graphManager.merge(graphURI, new StringHandle(tripleStore));
                        		}
                               }
                            catch(Exception e2)
                            {
                                System.out.println("error:"+e2);
                            }
                            //con.close();
                         
                //System.out.println(e);
             conceptfamilystore.put(e,b);
            }}
                    str_a=new String[10];
            s=0;
           m=new Mysqlconn();
           p1=m.term_concept.size();
            if(p1>0){
            System.out.println("The number of terms that are not having the meaning are:"+p1+"\n and its entries are as follows:");
            for(String st11:m.term_concept)
            {
                System.out.println("st11==="+st11);
                if(st11 != null || !st11.equals("")){
                str_a[s]=st1.stripAffixes(st11)[0];
                System.out.println(str_a[s]);}
                else{str_a[s]=" ";}
                s++;
            }
            if(concept!=""){
                System.out.println("It belongs to the concept of "+concept+"length="+str_a.length+"s="+s);
                minsert(concept, term_concept);
            }}
      }
            
      //else{
          if(d<20.0){
          System.out.println("the concept1 of title decided bcz df<20%");
            for(String s1:keywords) {
                e=concept+"^"+s1;
              //  int ct=0,i2=0;
                    //System.out.println("The concept of "+title+"is = "+concept1+" and ");
                //System.out.println("concept="+concept);
                if(concept1!=""){
                    
                    	client =DatabaseClientFactory.newClient("localhost",8060,"Documents",new DatabaseClientFactory.DigestAuthContext("admin","admin"));
                    	sqmgr = client.newSPARQLQueryManager();
                            //here title_store is database name, root is username and xampp has no password by default  
                           
                            try{
                                //concept=concept1.replaceAll("[:,.-]", "_");
                            	// SPARQLQueryManager sqmgr = client.newSPARQLQueryManager();
                         		query = sqmgr.newQueryDefinition("SELECT  ?word " +
                         			    "WHERE {?concept <haswordindocumentdata2>  ?word} "
                         );//"SELECT * WHERE { ?s ?p ?o } LIMIT 10");//.withBinding("o", "http://example.org/object1");
                         		handle = new InputStreamHandle();
                         		handle.setMimetype(SPARQLMimeTypes.SPARQL_XML);
                         	     results = sqmgr.executeSelect(query, handle);
                         	     String r=results.toString();
                         	    s1=s1.replaceAll(" ","_");
                         		if(!(r.contains(s1))) {
                         		String graphURI = "concept_word_weight";
                         		System.out.println("concept is:::4"+concept1);
                         		String tripleStore = "<" + concept1 + ">" + " " + " <haswordindocumentdata2>" + " " + "<" + s1 + ">" + ".";
                         		        	    GraphManager graphManager = client.newGraphManager();
                         		
                         	    graphManager.setDefaultMimetype(RDFMimeTypes.NTRIPLES);
                         	    graphManager.merge(graphURI, new StringHandle(tripleStore));
                         	    float weight=hash1.get(s1);
                         	   tripleStore = "<" + concept1 + ">" + " " + " <hasweightindocumentdata2>" + " " + "<" + weight + ">" + ".";
         		        	   graphManager = client.newGraphManager();
         		
         	    graphManager.setDefaultMimetype(RDFMimeTypes.NTRIPLES);
         	    graphManager.merge(graphURI, new StringHandle(tripleStore));
                 
                           // con.close();
                  
                    ct=0;
                //insert this number title into the database mch_project under the table name: "title_store"
                    
                        
                                concept=concept.replaceAll("[:,.-]", "_");
                                if(concept1.contains(" "))
                                    concept1=concept1.replaceAll(" ", "_");
                              //  sqmgr = client.newSPARQLQueryManager();
                        		query = sqmgr.newQueryDefinition("SELECT  ?word " +
                        			    "WHERE {?concept <haswordindocumentdata2>  ?word} "
                        );//"SELECT * WHERE { ?s ?p ?o } LIMIT 10");//.withBinding("o", "http://example.org/object1");
                        		handle = new InputStreamHandle();
                        		handle.setMimetype(SPARQLMimeTypes.SPARQL_XML);
                        		results = sqmgr.executeSelect(query, handle);
                        		//String r=results.toString();
                        		s1=s1.replaceAll(" ","_");
                        		
                                   graphURI = "concept_word_weight";
                                System.out.println("kjhgrtytuy5"+concept1);
                        		tripleStore = "<" + concept1 + ">" + " " + " <haswordindocumentdata2>" + " " + "<" + s1 + ">" + ".";
                        		//        	    GraphManager graphManager = client.newGraphManager();
                        		
                        	    graphManager.setDefaultMimetype(RDFMimeTypes.NTRIPLES);
                        	    graphManager.merge(graphURI, new StringHandle(tripleStore));
                        	    weight=hash1.get(s1);
                        	    System.out.println("concept is:::5"+concept1);
                        	    tripleStore = "<" + concept1 + ">" + " " + " <hasweightindocumentdata2>" + " " + "<" + weight + ">" + ".";
        		        	    graphManager = client.newGraphManager();
        		
        	    graphManager.setDefaultMimetype(RDFMimeTypes.NTRIPLES);
        	    graphManager.merge(graphURI, new StringHandle(tripleStore));
                        		}
                
                            }
                            catch(Exception e2)
                            {
                                System.out.println("error:"+e2);
                            }
                            
                    }
                    
                //System.out.println(e);
             conceptfamilystore.put(e,b);
            }}
          str_a=new String[10];
            s=0;
            m=new Mysqlconn();
           p1=m.term_concept.size();
            if(p1>0){
            System.out.println("The number of terms that are not having the meaning are:"+p1+"\n and its entries are as follows:");
             for(String st11:m.term_concept)
            {
                System.out.println("st11==="+st11);
                if(st11 != null || !st11.equals("")){
                str_a[s]=st1.stripAffixes(st11)[0];
                System.out.println(str_a[s]);}
                else{str_a[s]=" ";}
                s++;
            }
            if(concept!=""){
                System.out.println("It belongs to the concept of "+concept+"length="+str_a.length+"s="+s);
                minsert(concept, term_concept);
            }}        }
                 float value;
            for(String s1:keywords) {
                e=concept+"^"+s1;
                int ct=0;
                if(concept!=""){
                            //Statement stmt=con.createStatement();
                            try{
                                concept=concept.replaceAll("[:,.-]", "_");
                                if(concept.contains(" ")||concept.contains(":"))
                                    concept=concept.replaceAll(" ", "_");
                                sqmgr = client.newSPARQLQueryManager();
                        		query = sqmgr.newQueryDefinition("SELECT  ?word " +
                        			    "WHERE {?concept <haswordindocumentdata2>  ?word} "
                        );//"SELECT * WHERE { ?s ?p ?o } LIMIT 10");//.withBinding("o", "http://example.org/object1");
                        	handle = new InputStreamHandle();
                        		handle.setMimetype(SPARQLMimeTypes.SPARQL_XML);
                               results = sqmgr.executeSelect(query, handle);
                               String r=results.toString();
                               s1=s1.replaceAll(" ","_");
                        		if(!(r.contains(s1))) {
                        		String graphURI = "concept_word_weight";
                        		System.out.println("concept is:::6"+concept);
                        		String tripleStore = "<" + concept + ">" + " " + " <haswordindocumentdata2>" + " " + "<" + s1 + ">" + ".";
                        		        	    GraphManager graphManager = client.newGraphManager();
                        		
                        	    graphManager.setDefaultMimetype(RDFMimeTypes.NTRIPLES);
                        	    graphManager.merge(graphURI, new StringHandle(tripleStore));
                        	    float weight=hash1.get(s1);
                        	    tripleStore = "<" + concept + ">" + " " + " <hasweightindocumentdata2>" + " " + "<" + weight + ">" + ".";
        		        	    graphManager = client.newGraphManager();
        		
        	    graphManager.setDefaultMimetype(RDFMimeTypes.NTRIPLES);
        	    graphManager.merge(graphURI, new StringHandle(tripleStore));
                        		}
                            }
                            catch(Exception e2)
                            {
                                System.out.println("error:"+e2);
                            }
                           // con.close();
                    }
                    
                    ct=0;

                            try{
                                concept=concept.replaceAll("[:,.-]", "_");
                                if(concept.contains(" "))
                                    concept=concept.replaceAll(" ", "_");
                              
                                sqmgr = client.newSPARQLQueryManager();
                        		query = sqmgr.newQueryDefinition("SELECT  ?word " +
                        			    "WHERE {?concept <haswordindocumentdata2>  ?word} "
                        );//"SELECT * WHERE { ?s ?p ?o } LIMIT 10");//.withBinding("o", "http://example.org/object1");
                        		handle = new InputStreamHandle();
                        		handle.setMimetype(SPARQLMimeTypes.SPARQL_XML);
                                results = sqmgr.executeSelect(query, handle);
                        		String r=results.toString();
                        		s1=s1.replaceAll(" ","_");
                        		if(!(r.contains(s1))) {
                        		String graphURI = "concept_word_weight";
                        		System.out.println("concept is:::7"+concept);
                        		String tripleStore = "<" + concept + ">" + " " + " <haswordindocumentdata2>" + " " + "<" + s1 + ">" + ".";
                        		        	    GraphManager graphManager = client.newGraphManager();
                        		
                        	    graphManager.setDefaultMimetype(RDFMimeTypes.NTRIPLES);
                        	    graphManager.merge(graphURI, new StringHandle(tripleStore));
                        	    float weight=hash1.get(s1);
                        	    tripleStore = "<" + concept + ">" + " " + " <hasweightindocumentdata2>" + " " + "<" + weight + ">" + ".";
        		        	   
        		
        	    graphManager.setDefaultMimetype(RDFMimeTypes.NTRIPLES);
        	    graphManager.merge(graphURI, new StringHandle(tripleStore));
                        		}
                            }
                            catch(Exception e2)
                            {
                                System.out.println("error:"+e2);
                            }
                            //con.close();
                   
                             conceptfamilystore.put(e,hash1.get(s1));
            }
            String[] str_a=new String[10];
             int s1=0;
            Mysqlconn m=new Mysqlconn();
            int p1=m.term_concept.size();
            if(p1>0){
            System.out.println("The number of terms that are not having the meaning are:"+p1+"\n and its entries are as follows:");
             for(String st11:m.term_concept)
            {
                System.out.println("st11==="+st11);
                if(st11 != null || !st11.equals("")){
                str_a[s1]=st1.stripAffixes(st11)[0];
                System.out.println(str_a[s1]);}
                else{str_a[s1]=" ";}
                s1++;
            }
            if(concept!=""){
                System.out.println("It belongs to the concept of "+concept+"length="+str_a.length+"s="+s);
                minsert(concept, term_concept);
            }}
     
      
      
    	  query = sqmgr.newQueryDefinition("SELECT DISTINCT ?word  " +
  			    "WHERE {?word <hasmeaning>  ?meaning}");
  		//InputStreamHandle handle = new InputStreamHandle();
  		handle.setMimetype(SPARQLMimeTypes.SPARQL_XML);
  		results = sqmgr.executeSelect(query, handle);
  		String rs=results.toString();
  		
  		query = sqmgr.newQueryDefinition("SELECT ?meaning  " +
			    "WHERE {?word <hasmeaning>  ?meaning}");
		handle = new InputStreamHandle();
		handle.setMimetype(SPARQLMimeTypes.SPARQL_XML);
	      results = sqmgr.executeSelect(query, handle);
		String rs1=results.toString();
            String x[];
            String data[];
            System.out.println("The different tables in the database are:\n");            
            str= rs.split("<uri>");
            int count9=0;
   		 for(String s: str){
	        	x=s.split("</uri>");
	        	if(count9==1) {
	        	x[0]=x[0].replaceAll("_"," ");
                if(!ExtractMetaData.title_num.containsKey(x[0]))
                {
                    System.out.println("x="+x[0]);
                    str = rs1.split("<uri>");
           		 for(String s2: str){
        	        	data=s2.split("</uri>");
        	        	data[0]=data[0].replaceAll("_"," ");
                        Stemmer st5=new Stemmer();

                        data[0]=st5.stripAffixes(data[0])[0];
                        if(ExtractMetaData.title_num.containsKey(data[0]))
                        {
                            int n=ExtractMetaData.title_num.remove(data[0]);
                                ExtractMetaData.title_num.put(x[0],n); 
                                //con.close();
                            break;
                        }
                    }
                }
            }
	        	if(count==0) {
	        		count++;
	        	}
	        	}
      
}
    private static Map<String, Float> sortByValue(Map<String, Float> unsortMap) {
        List<Map.Entry<String, Float>> list =
                new LinkedList<Map.Entry<String, Float>>(unsortMap.entrySet());

        Collections.sort(list, new Comparator<Map.Entry<String, Float>>() {
            public int compare(Map.Entry<String, Float> o1,
                               Map.Entry<String, Float> o2) {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });

        // 3. Loop the sorted list and put it into a new insertion order Map LinkedHashMap
        Map<String, Float> sortedMap = new LinkedHashMap<String,Float>();
        for (Map.Entry<String, Float> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }

    /*public void conceptquery1(String query) throws ClassNotFoundException{ 
        String concept="";
        int k=0;
        String[] arr1=new String[100];
        String[] a=new String[50];
        String p1;
        StringTokenizer at=new StringTokenizer(query," ");
        while(at.hasMoreTokens()){
            Stemmer st=new Stemmer();
            arr1=st.stripAffixes(at.nextToken());
                   try
                   {
                      //If the stemmed words contains more than one word, then iterate through the arr1 array
                      for(int i1=0;i1<arr1.length;i1++)
                      {
                          p1=arr1[i1].replaceAll("[\\d()]","");
                          //if the word already exists in the hash table, then increment the count of the word
                          a[k++]=p1;
                          System.out.println(a[k-1]);
                      }
                   }
                   catch(NullPointerException e1)
                   {
                      //System.out.println("The exception raised is:"+e1);
                   }
        }
        //Set<String> keywords=hash1.keySet();
        Map<String,Float> Perfectmatch=new HashMap<>();
        Map<String,Float> Partialmatch=new HashMap<>();
        int count=0;
        float Average;
        float sum=0;
        int ct=0;
        Map<String,Float> concept_store=new HashMap<>();
        Perfectmatch.clear();
        Partialmatch.clear();
        try{
        	DatabaseClient client =DatabaseClientFactory.newClient("localhost",8060,"Documents",new DatabaseClientFactory.DigestAuthContext("admin","admin"));
        	SPARQLQueryManager sqmgr = client.newSPARQLQueryManager();
               concept_store.clear();
      //         Statement stmt=con.createStatement();
        //       DatabaseMetaData md = con.getMetaData();
          //     ResultSet r = md.getTables(null, null, "%", null);
               String x;
               //System.out.println("The different tables in the database are:\n");
               while (r.next()) {
                   x=r.getString(3);
                       count=0;                    
                       concept_store.clear();
                           //System.out.println("x="+x);
                           try{
                               //System.out.println("inside try box\n");
                               ResultSet rs1 = stmt.executeQuery("select * from "+x);
                               while(rs1.next()) {
                                   String value1 = rs1.getString(1);
                                   Float value2 = Float.valueOf(rs1.getString(2));
                                   //System.out.println("term = "+value1+"  value="+value2);
                                   concept_store.put(value1, value2);
                               }
                               Set<String> keys=concept_store.keySet();
                                for( int i = 0; i < k; i++)
                               {
                                   if(keys.contains(a[i]))
                                   {
                                       System.out.println("key="+a[i]);
                                       count++;
                                       sum+=concept_store.get(a[i]);
                                   }
                                }
                               Average=sum/count;
                               if(count==k){
                                        Perfectmatch.put(x,Average);
                               }
                               else if(count==(k-1)&& k>1){     
                                        Partialmatch.put(x,Average);
                           }
                           }
                           catch(Exception e2)
                           {
                               System.out.println("error:"+e2);
                           }
                           if(Perfectmatch.size()==0&&Partialmatch.size()==0)
                        	   conceptquery(query);
                           concept_store.clear();
                       }
               con.close();
               }*/
public void conceptquery(String query1) throws ClassNotFoundException{ 
     String concept="";
     int k=0;
     
     String[] arr1=new String[100];
     String[] a=new String[50];
     String p1;
     StringTokenizer at=new StringTokenizer(query1," ");
     while(at.hasMoreTokens()){
         Stemmer st=new Stemmer();
         arr1=st.stripAffixes(at.nextToken());
                try
                {
                   //If the stemmed words contains more than one word, then iterate through the arr1 array
                   for(int i1=0;i1<arr1.length;i1++)
                   {
                       p1=arr1[i1].replaceAll("[\\d()]","");
                       //if the word already exists in the hash table, then increment the count of the word
                       a[k++]=p1;
                       System.out.println(a[k-1]);
                   }
                }
                catch(NullPointerException e1)
                {
                   //System.out.println("The exception raised is:"+e1);
                }
     }
     //Set<String> keywords=hash1.keySet();
     Map<String,Float> Perfectmatch=new HashMap<>();
     Map<String,Float> Partialmatch=new HashMap<>();
     int count=0;
     float Average;
     //float sum=0;
    // int ct=0;
     Map<String,Float> concept_store=new HashMap<>();
     Perfectmatch.clear();
     Partialmatch.clear();
     
     
     	DatabaseClient client =DatabaseClientFactory.newClient("localhost",8060,"Documents",new DatabaseClientFactory.DigestAuthContext("admin","admin"));
     	SPARQLQueryManager sqmgr = client.newSPARQLQueryManager();
     	try {
     		System.out.println("concept is:::8"+concept);
       SPARQLQueryDefinition query = sqmgr.newQueryDefinition("SELECT DISTINCT ?concept  " +
  			    "WHERE {?concept <haswordindocumentdata2>  ?word}");
     	InputStreamHandle handle = new InputStreamHandle();
		handle.setMimetype(SPARQLMimeTypes.SPARQL_XML);
		InputStreamHandle results = sqmgr.executeSelect(query, handle);
		String r=results.toString();
		System.out.println(r);
//            Class.forName("com.mysql.jdbc.Driver");  
  //          Connection con=DriverManager.getConnection("jdbc:mysql://localhost:3306/concept_family_store","root","");  
            //here concept_family_store is database name, root is username and password  
            //Statement stmt=con.createStatement();  
            //try{
            //System.out.println("T: "+table+"       D:"+data1);
            concept_store.clear();
   //         Statement stmt=con.createStatement();
     //       DatabaseMetaData md = con.getMetaData();
       //     ResultSet r = md.getTables(null, null, "%", null);
            String x[];
            //System.out.println("The different tables in the database are:\n");
          int count1=0;
    		String str1[] = r.split("<uri>");
    		 for(String s: str1){
 	        	x=s.split("</uri>");
 	        	x[0]=x[0].replaceAll("_"," ");
 	        	System.out.println(x[0]);
    		if(count1==1){
                    count=0;                    
                    concept_store.clear();
                        //System.out.println("x="+x);
                    query = sqmgr.newQueryDefinition("SELECT DISTINCT ?weight  " +
              			    "WHERE {?concept <hasweightindocumentdata2>  ?weight}");
                    
            		handle.setMimetype(SPARQLMimeTypes.SPARQL_XML);
            	     results = sqmgr.executeSelect(query, handle);
            		String rs1=results.toString();
            		str1= rs1.split("<uri>");
                        String[] rs2;
                        int count2=0;
              		 for(String s2: str1){
           	        	rs2=s2.split("</uri>");
           	        	rs2[0]=rs2[0].replaceAll("_"," ");
                            //System.out.println("inside try box\n");
                            //ResultSet rs1 = stmt.executeQuery("select * from "+x[0]);
                          if(count2==1){
                                String value1 = x[0];
                                Float value2 = Float.parseFloat(rs2[0]);
                                //System.out.println("term = "+value1+"  value="+value2);
                                concept_store.put(value1, value2);
                            }
                          else count2++;
                         }
                            Set<String> keys=concept_store.keySet();
                             for( int i = 0; i < k; i++)
                            {
                                if(keys.contains(a[i]))
                                {
                                    System.out.println("key="+a[i]);
                                    count++;
                                    sum+=concept_store.get(a[i]);
                                }
//                                
                             }
                             Average=sum/count;
                            if(count==k){
                                     Perfectmatch.put(x[0],Average);
                            }
                            else if(count==(k-1)&& k>1){     
                                     Partialmatch.put(x[0],Average);
                        }
                        
                        concept_store.clear();
                    }
                else count1++;
                 }
            //con.close();
            }
            
     catch(Exception ex2)
     {
         
     } 
    Perfectmatch=sortByValue(Perfectmatch);        
    Partialmatch=sortByValue(Partialmatch);
    Set<String> keys_perfect=Perfectmatch.keySet();
    System.out.println("The concepts that match the query exactly are:");
    for(String p:keys_perfect)
        System.out.println(p);
    Set<String> keys_partial1=Partialmatch.keySet();
    System.out.println("The concepts that match the query partially are:");
    for(String p:keys_partial1)
        System.out.println(p);
    System.out.println("\n\n");
	client =DatabaseClientFactory.newClient("localhost",8060,"Documents",new DatabaseClientFactory.DigestAuthContext("admin","admin"));
    sqmgr = client.newSPARQLQueryManager();
    InputStreamHandle handle = new InputStreamHandle();
    InputStreamHandle results;
    for(String p:keys_perfect)
    {
        System.out.println(p);
          
         
  try{
            	SPARQLQueryDefinition query = sqmgr.newQueryDefinition("SELECT ?title  " +
        			    "WHERE {<"+p+"> <hastitle>  ?title}");
        		
        		handle.setMimetype(SPARQLMimeTypes.SPARQL_XML);
        		results = sqmgr.executeSelect(query, handle);
        		String rs=results.toString();
        		String str1[] = rs.split("<uri>");
    			String d[];
    			int count1=0;
    			for(String s: str1){
    	         
    	        	d=s.split("</uri>");
    	        	d[0]=d[0].replaceAll("_"," ");
    	             if(count1==1)
    	            	 System.out.println(d[0]);
    	             else count1++;
    			}
            }
            catch(Exception e4)
            {
                System.out.println("Error: "+e4+"  Exiting");
            }
   
        
    }
    
    Set<String> keys_partial=Partialmatch.keySet();
    for(String p:keys_partial)
    {
        System.out.println(p);
          
         	client =DatabaseClientFactory.newClient("localhost",8060,"Documents",new DatabaseClientFactory.DigestAuthContext("admin","admin"));
         	sqmgr = client.newSPARQLQueryManager();
  
            try{
          	  SPARQLQueryDefinition query = sqmgr.newQueryDefinition("SELECT ?title  " +
        			    "WHERE {<"+p+"> <hastitle>  ?title}");
        		handle = new InputStreamHandle();
        		handle.setMimetype(SPARQLMimeTypes.SPARQL_XML);
        		results = sqmgr.executeSelect(query, handle);
        		String rs=results.toString();
        		String str1[] = rs.split("<uri>");
    			String d[];
    			int count1=0;
    			for(String s: str1){
    	         
    	        	d=s.split("</uri>");
    	        	d[0]=d[0].replaceAll("_"," ");
    	             if(count1==1)
    	            	 System.out.println(d[0]);
    	             else count1++;
    			}
                 
        		str1 = rs.split("<uri>");
    			
    			count1=0;
    			for(String s: str1){
    	         
    	        	d=s.split("</uri>");
    	        	d[0]=d[0].replaceAll("_"," ");
    	             if(count1==1)
    	            	 System.out.println(d[0]);
    	             else count1++;
    			}
                
            }
            catch(Exception e4)
            {
                System.out.println("Error: "+e4+"  Exiting");
            }
          
        }
    
     
}

}
