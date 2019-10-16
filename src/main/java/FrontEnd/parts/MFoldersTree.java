package FrontEnd.parts;

import FrontEnd.MainWindow;
import FrontEnd.editor.FileList;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.io.File;

public class MFoldersTree implements TreeSelectionListener {

    private MainWindow mainWindow;
    private JTree tree;
    String[] data;
    DefaultMutableTreeNode root,child,parent;
    DefaultTreeModel model;
    public MFoldersTree(MainWindow mainWindow)
    {
        this.mainWindow = mainWindow;
        data=new String[30];
        data[0]="未命名";
        setTree();
        tree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                System.out.println("asd");
            }
        });
    }

    public void init()
    {

    }

    public JTree getTree()
    {
        return tree;
    }

    public void setTree()
    {
        root=new DefaultMutableTreeNode("文件目录");
        tree=new JTree(root);
        model=(DefaultTreeModel)tree.getModel();
        child=new FileTree(data).node();
        parent=(DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
        if(parent==null)parent=root;
        model.insertNodeInto(child, parent, 0);

        for(int i=0; i<tree.getRowCount(); i++)//使树默认展开
        {
            tree.expandRow(i);
        }

    }

    public void setData(String[] data)
    {
        this.data=data;
    }

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        if(e.getSource()==tree)
        {
            DefaultMutableTreeNode node=(DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
            if(node.isLeaf())
            {
                String s=node.toString();
                mainWindow.getFileOperation().treeOpen(s);

            }
        }
    }

    class FileTree{
        DefaultMutableTreeNode r;
        public FileTree(String[] data)
        {
            r=new DefaultMutableTreeNode(data[0]);
            for(int i=1;i<data.length;i++)
            {
                if(data[i]!=null)
                    r.add(new DefaultMutableTreeNode(data[i]));
            }
        }
        public DefaultMutableTreeNode node()
        {
            return r;
        }
    }


    public MFoldersTree setFoldersTree()
    {

        MFoldersTree mFoldersTree=new MFoldersTree(mainWindow);;
        String[] list=new String[30];
        String rootName;
        String path=mainWindow.getPathLabel().getText() ;
        int x=path.lastIndexOf('\\', path.length()-2);
        if(x==-1)
        {
            rootName=path.charAt(0)+"盘";
        }
        else{
            rootName=path.substring(x+1,path.length()-1);
        }
        list[0]=rootName;
        int p=1;
        int index = path.lastIndexOf('\\');
        String folderpath = path.substring(0,index);
        File folderfile=new File(folderpath);
        String[] s=folderfile.list();
        File file=new File(path);
        String fileName=file.getName();
        int point=fileName.indexOf('.');
        String end=fileName.substring(point, fileName.length());
        for(int i=0;i<s.length;i++)
        {
            if(s[i].indexOf(end)!=-1)
            {
                list[p++]=s[i];
            }
        }

        mFoldersTree.setData(list);
        mFoldersTree.setTree();
        return mFoldersTree;

    }

}
