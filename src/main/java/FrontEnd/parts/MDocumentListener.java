package FrontEnd.parts;

import FrontEnd.MainWindow;
import FrontEnd.parts.conf.MColor;
import FrontEnd.parts.conf.MWord;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;
import java.util.Hashtable;

public class MDocumentListener implements DocumentListener {

    private MainWindow mainWindow;
    private JTextPane editPane;

    private Hashtable<String, Style> styleHashtable=new Hashtable<>();
    private Style symbolStyle;
    private Style basicTypeStyle;
    private Style definedWordsStyle;
    private Style constantsStyle;
    private Style idStyle;
    private Style annotationStyle;
    private Style normalStyle;
    private Style errorStyle;

    public MDocumentListener(MainWindow mainWindow){
        this.mainWindow = mainWindow;
        this.editPane = mainWindow.getEditPane();
        init();
    }

    /*
   Token��
   (1) Symbol: ||, &&, ==, !=, >=, <=, +, -, *, /, ;
   (2) Type: int, float, char, long
   (3) Reserved Words: break, do, else, if, while, read, write
   (4) Constant: false, true, num
   (5) ID: identifiers
   (6) annotation
*/
    private void init(){
        symbolStyle = ((StyledDocument) editPane.getDocument()).addStyle("symbolStyle", null);
        basicTypeStyle = ((StyledDocument) editPane.getDocument()).addStyle("basicTypeStyle", null);
        definedWordsStyle = ((StyledDocument) editPane.getDocument()).addStyle("definedWordsStyle", null);
        constantsStyle = ((StyledDocument) editPane.getDocument()).addStyle("constantsStyle", null);
        idStyle = ((StyledDocument) editPane.getDocument()).addStyle("idStyle", null);
        annotationStyle = ((StyledDocument) editPane.getDocument()).addStyle("annotationStyle", null);
        normalStyle = ((StyledDocument) editPane.getDocument()).addStyle("normalStyle", null);
        errorStyle = ((StyledDocument) editPane.getDocument()).addStyle("errorStyle", null);

        //������ɫ
        StyleConstants.setForeground(symbolStyle, MColor.symbolColor);
        StyleConstants.setForeground(basicTypeStyle, MColor.basicTypeColor);
        StyleConstants.setForeground(definedWordsStyle, MColor.defineWordsColor);
        StyleConstants.setForeground(constantsStyle, MColor.constantsColor);
        StyleConstants.setForeground(idStyle, MColor.idColor);
        StyleConstants.setForeground(annotationStyle, MColor.annotationColor);
        StyleConstants.setForeground(normalStyle, MColor.normalColor);
        StyleConstants.setForeground(errorStyle, MColor.errorColor);

        //��������
        StyleConstants.FontConstants.setUnderline(errorStyle, true);
        StyleConstants.FontConstants.setItalic(errorStyle, true);
        StyleConstants.FontConstants.setItalic(annotationStyle, true);
        StyleConstants.FontConstants.setBold(definedWordsStyle, true);
        StyleConstants.FontConstants.setBold(basicTypeStyle, true);
        StyleConstants.FontConstants.setBold(constantsStyle, true);

        //Symbol
        mAddStyle(MWord.symbols,symbolStyle);

        //BasicType
        mAddStyle(MWord.basicType,basicTypeStyle);

        //Reserve Words
        mAddStyle(MWord.reservedWords,definedWordsStyle);

        //Constant
        mAddStyle(MWord.constants,constantsStyle);
    }

    private void mAddStyle(String[] words,Style style){
        for (String word:words) {
            styleHashtable.put(word,style);
        }
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        try {
            colouring((StyledDocument) e.getDocument(), e.getOffset(), e.getLength());
        } catch (BadLocationException ex) {
            ex.printStackTrace();
        }
        mainWindow.getmScrollPane().updateLineNum();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        try {
            colouring((StyledDocument) e.getDocument(), e.getOffset(), 0);
        } catch (BadLocationException ex) {
            ex.printStackTrace();
        }
        mainWindow.getmScrollPane().updateLineNum();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {

    }

    public void colouring(StyledDocument doc, int pos, int len) throws BadLocationException {
        // ȡ�ò������ɾ����Ӱ�쵽�ĵ���.
        // ����"public"��b�����һ���ո�, �ͱ����:"pub lic", ��ʱ������������Ҫ����:"pub"��"lic"
        // ��ʱҪȡ�õķ�Χ��pub��pǰ���λ�ú�lic��c�����λ��
        int start = indexOfWordStart(doc, pos);
        int end = indexOfWordEnd(doc, pos + len);

        char ch;
        while (start < end) {
            ch = getCharAt(doc, start);
            if (Character.isLetter(ch) || ch == '_') {      //����Word
                // ���������ĸ�����»��߿�ͷ, ˵���ǵ���
                // posΪ���������һ���±�

                //TODO: ��ʶ���Ķ���
                start = colouringWord(doc, start);

            } else if (Character.isDigit(ch)) {      //��������
                start = colouringNum(doc, start);
            } else {
                SwingUtilities.invokeLater(new ColouringTask(doc, start, 1, normalStyle));
                ++start;
            }
        }
    }

    /**
     * �Ե��ʽ�����ɫ, �����ص��ʽ������±�.
     *
     * @param doc
     * @param pos
     * @return
     * @throws BadLocationException
     */
    public int colouringWord(StyledDocument doc, int pos) throws BadLocationException {
        int wordEnd = indexOfWordEnd(doc, pos);
        String word = doc.getText(pos, wordEnd - pos);

        if (styleHashtable.containsKey(word)) {
            // ����ǹؼ���, �ͽ��йؼ��ֵ���ɫ, ����ʹ����ͨ����ɫ.
            // ������һ��Ҫע��, ��insertUpdate��removeUpdate�ķ������õĹ�����, �����޸�doc������.
            // ��������Ҫ�ﵽ�ܹ��޸�doc������, ���԰Ѵ�����ŵ��������������ȥִ��.
            // ʵ����һĿ��, ����ʹ�����߳�, ���ŵ�swing���¼�������ȥ��������һ��.
            SwingUtilities.invokeLater(new ColouringTask(doc, pos, wordEnd - pos, styleHashtable.get(word)));
        } else {
            SwingUtilities.invokeLater(new ColouringTask(doc, pos, wordEnd - pos, normalStyle));
        }

        return wordEnd;
    }

    //TODO: ��������
    public int colouringNum(StyledDocument doc, int pos) throws BadLocationException {
        int numEnd = indexOfWordEnd(doc, pos);
        if (getCharAt(doc, numEnd) == '.') {
            numEnd = indexOfWordEnd(doc, numEnd + 1);
        }

        String num = doc.getText(pos, numEnd - pos);

        int index = 0;
        char c;
        do {
            c = num.charAt(index);
            index++;
        } while (Character.isDigit(c) && index < num.length());

        if (Character.isDigit(c) && index == num.length()) {
            SwingUtilities.invokeLater(new ColouringTask(doc, pos, numEnd - pos, constantsStyle));
            return numEnd;
        }

        //real num
        if (c == '.') {
            index++;
            for (; index < num.length(); index++) {
                c = num.charAt(index);
                if (!Character.isDigit(c)) {
                    SwingUtilities.invokeLater(new ColouringTask(doc, pos, numEnd - pos, errorStyle));
                    return numEnd;
                }
            }
            SwingUtilities.invokeLater(new ColouringTask(doc, pos, numEnd - pos, constantsStyle));
        } else {
            SwingUtilities.invokeLater(new ColouringTask(doc, pos, numEnd - pos, errorStyle));
        }

        return numEnd;
    }

    //ȡ�����ĵ����±���pos�����ַ�.
    public char getCharAt(Document doc, int pos) throws BadLocationException {
        return doc.getText(pos, 1).charAt(0);
    }

    //���һ���ַ�����ĸ, ����, �»���, �򷵻�true.
    public boolean isWordCharacter(Document doc, int pos) throws BadLocationException {
        char ch = getCharAt(doc, pos);
        return Character.isLetter(ch) || Character.isDigit(ch) || ch == '_';
    }

    //��ȡpos����λ�õ��ʵĿ�ʼλ��
    public int indexOfWordStart(Document doc, int pos) throws BadLocationException {
        while (pos > 0 && isWordCharacter(doc, pos - 1)) {
            --pos;
        }

        return pos;
    }

    //��ȡpos����λ�õ��ʵĽ���λ��
    public int indexOfWordEnd(Document doc, int pos) throws BadLocationException {
        while (isWordCharacter(doc, pos)) {
            ++pos;
        }

        return pos;
    }

}
