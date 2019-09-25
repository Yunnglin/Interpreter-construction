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
		JMenu file=new JMenu("�ļ�");
		JMenu style=new JMenu("��ʽ");
		JMenu language=new JMenu("����");
		JMenu about=new JMenu("����");
		JMenu run=new JMenu("����");
		
		menubar.add(file);
		menubar.add(style);
		menubar.add(language);
		menubar.add(run);
		menubar.add(about);
		
		JMenuItem newFile=new JMenuItem("�½�");
		setShortcut(newFile, KeyEvent.VK_N);
		JMenuItem open=new JMenuItem("��");
		setShortcut(open, KeyEvent.VK_O);
		JMenuItem save=new JMenuItem("����");
		setShortcut(save, KeyEvent.VK_S);
		JMenuItem quit=new JMenuItem("�ر�");
		setShortcut(quit, KeyEvent.VK_Q);
		JMenuItem setFont=new JMenuItem("��������");
		JMenuItem java=new JMenuItem("Java");
		JMenuItem c=new JMenuItem("C/C++");
		JMenuItem html=new JMenuItem("HTML");
		JMenuItem about1=new JMenuItem("�����Ϣ");
		JMenuItem about2=new JMenuItem("ʹ��˵��");
		JMenuItem javaRun=new JMenuItem("��������");
		setShortcut(javaRun, KeyEvent.VK_F11);
		JMenuItem htmlRun=new JMenuItem("HTML����");
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
		if(e.getActionCommand().equals("����"))
		{
			if(fileOperation.save())
			textEditor.setLeftSplitPane(fileOperation.setFileList());
		}
		if(e.getActionCommand().equals("�½�"))
		{
			if(fileOperation.creat())
			textEditor.setLeftSplitPane(fileOperation.setFileList());
		}
		if(e.getActionCommand().equals("��"))
		{
			if(fileOperation.open())
			textEditor.setLeftSplitPane(fileOperation.setFileList());
		}
		if(e.getActionCommand().equals("�ر�"))
		{
			textEditor.dispose();
		}
		if(e.getActionCommand().equals("��������"))
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
		if(e.getActionCommand().equals("�����Ϣ"))
		{
			JOptionPane.showMessageDialog(null,"������Ա�����ڣ�����ΰ����ȫ��\n�汾:1.0.0(��Ѱ�)","�����Ϣ",JOptionPane.PLAIN_MESSAGE);
		}
		if(e.getActionCommand().equals("ʹ��˵��"))
		{
			JOptionPane.showMessageDialog(null,"1.��ȷ����ȷ����JAVA����,�����޷����б�������\n2.Ŀǰ��֧��Java���Ա���"
					+"\n3.����HTML�ĵ������ú�Ĭ�������","ʹ��˵��",JOptionPane.PLAIN_MESSAGE);
		}
		if(e.getActionCommand().equals("��������"))
		{
			fileOperation.save();
			String src=fileOperation.getPath();
			String name=fileOperation.getFileName();
			new Run().javaRun(src, name);
		}
		if(e.getActionCommand().equals("HTML����"))
		{
			fileOperation.save();
			String src=fileOperation.getPath();
			String name=fileOperation.getFileName();
			new Run().htmlRun(src, name);
		}
	}
	

}
