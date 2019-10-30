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

    public MFoldersTree(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
    }


    public JTree getTree() {
        return tree;
    }

    public void setTree() {
        File[] roots = File.listRoots();
        FileTreePanel.FileTreeNode rootTreeNode = new FileTreePanel.FileTreeNode(roots);
        this.tree = new JTree(rootTreeNode);
        this.tree.setCellRenderer(new FileTreePanel.FileTreeCellRenderer());
        this.tree.setRootVisible(false);
        this.tree.addTreeSelectionListener(this);

    }

    public void setFoldersTree(File file) {
        File[] roots = new File[]{
                new File(file.getParent())
        };
        FileTreePanel.FileTreeNode rootTreeNode = new FileTreePanel.FileTreeNode(roots);
        this.tree = new JTree(rootTreeNode);
        this.tree.setCellRenderer(new FileTreePanel.FileTreeCellRenderer());
        this.tree.setRootVisible(false);
        this.tree.addTreeSelectionListener(this);
        this.tree.expandRow(0);

    }


    @Override
    public void valueChanged(TreeSelectionEvent e) {
        if (e.getSource() == tree) {
            FileTreePanel.FileTreeNode node = (FileTreePanel.FileTreeNode) tree.getLastSelectedPathComponent();
            if (node.isLeaf()) {
                mainWindow.getFileOperation().treeOpenFile(node.getPath());

            }
        }
    }
}