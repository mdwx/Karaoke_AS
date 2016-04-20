package com.fonsview.localktv.tool;

import java.util.HashSet;
import java.util.Set;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

/*******************************************************************************
 * pinyin4j is a plug-in, you can kind of Chinese characters into phonetic.Multi-tone character,Tone
 * Detailed view http://pinyin4j.sourceforge.net/
 * 
 * @author Administrator
 * ClassName: Pinyin4jUtil
 * Description: TODO
 * @author wang_china@foxmail.com
 */
public class SpellUtil {

	/***************************************************************************
	 * ��ȡ���ĺ���ƴ�� Ĭ�����
	 * Name: Pinyin4jUtil.java
	 * Description: TODO
	 * @author: wang_chian@foxmail.com
	 * @version: Jan 13, 2012 9:54:01 AM
	 * @param chinese
	 * @return
	 */
	public static String getsPell(String chinese) {
		return getsPellZh_CN(makeStringByStringSet(chinese));
	}

	/***************************************************************************
	 * ƴ����д���
	 * 
	 * Name: Pinyin4jUtil.java
	 * Description: TODO
	 * @author: wang_chian@foxmail.com
	 * @version: Jan 13, 2012 9:58:45 AM
	 * @param chinese
	 * @return
	 */
	public static String getSpellToUpperCase(String chinese) {
		return getsPellZh_CN(makeStringByStringSet(chinese)).toUpperCase();
	}

	/***************************************************************************
	 * ƴ��Сд���
	 * 
	 * Name: Pinyin4jUtil.java
	 * Description: TODO
	 * @author: wang_chian@foxmail.com
	 * @version: Jan 13, 2012 9:58:45 AM
	 * @param chinese
	 * @return
	 */
	public static String getSpellToLowerCase(String chinese) {
		return getsPellZh_CN(makeStringByStringSet(chinese)).toLowerCase();
	}

	/***************************************************************************
	 * ����ĸ��д���
	 * 
	 * Name: Pinyin4jUtil.java
	 * Description: TODO
	 * @author: wang_chian@foxmail.com
	 * @version: Jan 13, 2012 10:00:54 AM
	 * @param chinese
	 * @return
	 */
	public static String getSpellFirstToUpperCase(String chinese) {
		return capitalize(getsPell(chinese));
	}

	/***************************************************************************
	 * ƴ����ƴ�����д
	 * 
	 * Name: Pinyin4jUtil.java
	 * Description: TODO
	 * @author: wang_chian@foxmail.com
	 * @version: Jan 13, 2012 11:08:15 AM
	 * @param chinese
	 * @return
	 */
	public static String getSpellAcronym(String chinese) {
		return getsPellConvertAcronym(capitalize(getsPell(chinese)));
	}
	/***************************************************************************
	 * ƴ����ƴ���Сд
	 * 
	 * Name: Pinyin4jUtil.java
	 * Description: TODO
	 * @author: wang_chian@foxmail.com
	 * @version: Jan 13, 2012 11:08:15 AM
	 * @param chinese
	 * @return
	 */
	public static String getSpellAcronymToLowerCase(String chinese) {
		return getsPellConvertAcronym(capitalize(getsPell(chinese))).toLowerCase();
	}

	/***************************************************************************
	 * �ַ���ת��
	 * 
	 * Name: Pinyin4jUtil.java
	 * Description: TODO
	 * @author: wang_chian@foxmail.com
	 * @version: Jan 13, 2012 9:34:11 AM
	 * @param chinese
	 *            ���ĺ���	
	 */
	public static Set<String> makeStringByStringSet(String chinese) {
		char[] chars = chinese.toCharArray();
		if (chinese != null && !chinese.trim().equalsIgnoreCase("")) {
			char[] srcChar = chinese.toCharArray();
			String[][] temp = new String[chinese.length()][];
			for (int i = 0; i < srcChar.length; i++) {
				char c = srcChar[i];

				// �����Ļ���a-z����A-Zת��ƴ��
				if (String.valueOf(c).matches("[\\u4E00-\\u9FA5]+")) {

					try {
						temp[i] = PinyinHelper.toHanyuPinyinStringArray(
								chars[i], getDefaultOutputFormat());

					} catch (BadHanyuPinyinOutputFormatCombination e) {
						e.printStackTrace();
					}
				} else if ( c >= 33 &&  c <= 126) {
					temp[i] = new String[] { String.valueOf(srcChar[i]) };
				} else {
					temp[i] = new String[] { "#" };
				}
			}
			String[] pingyinArray = Exchange(temp);
			Set<String> zhongWenPinYin = new HashSet<String>();
			for (int i = 0; i < pingyinArray.length; i++) {
				zhongWenPinYin.add(pingyinArray[i]);
			}
			return zhongWenPinYin;
		}
		return null;
	}

	/***************************************************************************
	 * Default Format Ĭ�������ʽ
	 * 
	 * Name: Pinyin4jUtil.java
	 * Description: TODO
	 * @author: wang_chian@foxmail.com
	 * @version: Jan 13, 2012 9:35:51 AM
	 * @return
	 */
	public static HanyuPinyinOutputFormat getDefaultOutputFormat() {
		HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
		format.setCaseType(HanyuPinyinCaseType.LOWERCASE);// Сд
		format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);// û����������
		format.setVCharType(HanyuPinyinVCharType.WITH_U_AND_COLON);// u��ʾ
		return format;
	}

	/***************************************************************************
	 * 
	 * Name: Pinyin4jUtil.java
	 * Description: TODO
	 * @author: wang_chian@foxmail.com
	 * @version: Jan 13, 2012 9:39:54 AM
	 * @param strJaggedArray
	 * @return
	 */
	public static String[] Exchange(String[][] strJaggedArray) {
		String[][] temp = DoExchange(strJaggedArray);
		return temp[0];
	}

	/***************************************************************************
	 * 
	 * Name: Pinyin4jUtil.java
	 * Description: TODO
	 * @author: wang_chian@foxmail.com
	 * @version: Jan 13, 2012 9:39:47 AM
	 * @param strJaggedArray
	 * @return
	 */
	private static String[][] DoExchange(String[][] strJaggedArray) {
		int len = strJaggedArray.length;
		if (len >= 2) {
			int len1 = strJaggedArray[0].length;
			int len2 = strJaggedArray[1].length;
			int newlen = len1 * len2;
			String[] temp = new String[newlen];
			int Index = 0;
			for (int i = 0; i < len1; i++) {
				for (int j = 0; j < len2; j++) {
					temp[Index] = capitalize(strJaggedArray[0][i])
							+ capitalize(strJaggedArray[1][j]);
					Index++;
				}
			}
			String[][] newArray = new String[len - 1][];
			for (int i = 2; i < len; i++) {
				newArray[i - 1] = strJaggedArray[i];
			}
			newArray[0] = temp;
			return DoExchange(newArray);
		} else {
			return strJaggedArray;
		}
	}

	/***************************************************************************
	 * ����ĸ��д
	 * 
	 * Name: Pinyin4jUtil.java
	 * Description: TODO
	 * @author: wang_chian@foxmail.com
	 * @version: Jan 13, 2012 9:36:18 AM
	 * @param s
	 * @return
	 */
	public static String capitalize(String s) {
		char ch[];
		ch = s.toCharArray();
		if (ch[0] >= 'a' && ch[0] <= 'z') {
			ch[0] = (char) (ch[0] - ('a'-'A'));
		}
		String newString = new String(ch);
		return newString;
	}

	/***************************************************************************
	 * �ַ�������ת���ַ���(���ŷָ�)
	 * 
	 * Name: Pinyin4jUtil.java
	 * Description: TODO
	 * @author: wang_chian@foxmail.com
	 * @version: Jan 13, 2012 9:37:57 AM
	 * @param stringSet
	 * @return
	 */
	public static String getsPellZh_CN(Set<String> stringSet) {
		StringBuilder str = new StringBuilder();
		int i = 0;
		for (String s : stringSet) {
			if (i == stringSet.size() - 1) {
				str.append(s);
			} else {
				str.append(s + ",");
			}
			i++;
		}
		return str.toString();
	}

	/***************************************************************************
	 * ��ȡÿ��ƴ���ļ��
	 * 
	 * Name: Pinyin4jUtil.java
	 * Description: TODO
	 * @author: wang_chian@foxmail.com
	 * @version: Jan 13, 2012 11:05:58 AM
	 * @param chinese
	 * @return
	 */
	public static String getsPellConvertAcronym(String chinese) {
		String[] strArray = chinese.split(",");
		String strChar = "";
		StringBuffer strBuff = new StringBuffer();
		
		for (String str : strArray) {
			char arr[] = str.toCharArray(); // ���ַ���ת����char������
			
			for (int i = 0; i < arr.length; i++) {
				if (arr[i] >= 'A' && arr[i] <= 'Z') { // �ж��Ƿ��Ǵ�д��ĸ
					strChar += new String(arr[i] + "");
				}			
			}
			if (strBuff.length() == 0) {
				strBuff.append(strChar);
			} else if(strBuff.indexOf(strChar) == -1) {
				strBuff.append("," + strChar);
			}
			strChar = "";
						
		}
		return strBuff.toString();
	}

	/***************************************************************************
	 * Test
	 * 
	 * Name: Pinyin4jUtil.java
	 * Description: TODO
	 * @author: wang_chian@foxmail.com
	 * @version: Jan 13, 2012 9:49:27 AM
	 * @param args
	 */
//	public static void main(String[] args) {
//		String str = "����atr������";
//		System.out.println("Сд�����" + getSpellToLowerCase(str));
//		System.out.println("��д�����" + getSpellToUpperCase(str));
//		System.out.println("����ĸ��д�����" + getSpellFirstToUpperCase(str));
//		System.out.println("��ƴ��д�����" + getSpellAcronym(str));
//		System.out.println("��ƴСд�����" + getSpellAcronymToLowerCase(str));
//	}
}
