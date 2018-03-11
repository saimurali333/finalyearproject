import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.StringTokenizer;
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;


public class TwoWord extends PDFTokens {
	Map<String,Float> res1=new HashMap<>(res);
    public void twowords(File file, String title,String word_title) throws IOException{
       String names[] = new String[]{"the","a", "use","about","it's", "above", "above", "across", "after", "afterwards", "again", "against", "all",        "almost",  
                    "alone ", "along", "already", "also","although","always","am","among", "amongst", "amoungst", "amount",  "an", "and","instead",  
                    "another", "any","anyhow","anyone","anything","anyway", "anywhere", "are", "around", "as",  "at", "back","be","became",  
                    "because","become","becomes", "becoming", "been", "before", "beforehand", "behind", "being", "below", "beside", "besides",  
                    "between", "beyond", "bill", "both","based", "bottom","but", "by", "call", "can", "cannot", "cant", "co", "con", "could", "couldnt", 
                    "cry", "de", "describe", "detail", "do", "done", "down", "due", "during", "each", "eg", "eight", "either", "eleven","else", 
                    "elsewhere", "empty", "enough", "etc", "even", "ever", "every", "everyone", "everything", "everywhere", "except", "few",  
                    "fifteen", "fify", "fill", "find", "fire", "first", "five", "for", "former", "formerly", "forty", "found", "four", "from",  
                    "front", "full", "further", "get", "give", "go", "had", "has", "hasnt", 
                    "have", "he", "hence", "her", "here", "hereafter", "hereby", "herein", "hereupon", "hers", "herself",  
                    "him", "himself", "his", "how", "however", "hundred", "ie", "if", "in", "inc", "indeed", "interest", "into",  
                    "is", "it", "its", "itself", "keep", "last", "latter", "latterly", "least", "less", "ltd", "made", "many",  
                    "may", "me", "meanwhile", "might", "mill", "mine", "more", "moreover", "most", "mostly", "move", "much", "must",  
                    "my", "myself", "name", "namely", "neither", "never", "nevertheless", "next", "nine", "no", "nobody", "none",  
                    "noone", "nor", "not", "nothing", "now", "nowhere", "of", "off", "often", "on", "once", "one", "only", "onto",  
                    "or", "other", "others", "otherwise", "our", "ours", "ourselves", "out", "over", "own","part", "per", "perhaps", 
                    "please", "put", "rather", "re", "same", "see", "seem", "seemed", "seeming", "seems", "serious", "several", "she", 
                    "should", "show", "side", "since", "sincere", "six", "sixty", "so", "some", "somehow", "someone", "something",  
                    "sometime", "sometimes", "somewhere", "still", "such", "system", "take", "ten", "than", "that","their",  
                    "them", "themselves", "then", "thence", "there", "thereafter", "thereby", "therefore", "therein", "thereupon",  
                    "these", "they", "thickv", "thin", "third", "this", "those", "though", "three", "through", "throughout", "thru",  
                    "thus", "to", "together", "too", "top", "toward", "towards", "twelve", "twenty", "two", "un", "under", "until",  
                    "up", "upon", "us", "very", "via", "was", "we", "well", "were", "what", "whatever", "when", "whence", "whenever", 
                    "where", "whereafter", "whereas", "whereby", "like","tell", "cbd", "wait", "common", "allow", "schwarz", "wherein", "whereupon", "wherever", "whether", "which", "while",  
                    "whither", "who", "whoever", "whole", "whom", "whose", "why", "will", "with", "within", "without", "would", "yet", 
                    "you", "your", "yours", "yourself", "yourselves","1","2","3","4","5","6","7","8","9","10","1.","2.","3.","4.","5.","6.","11", 
                    "7.","8.","9.","12","13","14","A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z", 
                    "terms","CONDITIONS","conditions","values","interested.","care","sure",".","!","@","#","$","%","^","&","*","(",")","{","}","[","]",":",";",",","<",".",">","/","?","_","-","+","=", 
                    "a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z", 
                    "contact","grounds","buyers","tried","said,","plan","value","principle.","forces","sent:","is,","was","like", 
                    "discussion","tmus","diffrent.","layout","area.",",","does","grow","thanks","thankyou","hello","bye","rise","fell","fall","psqft.","http://","km","miles"}; 
         int name=names.length;
         ArrayList<String> StopWords = new ArrayList<>(name);
         int i;
         for(i=0;i<name;i++)
         {
             StopWords.add(names[i]);
         }
    //File file=new File("C:\\Users\\HP\\Desktop\\4-2 project files\\gnowsis.pdf"); 
    FileInputStream inputStream = new FileInputStream(file);
    
    byte[] thePDFFileBytes= IOUtils.toByteArray(inputStream);
    String text;
    try (PDDocument document = PDDocument.load(thePDFFileBytes))
    {
        PDFTextStripper pdfStripper=new PDFTextStripper();
        text = pdfStripper.getText(document);
    }
    Float count=1.0f;
    Scanner scanner = new Scanner(text);
    StringTokenizer st = new StringTokenizer(text,".");
    ArrayList<String> twoWords= new ArrayList<>(name);
    ArrayList<String> threeWords= new ArrayList<>(name);
    String two="";
    String three="";
    String wordcopy="";
    String wordaftermodification="";
    Stemmer stem=new Stemmer();
    //Map<String,Integer> res1=new HashMap<>(res);
    int j=0,l=0,twowordscount=0,threewordscount=0;
    String line[] = new String[10000];
    while(st.hasMoreTokens())
    {
            line[l++]=" "+st.nextToken().toLowerCase();
            StringTokenizer st1=new StringTokenizer(line[l-1]," ");
            
            while(st1.hasMoreTokens())
            {
                 String word=st1.nextToken();
                // System.out.println(word);
                if(word!=" ")
                {
                        if(word.contains("-") && word.contains("\n"))
                 {
                     /*If a word is split into 2 lines, then such strings are handled here by eliminating - and \n characters*/
                     wordcopy=word;
                     wordaftermodification=wordcopy.substring(0, wordcopy.indexOf("-"))+wordcopy.substring(wordcopy.indexOf("\n")+1, wordcopy.length());         
                 }
                 else if(word.contains("\n"))
                 {
                     //if a token started in a new line, then such string is handled by removing \n character
                     wordcopy=word.replaceAll("[+.^:,']","");
                     wordaftermodification=wordcopy.substring(wordcopy.indexOf("\n")+1,wordcopy.length());
                 }
                 else 
                 {
                     //handle other strings which may contain punctuation marks like coma, fullstop, hyphen etc.,
                     wordaftermodification=word.replaceAll("[-+.^:,']","");  
                 }

                       if(StopWords.contains(wordaftermodification)){
                  twowordscount=0;
                  threewordscount=0;     }
              else{
                         String[]  wordaftermodification1= stem.stripAffixes(wordaftermodification);
                         try
                 {
                    //If the stemmed words contains more than one word, then iterate through the arr1 array
                    for(int i1=0;i1<wordaftermodification1.length;i1++)
                    {
                        wordaftermodification=wordaftermodification1[i1].replaceAll("[\\d()]","");
                        //if the word already exists in the hash table, then increment the count of the word

                    }
                 }
                 catch(NullPointerException e1)
                 {
                    //System.out.println("The exception raised is:"+e1);
                 }
                     if(twowordscount==1)   {      
                  two=two+wordaftermodification;
                     twowordscount++;}
                     else if(twowordscount==0){
                      two=wordaftermodification+" ";
                      twowordscount++;
                  }
                     if(twowordscount==2){
                          if(res1.containsKey(two))
                             count=res1.get(two)+1;
                        //otherwise, this the first time the word appeared
                        else
                         count=1.0f;
                         res1.put(two,count);
                         //sum+=count;
                         two=wordaftermodification+" ";
                         twowordscount=1;//we have to divide in to two word combination three word be ex:semantic desktop application;now two words are semantic desktop,desktop application

                      }
                     if(threewordscount==3){
                         if(res1.containsKey(three))
                             count=(Float) (res1.get(three)+1);
                        //otherwise, this the first time the word appeared
                        else
                         count=(Float) 1.0f;
                         res1.put(three,count);
                         //sum+=count;
                         three=wordaftermodification+" ";
                         threewordscount=1;
                     }

                       if(threewordscount>=1)   {      
                  three=three+wordaftermodification+" ";
                     threewordscount++;}

                     if(threewordscount==0){
                      three=wordaftermodification+" ";
                      threewordscount++;
                  }

                     wordaftermodification="";
                         wordcopy="";
                }
    } 
}
    }
    //System.out.println("Total sum of values are:"+sum);
Set<String> key=res1.keySet();
for(String k:key){
    if(res1.get(k)>1.0){
        sum+=res1.get(k);
        //System.out.println(k+" : "+res1.get(k));
    }}
//System.out.println("sum="+sum);
System.out.println("total number of terms in the document are:"+total_terms);
//System.out.println("Two word and three word combination keywords are:");
Map<String,Float> res2=new HashMap<>();
res2.clear();
for(String k:key)
{
    if(res1.get(k)>1.0){
        float r=res1.get(k)/total_terms;
        res2.put(k,r);
        //System.out.println(k+" : "+(Float)(res2.get(k)));
}}
ConceptIdentifier ci=new ConceptIdentifier();
ci.title_concept(res2,title,word_title);
    
//return res1;
}
}
