package fr.baptgosse.automates.amdl;

/**
 * Exception levée lors du parsing AMDL
 */
public class AMDLException extends Exception {
    private final int line;
    private final int column;
    
    public AMDLException(String message) {
        super(message);
        this.line = -1;
        this.column = -1;
    }
    
    public AMDLException(String message, int line, int column) {
        super(String.format("Line %d, Column %d: %s", line, column, message));
        this.line = line;
        this.column = column;
    }
    
    public int getLine() {
        return line;
    }
    
    public int getColumn() {
        return column;
    }
}
