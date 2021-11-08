import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import javax.xml.namespace.QName;

class Parser {
    String commandName;
    String[] args;
    Scanner scan = new Scanner(System.in);

    public void waitForInput(){
        String input;
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
    public String echo(String[] args){
        String str = "";
        for (int i = 0; i < args.length; i++)
            str += args[i] + " ";
        return str;
    }

    /**
     * prints current working directory
     */
    public String pwd(){ return currentDir.toString(); }


    /**
     * "ls" lists contents of current working dir
     */
    public String ls(String[] args){
        ArrayList<String> content = new ArrayList<>();
        File f = new File(String.valueOf(currentDir));
        ArrayList<File> files = new ArrayList<File>(Arrays.asList(f.listFiles()));
        for (int i = 0; i < files.size(); i++)
            content.add(files.get(i).getName());
        return String.join(" ", content);
    }

    /**
     * "ls -r" lists contents of current working dir in reverse order
     */
    public String lsr(String[] args){
        ArrayList<String> content = new ArrayList<>();
        File f = new File(String.valueOf(currentDir));
        ArrayList<File> files = new ArrayList<File>(Arrays.asList(f.listFiles()));
        for (int i = files.size() - 1; i >= 0; i--)
            content.add(files.get(i).getName());
        return String.join(" ", content);
    }

    /**
     * "mkdir [dir name]" creates a folder in current working dir
     * <p>
     * "mdkir [path]" creates a folder in selected path
     */

    // Path must be without spaces
    public void mkdir(String[] args){
        for (int i = 0; i < args.length; i++){
            if (!args[i].contains("\\")) {
                File file = new File(currentDir.toString() + "\\" + args[i]);
                if(file.mkdir())
                    System.out.println("Done");
                else
                    System.out.println("None");
            }
            else {
                File file = new File(args[i]);
                if(file.mkdir())
                    System.out.println("Done");
                else
                    System.out.println("None");
            }
        }
    }

    /**
     * WARNING: ONLY REMOVES EMPTY DIRS
     * <p>
     * "rmdir *" removes all EMPTY dirs in current working dir
     * <p>
     * "rmdir [path]" removes dir at selected path iff the dir is empty
     */
    public void rmdir(String[] args){
        if (args[0].equals("*")) {
            File f = new File(String.valueOf(currentDir));
            ArrayList<File> files = new ArrayList<File>(Arrays.asList(f.listFiles()));
            for (int i = 0; i < files.size(); i++) {
                f = files.get(i);
                if(f.delete())
                    System.out.println("Done");
                else
                    System.out.println("None");
            }
        }
        else if (!args[0].contains("\\")){
            File file = new File(currentDir.toString() + "\\" + args[0]);
            if(file.delete())
                System.out.println("Done");
            else
                System.out.println("None");
        }
        else {
            File file = new File(args[0]);
            if(file.delete())
                System.out.println("Done");
            else
                System.out.println("None");
        }
    }

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
        // if args is null, no args were passed and we return to home dir
            currentDir = homeDir;
        else{
            if(args[0].equals(".."))
                currentDir = currentDir.getParent();
            else{
                Path tmpPath = currentDir.resolve(String.join(" ", args));
                if(!exists(tmpPath))
                    throw new Exception("Directory not found");
                if(!isDir(tmpPath))
                    throw new Exception(tmpPath+ " is not a directory");
                currentDir = currentDir.resolve(tmpPath).normalize();

            }
        }
    }


    /**
     * "touch [path]" creates a file in selected path
     */
    public void touch(String[] args) throws Exception{
        if(args == null || args.length != 1)
            throw new Exception("Invalid arguments");

        args = args[0].split("\\\\"); //split path into array
        Path parentPath = currentDir;
        
        // now separate parent path and the actual file path
        parentPath = parentPath.resolve(
            String.join("\\\\", Arrays.copyOfRange(
                args, 
                0, 
                args.length-1
            ))
        ).normalize();
        
        Path filePath = parentPath.resolve(args[args.length - 1]);
        if(exists(filePath))
            throw new Exception("File already exists");
        Files.createDirectories(parentPath); //create intermedieate directories if needed
        Files.createFile(filePath); //creates the file
    }
        

    
    /**
     * "rm [file]" deletes a file in current working dir
     * @throws Exception
     */
    public void rm(String[] args) throws Exception{
        if(args == null || args.length != 1)
            throw new Exception("Invalid arguments");

        File deletedFile = currentDir.resolve(args[0]).toFile();
        if(!exists(deletedFile))
            throw new Exception("no such file");
        boolean isDeleted = deletedFile.delete();
        if(!isDeleted)
            throw new Exception("Failed to delete file");
    }

    /**
     * "cat [file]" prints the file contents
     * <p>
     * "cat [file1] [file2]" prints file1 and file2 contents
     */
    public String cat(String[] args) throws Exception{ 
        if(args == null || args.length > 2)
            throw new Exception("Invalid arguments");

        String fileContents = "";
        Scanner fileReader;

        for(String arg : args){
            File toBeRead = currentDir.resolve(arg).toFile();
            
            if(!exists(toBeRead))
                throw new Exception(arg + " No such file");
            if(!isFile(toBeRead))
                throw new Exception(arg + " is not a file");

            fileReader = new Scanner(toBeRead);
            
            while(fileReader.hasNextLine()){
                fileContents += (fileReader.nextLine() + "\n");
            }
        }
    
        return fileContents; 
    }

    /**
     * "cp [file1] [file2]" copies contents of file 1 into file 2
     */
    public void cp(String[] args) throws Exception{
        if(args == null || args.length != 2)
            throw new Exception("invalid arguments");

        File file1 = currentDir.resolve(args[0]).toFile();
        File file2 = currentDir.resolve(args[1]).toFile();
        
        if(!exists(file1) || !exists(file2))
            throw new Exception("no such file");
        if(!isFile(file1) || !isFile(file2))
            throw new Exception("isn't a file");

        Scanner fileReader = new Scanner(file1);

        String file1Contents = "";
        
        while(fileReader.hasNext())
            file1Contents += (fileReader.nextLine() + "\n");
        
        Files.write(
            file2.toPath(), 
            file1Contents.getBytes(), 
            StandardOpenOption.APPEND
        );

        fileReader.close();
    }

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
                case "echo":
                    System.out.println(echo(parser.args));
                    break;
                case "ls":
                    System.out.println(ls(parser.args));
                    break;
                case "ls -r":
                    System.out.println(lsr(parser.args));
                    break;
                case "mkdir":
                    mkdir(parser.args);
                    break;
                case "rmdir":
                    rmdir(parser.args);
                
                case "touch":
                    touch(parser.args);
                    break;
                
                case "rm":
                    rm(parser.args);
                    break;

                case "cat":
                    System.out.println(cat(parser.args));
                    break;
                
                case "cp":
                    cp(parser.args);
                    break;
                default:
                    break;
            }
        }catch(Exception e){
            System.out.println("ERROR: " + e.getMessage());
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

    private boolean isFile(File path){
        return path.isFile();
    }

    private boolean isDir(Path path){
        return Files.isDirectory(path);
    }

    private boolean isDir(File path){
        return path.isDirectory();
    }

    private boolean exists(Path path){
        return Files.exists(path);
    }

    private boolean exists(File path){
        return path.exists();
    }

    public static void main(String[] args) {
        Terminal term = new Terminal();
        term.run();
    }
}
