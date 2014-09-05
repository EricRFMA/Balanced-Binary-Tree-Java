package erflink;

/**
 * Created by eric on 4/16/14.
 */
public class StringNode extends TreeNode
{
    StringNode(String value)
    {
        super();
        stringNodeValue = value;
    }

    int compare(TreeNode rhs)
    {
        assert rhs instanceof StringNode : "StringNode compare argument is not a StringNode";

        return stringNodeValue.compareToIgnoreCase(((StringNode)rhs).stringNodeValue);
    }

    int compare(StringNode rhs)
    {
        return stringNodeValue.compareToIgnoreCase(rhs.stringNodeValue);
    }

    String getStringValue()
    {
        return stringNodeValue;
    }

    String nodeColor()
    {
        if (isRed())
            return "(Red)";
        else
            return "(Black)";
    }

    public String toString()
    {
        assert stringNodeValue != null : "Call to toString would return null";

        return stringNodeValue + nodeColor();
    }

    private String stringNodeValue;
}
