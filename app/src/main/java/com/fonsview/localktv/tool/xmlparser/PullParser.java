package com.fonsview.localktv.tool.xmlparser;

import java.io.InputStream;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import android.util.Xml;

import com.fonsview.localktv.model.Factory;
import com.fonsview.localktv.model.ModelBase;

public class PullParser<models extends ModelBase> {	
	
	private String regEx = "[A-z]*$";
	private Matcher m;
	private Pattern p = Pattern.compile(regEx); 	
	private String type;
	private String root;
	private String node;
	
	Field[] fields;		
    String Tem;
    

	public PullParser(){
	}
	public PullParser(String Type){
		this.type = Type;
		root = "Root";
		node = "Node";
	}
	public PullParser(String Type,String root, String node){
		this.type = Type;
		this.root = root;
		this.node = node;
	}

	@SuppressWarnings("unchecked")
	public List<models> parse(InputStream is) throws Exception {
		
		List<models> List_T = null;
		models Node_T= null;		
		XmlPullParser parser = Xml.newPullParser();	//由android.util.Xml创建一个XmlPullParser实例
    	parser.setInput(is, "UTF-8");				//设置输入流 并指明编码方式
    	
		int eventType = parser.getEventType();		
	
		Node_T = (models)Factory.createObj(type);
		fields = Node_T.getClass().getDeclaredFields();
		
		 
	    while (eventType != XmlPullParser.END_DOCUMENT) {
	   
			switch (eventType) {
			case XmlPullParser.START_DOCUMENT:
				if(List_T == null)
				{
					List_T = new ArrayList<models>();		
				}
			
				break;
				
			case XmlPullParser.START_TAG:
				
				if(parser.getName().equals(node))
				{					
					Node_T = (models)Factory.createObj(type);
				}
				else if(!parser.getName().equals("Root"))
				{
					if(Node_T == null)
					{
						Node_T = (models)Factory.createObj(type);
					}
					String str = parser.getName();
					 eventType = parser.next();					 
					 Node_T.SetMember(str,parser.getText());
					
				}					
				break;
			case XmlPullParser.END_TAG:
				
				if(parser.getName().equals(node))
				{		
					List_T.add(Node_T);
					Node_T = null;
				}
				break;
			}
			eventType = parser.next();
		}
		return List_T;
	}
    
	
	public String serialize(List<models> List_T) throws Exception {

		if(List_T == null || List_T.size() == 0)
		{			
			return null;
		}
    	XmlSerializer serializer = Xml.newSerializer();	//由android.util.Xml创建一个XmlSerializer实例
    	StringWriter writer = new StringWriter();
    	serializer.setOutput(writer);	//设置输出方向为writer
    	models Node_T= null;
    	List<String> Str_list = new ArrayList<String>();
    	
       	
		serializer.startDocument("UTF-8", true);		
	
		serializer.startTag("", root);

	
	    Node_T = List_T.get(0);
		fields = Node_T.getClass().getDeclaredFields();
		
		Str_list.add(new String("id"));
		
		for (int i=0; i<fields.length; i++)
		{			
			 m = p.matcher(fields[i].toString());  
			 if(m.find())
			  {				
				 Str_list.add(m.group(0)); 					  
			  }
		}
		
			
		for (int i=0; i<List_T.size(); i++) {
		
			Node_T = List_T.get(i);		
		
			if(Node_T != null)
			{
				serializer.startTag("", node);
			
				for (String str : Str_list) {				
								
					if(str.equals("id")){
						serializer.attribute("", "id", Node_T.getMember(str) + "");
					}else{
						serializer.startTag("", str);
						serializer.text(Node_T.getMember(str));						
						serializer.endTag("", str);
					}	
				}
				
				serializer.endTag("", node);	
			}
			Node_T = null;			
		}
		

		serializer.endTag("", root);

		serializer.endDocument();
		
		return writer.toString();
    }
	
	
	public String getRoot() {
		return root;
	}
	public void setRoot(String root) {
		this.root = root;
	}
	public String getNode() {
		return node;
	}
	public void setNode(String node) {
		this.node = node;
	}
	
}
