/**
 * Created by eric on 4/16/14.
 */

package erflink;

public class SearchTree<NodeType extends TreeNode>
{
    SearchTree()
    {
        theTreeRoot = null;
    }

    NodeType addNode(NodeType theRoot, NodeType theNode)
    {
        NodeType newRoot;

        if (getRoot() == null)   // Must be THE root
        {
            setRoot(theNode);
            return theNode;
        }

        erflink.Debug.println("Adding node '" + theNode.toString() + "'");
        newRoot = addTheNode(theRoot, theNode);
        erflink.Debug.println("+++++++++++== Raw Tree Dump ==+++++++++++");

        if (newRoot != theRoot)
            setRoot(newRoot);

        dumpRawTree(theRoot);
        erflink.Debug.println("+++++++++++== End Tree Dump ==+++++++++++");

        try
        {
            verifyTree(getRoot());
        }
        catch (AssertionError ae)
        {
            erflink.Debug.println("verifyTree FAILED: " + ae.getMessage());
            System.exit(1);
        }

        assert newRoot != null : "newRoot gets null from addTheNode";

//        if (!newRoot.toString().equals(theRoot.toString()))
//        {
//            erflink.Debug.println("New root '" + newRoot.toString() +
//                    "' replacing old root '" +  theRoot.toString() + "'");
//            setRoot(newRoot);
//        }
//


        return newRoot;
    }

    /// Does the real work of recursing through the tree to add the node in the
    /// right place, and rebalance the tree.  Returns the new root of the subtree
    /// if rebalancing has happened
    NodeType addTheNode(NodeType theRoot, NodeType theNode)
    {

        ChildDir dir = new ChildDir();

        int comparison = theRoot.compare(theNode);

        if (comparison == 0)
        {
            // Node found!
            return theRoot;
        }

        if (comparison > 0)
            dir.setDirection(ChildDir.Dirs.LEFT);
        else
            dir.setDirection(ChildDir.Dirs.RIGHT);

        if (theRoot.childNode(dir) == null)  // child pointer is free, use it
        {
            theRoot.setValue(dir, theNode);
            theNode.setToRed();

            erflink.Debug.println("Added node '" + theNode.toString() + "' to the " + dir.toString() +
                    " of '" + theRoot.toString() + "'");
        }
        else  // keep searching
        {
            NodeType saveChild = (NodeType)theRoot.childNode(dir);

            NodeType newChild = addTheNode((NodeType) theRoot.childNode(dir), theNode);

            // make sure the original Root's child
            // is the possibly newly rotated root
            if (saveChild != newChild)
                theRoot.setValue(dir, newChild);

        }

        // Do rebalancing here if needed
        theRoot = doRebalancing(theRoot, dir);


        return theRoot;
    }

    /// Rebalance the tree starting at "node"
    /// Returns the new node which is the root of the subtree after rebalancing
    NodeType doRebalancing(NodeType node, ChildDir dir)
    {
        // Simple case:  Split a four-node into two two-nodes
        if (node.leftNode != null && node.rightNode != null &&
                node.leftNode.isRed() && node.rightNode.isRed())
        {
            if (node.isBlack())
            {
                if (!isRoot(node))  // The root can't be red
                    node.setToRed();
                node.leftNode.setToBlack();
                node.rightNode.setToBlack();

                return node;
            }
        }

        // Trickier case... balance a 3-node
        // If we have a red child node, in the direction we added to
        // and one of ITS children is red, we have to do some shuffling around
        if (node.childNode(dir) != null && node.childNode(dir).isRed())
        {
            if (node.childNode(dir).childNode(dir) != null &&
                    node.childNode(dir).childNode(dir).isRed())
            {
                node = rotateSingle(node, dir.otherDir());
            }
            else if (node.childNode(dir).childNode(dir.otherDir()) != null &&
                       node.childNode(dir).childNode(dir.otherDir()).isRed())
            {
                node = rotateDouble(node, dir.otherDir());
            }
        }
        return node;
    }

    NodeType rotateSingle(NodeType node, ChildDir.Dirs dir)
    {
        erflink.Debug.println("***Rotate Single " + dir.toString() + " around '" + node.toString() + "'");

        erflink.Debug.println("Before rotation:");
        if (erflink.Debug.debuggingON)
            dumpRawTree(node);

        NodeType save = (NodeType)node.childNode(ChildDir.otherDir(dir));

        node.setValue(ChildDir.otherDir(dir), save.childNode(dir));
        save.setValue(dir, node);

        node.setToRed();
        save.setToBlack();

        erflink.Debug.println("After rotation:");
        if (erflink.Debug.debuggingON)
            dumpRawTree(save);

        return save;

    }

    NodeType rotateDouble(NodeType node, ChildDir.Dirs dir)
    {
        erflink.Debug.println("Rotate Double " + dir.toString() + " around '" + node.toString() + "'");
        node.setValue(ChildDir.otherDir(dir),
                rotateSingle((NodeType)node.childNode(ChildDir.otherDir(dir)), ChildDir.otherDir(dir)));
        return rotateSingle(node, dir);
    }

    NodeType getRoot()
    {
        return theTreeRoot;
    }

    void setRoot(NodeType newRoot)
    {
        theTreeRoot = newRoot;
        theTreeRoot.setToBlack();
    }

    void dumpRawTree(NodeType theRoot, int depth)
    {
        if (erflink.Debug.debuggingON)
        {
            if (theRoot == null) return;

            if (theRoot.leftNode == null && theRoot.rightNode == null) return;

            String tabs = "";
            for (int i = 0 ; i < depth ; i++)
                tabs = tabs + " ";

            erflink.Debug.print(tabs);
            erflink.Debug.println("Root: '" + theRoot.toString() + "'");


            if (theRoot.leftNode != null)
            {
                erflink.Debug.print(tabs);
                erflink.Debug.println(" Left child: '" + theRoot.leftNode.toString() + "'");
            }

            if (theRoot.rightNode != null)
            {
                erflink.Debug.print(tabs);
                erflink.Debug.println(" Right child: '" + theRoot.rightNode.toString() + "'");
            }

            erflink.Debug.println("");

            dumpRawTree((NodeType)theRoot.leftNode, depth + 1);
            dumpRawTree((NodeType)theRoot.rightNode, depth + 1);
        }
    }

    /// For debugging, check that we have a valid Red-Blackish Binary Tree
    int verifyTree(NodeType theRoot) throws AssertionError
    {
        if (erflink.Debug.debuggingON)
        {
            NodeType rightChild, leftChild;
            int leftHeight, rightHeight;

            if (theRoot == null)
                return 0;

            leftChild = (NodeType)theRoot.leftNode;
            rightChild = (NodeType)theRoot.rightNode;

            // Check for valid binary search tree
            if ((rightChild != null && rightChild.toString().compareToIgnoreCase(theRoot.toString()) <= 0) ||
                    (leftChild != null && leftChild.toString().compareToIgnoreCase(theRoot.toString()) >= 0))
            {
                throw new AssertionError("Bad binary search tree at " + theRoot);
            }

            // Check for red->red->red
            if (theRoot.isRed() &&
                    ((leftChild != null && leftChild.isRed()) || (rightChild != null && rightChild.isRed())))
            {
                throw new AssertionError("Red violation on " + theRoot);
            }

            // Verify and get the heights of the left and right subtrees
            leftHeight = verifyTree(leftChild);
            rightHeight = verifyTree(rightChild);

            // Check branch heights
            if (leftHeight > 0 && rightHeight > 0 && rightHeight != leftHeight)
            {
                throw new AssertionError("Black height mismatch at " + theRoot);
            }

            if (leftHeight > 0 && rightHeight > 0)
                return theRoot.isRed() ? leftHeight : leftHeight + 1;
            else
                return 0;
        }
        else return 0;
    }
    void dumpRawTree(NodeType theRoot)
    {
        dumpRawTree(theRoot, 0);
    }

    boolean isRoot(NodeType node)
    {
        return theTreeRoot == node;
    }

    private NodeType theTreeRoot;
}
