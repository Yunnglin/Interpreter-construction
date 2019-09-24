package FrontEnd.editor;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Element;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;
import javax.swing.tree.DefaultMutableTreeNode;

public class TextEditor extends JFrame implements KeyListener, TreeSelectionListener, ActionListener, ChangeListener{
	JTextPane text; //定义编辑区
	JTextPane row;
	JScrollPane scroll;
	JScrollPane rowScroll;
	JPopupMenu menu;
	ContralPanel contralPanel;
	Font font;//当前字体
	ColorKeyWords h;
	JSplitPane jsp;
	FileList fileList;
	JTree tree;
	JPopupMenu jPopMenu;
	Clipboard clipboard;
	StringBuffer rowContent;
	BoundedRangeModel model;
	JPanel editArea;
	public TextEditor()
	{	
		super("代码编辑器");
		clipboard=getToolkit().getSystemClipboard();
		contralPanel=new ContralPanel(this);
		editArea=new JPanel(new BorderLayout());
		text=new JTextPane();
		scroll=new JScrollPane(text);
		row=new JTextPane();
		rowScroll=new JScrollPane(row);
		fileList=new FileList();
		jPopMenu=new JPopupMenu();
		h=new ColorKeyWords(text);
		tree=new JTree();
		font=new Font("楷体",Font.PLAIN,16);//默认字体
		setLayout(new BorderLayout());
		winInit();
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setMinimumSize(new Dimension(850, 700));
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
	}
	public void winInit()
	{
		contralPanel.addMenuBar();
		text.getDocument().addDocumentListener(h);
		JMenuItem cut=new JMenuItem("剪切(X)");
		JMenuItem copy=new JMenuItem("复制(C)");
		JMenuItem paste=new JMenuItem("粘贴(V)");
		jPopMenu.add(cut);
		jPopMenu.add(copy);
		jPopMenu.add(paste);
		cut.addActionListener(this);
		copy.addActionListener(this);
		paste.addActionListener(this);
		
		
		text.setFont(font);//设置字体
		text.add(jPopMenu);
		text.addMouseListener(new MyMouseListener());
		
		
		row.setFont(font);
		row.setText("1   ");
		row.setPreferredSize(new Dimension(56, getHeight()));
		row.setEditable(false);
		
		rowScroll=new JScrollPane(row);
		rowScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); 
		rowScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER); 
				
		editArea.add("West",rowScroll);
		editArea.add("Center",scroll);
		
		jsp=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,fileList.getTree(),editArea);
		jsp.setDividerSize(5);
		jsp.setDividerLocation(150);
		Container con=getContentPane();
		con.add("Center",jsp);
		text.addKeyListener(this);
		tree.addTreeSelectionListener(this);
		model = scroll.getVerticalScrollBar().getModel();
		model.addChangeListener(this);
	}
	public void setLeftSplitPane(FileList fl)
	{
		fileList=fl;
		tree=fileList.getTree();
		tree.addTreeSelectionListener(this);
		jsp.setLeftComponent(tree);
		jsp.setDividerLocation(150);
	}
	public void setFontStyle(Font font)
	{
		this.font=font;
		text.setFont(font);
		row.setFont(font);
		setRowContent();
	}
	
	public void setLanguage(String lan)
	{
		h.lan=lan;
	}
	
	public String getContent()
	{
		return text.getText();
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
	}
	@Override
	public void keyReleased(KeyEvent e) {
		StringBuffer s=new StringBuffer(text.getText());//获取当前文本内容
		if(e.getKeyCode()==9)//修改tab缩进值
		{
			int pos=text.getCaretPosition();
			StyledDocument doc = text.getStyledDocument();
			Style style = doc.addStyle("normalstyle", null);
				try {
					doc.remove(pos-1, 1);
					doc.insertString(pos-1, "   ", style);
				} catch (BadLocationException be) {
					be.printStackTrace();
				}
		}
	}
	@Override
	public void keyTyped(KeyEvent e) {
		char x=e.getKeyChar();//获取当前键盘输入符号
		String s=getContent().replaceAll("\n", "");//获取当前文本内容
		int pos=text.getCaretPosition();
		if(x=='\n')
		{
			setRowContent();
			tabTime();
			if(pos<s.length()&&s.charAt(pos)=='}')
			{
				StyledDocument doc = text.getStyledDocument();
				try {
					doc.remove(pos+3, 3);
				} catch (BadLocationException e1) {
					e1.printStackTrace();
				}
			}
			return ;
		}
		if(e.getKeyChar()=='}'&&s.charAt(pos-1)==' ')//当未回车插入}时不进行此操作
		{
			StyledDocument doc = text.getStyledDocument();
			try {
				doc.remove(pos-3, 3);
			} catch (BadLocationException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	public void setRowContent()
	{
		rowContent=new StringBuffer();
		Element map = text.getDocument().getDefaultRootElement();
        int count=map.getElementCount();
		row.setText("");
		for(int i=0;i<count;i++)
		{
			rowContent.append((i+1)+"\n");
		}
		row.setText(rowContent.toString());
		
	}
	
	public void tabTime() //缩进处理
	{
		StyledDocument doc = text.getStyledDocument();
		Style style = doc.addStyle("normalstyle", null);
		int tabNum=0;
		String sx=getContent().replaceAll("\n", "");
		int pos=text.getCaretPosition();
		for(int i=0;i<pos;i++)
		{
			if(sx.charAt(i)=='{')
			{
				tabNum++;
			}
			if(sx.charAt(i)=='}')
			{
				tabNum--;
			}
		}
		for(int i=0;i<tabNum;i++)
		{
			try {
				doc.insertString(text.getCaretPosition(), "   ", style);
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void valueChanged(TreeSelectionEvent e) {
		if(e.getSource()==tree)
		{
			DefaultMutableTreeNode node=(DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
			   if(node.isLeaf())
			   {
				   String s=node.toString(); 
				   contralPanel.fileOperation.treeOpen(s);
				  
			   }
		}
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		String name= e.getActionCommand();
		if(name.equals("剪切(X)"))
		{
			String temp=text.getSelectedText();
			StringSelection content = new StringSelection(temp);
			clipboard.setContents(content,null);
			int x=text.getSelectionStart();
			int y=text.getSelectionEnd();
			StyledDocument doc = text.getStyledDocument();
				try {
					doc.remove(x,y-x);
				} catch (BadLocationException be) {
					be.printStackTrace();
				}
		}
		if(name.equals("粘贴(V)"))
		{
			Transferable contents = clipboard.getContents(this);
            DataFlavor flavor = DataFlavor.stringFlavor;
            String str="";
            if(contents.isDataFlavorSupported(flavor))
                try{
                    str = (String)contents.getTransferData(flavor);
                }catch(Exception ee){}    

			
			int pos=text.getCaretPosition();
			StyledDocument doc = text.getStyledDocument();
			Style style = doc.addStyle("normalstyle", null);
				try {
					doc.insertString(pos, str, style);
				} catch (BadLocationException be) {
					be.printStackTrace();
				}
		}
		if(name.equals("复制(C)"))
		{
			String temp=text.getSelectedText();
			StringSelection content = new StringSelection(temp);
			clipboard.setContents(content,null);
		}
	}
	
	private class MyMouseListener extends MouseAdapter
	{
		public void mouseClicked(MouseEvent e) {
			if (e.getButton() == MouseEvent.BUTTON3) {
			     jPopMenu.show(text, e.getX(), e.getY());
			    }
		}
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		if(e.getSource()==model)
		{
			JScrollBar sBar = scroll.getVerticalScrollBar(); 
			int x=sBar.getValue();
			JScrollBar sBar2 = rowScroll.getVerticalScrollBar(); 
			sBar2.setValue(x);
			rowScroll.setVerticalScrollBar(sBar2);
		}
	}
}
