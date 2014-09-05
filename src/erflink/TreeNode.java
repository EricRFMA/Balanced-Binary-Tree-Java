/**
 * Created by eric on 4/16/14.
 */
package erflink;

public abstract class TreeNode
{
    TreeNode()
    {
        isRed = true;
        leftNode = null;
        rightNode = null;
    }



    void spliceNodeRight(TreeNode parentNode)
    {
        assert parentNode.rightNode != null : "Attempt to add to non-null right node" ;

        parentNode.rightNode = this;
    }

    void spliceNodeLeft(TreeNode parentNode)
    {
        assert parentNode.leftNode != null : "Attempt to add to non-null left node" ;

        parentNode.leftNode = this;
    }


    TreeNode childNode(ChildDir dir)
    {
        return childNode(dir.getDirection());
    }

    TreeNode childNode(ChildDir.Dirs dir)
    {
        assert dir != ChildDir.Dirs.NONE : "Child direction not specified is childNode";
        switch (dir)
        {
            case LEFT:
                return leftNode;

            case RIGHT:
                return rightNode;

            default:
                assert false : "Invalid ChildDir in childNode";
                return null;
        }
    }

    void setValue(ChildDir.Dirs dir, TreeNode theNode)
    {
        assert dir != ChildDir.Dirs.NONE : "Child direction not specified in setValue";
        switch (dir)
        {
            case LEFT:
                leftNode = theNode;
                break;

            case RIGHT:
                rightNode = theNode;
                break;

            default:
                assert false : "Invalid ChildDir in setValue";
        }
    }

    void setValue(ChildDir dir, TreeNode theNode)
    {
        setValue(dir.getDirection(), theNode);

    }
    boolean isRed()
    {
        return isRed;
    }

    boolean isBlack()
    {
        return !isRed();
    }

    void setToRed()
    {
        isRed = true;
    }

    void setToBlack()
    {
        isRed = false;
    }

    abstract<NodeType extends TreeNode> int compare(NodeType val);

    private boolean isRed;
    TreeNode leftNode;
    TreeNode rightNode;
}
