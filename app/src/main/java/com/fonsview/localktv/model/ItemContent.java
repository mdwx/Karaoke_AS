package com.fonsview.localktv.model;

/** 
 * @author Vince  E-mail: xhys01@163.com
 * @version ����ʱ�䣺2015-5-27 ����1:35:23 
 * ��˵�� 
 */
public class ItemContent
{
	public Integer id;
	public String title;

	public ItemContent(Integer id, String title)
	{
		this.id = id;
		this.title = title;			
	}

	@Override
	public String toString()
	{
		return title;
	}
}
