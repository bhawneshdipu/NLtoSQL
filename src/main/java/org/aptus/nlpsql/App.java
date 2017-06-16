package org.aptus.nlpsql;
import java.util.*;
import java.io.*;
import java.util.regex.*;

import opennlp.tools.cmdline.PerformanceMonitor;
import opennlp.tools.cmdline.postag.POSModelLoader;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSSample;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.WhitespaceTokenizer;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;




import opennlp.tools.postag.POSModel; 
import opennlp.tools.postag.POSSample; 
import opennlp.tools.postag.POSTaggerME; 
import opennlp.tools.tokenize.WhitespaceTokenizer;  


/**
 * Hello world!
 *
 */
class Pair{
	public String first,second;
	Pair(){
		first="";
		second="";
		
	}
	Pair(String a,String b){
		first=a;
		second=b;
	}
}
public class App 
{
    public static void main( String[] args ) throws IOException
    {
        System.out.println( "Hello NLP!" );
        Scanner in=new Scanner(System.in);
        String input=in.nextLine();
        //input=input.toLowerCase().replaceAll("[^a-z0-9 ]", " ");;
        Pattern insidequotes=Pattern.compile("\"(.*?)\"");
        Matcher m = insidequotes.matcher(input);

        ArrayList<String> stringcnst=new ArrayList<String>();
        while (m.find()) {
        	
            String s = m.group(1);
            System.out.println("hellooo-->"+s);
            stringcnst.add(s);
            // s now contains "BAR"
        }
       String[] arr;
        arr=input.split("\"");
        String[] temp;
        ArrayList <String> al=new ArrayList<String>();
        for(String a:arr){
        	System.out.println(a);
        	temp=a.split(" ");
        	for(String t:temp){
        		al.add(t);
        	}
        }
        
        for (String a:al){
        	System.out.println(a);
        	
        }
        ArrayList<String> hm=new ArrayList<String>();
        try {

        	BufferedReader br = new BufferedReader(new FileReader("/home/aptus-hp/workspace/nlpsql/src/main/java/org/aptus/nlpsql/stop-words.txt"));
        	
        	String str;
        	Integer i=new Integer(0);
        	while((str=br.readLine())!=null){
        		hm.add(str);
        		//System.out.println(str);
        	}
        }catch(Exception e){
        	System.out.println("Exception file reading"+e.toString());
        }
        //ArrayList <String> al=new  ArrayList<String> ();
        for (String a:arr){
        	if(hm.contains(a)){
        		al.remove(a);
        	}
        }
      //Loading Parts of speech-maxent model 
        InputStream inputStream = new FileInputStream("/home/aptus-hp/workspace/nlpsql/src/main/java/org/aptus/nlpsql/en-pos-maxent.bin"); 
        POSModel model = new POSModel(inputStream); 
      //Instantiating POSTaggerME class 
        POSTaggerME tagger = new POSTaggerME(model);
        
      //Generating tags 
        String[] tokens= al.toArray(new String[al.size()]);
        String[] tags = tagger.tag(tokens); 
      //Instantiating the POSSample class 
        POSSample sample = new POSSample(tokens, tags); 
        String result = sample.toString();
        String[] res=result.split(" ");
        ArrayList <Pair> pos=new ArrayList<Pair> ();
        for (String s:res){
        	String[] s_temp=s.split("_");
        	Pair p=new Pair(s_temp[0],s_temp[1]);
        	System.out.println(s_temp[0]+" "+s_temp[1]);
        	pos.add(p);
        	
        }
        
        for (Pair p :pos){
        	System.out.println(p.first+"--->"+p.second);
        }
      
      
        
       ArrayList <String> noun=new ArrayList<String>();
        try {

        	BufferedReader br = new BufferedReader(new FileReader("/home/aptus-hp/workspace/nlpsql/src/main/java/org/aptus/nlpsql/columns.txt"));
        	
        	String str;
        	Integer i=new Integer(0);
        	while((str=br.readLine())!=null){
        		noun.add(str);
        		//System.out.println(str);
        	}
        }catch(Exception e){
        	System.out.println("Exception file reading noun"+e.toString());
        }
        
        ArrayList<String> verb=new ArrayList<String>();
        try {

        	BufferedReader br = new BufferedReader(new FileReader("/home/aptus-hp/workspace/nlpsql/src/main/java/org/aptus/nlpsql/verb_list.txt"));
        	
        	String str;
        	Integer i=new Integer(0);
        	while((str=br.readLine())!=null){
        		verb.add(str);
        		//System.out.println(str);
        	}
        }catch(Exception e){
        	System.out.println("Exception file reading"+e.toString());
        }
        
        
        for(String s:noun){
        	System.out.println("columns-->"+s);
        }
        for(String s: verb){
        	System.out.println("verb-->"+s);
        }
        ArrayList <String> listcol=new ArrayList<String>();
        ArrayList <String> listverb=new ArrayList<String>();
        
        ArrayList <Integer>values=new ArrayList<Integer>();
        boolean verbcome=false;
        String lastcol="";
        ArrayList <String> otherwords=new ArrayList<String>();
        for (Pair p :pos){
        	if(noun.contains(p.first)){
        		System.out.println("Columns->"+p.first);
        		if(!verbcome){
        		listcol.add(p.first);
        		
        		}
        		lastcol=p.first;
        	}else if(verb.contains(p.first)){
        		System.out.println("Verb:"+p.first);
        		listverb.add(p.first);
        		verbcome=true;
        	}else if(p.second.equals("CD")){
        		System.out.println("Integers:"+p.first);
        		values.add(Integer.parseInt(p.first));
        	}else{
        		System.out.println(p.first+"-*->"+p.second);
        		if(verbcome)
        		otherwords.add(p.first);
        	}
        }
        
        String query="SELECT ";
        int flag=0;
        for (String a:listcol){
        	if(flag==1)
        		query+=", ";
        	query+=a+" ";
        	flag=1;
        	
        }
        
        System.out.println(query);
        
        
        if(listverb.size()>0 && values.size()>0 && listverb.get(0).equals("between"))
        query+="FROM table_name WHERE "+lastcol+" "+listverb.get(0)+" "+values.get(0)+" and  "+values.get(1);
        else if(listverb.size()>0 && values.size()>0)
        query+="FROM table_name WHERE "+lastcol+" "+listverb.get(0)+" "+values.get(0);
        else if(listverb.size()>0){
        	if(!listverb.get(0).equals("is"))
        		query+=" FROM table_name WHERE "+lastcol+" "+listverb.get(0)+" ";
        	else
        		query+=" FROM table_name WHERE "+lastcol+" "+"equals"+" ";
        	
        }
        
        
        System.out.println(query);
        for (String a: stringcnst){
        	query+="\""+a+"\"";
        }
        System.out.println(query);
       
        
    }
}
