package FrontEnd.parts;

import FrontEnd.MainWindow;

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
        data[0]="Unnamed";
        setTree();
    }


    public JTree getTree()
    {
        return tree;
    }

    public void setTree()
    {
        File[] roots = File.listRoots();
        FileTreePanel.FileTreeNode rootTreeNode = new FileTreePanel.FileTreeNode(roots);
        this.tree=new JTree(rootTreeNode);
        this.tree.setCellRenderer(new FileTreePanel.FileTreeCellRenderer());
        this.tree.setRootVisible(true);
        this.tree.addTreeSelectionListener(this);
        this.tree.expandRow(0);
        // old
//        root=new DefaultMutableTreeNode("File Directory");
//        tree.addTreeSelectionListener(this);
//        model=(DefaultTreeModel)tree.getModel();
//        child=new FileTree(data).node();
//        parent=(DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
//        if(parent==null)parent=root;
//        model.insertNodeInto(child, parent, 0);
//
//        for(int i=0; i<tree.getRowCount(); i++)//打开子节点
//        {
//            tree.expandRow(i);
//        }

    }
    public void setFoldersTree(File file){
        File[] roots=new File[]{
                new File(file.getParent())
        };
        FileTreePanel.FileTreeNode rootTreeNode = new FileTreePanel.FileTreeNode(roots);
        this.tree=new JTree(rootTreeNode);
        this.tree.setCellRenderer(new FileTreePanel.FileTreeCellRenderer());
        this.tree.setRootVisible(true);
        this.tree.addTreeSelectionListener(this);
        this.tree.expandRow(1);
    }


    @Override
    public void valueChanged(TreeSelectionEvent e) {
        if(e.getSource()==tree)
        {
            DefaultMutableTreeNode node=(DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
            if(node.isLeaf())
            {
                String path=node.toString();
                System.out.println(path);
                mainWindow.getFileOperation().treeOpenFile(path);

            }
        }
    }

//    public void setData(String[] data)
//    {
//        this.data=data;
//    }
//    class FileTree{
//        DefaultMutableTreeNode r;
//        public FileTree(String[] data)
//        {
//            r=new DefaultMutableTreeNode(data[0]);
//            for(int i=1;i<data.length;i++)
//            {
//                if(data[i]!=null)
//                    r.add(new DefaultMutableTreeNode(data[i]));
//            }
//        }
//        public DefaultMutableTreeNode node()
//        {
//            return r;
//        }
//    }
//
//
//    public void setFoldersTree()
//    {
//
//       // MFoldersTree mFoldersTree=new MFoldersTree(mainWindow);
//        String[] list=new String[30];
//        String rootName;
//        String path=mainWindow.getPathLabel().getText() ;
//        int x=path.lastIndexOf('\\', path.length()-2);
//        if(x==-1)
//        {
//            rootName=path.charAt(0)+":";
//        }
//        else{
//            rootName=path.substring(x+1,path.length()-1);
//        }
//        list[0]=rootName;
//        int p=1;
//        int index = path.lastIndexOf('\\');
//        String folderpath = path.substring(0,index);
//        File folderfile=new File(folderpath);
//        String[] s=folderfile.list();
//        File file=new File(path);
//        String fileName=file.getName();
//        int point=fileName.indexOf('.');
//        String end=fileName.substring(point);
////        assert s != null;
//        for (String value : s) {
//            if (value.contains(end)) {
//                list[p++] = value;
//            }
//        }
//
//        this.setData(list);
//        this.setTree();
//
//    }

}
