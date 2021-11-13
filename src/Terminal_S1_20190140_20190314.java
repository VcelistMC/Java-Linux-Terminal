import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

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
        /*for (int i = 0; i < args.length; i++){
            boolean found = false;
            for (int j = 0; j < args[i].length(); j++){
                if (args[i].indexOf(j) == '"'){
                    if (!found)
                        found = true;
                    else
                        found = false;
                }
            }
        }*/
        return true;
    }

    public String getCommandName(){
        return commandName;
    }

    public String[] getArgs(){
        return args;
    }

    public void clear(){
        commandName = "";
        args = null;
    }
}

public class Terminal_S1_20190140_20190314 {
    Parser parser;
    Path currentDir, homeDir;

    Terminal_S1_20190140_20190314(){
        parser = new Parser();
        //gets the dir the program was launched in
        currentDir = Paths.get(System.getProperty("user.dir"));
        homeDir = Paths.get(System.getProperty("user.home"));
    }

    /**
     * Takes 1 arguments and prints it
     */
    public String echo(String[] args) throws Exception {
        if(args == null)
            throw new Exception("Invalid arguments");
        isRedirected(args);
        String str = "";
        for (int i = 0; i < args.length; i++)
            str += args[i] + " ";
        return str;
    }

    /**
     * prints current working directory
     */
    public String pwd(String[] args) throws Exception{
        if(args != null) 
            isRedirected(args); 
        return currentDir.toString(); 
    }

    public String pwd() throws Exception{
        return pwd(null);
    }


    /**
     * "ls" lists contents of current working dir
     */
    public String ls(String[] args) throws Exception{
        isRedirected(args);

        ArrayList<String> content = new ArrayList<>();
        File file = new File(String.valueOf(currentDir));
        ArrayList<File> fileList = new ArrayList<File>(Arrays.asList(file.listFiles()));
        for (int i = 0; i < fileList.size(); i++)
            content.add(fileList.get(i).getName());
        return String.join(" ", content);
    }

    /**
     * "ls -r" lists contents of current working dir in reverse order
     */
    public String lsr(String[] args)throws Exception{
        isRedirected(args);
        ArrayList<String> content = new ArrayList<>();
        File file = new File(String.valueOf(currentDir));
        ArrayList<File> fileList = new ArrayList<File>(Arrays.asList(file.listFiles()));
        for (int i = fileList.size() - 1; i >= 0; i--)
            content.add(fileList.get(i).getName());
        return String.join(" ", content);
    }

    /**
     * "mkdir [dir name]" creates a folder in current working dir
     * <p>
     * "mdkir [path]" creates a folder in selected path
     * @throws Exception
     */

    public void mkdir(String[] args) throws Exception{
        if(args == null)
            throw new Exception("Invalid arguments");
        for (int i = 0; i < args.length; i++){
            if (args[i].contains(":")){
                File file = new File("");
                String[] folders = args[i].split("\\\\");
                for (int j = 0; j < folders.length; j++) {
                    file = new File(file.toString() + "\\" + folders[j]);
                    if (!file.exists())
                        file.mkdir();
                }
            }
            else {
                String[] folders = args[i].split("\\\\");
                File file = new File(currentDir.toString());
                for (int j = 0; j < folders.length; j++) {
                    file = new File(file.toString() + "\\" + folders[j]);
                    if (!file.exists())
                        file.mkdir();
                }
            }
        }
//         if(args == null)
//             throw new Exception("Invalid arguments");
//         for(String arg : args){
//             Path dirPath = currentDir.resolve(arg);
//             if(exists(dirPath))
//                 throw new Exception("Folder already exists");
//             Files.createDirectories(dirPath);
//         }
    }

    /**
     * WARNING: ONLY REMOVES EMPTY DIRS
     * <p>
     * "rmdir *" removes all EMPTY dirs in current working dir
     * <p>
     * "rmdir [path]" removes dir at selected path iff the dir is empty
     */
    public void rmdir(String[] args) throws Exception{
        if(args == null)
            throw new Exception("Invalid arguments");

        File file = new File(currentDir.toString());
        if (args[0].equals("*")) {
            ArrayList<File> fileList = new ArrayList<File>(Arrays.asList(file.listFiles()));
            for (int i = 0; i < fileList.size(); i++) {
                file = fileList.get(i);
                if (file.isDirectory())
                    file.delete();
            }
        }
        else if (args[0].contains(":")){
            file = new File(args[0]);
            if (!file.exists())
                throw new Exception("Directory not found");
            if (!(file.listFiles().length == 0))
                throw new Exception("Directory is not empty");
            file.delete();
        }
        else {
            String[] folders = args[0].split("\\\\");
            file = new File(currentDir.toString());
            for (int j = 0; j < folders.length; j++) {
                file = new File(file.toString() + "\\" + folders[j]);
                if (!file.exists())
                    throw new Exception("Directory not found");
            }
            if (!(file.listFiles().length == 0))
                throw new Exception("Directory is not empty");
            file.delete();
        }
//         if(args[0].equals("*")){
//             File[] folderList = currentDir.toFile().listFiles();
//             for(File folder: folderList){
//                 if(isDir(folder))
//                     folder.delete();
//             }
//         }

//         else{
//             String dirPath = String.join(" ", args);
//             File folder = currentDir.resolve(dirPath).toFile();

//             if(!exists(folder))
//                 throw new Exception("folder not found");
//             if(!isDir(folder))
//                 throw new Exception("is not a folder");
//             if(folder.list().length != 0)
//                 throw new Exception("folder is not empty");

//             folder.delete();
//         }
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
        if(args == null)
            throw new Exception("Invalid arguments");
        
        isRedirected(args);

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


    public void redirect(String output, String path) throws Exception{
        File outputFile = currentDir.resolve(path).toFile();
        FileWriter fileWriter = new FileWriter(outputFile);
        fileWriter.write(output);
        fileWriter.close();
    }

    private void isRedirected(String[] args) throws Exception{
        if(args == null)
            return;
        try{
            for(int i = 0; i < args.length; i++){
                if(args[i].equals(">")){
                    String outString = "";
                    switch (parser.commandName) {
                        case "echo":
                            outString = echo(Arrays.copyOfRange(args, 0, i));
                            break;
                        
                        case "ls":
                            outString = ls(Arrays.copyOfRange(args, 0, i));
                            break;

                        case "ls -r":
                            outString = lsr(Arrays.copyOfRange(args, 0, i));
                            break;

                        case "cat":
                            outString = cat(Arrays.copyOfRange(args, 0, i));
                            break;
                        case "pwd":
                            outString = pwd();
                            break;
                    }
                    redirect(outString, args[args.length-1]);
                }
            }
        }catch(Exception e){System.out.println(e.getMessage());}
    }

    public void chooseCommandAction(){
        try{
            switch (parser.commandName){
                case "cd":
                    cd(parser.args);
                    break;

                case "pwd":
                    System.out.println(pwd(parser.args));
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
                    break;

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
                    throw new Exception("invalid command");
            }
        }catch(Exception e){
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    public void run()throws Exception{
        boolean userExited = false;
        while(!userExited){
            parser.clear();
            System.out.print(pwd()+">");
            parser.waitForInput();
            userExited = parser.commandName.equals("exit");
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
        Terminal_S1_20190140_20190314 term = new Terminal_S1_20190140_20190314();
        try {
            term.run();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
