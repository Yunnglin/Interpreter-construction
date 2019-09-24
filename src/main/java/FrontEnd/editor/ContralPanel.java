package FrontEnd.editor;

import java.awt.event.*;

import javax.swing.*;

public class ContralPanel extends JPanel implements ActionListener{
	
	TextEditor textEditor;
	FileOperation fileOperation;
	JMenuBar menubar;
	public ContralPanel(TextEditor textEditor)
	{
		this.textEditor=textEditor;
		fileOperation=new FileOperation(textEditor);
		creatMenu();
	}
	
	public void creatMenu()
	{
		menubar=new JMenuBar();
		JMenu file=new JMenu("文件");
		JMenu style=new JMenu("格式");
		JMenu language=new JMenu("语言");
		JMenu about=new JMenu("关于");
		JMenu run=new JMenu("运行");
		
		menubar.add(file);
		menubar.add(style);
		menubar.add(language);
		menubar.add(run);
		menubar.add(about);
		
		JMenuItem newFile=new JMenuItem("新建");
		setShortcut(newFile, KeyEvent.VK_N);
		JMenuItem open=new JMenuItem("打开");
		setShortcut(open, KeyEvent.VK_O);
		JMenuItem save=new JMenuItem("保存");
		setShortcut(save, KeyEvent.VK_S);
		JMenuItem quit=new JMenuItem("关闭");
		setShortcut(quit, KeyEvent.VK_Q);
		JMenuItem setFont=new JMenuItem("字体设置");
		JMenuItem java=new JMenuItem("Java");
		JMenuItem c=new JMenuItem("C/C++");
		JMenuItem html=new JMenuItem("HTML");
		JMenuItem about1=new JMenuItem("相关信息");
		JMenuItem about2=new JMenuItem("使用说明");
		JMenuItem javaRun=new JMenuItem("编译运行");
		setShortcut(javaRun, KeyEvent.VK_F11);
		JMenuItem htmlRun=new JMenuItem("HTML运行");
		setShortcut(htmlRun, KeyEvent.VK_F12);
		file.add(newFile);
		file.addSeparator();
		file.add(open);
		file.addSeparator();
		file.add(save);
		file.addSeparator();
		file.add(quit);
		style.add(setFont);
		language.add(java);
		language.addSeparator();
		language.add(c);
		language.addSeparator();
		language.add(html);
		about.add(about1);
		about.addSeparator();
		about.add(about2);
		run.add(javaRun);
		run.add(htmlRun);
		newFile.addActionListener(this);
		open.addActionListener(this);
		save.addActionListener(this);
		quit.addActionListener(this);
		setFont.addActionListener(this);
		c.addActionListener(this);
		java.addActionListener(this);
		html.addActionListener(this);
		about1.addActionListener(this);
		about2.addActionListener(this);
		javaRun.addActionListener(this);
		htmlRun.addActionListener(this);
	}
	
	public void addMenuBar()
	{
		textEditor.setJMenuBar(menubar);
	}
	
	public void setShortcut(JMenuItem jmi,int key)
	{
		KeyStroke ms1 = KeyStroke.getKeyStroke(key,InputEvent.CTRL_MASK); 
		jmi.setMnemonic(key);
		jmi.setAccelerator(ms1); 
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("保存"))
		{
			if(fileOperation.save())
			textEditor.setLeftSplitPane(fileOperation.setFileList());
		}
		if(e.getActionCommand().equals("新建"))
		{
			if(fileOperation.creat())
			textEditor.setLeftSplitPane(fileOperation.setFileList());
		}
		if(e.getActionCommand().equals("打开"))
		{
			if(fileOperation.open())
			textEditor.setLeftSplitPane(fileOperation.setFileList());
		}
		if(e.getActionCommand().equals("关闭"))
		{
			textEditor.dispose();
		}
		if(e.getActionCommand().equals("字体设置"))
		{
			new SetFont(textEditor);
		}
		if(e.getActionCommand().equals("C/C++"))
		{
			textEditor.setLanguage("C++");
		}
		if(e.getActionCommand().equals("Java"))
		{
			textEditor.setLanguage("java");
		}
		if(e.getActionCommand().equals("HTML"))
		{
			textEditor.setLanguage("html");
		}
		if(e.getActionCommand().equals("相关信息"))
		{
			JOptionPane.showMessageDialog(null,"开发人员：李磊，周鑫伟，李全香\n版本:1.0.0(免费版)","相关信息",JOptionPane.PLAIN_MESSAGE);
		}
		if(e.getActionCommand().equals("使用说明"))
		{
			JOptionPane.showMessageDialog(null,"1.请确认正确配置JAVA环境,否则无法进行编译运行\n2.目前仅支持Java语言编译"
					+"\n3.运行HTML文档请设置好默认浏览器","使用说明",JOptionPane.PLAIN_MESSAGE);
		}
		if(e.getActionCommand().equals("编译运行"))
		{
			fileOperation.save();
			String src=fileOperation.getPath();
			String name=fileOperation.getFileName();
			new Run().javaRun(src, name);
		}
		if(e.getActionCommand().equals("HTML运行"))
		{
			fileOperation.save();
			String src=fileOperation.getPath();
			String name=fileOperation.getFileName();
			new Run().htmlRun(src, name);
		}
	}
	

}
