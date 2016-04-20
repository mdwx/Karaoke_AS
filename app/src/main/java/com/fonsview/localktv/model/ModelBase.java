package com.fonsview.localktv.model;


/** 
 * @author Vince  E-mail: xhys01@163.com
 * @version 创建时间：2015-6-9 上午11:55:29 
 * 类说明 
 */
public class ModelBase {


	private int id;
	
	public ModelBase() {
		// TODO Auto-generated constructor stub
	}
	
	public void  SetMember(String Member,String Value){
		
		if(Member.equals("id"))
		{
			setId(Integer.parseInt(Value.toString()));			
	    }	
	}
	
	public String getMember(String Member)
	{
		if(Member.equals("id"))
		{
			return String.valueOf(getId());
		}
		return null;
	}	
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	@Override
	public String toString() {
		return "ModelBase [id=" + id + "]";
	}
}
