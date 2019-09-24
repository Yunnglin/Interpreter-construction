package FrontEnd.editor;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
public class SetFont implements ActionListener {
	JDialog setFont;
	String[] fontValues={"����","����","΢���ź�"};
	String[] styleValues={"��ͨ","б��","����"};
	int[] styleValues2={Font.PLAIN,Font.ITALIC,Font.BOLD};
	int[] sizeValues={5,6,7,8,9,10,12,14,16,18,20,22,24,26,28,36};
	JComboBox fontValue;
	JComboBox styleValue;
	JComboBox sizeValue;
	TextEditor textEditor;
	public SetFont(TextEditor te)
	{
		
		this.textEditor=te;
		setFont=new JDialog(te);
		setFont.setTitle("��������");
		fontValue=new JComboBox();
		styleValue=new JComboBox();
		sizeValue=new JComboBox();
		setFont.setLayout(new BorderLayout());
		init();
	}
	
	public void init()
	{
		JLabel font=new JLabel("��  ��");
		JLabel style=new JLabel("��  ��");
		JLabel size=new JLabel("��  С");
		
		for(int i=0;i<fontValues.length;i++)
		{
			fontValue.addItem(fontValues[i]);
		}
		for(int i=0;i<styleValues.length;i++)
		{
			styleValue.addItem(styleValues[i]);
		}
		for(int i=0;i<sizeValues.length;i++)
		{
			sizeValue.addItem(sizeValues[i]+"");
		}
		
		sizeValue.setSelectedIndex(8);
		
		JButton ok=new JButton("ȷ��");
		JButton no=new JButton("ȡ��");
		
		Panel names=new Panel(new GridLayout(3,1));
		names.add(font);
		names.add(style);
		names.add(size);
		Panel values=new Panel(new GridLayout(3, 1));
		values.add(fontValue);
		values.add(styleValue);
		values.add(sizeValue);
		Panel contral=new Panel();
		contral.add(ok);
		contral.add(no);
		ok.addActionListener(this);
		no.addActionListener(this);
		setFont.add("West",names);
		setFont.add("Center", values);
		setFont.add("South", contral);
		setFont.setSize(200, 200);
		setFont.setLocation(200, 200);
		setFont.setVisible(true);
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("ȷ��"))
		{
			int fontIndex=fontValue.getSelectedIndex();
			int styleIndex=styleValue.getSelectedIndex();
			int sizeIndex=sizeValue.getSelectedIndex(); 
			Font f=new Font(fontValues[fontIndex],styleValues2[styleIndex],sizeValues[sizeIndex]);
			textEditor.setFontStyle(f);
			setFont.dispose();
		}
		if(e.getActionCommand().equals("ȡ��"))
		{
			setFont.dispose();
		}
	}
	
}
