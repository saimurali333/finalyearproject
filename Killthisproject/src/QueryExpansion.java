import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.io.InputStreamHandle;
import com.marklogic.client.semantics.SPARQLMimeTypes;
import com.marklogic.client.semantics.SPARQLQueryDefinition;
import com.marklogic.client.semantics.SPARQLQueryManager;


public class QueryExpansion {
    //This class implements methods for ranking the titles that matches with the user query
    //It can be either exact match or partial match
    /**
     * @param args the command line arguments
     */
    int q_arr[]=new int[10];
    int t_arr[]=new int[10];
    public int Ranking(String query1) throws ClassNotFoundException
    {
        String title="";
        int t=0,q=0,count=0;
        int qmp,tmp,df;
        Map<String, List<String>> match = new HashMap<String, List<String>>();
        List<String> exact_match=new ArrayList<>();
        List<String> partial_match1=new ArrayList<>();
        List<String> partial_match2=new ArrayList<>();
        List<String> partial_match3=new ArrayList<>();
        List<String> partial_match4=new ArrayList<>();        
        Set<Integer> q_ar=new TreeSet<>();//A treeset that stores the numbers of the query(retrieved from title_store)
        Set<Integer> t_ar=new TreeSet<>();//A treeset that stores the numbers of the title(retrieved from title_store)
        /*The title_store contains the titles as numbers eg: 1^2^3^.....
          =>Also the user query is converted into the similar format 
            depensing on the numbers assigned to the terms in the term number store
          =>So retrieve each term, we have to tokenize them and store the term number in the respective treeset
            we have chosen treeset because the elements in treeset will be in sorted order
        */
        StringTokenizer qt=new StringTokenizer(query1,"/");
        //inserting query terms (that are assigned numbers) in a tree set
        while(qt.hasMoreTokens()){
            Integer k=Integer.parseInt(qt.nextToken());
            q_arr[q]=(k);
            q_ar.add(k);
            //System.out.println(q_arr[q]+" ");
            q++;
        }
                int cm=1;
        
            
        	DatabaseClient client =DatabaseClientFactory.newClient("localhost",8060,"Documents",new DatabaseClientFactory.DigestAuthContext("admin","admin"));
    		SPARQLQueryManager sqmgr = client.newSPARQLQueryManager();
    		
    		
            try{
                //title_store is the table which contains the titles (in number format) of various papers in our db
            	SPARQLQueryDefinition query = sqmgr.newQueryDefinition("SELECT ?title  " +
        			    "WHERE {?sdh <hasatitle>  ?title}");
        		InputStreamHandle handle = new InputStreamHandle();
        		handle.setMimetype(SPARQLMimeTypes.SPARQL_XML);
        		InputStreamHandle results = sqmgr.executeSelect(query, handle);
        		String rs1=results.toString();
        		String str[] = rs1.split("<uri>");
        		int count1=0;
        		String d[];
        		 for(String s: str){
        			 d=s.split("</uri>");
        			 title=d[0];
        			 if(count1==1) {
                 //   System.out.println("in loop");
                    t=0;count=0;    
                    if(title.contains("/")){
                    StringTokenizer tt=new StringTokenizer(title,"/");
                    t_ar.clear();
                    //inserting title terms (that are assigned numbers) in a tree set
                    while(tt.hasMoreTokens()){
                        String k1=tt.nextToken();
                        t_ar.add(Integer.parseInt(k1));
                        t_arr[t++]=Integer.parseInt(k1);
                    }
                    for(Object k:q_ar)
                    {
                        if(t_ar.contains(k))
                            count++;
                    }

                  System.out.println(query+" "+title);
                   System.out.print("q="+q+" t="+t+" count="+count);
                    //query matching percentage
                    qmp=((q-count)*100)/q;
                    //title matching percentage
                    tmp=((t-count)*100)/t;
                    //decision factor
                    df=(qmp+tmp)/2;
                  System.out.println("  qmp="+qmp+" tmp="+tmp+" df="+df);
                    /*From our observations, we came to know that if decision factor is less than 25%,
                      then it means that we have titles in our db that either match exactly or partially
                      with the query
                    */
                    System.out.println(df);
                    if(df<=25 && df>=0)
                    {
                        //If the query exactly matches with the title present in our data base
                        if(q==count && count==t){
                            exact_match.add(title);
                           System.out.println("\n\nExact match\n\n");
                            }
                        //Other wise if it is partially amtched
                        if(t!=q)
                        {
                            //if n(t)=n(q)+1
                            if(count==q && count+1==t){ 
                                partial_match1.add(title);
                               //match.put("partial match rank 1", title);
                           System.out.println("\n\npartial match rank1\n\n");
                                }
                            //if n(t)=n(q)-1
                            else if(count==q && count+2==t){
                                partial_match3.add(title);
                                //match.put("partial match rank 3", title);
                               System.out.println("\n\npartial match rank3\n\n");
                                }
                            //if(n(t)=n(q)+2
                            else if(count==t && count+1==q){
                                partial_match2.add(title);
                                //match.put("partial match rank 2", title);
                             System.out.println("\n\npartial match rank2\n\n");
                                }
                        }
                        else if(count>=q/2 && count>=t/2){//the condition of partial match has been changed
                            partial_match4.add(title);
                           System.out.println("\n\npartial match rank4\n\n");
                            }
                        //else
                          //  System.out.println("Let us try with the another title in the database if any\n");
                        //break;
                        cm=0;
                    }
                    //else cm=1;
                    //t_ar.clear();
                }
                }
        			 else count1++;
                if(exact_match.size()>0)
                    match.put("Exact match", exact_match);
                if(partial_match1.size()>0)
                    match.put("partial match rank 1", partial_match1);
                if(partial_match2.size()>0)
                    match.put("partial match rank 2", partial_match2);
                if(partial_match3.size()>0)
                    match.put("partial match rank 3", partial_match3);
                if(partial_match4.size()>0)
                    match.put("partial match rank 4", partial_match4);
               System.out.println("Fetching Keys and corresponding [Multiple] Values n");
                for (Map.Entry<String, List<String>> entry : match.entrySet()) {
                    String key = entry.getKey();
                    List<String> values = entry.getValue();
                   System.out.println("\n"+ key+":");
                    for (int i1=0;i1 < values.size();i1++)
                    {
                    System.out.println(values.get(i1));
                    }
                    //System.out.println(values + "\n");
                }
                exact_match.clear();
                partial_match1.clear();
                partial_match2.clear();
                partial_match3.clear();
                partial_match4.clear();
                match.clear();
        		 }
        		 count1++;
            }
            catch(Exception e1)
            {
                System.out.println("error: "+e1);
            }
           
       
        if(title==""){
            System.out.println("The title is not present in the database");
            return 0;
       }
        if(cm==0)
        return 1;
        else return 0;//need to change +here
    }
}
