import java.io.File; 
import java.io.FileInputStream;
import java.io.IOException; 
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.StringTokenizer;  
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument; 
import org.apache.pdfbox.text.PDFTextStripper; 
public class PDFTokens extends Mysqlconn {
	public static Map<String,Float> res=new HashMap<>();

    public static Float sum=0.0f;
    public static int total_terms=0;
     public void keyword_extraction(File file, String title,String word_title)throws IOException 
     { 
        String p1="";
        String p="";
        int i,l=0,e=0;  
        Float count=0.0f;
       // int c,m=0,r=0;
        /*we cleared res hashset because each time when we insert a new pdf document, the keywords will vary .
          so inorder to avoid ambiguity, we are clearing the hash set "res"*/
         res.clear();
    Stemmer stem=new Stemmer();
    //File file=new File("C:\\Users\\HP\\Desktop\\4-2 project files\\gnowsis.pdf"); 
    //new Stemmer().call(new File("C:\\Users\\HP\\Desktop\\4-2 project files\\sample.pdf"));
    
    String text;
    String arr1[]={""};
    FileInputStream inputStream = new FileInputStream(file);
    byte[] thePDFFileBytes= IOUtils.toByteArray(inputStream);
    try (PDDocument document = PDDocument.load(thePDFFileBytes))
    {
        PDFTextStripper pdfStripper=new PDFTextStripper();
        text = pdfStripper.getText(document);
        //here TwoWord is a class that contains code for identifying all the 2 word combinations possible
        //TwoWord tw=new TwoWord();
        //tw.find(text);
    }
    Scanner scanner = new Scanner(text);
    StringTokenizer st = new StringTokenizer(text,"."); 
    /*List<String> elements = new ArrayList<String>();*/ 
    //System.out.println(st.countTokens()); 
    String elements[] = new String[1000];//elements[] holds the individual keywords in a sentence
    String line[] = new String[10000];//line[] holds each sentence in a pdf
    //System.out.println(st.nextToken().toString());
    l=0;
    while(st.hasMoreTokens()) { 
        e=0;
        //System.out.println("l="+l);
            line[l++]=" "+st.nextToken().toLowerCase();
           // System.out.println(line[l-1]);
            StringTokenizer st1=new StringTokenizer(line[l-1]," ");
            while(st1.hasMoreTokens())
            {
                elements[e++]=st1.nextToken();
            }
     //This for loop iterates over each token which are stored in elements 
        for(i=0;i<e;i++)
        { 
            total_terms++;
           if(elements[i].contains("-"))
           {
               /*If a word is split into 2 lines, then such strings are handled here by eliminating - and \n characters*/
               p1=elements[i];
               p=p1.substring(0, p1.indexOf("-"))+p1.substring(p1.indexOf("\n")+1, p1.length());         
           }
           else if(elements[i].contains("\n"))
           {
               //if a token started in a new line, then such string is handled by removing \n character
               p1=elements[i].replaceAll("[+.^:,']","");
               p=p1.substring(p1.indexOf("\n")+1,p1.length());
           }
           else 
           {
               //handle other strings which may contain punctuation marks like coma, fullstop, hyphen etc.,
               p=elements[i].replaceAll("[+.^:,']","");  
           }
           //this for loop is for stop word removal
//            for(j=0;j<names.length;j++) 
//           { 
//               //if the string matches with one stop word(stored in names[] array), the we can remove that string from
//               //the list of tokens and break the loop because it is no use to traverse through the remaining stop word.
//               if(p.equals(names[j]) && p.contains(names[j]))
//               {
//                   elements[i]=" ";
//                   break;
//               }
//
//           }
           if(stopWordsSet.contains(p))
           {
               elements[i]="";
           }
           if(elements[i]!=" ")
           {
                //In stemmer.java, we have the porter stemmer algorithm to stem each word and get the root word
                arr1=stem.stripAffixes(p);
                try
                {
                   //If the stemmed words contains more than one word, then iterate through the arr1 array
                   for(int i1=0;i1<arr1.length;i1++)
                   {
                       p=arr1[i1].replaceAll("[\\d()]","");
                       //if the word already exists in the hash table, then increment the count of the word
                       if(res.containsKey(p))
                            count=(Float) (res.get(p)+1);
                       //otherwise, this the first time the word appeared
                       else
                        count=1.0f;
                       res.put(p,count);
                       //sum+=count;
                   }
                }
                catch(NullPointerException e1)
                {
                   //System.out.println("The exception raised is:"+e1);
                }
           }
       }
    }
        TwoWord tw=new TwoWord();
        tw.twowords(file,title,word_title);
      }
}
