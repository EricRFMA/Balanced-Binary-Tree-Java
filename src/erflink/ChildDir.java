package erflink;

/**
 * Created by eric on 4/22/14.
 */
public class ChildDir
{
    ChildDir()
    {
        theDirection = Dirs.NONE;
    }

    ChildDir(Dirs dir)
    {
        theDirection = dir;
    }

    enum Dirs
    {
        LEFT,
        RIGHT,
        NONE
    }

    public String toString()
    {
        switch (theDirection)
        {
            case LEFT:
                return "LEFT";

            case RIGHT:
                return "RIGHT";

            case NONE:
                return "NONE";

            default:
                return null;
        }
    }

    // Return LEFT if passed RIGHT, and RIGHT if passed LEFT
    Dirs otherDir()
    {
        assert theDirection != Dirs.NONE : "No direction passed to otherDir";
        if (theDirection == Dirs.LEFT)
            return Dirs.RIGHT;
        else if (theDirection == Dirs.RIGHT)
            return Dirs.LEFT;

        return null;
    }

    // static version of otherDir for when you just have a direction and not a ChildDir object
    static Dirs otherDir(Dirs dir)
    {
        assert dir != Dirs.NONE : "No direction passed to static otherDir";

        if (dir == Dirs.LEFT)
            return Dirs.RIGHT;
        else if (dir == Dirs.RIGHT)
            return Dirs.LEFT;

        return null;
    }
    Dirs getDirection()
    {
        return theDirection;
    }

    void setDirection(Dirs theDir)
    {
        theDirection = theDir;
    }

    private Dirs theDirection;
}
