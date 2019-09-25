package FrontEnd.editor;

public class KeyWord {
	private String[] javaKeyWords;
	private String[] cKeyWords;
	private String[] htmlKeyWords;
	public KeyWord()
	{
		javaKeyWords=new String[]{"boolean","byte","char","double","float","int","long","short","void",
								  "new","super","this","instanceof","null","if","else","switch","case",
								  "default","do","while","for","break","continue","return","try","catch",
								  "finally","throw","assert","synchronized","abstract","final","private",
								  "protected","public","static","class","extends","interface","implements",
								  "import","package","native","transient","volatile","true","false","String",
								  "StringBuffer","StringBulider","import","Vector","Stack","HashSet","TreeSet",
								  "ArrayList","LinkList","Map","Date","Calendar",
								};
		cKeyWords=new String[]{"include","char","double","float","int","long","short","void","new","this","NULL",
								"if","else","switch","case","default","do","while","for","break","continue",
								"return","try","catch","private","protected","public","static","class","define",
								"const","using","namespace","string"
								};
		htmlKeyWords=new String[]{"html","head","body","title","base","META","link","ISINDEX","style","pre","h1",
									"h2","h3","h4","h5","h6","h7","b","i","tt","cite","em","strong","font","p",
									"br","blockquote","dl","dt","dd","ol","li","ul","div","img","hr","table",
									"tr","td","th","frameset","noframes","frame","form","select","option",
									"textarea","input","PRE","TT","ADDRESS","SAMP","DL"};
	}
	public boolean isKeyWord(String word,String lan)
	{
		if(lan.equals("java"))
		{
			for(int i=0;i<javaKeyWords.length;i++)
			{
				if(javaKeyWords[i].equals(word))
				{
					return true;
				}
			}
		}
		if(lan.equals("C++"))
		{
			
			for(int i=0;i<cKeyWords.length;i++)
			{
				if(cKeyWords[i].equals(word))
				{
					return true;
				}
			}
		}
		if(lan.equals("html"))
		{
			
			for(int i=0;i<htmlKeyWords.length;i++)
			{
				if(htmlKeyWords[i].equals(word))
				{
					return true;
				}
			}
		}
		return false;
	}
}
