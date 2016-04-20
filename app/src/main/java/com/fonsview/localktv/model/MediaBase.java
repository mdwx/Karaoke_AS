package com.fonsview.localktv.model;



/** 
 * @author Vince  E-mail: xhys01@163.com
 * @version 创建时间：2015-5-27 下午1:35:23 
 * 类说明 
 */
public class MediaBase extends ModelBase implements Comparable<MediaBase>{
	
		private StringBuffer title;
		private StringBuffer url;
		private StringBuffer Keyword;
		private StringBuffer Singer;
		private StringBuffer Number;
		private StringBuffer language;
		private StringBuffer style;
		
		
	public MediaBase(){}	
		
	public MediaBase(StringBuffer title, StringBuffer url) {
		// TODO Auto-generated constructor stub
		this.title = title;
		this.url = url;
	}
	public MediaBase(String title, String url) {
		// TODO Auto-generated constructor stub
		this.title = new StringBuffer(title);
		this.url = new StringBuffer(url);
	}	
	
	
	public void SetMember(MediaBase other){
		
		if(other.getId() != 0)
		{
			super.setId(other.getId());
		}
		if(other.getLanguage() != null)
		{
			setLanguage(other.getLanguage());
		}
		if(other.getNumber() != null)
		{
			setLanguage(other.getNumber());
		}
		if(other.getSinger() != null)
		{
			setLanguage(other.getSinger());
		}
		if(other.getStyle() != null)
		{
			setLanguage(other.getStyle());
		}
		
	}
	
	
	@Override
	public void SetMember(String Member,String Value){
		
		if(Member.equalsIgnoreCase("title"))
		{
			setTitle(Value);
		}
		else if(Member.equalsIgnoreCase("url"))
		{
			setUrl(Value);
		}
		else if(Member.equalsIgnoreCase("Keyword"))
		{
			setKeyword(Value);
		}
		else if(Member.equalsIgnoreCase("Singer"))
		{
			setSinger(Value);
		}
		else if(Member.equalsIgnoreCase("Number"))
		{
			setNumber(Value);
		}
		else if(Member.equalsIgnoreCase("language"))
		{
			setLanguage(Value);
		}
		else if(Member.equalsIgnoreCase("style"))
		{
			setStyle(Value);
		}
		else
		{
			super.SetMember(Member,Value);
		}

	}
	
	@Override
	public String getMember(String Member)
	{
			
		if(Member.equalsIgnoreCase("title"))
		{
			getTitle().toString();
		}
		else if(Member.equalsIgnoreCase("url"))
		{
			getUrl().toString();
		}
		else if(Member.equalsIgnoreCase("Keyword"))
		{
			getKeyword().toString();
		}
		else if(Member.equalsIgnoreCase("Singer"))
		{
			getSinger().toString();
		}
		else if(Member.equalsIgnoreCase("Number"))
		{
			getNumber().toString();
		}
		else if(Member.equalsIgnoreCase("language"))
		{
			getLanguage().toString();
		}
		else if(Member.equalsIgnoreCase("style"))
		{
			getStyle().toString();
		}
		else
		{
			super.getMember(Member);
		}
		
		return null;		
	}

	
  @Override  
    public int compareTo(MediaBase other) {  	   
		   if(this.getKeyword() == null || other.getKeyword() == null)
		   {
			   return 0;
		   }
        return this.getKeyword().toString().compareToIgnoreCase(other.getKeyword().toString());  
    }

   @Override
	public String toString() {//用于listview显示的数据
		 return title.toString();
	}


public StringBuffer getTitle() {
	return title;
}

public void setTitle(StringBuffer title) {
	this.title = title;
}
public void setTitle(String title) {
	this.title = new StringBuffer(title);
}

public StringBuffer getUrl() {
	return url;
}

public void setUrl(StringBuffer url) {
	this.url = url;
}
public void setUrl(String url) {
	this.url = new StringBuffer(url);
}

public StringBuffer getKeyword(){
	return Keyword;
}
public void setKeyword(StringBuffer Keyword) {
	this.Keyword = Keyword;
}
public void setKeyword(String Keyword) {
	this.Keyword = new StringBuffer(Keyword);
}

public StringBuffer getLanguage() {
	return language;
}

public void setLanguage(StringBuffer language) {
	this.language = language;
}
public void setLanguage(String language) {
	this.language = new StringBuffer(language);
}

public StringBuffer getStyle() {
	return style;
}

public void setStyle(StringBuffer style) {
	this.style = style;
}
public void setStyle(String style) {
	this.style = new StringBuffer(style);
}

public StringBuffer getSinger() {
	return Singer;
}

public void setSinger(StringBuffer singer) {
	Singer = singer;
}
public void setSinger(String singer) {
	Singer = new StringBuffer(singer);
}

public void setNumber(StringBuffer number) {
	Number = number;
}
public void setNumber(String number) {
	Number = new StringBuffer(number);
}
public StringBuffer getNumber() {
	return Number;
}
}
