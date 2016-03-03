package com.leechwin.csslint.eclipse.engine;

/**
 * A single issue with the code that is being checked for problems.
 * @author leechwin1@gmail.com
 */
public class Issue {

    private final int line;
    private final int column;
    private final String message;
    private final String type;

    public Issue(int line, int column, String message, String type) {
        this.line = line;
        this.column = column;
        this.message = message;
        this.type = type;
    }

    /**
     * @return the number of the line on which this issue occurs.
     */
    public int getLine() {
        return line;
    }

    /**
     * @return the position of the issue within the line. Starts at 0.
     */
    public int getColumn() {
        return column;
    }

    /**
     * @return a textual description of this issue.
     */
    public String getMessage() {
        return message;
    }

    /**
     * @return the name of the issue type.
     */
    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return getLine() + ":" + getColumn() + ":" + getMessage() + ":" + getType();
    }

}
