package interpreter.debugger;

public class Breakpoint {

    private int line;

    @Override
    public String toString() {
        return "Breakpoint at line " + line;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        return this.line == ((Breakpoint) obj).getLine();
    }
    public Breakpoint(int line){
        this.line = line;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

}

