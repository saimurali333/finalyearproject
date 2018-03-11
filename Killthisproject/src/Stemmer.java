
import java.io.*;

class NewString {
  public String str;

  NewString() {
     str = "";
  }
}

public class Stemmer {
	private String[] Clean( String str ) {
	     int last = str.length();
	     int k=0;
	        if(str.contains("//") && last>0)
	         {
	            String[] temp=new String[2];
	            temp[k++]=str.substring(0,str.indexOf("//"));
	            temp[k]=str.substring(str.indexOf("//")+2,str.length());
	            return temp;
	         }
	        else if(str.contains("*") && last>0)
	        {
	             String[] temp=new String[2];
	             temp[k++]=str.substring(0,str.indexOf("*"));
	             temp[k]=str.substring(str.indexOf("//")+2,str.length());
	             return temp;
	        }
	        else if(last>0 && str.contains(" "))
	         {
	            String[] temp2=str.split(" ");
	            return temp2;
	         }
	        else if(last>0)
	        {
	            int i1=0;
	             Character ch = new Character( str.charAt(0) );
	             String[] temp2 ={""};
	             for ( int i=0; i < last; i++ ) {
	                 if ( ch.isLetterOrDigit( str.charAt(i) ) )
	                    temp2[i1] += str.charAt(i);
	             }
	            return temp2;
	        }
	      return null;
	}  //clean
	 
	  private boolean hasSuffix( String word, String suffix, NewString stem ) {

	     String tmp = "";

	     if ( word.length() <= suffix.length() )
	        return false;
	     if (suffix.length() > 1) 
	        if ( word.charAt( word.length()-2 ) != suffix.charAt( suffix.length()-2 ) )
	           return false;
	  
	     stem.str = "";

	     for ( int i=0; i<word.length()-suffix.length(); i++ )
	         stem.str += word.charAt( i );
	     tmp = stem.str;

	     for ( int i=0; i<suffix.length(); i++ )
	         tmp += suffix.charAt( i );

	     if ( tmp.compareTo( word ) == 0 )
	        return true;
	     else
	        return false;
	  }

	  private boolean vowel( char ch, char prev ) {
	     switch ( ch ) {
	        case 'a': case 'e': case 'i': case 'o': case 'u': 
	          return true;
	        case 'y': {

	          switch ( prev ) {
	            case 'a': case 'e': case 'i': case 'o': case 'u': 
	              return false;

	            default: 
	              return true;
	          }
	        }
	        
	        default : 
	          return false;
	     }
	  }

	  private int measure( String stem ) {
	    
	    int i=0, count = 0;
	    int length = stem.length();

	    while ( i < length ) {
	       for ( ; i < length ; i++ ) {
	           if ( i > 0 ) {
	              if ( vowel(stem.charAt(i),stem.charAt(i-1)) )
	                 break;
	           }
	           else {  
	              if ( vowel(stem.charAt(i),'a') )
	                break; 
	           }
	       }

	       for ( i++ ; i < length ; i++ ) {
	           if ( i > 0 ) {
	              if ( !vowel(stem.charAt(i),stem.charAt(i-1)) )
	                  break;
	              }
	           else {  
	              if ( !vowel(stem.charAt(i),'?') )
	                 break;
	           }
	       } 
	      if ( i < length ) {
	         count++;
	         i++;
	      }
	    } //while
	    
	    return(count);
	  }

	  private boolean containsVowel( String word ) {

	     for (int i=0 ; i < word.length(); i++ )
	         if ( i > 0 ) {
	            if ( vowel(word.charAt(i),word.charAt(i-1)) )
	               return true;
	         }
	         else {  
	            if ( vowel(word.charAt(0),'a') )
	               return true;
	         }
	        
	     return false;
	  }

	  private boolean cvc( String str ) {
	     int length=str.length();

	     if ( length < 3 )
	        return false;
	    
	     if ( (!vowel(str.charAt(length-1),str.charAt(length-2)) )
	        && (str.charAt(length-1) != 'w') && (str.charAt(length-1) != 'x') && (str.charAt(length-1) != 'y')
	        && (vowel(str.charAt(length-2),str.charAt(length-3))) ) {

	        if (length == 3) {
	           if (!vowel(str.charAt(0),'?')) 
	              return true;
	           else
	              return false;
	        }
	        else {
	           if (!vowel(str.charAt(length-3),str.charAt(length-4)) ) 
	              return true; 
	           else
	              return false;
	        } 
	     }   
	  
	     return false;
	  }

	  private String step1( String str ) {
	 
	     NewString stem = new NewString();

	     if ( str.charAt( str.length()-1 ) == 's' ) {
	        if ( (hasSuffix( str, "sses", stem )) || (hasSuffix( str, "ies", stem)) ){
	           String tmp = "";
	           for (int i=0; i<str.length()-2; i++)
	               tmp += str.charAt(i);
	           str = tmp;
	        }
	        else {
	           if ( ( str.length() == 1 ) && ( str.charAt(str.length()-1) == 's' ) ) {
	              str = "";
	              return str;
	           }
	           if ( str.charAt( str.length()-2 ) != 's' ) {
	              String tmp = "";
	              for (int i=0; i<str.length()-1; i++)
	                  tmp += str.charAt(i);
	              str = tmp;
	           }
	        }  
	     }

	     if ( hasSuffix( str,"eed",stem ) ) {
	           if ( measure( stem.str ) > 0 ) {
	              String tmp = "";
	              for (int i=0; i<str.length()-1; i++)
	                  tmp += str.charAt( i );
	              str = tmp;
	           }
	     }
	     else {  
	        if (  (hasSuffix( str,"ed",stem )) || (hasSuffix( str,"ing",stem )) ) { 
	           if (containsVowel( stem.str ))  {

	              String tmp = "";
	              for ( int i = 0; i < stem.str.length(); i++)
	                  tmp += str.charAt( i );
	              str = tmp;
	              if ( str.length() == 1 )
	                 return str;

	              if ( ( hasSuffix( str,"at",stem) ) || ( hasSuffix( str,"bl",stem ) ) || ( hasSuffix( str,"iz",stem) ) ) {
	                 str += "e";
	           
	              }
	              else {   
	                 int length = str.length(); 
	                 if ( (str.charAt(length-1) == str.charAt(length-2)) 
	                    && (str.charAt(length-1) != 'l') && (str.charAt(length-1) != 's') && (str.charAt(length-1) != 'z') ) {
	                     
	                    tmp = "";
	                    for (int i=0; i<str.length()-1; i++)
	                        tmp += str.charAt(i);
	                    str = tmp;
	                 }
	                 else
	                    if ( measure( str ) == 1 ) {
	                       if ( cvc(str) ) 
	                          str += "e";
	                    }
	              }
	           }
	        }
	     }

	     if ( hasSuffix(str,"y",stem) ) 
	        if ( containsVowel( stem.str ) ) {
	           String tmp = "";
	           for (int i=0; i<str.length()-1; i++ )
	               tmp += str.charAt(i);
	           str = tmp + "i";
	        }
	     return str;  
	  }

	  private String step2( String str ) {

	     String[][] suffixes = { { "ational", "ate" },
	                                    { "tional",  "tion" },
	                                    { "enci",    "ence" },
	                                    { "anci",    "ance" },
	                                    { "izer",    "ize" },
	                                    { "iser",    "ize" },
	                                    { "abli",    "able" },
	                                    { "alli",    "al" },
	                                    { "entli",   "ent" },
	                                    { "eli",     "e" },
	                                    { "ousli",   "ous" },
	                                    { "ization", "ize" },
	                                    { "isation", "ize" },
	                                    { "ation",   "ate" },
	                                    { "ator",    "ate" },
	                                    { "alism",   "al" },
	                                    { "iveness", "ive" },
	                                    { "fulness", "ful" },
	                                    { "ousness", "ous" },
	                                    { "aliti",   "al" },
	                                    { "iviti",   "ive" },
	                                    { "biliti",  "ble" }};
	     NewString stem = new NewString();

	     
	     for ( int index = 0 ; index < suffixes.length; index++ ) {
	         if ( hasSuffix ( str, suffixes[index][0], stem ) ) {
	            if ( measure ( stem.str ) > 0 ) {
	               str = stem.str + suffixes[index][1];
	               return str;
	            }
	         }
	     }

	     return str;
	  }

	  private String step3( String str ) {

	        String[][] suffixes = { { "icate", "ic" },
	                                       { "ative", "" },
	                                       { "alize", "al" },
	                                       { "alise", "al" },
	                                       { "iciti", "ic" },
	                                       { "ical",  "ic" },
	                                       { "ful",   "" },
	                                       { "ness",  "" }};
	        NewString stem = new NewString();

	        for ( int index = 0 ; index<suffixes.length; index++ ) {
	            if ( hasSuffix ( str, suffixes[index][0], stem ))
	               if ( measure ( stem.str ) > 0 ) {
	                  str = stem.str + suffixes[index][1];
	                  return str;
	               }
	        }
	        return str;
	  }

	  private String step4( String str ) {
	     String[] suffixes = { "al", "ance", "ence", "er", "ic", "able", "ible", "ant", "ement", "ment", "ent", "sion", "tion",
	                           "ou", "ism", "ate", "iti", "ous", "ive", "ize", "ise"};
	     
	     NewString stem = new NewString();
	        
	     for ( int index = 0 ; index<suffixes.length; index++ ) {
	         if ( hasSuffix ( str, suffixes[index], stem ) ) {
	            if ( measure ( stem.str ) > 1 ) {

	               str = stem.str;
	               return str;
	            }
	         }
	     }
	     return str;
	  }

	  private String step5( String str ) {

	     if ( str.charAt(str.length()-1) == 'e' ) { 
	        if ( measure(str) > 1 ) {/* measure(str)==measure(stem) if ends in vowel */
	           String tmp = "";
	           for ( int i=0; i<str.length()-1; i++ ) 
	               tmp += str.charAt( i );
	           str = tmp;
	        }
	        else
	           if ( measure(str) == 1 ) {
	              String stem = "";
	              for ( int i=0; i<str.length()-1; i++ ) 
	                  stem += str.charAt( i );

	              if ( !cvc(stem) )
	                 str = stem;
	           }
	     }
	     
	     if ( str.length() == 1 )
	        return str;
	     if ( (str.charAt(str.length()-1) == 'l') && (str.charAt(str.length()-2) == 'l') && (measure(str) > 1) )
	        if ( measure(str) > 1 ) {/* measure(str)==measure(stem) if ends in vowel */
	           String tmp = "";
	           for ( int i=0; i<str.length()-1; i++ ) 
	               tmp += str.charAt( i );
	           str = tmp;
	        } 
	     return str;
	  }

	  private String stripPrefixes ( String str) {

	     String[] prefixes = { "kilo", "micro", "milli", "intra", "ultra", "mega", "nano", "pico", "pseudo"};

	     int last = prefixes.length;
	     for ( int i=0 ; i<last; i++ ) {
	         if ( str.startsWith( prefixes[i] ) ) {
	            String temp = "";
	            for ( int j=0 ; j< str.length()-prefixes[i].length(); j++ )
	                temp += str.charAt( j+prefixes[i].length() );
	            return temp;
	         }
	     }
	     
	     return str;
	  }


	  private String stripSuffixes( String str ) {

	     str = step1( str );
	     if ( str.length() >= 1 ){
	        str = step2( str );
	     }
	     if ( str.length() >= 1 ){
	        str = step3( str );
	     }
	     if ( str.length() >= 1 ){
	        str = step4( str );
	     }
	     if ( str.length() >= 1 ){
	        str = step5( str );
	     }
	     return str; 
	  }


	  public String[] stripAffixes( String str ) {
	        str = str.toLowerCase();
	        String[] arr=new String[100];
	        try{
	            arr = Clean(str);
	            if(arr.length >0){
	                for(int i=0;i<arr.length;i++)
	                {
	                    if (( arr[i] != "" ) && (arr[i].length() > 2)) {
	                       arr[i] = stripPrefixes(arr[i]);
	                       if (arr[i] != "" ) {
	                          arr[i] = stripSuffixes(arr[i]);
	                        }   
	                    }
	                }
	                return arr;
	            } 
	        }     
	        catch(NullPointerException e)
	        {
	            //System.out.println("The exception raised is: "+e);
	        }
	      return null;
	  } //stripAffixes      
	} //class

	//*porter stemmer algorithm which takes a text file as input.For a pdf file, it does not work fine
	// * To change this license header, choose License Headers in Project Properties.
	// * To change this template file, choose Tools | Templates
	// * and open the template in the editor.
	// */
	//package pdftokens;
	//import java.io.*;
	//import java.io.FileInputStream;
	//import java.io.FileNotFoundException;
	//import java.io.IOException;
	//
	///**
	// *
	// * @author HP
	// */
	//public class Stemmer {
	///*
	//
	//   Porter stemmer in Java. The original paper is in
	//
//	       Porter, 1980, An algorithm for suffix stripping, Program, Vol. 14,
//	       no. 3, pp 130-137,
	//
	//   See also http://www.tartarus.org/~martin/PorterStemmer
	//
	//   History:
	//
	//   Release 1
	//
	//   Bug 1 (reported by Gonzalo Parra 16/10/99) fixed as marked below.
	//   The words 'aed', 'eed', 'oed' leave k at 'a' for step 3, and b[k-1]
	//   is then out outside the bounds of b.
	//
	//   Release 2
	//
	//   Similarly,
	//
	//   Bug 2 (reported by Steve Dyrdahl 22/2/00) fixed as marked below.
	//   'ion' by itself leaves j = -1 in the test for 'ion' in step 5, and
	//   b[j] is then outside the bounds of b.
	//
	//   Release 3
	//
	//   Considerably revised 4/9/00 in the light of many helpful suggestions
	//   from Brian Goetz of Quiotix Corporation (brian@quiotix.com).
	//
	//   Release 4
	//
	//*/
	//
	//
	//
	///**
	//  * Stemmer, implementing the Porter Stemming Algorithm
	//  *
	//  * The Stemmer class transforms a word into its root form.  The input
	//  * word can be provided a character at time (by calling add()), or at once
	//  * by calling one of the various stem(something) methods.
	//  */
	//  private char[] b;
	//   private int i,     /* offset into b */
//	               i_end, /* offset to end of stemmed word */
//	               j, k;
	//   private static final int INC = 50;
//	                     /* unit of size whereby b is increased */
	//   public Stemmer()
	//   {  b = new char[INC];
//	      i = 0;
//	      i_end = 0;
	//   }
	//
	//   /**
//	    * Add a character to the word being stemmed.  When you are finished
//	    * adding characters, you can call stem(void) to stem the word.
//	    */
	//
	//   public void add(char ch)
	//   {  if (i == b.length)
//	      {  char[] new_b = new char[i+INC];
//	         for (int c = 0; c < i; c++) new_b[c] = b[c];
//	         b = new_b;
//	      }
//	      b[i++] = ch;
	//   }
	//
	//
	//   /** Adds wLen characters to the word being stemmed contained in a portion
//	    * of a char[] array. This is like repeated calls of add(char ch), but
//	    * faster.
//	    */
	//
	//   public void add(char[] w, int wLen)
	//   {  if (i+wLen >= b.length)
//	      {  char[] new_b = new char[i+wLen+INC];
//	         for (int c = 0; c < i; c++) new_b[c] = b[c];
//	         b = new_b;
//	      }
//	      for (int c = 0; c < wLen; c++) b[i++] = w[c];
	//   }
	//
	//   /**
//	    * After a word has been stemmed, it can be retrieved by toString(),
//	    * or a reference to the internal buffer can be retrieved by getResultBuffer
//	    * and getResultLength (which is generally more efficient.)
//	     * @return 
//	    */
	//   public String toString() { return new String(b,0,i_end); }
	//
	//   /**
//	    * Returns the length of the word resulting from the stemming process.
//	    */
	//   public int getResultLength() { return i_end; }
	//
	//   /**
//	    * Returns a reference to a character buffer containing the results of
//	    * the stemming process.  You also need to consult getResultLength()
//	    * to determine the length of the result.
//	    */
	//   public char[] getResultBuffer() { return b; }
	//
	//   /* cons(i) is true <=> b[i] is a consonant. */
	//
	//   private final boolean cons(int i)
	//   {  switch (b[i])
//	      {  case 'a': case 'e': case 'i': case 'o': case 'u': return false;
//	         case 'y': return (i==0) ? true : !cons(i-1);
//	         default: return true;
//	      }
	//   }
	//
	//   /* m() measures the number of consonant sequences between 0 and j. if c is
//	      a consonant sequence and v a vowel sequence, and <..> indicates arbitrary
//	      presence,
	//
//	         <c><v>       gives 0
//	         <c>vc<v>     gives 1
//	         <c>vcvc<v>   gives 2
//	         <c>vcvcvc<v> gives 3
//	         ....
	//   */
	//
	//   private final int m()
	//   {  int n = 0;
//	      int i = 0;
//	      while(true)
//	      {  if (i > j) return n;
//	         if (! cons(i)) break; i++;
//	      }
//	      i++;
//	      while(true)
//	      {  while(true)
//	         {  if (i > j) return n;
//	               if (cons(i)) break;
//	               i++;
//	         }
//	         i++;
//	         n++;
//	         while(true)
//	         {  if (i > j) return n;
//	            if (! cons(i)) break;
//	            i++;
//	         }
//	         i++;
//	       }
	//   }
	//
	//   /* vowelinstem() is true <=> 0,...j contains a vowel */
	//
	//   private final boolean vowelinstem()
	//   {  int i; for (i = 0; i <= j; i++) if (! cons(i)) return true;
//	      return false;
	//   }
	//
	//   /* doublec(j) is true <=> j,(j-1) contain a double consonant. */
	//
	//   private final boolean doublec(int j)
	//   {  if (j < 1) return false;
//	      if (b[j] != b[j-1]) return false;
//	      return cons(j);
	//   }
	//
	//   /* cvc(i) is true <=> i-2,i-1,i has the form consonant - vowel - consonant
//	      and also if the second c is not w,x or y. this is used when trying to
//	      restore an e at the end of a short word. e.g.
	//
//	         cav(e), lov(e), hop(e), crim(e), but
//	         snow, box, tray.
	//
	//   */
	//
	//   private final boolean cvc(int i)
	//   {  if (i < 2 || !cons(i) || cons(i-1) || !cons(i-2)) return false;
//	      {  int ch = b[i];
//	         if (ch == 'w' || ch == 'x' || ch == 'y') return false;
//	      }
//	      return true;
	//   }
	//
	//   private final boolean ends(String s)
	//   {  int l = s.length();
//	      int o = k-l+1;
//	      if (o < 0) return false;
//	      for (int i = 0; i < l; i++) if (b[o+i] != s.charAt(i)) return false;
//	      j = k-l;
//	      return true;
	//   }
	//
	//   /* setto(s) sets (j+1),...k to the characters in the string s, readjusting
//	      k. */
	//
	//   private final void setto(String s)
	//   {  int l = s.length();
//	      int o = j+1;
//	      for (int i = 0; i < l; i++) b[o+i] = s.charAt(i);
//	      k = j+l;
	//   }
	//
	//   /* r(s) is used further down. */
	//
	//   private final void r(String s) { if (m() > 0) setto(s); }
	//
	//   /* step1() gets rid of plurals and -ed or -ing. e.g.
	//
//	          caresses  ->  caress
//	          ponies    ->  poni
//	          ties      ->  ti
//	          caress    ->  caress
//	          cats      ->  cat
	//
//	          feed      ->  feed
//	          agreed    ->  agree
//	          disabled  ->  disable
	//
//	          matting   ->  mat
//	          mating    ->  mate
//	          meeting   ->  meet
//	          milling   ->  mill
//	          messing   ->  mess
	//
//	          meetings  ->  meet
	//
	//   */
	//
	//   private final void step1()
	//   {  if (b[k] == 's')
//	      {  if (ends("sses")) k -= 2; else
//	         if (ends("ies")) setto("i"); else
//	         if (b[k-1] != 's') k--;
//	      }
//	      if (ends("eed")) { if (m() > 0) k--; } else
//	      if ((ends("ed") || ends("ing")) && vowelinstem())
//	      {  k = j;
//	         if (ends("at")) setto("ate"); else
//	         if (ends("bl")) setto("ble"); else
//	         if (ends("iz")) setto("ize"); else
//	         if (doublec(k))
//	         {  k--;
//	            {  int ch = b[k];
//	               if (ch == 'l' || ch == 's' || ch == 'z') k++;
//	            }
//	         }
//	         else if (m() == 1 && cvc(k)) setto("e");
//	     }
	//   }
	//
	//   /* step2() turns terminal y to i when there is another vowel in the stem. */
	//
	//   private final void step2() { if (ends("y") && vowelinstem()) b[k] = 'i'; }
	//
	//   /* step3() maps double suffices to single ones. so -ization ( = -ize plus
//	      -ation) maps to -ize etc. note that the string before the suffix must give
//	      m() > 0. */
	//
	//   private final void step3() { if (k == 0) return; /* For Bug 1 */ switch (b[k-1])
	//   {
//	       case 'a': if (ends("ational")) { r("ate"); break; }
//	                 if (ends("tional")) { r("tion"); break; }
//	                 break;
//	       case 'c': if (ends("enci")) { r("ence"); break; }
//	                 if (ends("anci")) { r("ance"); break; }
//	                 break;
//	       case 'e': if (ends("izer")) { r("ize"); break; }
//	                 break;
//	       case 'l': if (ends("bli")) { r("ble"); break; }
//	                 if (ends("alli")) { r("al"); break; }
//	                 if (ends("entli")) { r("ent"); break; }
//	                 if (ends("eli")) { r("e"); break; }
//	                 if (ends("ousli")) { r("ous"); break; }
//	                 break;
//	       case 'o': if (ends("ization")) { r("ize"); break; }
//	                 if (ends("ation")) { r("ate"); break; }
//	                 if (ends("ator")) { r("ate"); break; }
//	                 break;
//	       case 's': if (ends("alism")) { r("al"); break; }
//	                 if (ends("iveness")) { r("ive"); break; }
//	                 if (ends("fulness")) { r("ful"); break; }
//	                 if (ends("ousness")) { r("ous"); break; }
//	                 break;
//	       case 't': if (ends("aliti")) { r("al"); break; }
//	                 if (ends("iviti")) { r("ive"); break; }
//	                 if (ends("biliti")) { r("ble"); break; }
//	                 break;
//	       case 'g': if (ends("logi")) { r("log"); break; }
	//   } }
	//
	//   /* step4() deals with -ic-, -full, -ness etc. similar strategy to step3. */
	//
	//   private final void step4() { switch (b[k])
	//   {
//	       case 'e': if (ends("icate")) { r("ic"); break; }
//	                 if (ends("ative")) { r(""); break; }
//	                 if (ends("alize")) { r("al"); break; }
//	                 break;
//	       case 'i': if (ends("iciti")) { r("ic"); break; }
//	                 break;
//	       case 'l': if (ends("ical")) { r("ic"); break; }
//	                 if (ends("ful")) { r(""); break; }
//	                 break;
//	       case 's': if (ends("ness")) { r(""); break; }
//	                 break;
	//   } }
	//
	//   /* step5() takes off -ant, -ence etc., in context <c>vcvc<v>. */
	//
	//   private final void step5()
	//   {   if (k == 0) return; /* for Bug 1 */ switch (b[k-1])
//	       {  case 'a': if (ends("al")) break; return;
//	          case 'c': if (ends("ance")) break;
//	                    if (ends("ence")) break; return;
//	          case 'e': if (ends("er")) break; return;
//	          case 'i': if (ends("ic")) break; return;
//	          case 'l': if (ends("able")) break;
//	                    if (ends("ible")) break; return;
//	          case 'n': if (ends("ant")) break;
//	                    if (ends("ement")) break;
//	                    if (ends("ment")) break;
//	                    /* element etc. not stripped before the m */
//	                    if (ends("ent")) break; return;
//	          case 'o': if (ends("ion") && j >= 0 && (b[j] == 's' || b[j] == 't')) break;
//	                                    /* j >= 0 fixes Bug 2 */
//	                    if (ends("ou")) break; return;
//	                    /* takes care of -ous */
//	          case 's': if (ends("ism")) break; return;
//	          case 't': if (ends("ate")) break;
//	                    if (ends("iti")) break; return;
//	          case 'u': if (ends("ous")) break; return;
//	          case 'v': if (ends("ive")) break; return;
//	          case 'z': if (ends("ize")) break; return;
//	          default: return;
//	       }
//	       if (m() > 1) k = j;
	//   }
	//
	//   /* step6() removes a final -e if m() > 1. */
	//
	//   private final void step6()
	//   {  j = k;
//	      if (b[k] == 'e')
//	      {  int a = m();
//	         if (a > 1 || a == 1 && !cvc(k-1)) k--;
//	      }
//	      if (b[k] == 'l' && doublec(k) && m() > 1) k--;
	//   }
	//
	//   /** Stem the word placed into the Stemmer buffer through calls to add().
//	    * Returns true if the stemming process resulted in a word different
//	    * from the input.  You can retrieve the result with
//	    * getResultLength()/getResultBuffer() or toString().
//	    */
	//   public void stem()
	//   {  k = i - 1;
//	      if (k > 1) { step1(); step2(); step3(); step4(); step5(); step6(); }
//	      i_end = k+1; i = 0;
	//   }
	//
	//   /** Test program for demonstrating the Stemmer.  It reads text from a
//	    * a list of files, stems each word, and writes the result to standard
//	    * output. Note that the word stemmed is expected to be in lower case:
//	    * forcing lower case must be done outside the Stemmer class.
//	    * Usage: Stemmer file-name file-name ...
//	    */
	//   public void call(File args1)
	//   {
//	      char[] w = new char[501];
//	      Stemmer s = new Stemmer();
//	      //for (int i = 0; i < args1.length(); i++)
//	      try{
//	         FileInputStream in = new FileInputStream(args1);
//	         
//	         try{
//	             //System.out.println("in outer while loop:");
//	             while(true){ 
//	                 int ch = in.read();
//	                 //System.out.println("int(ch)= "+ch+"char(ch)= "+(char)ch);
//	              if (Character.isLetter((char) ch)){
//	                 int j = 0;
//	                 System.out.println("In inner while loop:");
//	                 while(true){
//	                     ch = Character.toLowerCase((char) ch);
//	                     //System.out.println("int(ch)= "+ch+"char(ch)"+(char)ch);
//	                    w[j] = (char) ch;
//	                    if (j < 500) j++;
//	                    ch = in.read();
//	                    if (!Character.isLetter((char) ch))
//	                    {
//	                       /* to test add(char ch) */
//	                       for (int c = 0; c < j; c++) s.add(w[c]);
	//
//	                       /* or, to test add(char[] w, int j) */
//	                       /* s.add(w, j); */
	//
//	                       s.stem();
//	                       {  String u;
	//
//	                          /* and now, to test toString() : */
//	                          u = s.toString();
	//
//	                          /* to test getResultBuffer(), getResultLength() : */
//	                          /* u = new String(s.getResultBuffer(), 0, s.getResultLength()); */
	//
//	                          System.out.println(u);
//	                       }
//	                       break;
//	                    }
//	                 }
//	              }
//	              if (ch < 0) break;
//	              System.out.print((char)ch);
//	           }
//	         }
//	         catch (IOException e)
//	         {
//	             System.out.println("error reading " + args1);
//	            //break;
//	         }
//	      }
//	      catch (FileNotFoundException e)
//	      {
//	          System.out.println("file " + args1 + " not found");
//	         //break;
//	      }
	//   }