class Parser {
    String commandName;
    String[] args;
    

    public boolean parse(String input){
        return false;
    }

    public String getCommandName(){
        return commandName;
    }

    public String[] getArgs(){
        return args;
    }
}

public class Terminal {
    Parser parser;

    /**
     * Takes 1 arguments and prints it
     */
    public String echo(String[] args){}

    /**
     * prints current working directory
     */
    public String pwd(){}

    
    /**
     * "ls" lists contents of current working dir
     */
    public String[] ls(String[] args){}
    
    /**
     * "ls -r" lists contents of current working dir in reverse order
     */
    public String[] lsr(String[] args){}
    
    /**
     * "mkdir [dir name]" creates a folder in current working dir
     * <p>
     * "mdkir [path]" creates a folder in selected path
     */
    public void mkdir(String[] args){}
    
    /**
     * WARNING: ONLY REMOVES EMPTY DIRS
     * <p>
     * "rmdir *" removes all EMPTY dirs in current working dir
     * <p>
     * "rmdir [path]" removes dir at selected path iff the dir is empty
     */
    public void rmdir(String[] args){}
    
    /**
     * "cd" with no args returns to home dir of user
     * <p>
     * "cd .." goes up one dir
     * <p>
     * "cd [abs path] or [relative path]" changes current working dir to path in arg
     */
    public void cd(String[] args){}
    
    /**
     * "touch [path]" creates a file in selected path
     */
    public void touch(String[] args){}
    
    /**
     * "rm [file]" deletes a file in current working dir
     */
    public void rm(String[] args){}
    
    /**
     * "cat [file]" prints the file contents
     * <p>
     * "cat [file1] [file2]" prints file1 and file2 contents
     */
    public String cat(String[] args){}
    
    /**
     * "cp [file1] [file2]" copies contents of file 1 into file 2
     */
    public void cp(String[] args){}
    
    /**
     * "cp -r [dir1] [dir2]" copies contents of dir1 into dir2
     */
    public void cpr(String[] args){}
}
