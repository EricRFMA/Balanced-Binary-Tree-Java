package erflink;

import erflink.Visualizationizer;

import java.io.FileInputStream;
import java.io.RandomAccessFile;
import java.lang.String;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;

public class Main {

    static SearchTree<StringNode> myTree;

    public static void main(String[] args) throws java.io.IOException
    {
      //  String[] testNodes = new String[]{"The", "Quick", "Brown", "Fox", "Jumps", "Over", "The", "Lazy", "Dog", "Eric", "Fred", "George", "Harry"};
        final String theDict = "/usr/share/dict/web2";

        long numWords = 50;  // how many dictionary words to fetch

        myTree = new SearchTree<StringNode>();

      //  bulkAddNodes(testNodes);

        RandomAccessFile dictionaryInput = new RandomAccessFile(theDict, "r");

        for (long i = 0 ; i < numWords ; i++)
        {
            String newWord = fetchWord(dictionaryInput);
            erflink.Debug.println(newWord);
            addOneNode(newWord);
        }

        dumpSortedNodes();

        // Now let's have a look at it
        Visualizationizer myViz = new Visualizationizer(myTree);
        myViz.makeMagicHappen();

    }

    static String fetchWord(RandomAccessFile theFile) throws java.io.IOException
    {
        long size = theFile.length();

        // Find a random place to seek to
        double rand = java.lang.Math.random();

        // random returns a value 0 <= n < 1 so scale it to the size of the file (in bytes)
        long seekTarget = java.lang.Math.round(rand * (double) size);

        // seek there
        theFile.seek(seekTarget);

        // Now, read backwards until we get a newline.
        // Since we can't seek backwards, adjust our seekTarget
        // as we move
        while (theFile.read() != '\n' && seekTarget > 0)
        {
            seekTarget -= 2;  // move backwards over the character just read, and the one more
        }

        // So, we either hit a newline beginning of line or beginning of file
        // In either case, grab the word on this line
        String theWord = theFile.readLine();

        return theWord;
    }
    static void bulkAddNodes(String[] bunchOfNodes)
    {
        for (String aNode : bunchOfNodes)
        {
            addOneNode(aNode);

        }
    }

    static void addOneNode(String aNodeValue)
    {
        StringNode theStringNode = new StringNode(aNodeValue);

        myTree.addNode(myTree.getRoot(), theStringNode);
    }

    static void dumpSortedNodes()
    {
        System.err.println("---------------\nSorted Node List\n---------------");
        dumpSortedNodesFrom(myTree.getRoot());
    }

    static void dumpSortedNodesFrom(StringNode root)
    {
        if (root.leftNode != null)
            dumpSortedNodesFrom((StringNode)root.leftNode);

        System.err.println(root.getStringValue());

        if (root.rightNode != null)
            dumpSortedNodesFrom((StringNode)root.rightNode);
    }
}
