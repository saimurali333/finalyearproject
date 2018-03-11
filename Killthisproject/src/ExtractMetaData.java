
import java.io.*;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.text.PDFTextStripper;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.io.InputStreamHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.semantics.GraphManager;
import com.marklogic.client.semantics.RDFMimeTypes;
import com.marklogic.client.semantics.SPARQLMimeTypes;
import com.marklogic.client.semantics.SPARQLQueryDefinition;
import com.marklogic.client.semantics.SPARQLQueryManager;

import java.util.*;
import java.util.StringTokenizer;
import org.apache.pdfbox.text.*;
import java.util.regex.*;
import edu.smu.tspell.wordnet.*;
import edu.smu.tspell.wordnet.Synset;
//be sure this package is included or nothing will work 

public class ExtractMetaData {
	public static String  title;
	public static float prevFontHeight;
	public ArrayList<String> author;
	public String addr;
	public ArrayList<String> authormail;
        int count=0;
        public static String Concept;
        public static HashSet<String> term_concept = new HashSet<String>();
        public static Map<String,Integer> title_num=new HashMap<>();
        public static Map<String,String> terms=new HashMap<>();
        /*the list of all possible stop words in english language are stored initilly in the names[] and then saved 
          in a hashset stopWordsSet*/
        Set<String> stopWordsSet = new HashSet<>();
        Stemmer s=new Stemmer();
                String names[] = new String[]{"the","a", "use","about","it's", "above", "above", "across", "after", "afterwards", "again", "against", "all",        "almost",  
                    "alone ", "along", "already", "also","although","always","am","among", "amongst", "amoungst", "amount",  "an", "and",  
                    "another", "any", "anyhow", "anyone", "like", "tell", "cbd", "wait", "common", "allow", "schwarz", "anything", "anyway", "anywhere", "are", "around", "as",  "at", "back","be","became",  
                    "because","become","becomes", "becoming", "been", "before", "beforehand", "behind", "being", "below", "beside", "besides",  
                    "between", "beyond", "bill", "both", "bottom","but", "by", "call", "can", "cannot", "cant", "co", "con", "could", "couldnt", 
                    "cry", "de", "describe", "detail", "do", "done", "down", "due", "during", "each", "eg", "eight", "either", "eleven","else", 
                    "elsewhere", "empty","based", "enough", "etc", "even", "ever", "every", "everyone", "everything", "everywhere", "except", "few",  
                    "fifteen", "fify", "fill", "find", "fire", "first", "five", "for", "former", "formerly", "forty", "found", "four", "from",  
                    "front", "full", "further", "get", "give", "go", "had", "has", "hasnt", 
                    "have", "he", "hence", "her", "here", "hereafter", "hereby", "herein", "hereupon", "hers", "herself",  
                    "him", "himself", "his", "how", "however", "hundred", "ie", "if", "in", "inc", "indeed", "interest", "into",  
                    "is", "it", "its", "itself", "keep", "last", "latter", "latterly", "least", "less", "ltd", "made", "many",  
                    "may", "me", "meanwhile", "might", "mill", "mine", "more", "moreover", "most", "mostly", "move", "much", "must",  
                    "my", "myself", "name", "namely", "neither", "never", "nevertheless", 
                    "next", "nine", "no", "nobody", "none",  
                    "noone", "nor", "not", "nothing", "now", "nowhere", "of", "off", "often", "on", "once", "one", "only", "onto",  
                    "or", "other", "others", "otherwise", "our", "ours", "ourselves", "out", "over", "own","part", "per", "perhaps", 
                    "please", "put", "rather", "re", "same", "see", "seem", "seemed", "seeming", "seems", "serious", "several", "she", 
                    "should", "show", "side", "since", "sincere", "six", "sixty", "so", "some", "somehow", "someone", "something",  
                    "sometime", "sometimes", "somewhere", "still", "such", "system", "take", "ten", "than", "that","their",  
                    "them", "themselves", "then", "thence", "there", "thereafter", "thereby", "therefore", "therein", "thereupon",  
                    "these", "they", "thickv", "thin", "third", "this", "those", "though", "three", "through", "throughout", "thru",  
                    "thus", "to", "together", "too", "top", "toward", "towards", "twelve", "twenty", "two", "un", "under", "until",  
                    "up", "upon", "us", "very", "via", "was", "we", "well", "were", "what", "whatever", "when", "whence", "whenever", 
                    "where", "whereafter", "whereas", "whereby", "wherein", "whereupon", "wherever", "whether", "which", "while",  
                    "whither", "who", "whoever", "whole", "whom", "whose", "why", "will", "with", "within", "without", "would", "yet", 
                    "you", "your", "yours", "yourself", "yourselves","1","2","3","4","5","6","7","8","9","10","1.","2.","3.","4.","5.","6.","11", 
                    "7.","8.","9.","12","13","14","A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z", 
                    "terms","CONDITIONS","conditions","values","interested.","care","sure",".","!","@","#","$","%","^","&","*","(",")","{","}","[","]",":",";",",","<",".",">","/","?","_","-","+","=", 
                    "a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z", 
                    "contact","grounds","buyers","tried","said,","plan","value","principle.","forces","sent:","is,","was","like", 
                    "discussion","tmus","diffrent.","layout","area.","does","grow","type", "thanks","thankyou","hello","bye","rise","fell","fall","psqft.","http://","km","miles"}; 
	ExtractMetaData()
	{
  		this.prevFontHeight=0.0f;
  		this.addr="";
  		this.title="";
		this.author= new ArrayList();
                this.authormail=new ArrayList();
	}
        public void concept(String table)
        {
            Concept=table;
        }
        public  String processPDF(String arg) throws IOException
        {
    		DatabaseClient client =DatabaseClientFactory.newClient("localhost",8060,"Documents",new DatabaseClientFactory.DigestAuthContext("admin","admin"));
                SPARQLQueryManager sqmgr = client.newSPARQLQueryManager();
        
            File file= new File(arg);
            boolean isauthor=false;
            String pdf_title="";
            int name=names.length;
            //System.out.println("No. of StopWords = "+name);
         for(int i1=0;i1<name;i1++)
         {
             stopWordsSet.add(names[i1]);
         }
            try( PDDocument document=PDDocument.load(file))
            {
                Pattern p = Pattern.compile("\\d");
               // Pattern pt = Pattern.compile("[^a-zA-Z0-9]");
                PDFTextStripper pdfstripper;
                pdfstripper = new PDFTextStripper() {
                  
                    //String prevBaseFont = "";

                   
                    protected void writeString(String text, List<TextPosition> textPositions) throws IOException 
                    {
                                   // String pdf_title="";

                        StringBuilder builder = new StringBuilder();
                        builder.setLength(0);
                        float prey=0.0f;
                      
                       for (TextPosition position : textPositions){
                                String baseFont = position.getFont().getName();
                                float size=position.getHeight();
                                                    
                               if (baseFont != null &&   (size>=8.0  && position.getY()>prey) )
                                {
                                       ExtractMetaData.prevFontHeight=size;
                                           //System.out.println(size);
                                        prey=position.getY();
                                        builder.append("title-");
                                    ExtractMetaData.title+=" ";
                                   ExtractMetaData.title+=position.getUnicode();
                              
                                }
                               else if(/*baseFont.contains("Bold") &&*/ (size>=8.0 /*|| size>PDFReader.prevFontHeight*/))
                               {
                                   ExtractMetaData.title+=position.getUnicode();
                                    builder.append("title-");
                               }
                              
                                    
                                builder.append(position.getUnicode());
                            }
                       
                        writeString(builder.toString());
                       
                    }
                    
                };
               
                
               pdfstripper.setStartPage(0);
               pdfstripper.setEndPage(1);
                String text=pdfstripper.getText(document);
                System.out.println(title);
                title=title.replaceAll("[-:.,]"," ");
                System.out.println("t="+title);
                String x=title.replaceAll("[^a-zA-Z0-9]"," ").trim();
               /*System.out.println("title:"
                       + ""
                       + ""
                       + "-"+title+"    "+x);*/
                System.out.println("Title is:"+x);
               StringTokenizer y=new StringTokenizer(x," ");
               //ExtractMetaData e1=new ExtractMetaData();
               String a;
               Set<String> meaningSet = new HashSet<>();
               //These are the list of stop words that are possible in a title
                stopWordsSet.add("a");stopWordsSet.add("iv");stopWordsSet.add("lim,");stopWordsSet.add("1");
                stopWordsSet.add("the");stopWordsSet.add("2");stopWordsSet.add("7");stopWordsSet.add("9");
                stopWordsSet.add("all");stopWordsSet.add("d.");stopWordsSet.add(":1");stopWordsSet.add("fox");
                stopWordsSet.add("for");stopWordsSet.add("and");stopWordsSet.add("of");stopWordsSet.add("70");
                stopWordsSet.add("when");stopWordsSet.add("3.");stopWordsSet.add("37");stopWordsSet.add("cs");
                stopWordsSet.add("with");stopWordsSet.add("m");stopWordsSet.add("v");stopWordsSet.add("x");
                stopWordsSet.add("along");stopWordsSet.add("to");stopWordsSet.add("6v");stopWordsSet.add("ar");
                stopWordsSet.add("guide");stopWordsSet.add("is");stopWordsSet.add("from");stopWordsSet.add("an");
                stopWordsSet.add("which");stopWordsSet.add("c");
                stopWordsSet.add("very");
                stopWordsSet.add("01");
                stopWordsSet.add("06");
                stopWordsSet.add("using");
                stopWordsSet.add("in");
                stopWordsSet.add("its");
                stopWordsSet.add("-");
                pdf_title="";
  //              Set<String> keys=title_num.keySet();
//                for(String d:keys)
//                    System.out.println(d+"="+title_num.get(d));
                
                try{

                	try{
                		SPARQLQueryDefinition query = sqmgr.newQueryDefinition("SELECT ?number  " +
                  			    "WHERE {?word <hasnumber>  ?number}");
                  		InputStreamHandle handle = new InputStreamHandle();
                  		handle.setMimetype(SPARQLMimeTypes.SPARQL_XML);
                  		InputStreamHandle results = sqmgr.executeSelect(query, handle);
                  		String rs1=results.toString();
                  		String str[] = rs1.split("<uri>");
                  		String d[];
                  		int k=-1;
                  		int p1;
                  		int count1=0;
                  		for(String s: str){
                  			if(count1==1) {
            	        	d=s.split("</uri>");
            	        	p1=Integer.parseInt(d[0]);
            	        	if(k<p1)
            	        	{
            	        		k=p1;
            	        	}
                  			}
                  			if(count1==0)
                  			count1++;
            	        }
                  		count=k;
//                        ResultSet rs1=stmt.executeQuery("select max(number) from term_encoder");
                       /* while(rs1.next())
                        {
                            count=rs1.getInt(1);
                            System.out.println("count="+count);
                        }*/
                    }
                    catch(Exception ex1)
                    {
                        System.out.println("Exception while getting max value from term number store");
                    }
                }
                catch(Exception ex1)
                {
                    System.out.println("Exception while getting max value from term number store");
                }
                String b;
                System.setProperty("wordnet.database.dir","C:\\Program Files (x86)\\WordNet\\2.1\\dict");
                WordNetDatabase db = WordNetDatabase.getFileInstance();
                Synset[] syn;
                
                //String[] usage;
                //array of strings for usage examples
                //String temp1 ;
                //BufferedReader in1 = null;
               Mysqlconn mycon=new Mysqlconn();
                term_concept.clear();
                //System.out.println("Aget finding max value in the column");
                while(y.hasMoreTokens())
                {
                    b=y.nextToken().toLowerCase();
                    //System.out.println("b="+b);
                    //stripAffixes is a method for performing stemming operation to get the root word
                    a=s.stripAffixes(b)[0];
                    if(!stopWordsSet.contains(b)){
                        a=a.replaceAll("[:,.-]", "");
                        if(!stopWordsSet.contains(a.toLowerCase()))
                        {
                            //terms is a hash set that contains mapping of the actual word to its root word
                            for(String s:term_concept)
                            {
                                System.out.println("s===="+s);
                            }
                            syn = db.getSynsets(b); 
                            if(syn.length==0){
                                System.out.println("the word "+b+" do not have any meanings in the wordnet");
                                term_concept.add(b);
                         //       System.out.println(term_concept.size());
                            }
                            else{
                                System.out.println("the meaning of "+b+" are:");
                            StringBuffer array = new StringBuffer(15);//This array is for combining all the synsets rertrieved from wordnet(for each word)
                            for(int i = 0; i < syn.length; i++)
                            {
                                  //System.out.println(syn[i]+"\n");  //print all the retrieved synsets
                                  String[] arr=syn[i].toString().split("]");//partitions the string into parts upto closed brace i.e.']'
                                  StringTokenizer y1=new StringTokenizer(arr[0].toString(),"[");  //divide string into 
                                  String x1=y1.nextToken();
                                  //System.out.println("x1="+x1);
                                  String token=y1.nextToken();
                                  array=array.append(token).append(",");
                                  System.out.println("\n token="+token);
                                  System.out.println("hello");
                            } 
			  //System.out.println(array);
			  String[] words = array.toString().split(",");
 
				// clean duplicates
				for (int i = 0; i < words.length; i++)
				{
                                    meaningSet.add(words[i]);
				}
                                mycon.insert(title,b,meaningSet);
                                meaningSet.clear();
                                //title_num.put(Concept, count);
                            }
                            terms.put(b,a);
                                if(!title_num.containsKey(a))
                                {
                                    int ct=0;
                                     try{
                                    		SPARQLQueryDefinition query = sqmgr.newQueryDefinition("SELECT ?number  " +
                                      			    "WHERE {?word <hasnumber>  ?number}");
                                      		InputStreamHandle handle = new InputStreamHandle();
                                      		handle.setMimetype(SPARQLMimeTypes.SPARQL_XML);
                                      		InputStreamHandle results = sqmgr.executeSelect(query, handle);
                                      		String rs1=results.toString();
                                      		String str[] = rs1.split("<uri>");
                                      		String d[];
                                      		Integer number[]=new Integer[10000];
                                      		int count1=0;
                                      		int k=0;
                                      		for(String s: str){
                                                    d=s.split("</uri>");
                                                   
                                      			if(count1==1) {
                                	        	
                                	        	number[k]=Integer.parseInt(d[0]);
                                	        	k++;
                                      			}
                                                        else count1++;
                                      		}
                                      		query = sqmgr.newQueryDefinition("SELECT ?word  " +
                                      			    "WHERE {?word <hasnumber>  ?number}");
                                      		handle = new InputStreamHandle();
                                      		handle.setMimetype(SPARQLMimeTypes.SPARQL_XML);
                                      		results = sqmgr.executeSelect(query, handle);
                                      		rs1=results.toString();
                                            String str1[] = rs1.split("<uri>");                             	
                                      		count1=0;
                                      		k=0;
                                      		for(String s: str1){
                                      			if(count1==1) {
                                	        	d=s.split("</uri>");
                                	                title_num.put(d[0], number[k]);
                                                    k++;
                                                    //System.out.println("value="+value);
                                                    if(d[0].equals(a) )
                                                    {
                                                        ct=1;break;
                                                    }
                                                }else count1++;
                                      		}
                                                if(ct==0)
                                                {
                                                    count++;
                                                    String tripleStore = "<" + a+ ">" + " " + " <hasnumber>" + " " + "<" + count + ">" + ".";
                                                    GraphManager graphManager = client.newGraphManager();

                                            	    graphManager.setDefaultMimetype(RDFMimeTypes.NTRIPLES);
                                            	    graphManager.merge("termnumber", new StringHandle(tripleStore));
                                            	    
                                                   // stmt.executeUpdate("INSERT INTO term_encoder VALUES('"+a+"','"+count+"');");
                                                    title_num.put(a, count);
                                                    //stmt.executeUpdate("INSERT INTO term_encoder VALUES('"+a+","+count+"');");
                                                }
                                            
                                    }
                                    catch(Exception e3)
                                    {
                                        System.out.println("error:"+e3+" in outer catch");
                                    }
                                    //out.writeBytes(a+"\t-"+(++count));
                                }
                                pdf_title+=title_num.get(a)+"/";
                                //out.writeBytes("\n");
                                //System.out.println(a);}
                        }
                    }
                }
                System.out.println("pdf_title after appending for "+x+" is "+pdf_title+"\n");
                PDFTokens pdf_tokenize=new PDFTokens();
                pdf_tokenize.keyword_extraction(file,pdf_title,x);
                //int ct=0;
                //insert this number title into the database mch_project under the table name: "title_store"
                client =DatabaseClientFactory.newClient("localhost",8060,"Documents",new DatabaseClientFactory.DigestAuthContext("admin","admin"));
                
                try{
                
                
                	SPARQLQueryDefinition query = sqmgr.newQueryDefinition("SELECT ?title  " +
            			    "WHERE {<onlytitles> <hasatitle>  ?title}");
            		InputStreamHandle handle = new InputStreamHandle();
            		handle.setMimetype(SPARQLMimeTypes.SPARQL_XML);
            		InputStreamHandle results = sqmgr.executeSelect(query, handle);
            		String rs1=results.toString();
            		
                       
                                
                                if(!rs1.contains(pdf_title) )
                                {
                                  
                                	String subjectURI ="onlytitles" ;
                            		String predicateURI = "hasatitle";
                            		String objectURI = pdf_title;
                            		//String graphURI = "Newontology12355";
                            	//	DatabaseClient client =DatabaseClientFactory.newClient("localhost",8060,"Documents",new DatabaseClientFactory.DigestAuthContext("admin","admin"));
                            		String tripleStore = "<" + subjectURI + ">" + " " + "<" + predicateURI
                            	            + ">" + " " + "<" + objectURI+ ">" + "."; 
                            	GraphManager graphManager = client.newGraphManager();

                        		graphManager = client.newGraphManager();
                        	    graphManager.setDefaultMimetype(RDFMimeTypes.NTRIPLES);
                        	    graphManager.merge("titlestore", new StringHandle(tripleStore));
                        	    
                        	          
                               // stmt.executeUpdate("INSERT INTO title_store VALUES('"+pdf_title+"');");
                            }
                        
                }
                catch(Exception e3)
                {
                    System.out.println("error:"+e3);
                }
                System.out.println("uyuyg");  
                StringTokenizer newline=new StringTokenizer(text,"\n");
               StringTokenizer coma;
                  // StringTokenizer space;
                 /* author= new ArrayList();
                   authormail=new ArrayList();*/
                  ArrayList<String> address=load();
                  boolean set=false;
               while(newline.hasMoreTokens())
                {
                    String token1=newline.nextToken().trim();
                    //System.out.println(token1);
                  if(!token1.contains("title-")  && set)
                  {     // System.out.println(token1);
                        if(token1.contains("Abstract")|| token1.contains("ABSTRACT") || token1.contains("Introduction") || token1.contains("Keywords") || token1.contains("key")|| token1.contains("Summary") )//|| token1.contains("Overview"))
                        {
                            break;
                        }
                        else
                        {
                           
                          if(!isauthor  )
                           {
                              coma=new StringTokenizer(token1,",*");
                              String auth;
                              while(coma.hasMoreTokens())
                              {     
                                  auth=coma.nextToken();
                                  
                                    if((auth.trim().length()>1 ) )
                                    {
                                        
                                        auth=auth.replace("and","");
					auth=auth.replaceAll(" ","_");
				
                                        author.add(auth.replaceAll("[^a-zA-Z]"," ").trim());
                                    }
                              }
                           }
                           else
                           {
                               if(match(token1,address) || p.matcher(token1).find())
                               {
                                   addr+=token1;
                                   addr+="\n";
                               }
                               else if(!token1.contains("@") && !p.matcher(token1).find()  )
                               {
                                  if(token1.trim().length()>0)
                                  {
                                     
                                       coma=new StringTokenizer(token1,",");
                                        while(coma.hasMoreTokens())
                                         {
						String auth=coma.nextToken().replaceAll(" ","_");
                                                 author.add(auth.replaceAll("[^a-zA-Z]"," ").trim());
                                         }
                                  }
                                 
                               }
                               else if(token1.contains("@"))
                               {
                                   authormail.add(token1);
                               }
                           }
                           
                               
                              isauthor=true;
                       }
                      
                    }
                  else if(token1.contains("title-") )
                  {
                      set=true;
                  }
               
               }
            }
        
        return pdf_title;
        }
        public  ArrayList<String> load()
        {
            ArrayList<String> addr=new ArrayList<String>();
            addr.add("National");
            addr.add("Institute");
            addr.add("Science");
            addr.add("Technology");
            addr.add("University");
            addr.add("Department");
            addr.add("Research");
            addr.add("Center");
            addr.add("Labs");
            addr.add("Management");
            addr.add("Business");
            addr.add("School");
            addr.add("College");
            addr.add("Engineering");
            addr.add("Graduate");
            addr.add("Graduation");
            addr.add("Info");
            addr.add("Solutions");
            addr.add("Informatics");
            addr.add("Information");
            addr.add("Digital");
            addr.add("Enterprise");
            addr.add("Company");
            addr.add("Network");
            addr.add("Hardware");
            addr.add("Software");
            addr.add("Generation");
            addr.add("Corporation");
            return addr;
        }
        public  boolean match(String token,ArrayList<String> addr)
        {
            boolean res=false;
            for(int i=0;i<addr.size();i++)
            {
                if(token.contains(addr.get(i)))
                {
                    res=true;
                    return res;
                }
            }
                
            return res;
        }
	public ArrayList<String> ExtractAuthor()
	{
		String Author="";
		 for(int i=0;i<author.size();i++)
               {
                    //System.out.println("authors:-"+author.get(i));
			Author+=author.get(i);
			Author+=",";
               }
			
		return author;
	}
	public String ExtractAuthorMail()
	{
		String mail="";
		 for(int i=0;i<authormail.size();i++)
               {
                  // System.out.println("mail:-"+authormail.get(i));
			mail+=authormail.get(i);
			mail+="\n";
               }
		return mail;
	}
	public String ExtractTitle()
	{
		return title.trim();
	}
	public String ExtractAddress()
	{
		return addr;
	}
	
        public String Tokenize_query(String query) throws  ClassNotFoundException
        {
        	
            String query_num="";
            int concept=0;
            int t=0;
            Set<String> tableset = new HashSet<>();
         	
            System.out.println("the entries in title_num are:");
            Set<String> keys=title_num.keySet();
            for(String p:keys)
                System.out.println(p+" : "+title_num.get(p));
            System.out.println("The query is : "+query+" and its tokens are:");
            t=count;
            StringTokenizer query_tokens=new StringTokenizer(query.toLowerCase()," ");
            while(query_tokens.hasMoreTokens())
            {
                //p1 represents the tokenized words in the query
                String p1=query_tokens.nextToken();
                if(!stopWordsSet.contains(p1)){
                    p1=s.stripAffixes(p1)[0];
                System.out.println(p1);
                concept=0;
                if(title_num.containsKey(p1))
                    query_num+=title_num.get(p1)+"/";
                else 
                {
                	DatabaseClient client =DatabaseClientFactory.newClient("localhost",8060,"Documents",new DatabaseClientFactory.DigestAuthContext("admin","admin"));
                    
                				SPARQLQueryManager sqmgr = client.newSPARQLQueryManager();
                		SPARQLQueryDefinition query1 = sqmgr.newQueryDefinition("SELECT ?word  " +
                			    "WHERE {?word <hasmeaning>  ?meaning}");
                		System.out.println("yes");
                		InputStreamHandle handle = new InputStreamHandle();
                		handle.setMimetype(SPARQLMimeTypes.SPARQL_XML);
                		InputStreamHandle results = sqmgr.executeSelect(query1, handle);
                		String rs1=results.toString();
                		System.out.println(rs1);
                		String d[];
                		String str[] = rs1.split("<uri>");
                		 for(String s: str){
             	        	d=s.split("</uri>");
             	        
                        String x[];
                        System.out.println("The different tables in the database are(in extract.java):");
                       // while (rs.next()) {
                         //   x=rs.getString(3); //x represents the names of tables in database
                        int count2=0;
                         for(String s1: str){
             	        	x=s1.split("</uri>");
             	        	
             	            if(count2==1){
                        try{
                        	query1 = sqmgr.newQueryDefinition("SELECT ?meaning  " +
                    			    "WHERE {<"+x[0]+"> <hasmeaning>  ?meaning}");
                    		
                    		handle.setMimetype(SPARQLMimeTypes.SPARQL_XML);
                    		results = sqmgr.executeSelect(query1, handle);
                    		String rs2=results.toString();x[0]=x[0].replaceAll("_"," ");
                    		
                    		String p[];
                    		int count1=0;
                    		String str1[] = rs2.split("<uri>");
                    		 for(String s2: str){
                                     if(count1==1){
                 	        	p=s2.split("</uri>");
                    		    String value=p[0].replaceAll("_"," ");
                        
                                //ResultSet rs1 = stmt.executeQuery("select * from "+x);
                                System.out.println("x in tokenize_query is: "+x[0]);
                               
                                    if(value.equals(p1))
                                    {
                                        System.out.println(p1+" is a meaning of the root word "+x[0]);
                                        if(title_num.containsKey(x[0]))
                                            query_num+=title_num.get(x[0])+"/";
                                            concept=1;break;
                                    }
                                    else{    concept=0;if(t==0) t=count;}
                                }else count1++;}
                                if(concept==1)  break;
                            }
                            catch(Exception e2)
                            {
                                System.out.println("error:"+e2);
                            }         
                            }
                        else count2++;
                            //System.out.println(x);
                        }
                    }

                
                if(concept==0)
                {
//                  ConceptIdentifier cm=new ConceptIdentifier();
                //                    cm.conceptquery(p1);
                                    System.out.println("The word "+p1+" is not in our database so we are assigning a number to it temporarily as:");
                                    t++;
                                    System.out.println(p1+" "+(t));
                                    query_num+=t+"/";
                                    //return null;
                                    //call the method in conceptIdentifier searching for the method that determines 
                                    //the concept of the words in the query
                                }
                }
            }}
            return query_num;
        }
 

	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		/*
		DatabaseClient client =DatabaseClientFactory.newClient("localhost",8060,"sample",new DatabaseClientFactory.DigestAuthContext("admin","admin"));
		SPARQLQueryManager sqmgr = client.newSPARQLQueryManager();		
		System.out.println("1.Register 2.Login");
		Scanner sc = new Scanner(System.in);
		int select = sc.nextInt();
		String email,detail;
		Scanner sc1 = new Scanner(System.in);
		switch(select)
		{
		case 1:{
			System.out.println("enter email");
			email = sc1.nextLine();
			System.out.println("enter password");
			
			detail= sc1.nextLine();
		System.out.print("eamil");
		System.out.println(email);
		System.out.print("password");
		System.out.println(detail);
    		String predicateURI = "haspassword";
    		
    swdsdc		String graphURI = "PersonalOntology";
    		String tripleStore = "<" + email + ">" + " " + " <" + predicateURI
    	            + ">" + " " + "<" + detail + ">" + ".";
    		GraphManager graphManager = client.newGraphManager();
    	    graphManager.setDefaultMimetype(RDFMimeTypes.NTRIPLES);
    	    graphManager.merge(graphURI, new StringHandle(tripleStore));
    	    
    	    break;
			
		}
		case 2:{
			System.out.println("enter email");
			email = sc1.nextLine();
			System.out.println("enter password");
			detail= sc1.nextLine();
			SPARQLQueryDefinition query = sqmgr.newQueryDefinition("SELECT ?email " +
    			    "WHERE {?email <haspassword>  ?password}");
    		InputStreamHandle handle = new InputStreamHandle();
    		handle.setMimetype(SPARQLMimeTypes.SPARQL_XML);
    		InputStreamHandle results = sqmgr.executeSelect(query, handle);
    		String rs1=results.toString();
    		//System.out.println(rs1);
    		if(rs1.contains(email)) {
    			query = sqmgr.newQueryDefinition("SELECT ?password " +
        			    "WHERE {<"+email+"> <haspassword>  ?password}");
    			handle = new InputStreamHandle();
        		handle.setMimetype(SPARQLMimeTypes.SPARQL_XML);
        		results = sqmgr.executeSelect(query, handle);
        		String rs2=results.toString();
    			String str1[] = rs2.split("<uri>");
    			String d[];
    			int count=0;
    			for(String s: str1){
    	
    	        	d=s.split("</uri>");
    	        	if(count>0) {
    	        	//System.out.println(d[0]);
    	        	//System.out.println(detail);
    	            if(d[0].equals(detail)) {
    	            	System.out.println("login successfull");
    	            }}
    	        	if(count==0)
    	        	count++;
    	        }
    			String concept="semantic";
    			String title="1^2^3^4";
    			String predicateURI=email+"concept";
    			String tripleStore = "<" + concept + ">" + " " + " <" + predicateURI
        	            + ">" + " " + "<" + title + ">" + ".";
                
        		GraphManager graphManager = client.newGraphManager();
        	    graphManager.setDefaultMimetype(RDFMimeTypes.NTRIPLES);
        	    graphManager.merge(email, new StringHandle(tripleStore));			
    		}
			//System.out.println(rs1);
			//String str[] = rs1.split("\"email\"><uri>");
			
			/*String d[];
	        //List n1 = new ArrayList();
	        //n1 = Arrays.asList(str);
	        for(String s: str){
	        	d=s.split("</uri>");
	            System.out.println(d[0]);
	        }
	        for(String s: str1){
	        	d=s.split("</uri>");
	            System.out.println(d[0]);
	        }

		}
		
		
		} */

            Set<String> keys=title_num.keySet();
        ExtractMetaData e=new ExtractMetaData();
        
        //String file = "‪‪C:\\Users\\saimu\\Desktop\\filepaths.txt";
        //BufferedReader in = null;
        String temp = "";
        String title="";
        //in = new BufferedReader(new FileReader(file));
        try{
            
           // while((temp = in.readLine()) != null)
            
                //System.out.println(temp);
                title=e.processPDF("D:\\project\\gnowsis.pdf");
               // System.out.println("lhkjh");
                System.out.println(title);

                //System.out.println("numbered title is "+title);
            
//            Systthe entries in title_num are:em.out.println("The term number store is as follows:");
//            for(String key:keys)
//                System.out.println(key+" : "+title_num.get(key));
        }
        catch(IOException e1)
        {
            System.out.println("error in parsing the pdf file:"+e1);
        }
         String s1="semantic desktop";
         
        String p1=e.Tokenize_query(s1);
        if(p1!=null){
            System.out.println("the equivalent numbers representing the qery terms is:"+p1);
            QueryExpansion qe=new QueryExpansion();
            int q=qe.Ranking(p1);
            if(q==0)
            {
                    System.out.println("\n please go for concept matching and get the relevant titles that match with the concept");
                    ConceptIdentifier ci=new ConceptIdentifier();
                    ci.conceptquery(s1);
            }
        }


	}

}
