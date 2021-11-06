import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Scanner;

class Parser {
    String commandName;
    String[] args;

    public void waitForInput(){
        String input;
        Scanner scan = new Scanner(System.in);
        input = scan.nextLine();
        parse(input);
    }

    public boolean parse(String input){
        String[] cmd = input.split(" ");
        commandName = cmd[0];

        if(cmd.length > 1){
            if(cmd[1].contains("-")){ 
                commandName += (" " + cmd[1]);
                args = Arrays.copyOfRange(cmd, 2, cmd.length);
            }
            else{
                args = Arrays.copyOfRange(cmd, 1, cmd.length);
            }
        }
        return true;
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
    Path currentDir, homeDir;

    Terminal(){
        parser = new Parser();
        //gets the dir the program was launched in
        currentDir = Paths.get(System.getProperty("user.dir"));
        homeDir = Paths.get(System.getProperty("user.home"));
    }

    /**
     * Takes 1 arguments and prints it
     */
    public String echo(String[] args){ return ""; }

    /**
     * prints current working directory
     */
    public String pwd(){ return currentDir.toString(); }

    
    /**
     * "ls" lists contents of current working dir
     */
    public String[] ls(String[] args){ String[] arr = new String[]{}; return arr; }
    
    /**
     * "ls -r" lists contents of current working dir in reverse order
     */
    public String[] lsr(String[] args){ String[] arr = new String[]{}; return arr; }
    
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
    public void cd(String[] args) throws Exception{
        // code can access zip files lol????????
        if(args == null)
            currentDir = homeDir;
        else{
            if(args[0].equals(".."))
                currentDir = currentDir.getParent();
            else{
                Path tmpPath = currentDir.resolve(String.join(" ", args));
                if(!exists(tmpPath))
                    throw new Exception("ERROR: Directory not found");
                if(!isDir(tmpPath))
                    throw new Exception("ERROR: "+tmpPath+ " is not a directory");
                currentDir = currentDir.resolve(tmpPath);

            }
        }
    }

    
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
    public String cat(String[] args){return ""; }
    
    /**
     * "cp [file1] [file2]" copies contents of file 1 into file 2
     */
    public void cp(String[] args){}
    
    /**
     * "cp -r [dir1] [dir2]" copies contents of dir1 into dir2
     */
    public void cpr(String[] args){}

    public void chooseCommandAction(){
        parser.waitForInput();
        try{
            switch (parser.commandName){
                case "cd":
                    cd(parser.args);
                    break;
            
                default:
                    break;
            }
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    public void run(){
        boolean userExited = false;
        while(!userExited){
            System.out.print(pwd()+">");
            chooseCommandAction();
        }
    }

    private boolean isFile(Path path){
        return !Files.isDirectory(path);
    }

    private boolean isDir(Path path){
        return Files.isDirectory(path);
    }
    
    private boolean exists(Path path){
        return Files.exists(path);
    }

    public static void main(String[] args) {
        Terminal term = new Terminal();
        term.run();
    }
}
