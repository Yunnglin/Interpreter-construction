package FrontEnd.parts;

import FrontEnd.MainWindow;
import FrontEnd.parts.conf.MColor;
import FrontEnd.parts.conf.MWord;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MDocumentListener implements DocumentListener {

    private MainWindow mainWindow;
    private JTextPane editPane;

    private Hashtable<String, Style> styleHashtable = new Hashtable<>();
    private Style symbolStyle;
    private Style basicTypeStyle;
    private Style definedWordsStyle;
    private Style constantsStyle;
    private Style idStyle;
    private Style annotationStyle;
    private Style normalStyle;
    private Style errorStyle;

    public MDocumentListener(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        this.editPane = mainWindow.getEditPane();
        init();
    }

    /*
   Token：
   (1) Symbol: ||, &&, ==, !=, >=, <=, +, -, *, /, ;
   (2) Type: int, float, char, long
   (3) Reserved Words: break, do, else, if, while, read, write
   (4) Constant: false, true, num
   (5) ID: identifiers
   (6) annotation
*/
    private void init() {
        symbolStyle = ((StyledDocument) editPane.getDocument()).addStyle("symbolStyle", null);
        basicTypeStyle = ((StyledDocument) editPane.getDocument()).addStyle("basicTypeStyle", null);
        definedWordsStyle = ((StyledDocument) editPane.getDocument()).addStyle("definedWordsStyle", null);
        constantsStyle = ((StyledDocument) editPane.getDocument()).addStyle("constantsStyle", null);
        idStyle = ((StyledDocument) editPane.getDocument()).addStyle("idStyle", null);
        annotationStyle = ((StyledDocument) editPane.getDocument()).addStyle("annotationStyle", null);
        normalStyle = ((StyledDocument) editPane.getDocument()).addStyle("normalStyle", null);
        errorStyle = ((StyledDocument) editPane.getDocument()).addStyle("errorStyle", null);

        //设置颜色
        StyleConstants.setForeground(symbolStyle, MColor.symbolColor);
        StyleConstants.setForeground(basicTypeStyle, MColor.basicTypeColor);
        StyleConstants.setForeground(definedWordsStyle, MColor.defineWordsColor);
        StyleConstants.setForeground(constantsStyle, MColor.constantsColor);
        StyleConstants.setForeground(idStyle, MColor.idColor);
        StyleConstants.setForeground(annotationStyle, MColor.annotationColor);
        StyleConstants.setForeground(normalStyle, MColor.normalColor);
        StyleConstants.setForeground(errorStyle, MColor.errorColor);

        //设置字体
        StyleConstants.FontConstants.setUnderline(errorStyle, true);
        StyleConstants.FontConstants.setItalic(errorStyle, true);
        StyleConstants.FontConstants.setItalic(annotationStyle, true);
        StyleConstants.FontConstants.setBold(definedWordsStyle, true);
        StyleConstants.FontConstants.setBold(basicTypeStyle, true);
        StyleConstants.FontConstants.setBold(constantsStyle, true);
        StyleConstants.FontConstants.setBold(symbolStyle,true);
        StyleConstants.FontConstants.setBold(normalStyle,true);

        //Symbol
        mAddStyle(MWord.symbols, symbolStyle);

        //BasicType
        mAddStyle(MWord.basicType, basicTypeStyle);

        //Reserve Words
        mAddStyle(MWord.reservedWords, definedWordsStyle);

        //Constant
        mAddStyle(MWord.constants, constantsStyle);
    }

    private void mAddStyle(String[] words, Style style) {
        for (String word : words) {
            styleHashtable.put(word, style);
        }
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        try {
//            System.out.println(e.getOffset() + " " + e.getLength());
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
        // 取得插入或者删除后影响到的单词.
        // 例如"public"在b后插入一个空格, 就变成了:"pub lic", 这时就有两个单词要处理:"pub"和"lic"
        // 这时要取得的范围是pub中p前面的位置和lic中c后面的位置
        int start = indexOfWordStart(doc, pos);
        int end = indexOfWordEnd(doc, pos + len);

        char ch;
        while (start < end) {
            ch = getCharAt(doc, start);
            if (Character.isLetter(ch) || ch == '_') {      //处理Word
                // 如果是以字母或者下划线开头, 说明是单词
                // pos为处理后的最后一个下标

                start = colouringWord(doc, start);

            } else if (Character.isDigit(ch)) {      //处理数字
                start = colouringNum(doc, start);
            }
            else if (isSpecialChar(Character.toString(ch))) {
                start = colouringSpecialChar(doc, start);
            }
            else {
                SwingUtilities.invokeLater(new ColouringTask(doc, start, 1, normalStyle));
                ++start;
            }
        }
    }

    // 对单词进行着色, 并返回单词结束的下标.
    public int colouringWord(StyledDocument doc, int pos) throws BadLocationException {
        int wordEnd = indexOfWordEnd(doc, pos);
        String word = doc.getText(pos, wordEnd - pos);

        if (styleHashtable.containsKey(word)) {
            // 如果是关键字, 就进行关键字的着色, 否则使用普通的着色.
            // 这里有一点要注意, 在insertUpdate和removeUpdate的方法调用的过程中, 不能修改doc的属性.
            // 但我们又要达到能够修改doc的属性, 所以把此任务放到这个方法的外面去执行.
            // 实现这一目的, 可以使用新线程, 但放到swing的事件队列里去处理更轻便一点.
            SwingUtilities.invokeLater(new ColouringTask(doc, pos, wordEnd - pos, styleHashtable.get(word)));
        } else {
            SwingUtilities.invokeLater(new ColouringTask(doc, pos, wordEnd - pos, normalStyle));
        }

        return wordEnd;
    }

    //对数字进行着色，包括整数和小数
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

    //对特殊字符上色
    public int colouringSpecialChar(StyledDocument doc,int pos) throws BadLocationException{
        SwingUtilities.invokeLater(new ColouringTask(doc,pos,1,symbolStyle));
        return pos+1;
    }

    //取得在文档中下标在pos处的字符.
    public char getCharAt(Document doc, int pos) throws BadLocationException {
        return doc.getText(pos, 1).charAt(0);
    }

    //如果一个字符是字母, 数字, 下划线, 则返回true.
    public boolean isWordCharacter(Document doc, int pos) throws BadLocationException {
        char ch = getCharAt(doc, pos);
        return Character.isLetter(ch) || Character.isDigit(ch) || ch == '_';
    }

    //用正则表达式判断一个字符是不是特殊字符
    public static boolean isSpecialChar(String str) {
        String regEx = "[`~!@#$%^&*()+=|{}':;,\\[\\].<>/?]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.find();
    }


    //获取pos所在位置单词的开始位置
    public int indexOfWordStart(Document doc, int pos) throws BadLocationException {
        while (pos > 0 && isWordCharacter(doc, pos - 1)) {
            --pos;
        }

        return pos;
    }

    //获取pos所在位置单词的结束位置
    public int indexOfWordEnd(Document doc, int pos) throws BadLocationException {
        while (isWordCharacter(doc, pos)) {
            ++pos;
        }

        return pos;
    }

}
