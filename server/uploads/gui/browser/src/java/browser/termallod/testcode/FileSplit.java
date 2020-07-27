/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package browser.termallod.testcode;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Scanner;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

/**
 *
 * @author elahi
 */
public class FileSplit {

    private static String testPath = "/users/elahi/NetBeansProjects/prepareVersion/browser/src/java/browser/termallod/saveData/test/";
    
   private static String path = "/users/elahi/NetBeansProjects/prepareVersion/browser/src/java/browser/termallod/data/iate/";
    private static String inputFilePathOrg = path + "tbx2rdf_iate.ttl";

    private static String inputFilePath = path + "test.ttl";
    private static String outputFolderPath = path + "output.ttl";

    public static void main(String[] args) throws FileNotFoundException, IOException {
        FileSplit fileSplit = new FileSplit();
        fileSplit1(inputFilePathOrg,testPath,1000000);
        //splitTextFiles(inputFilePath, 10000, "", "", path);
        //fileSplit.fileSplit4(new File(inputFilePath));
        //fileSplit.fileSplit5(inputFilePath,outputFolderPath);
        //fileSplit.join(inputFilePath);

    }

    private static void fileSplit1(String inputFilePath,String outputFolderPath,long linesPerSplit) throws FileNotFoundException, IOException {
        long linesWritten = 0;
        int count = 1;
        Integer stopIndeicator=0;
        File inputFile = new File(inputFilePath);
        InputStream inputFileStream = new BufferedInputStream(new FileInputStream(inputFile));
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputFileStream));

        String line = reader.readLine();

        String fileName = inputFile.getName();
        String outfileName = outputFolderPath + "output";

        while (line != null) {
            stopIndeicator++;
            
            File outFile = new File(outfileName + "_" + count + ".ttl");
            Writer writer = new OutputStreamWriter(new FileOutputStream(outFile));

            while (line != null && linesWritten < linesPerSplit) {
                writer.write(line);
                line = reader.readLine();
                linesWritten++;
            }
            if(stopIndeicator>=linesPerSplit){
                 writer.close();
                break;
            }

            writer.close();
            linesWritten = 0;//next file
            count++;//nect file count
        }

        reader.close();
    }

    public static void splitTextFiles(String fileName, int maxRows, String header, String footer, String targetDir) throws IOException {
        File bigFile = new File(fileName);
        int i = 1;
        String ext = fileName.substring(fileName.lastIndexOf("."));

        String fileNoExt = bigFile.getName().replace(ext, "");
        File newDir = null;
        if (targetDir != null) {
            newDir = new File(targetDir);
        } else {
            newDir = new File(bigFile.getParent() + "\\" + fileNoExt + "_split");
        }
        newDir.mkdirs();
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(fileName))) {
            String line = null;
            int lineNum = 1;
            Path splitFile = Paths.get(newDir.getPath() + "\\" + fileNoExt + "_" + String.format("%02d", i) + ext);
            BufferedWriter writer = Files.newBufferedWriter(splitFile, StandardOpenOption.CREATE);
            while ((line = reader.readLine()) != null) {
                if (lineNum == 1) {
                    //System.out.print("new file created '" + splitFile.toString());
                    if (header != null && header.length() > 0) {
                        writer.append(header);
                        writer.newLine();
                    }
                }
                writer.append(line);

                if (lineNum >= maxRows) {
                    if (footer != null && footer.length() > 0) {
                        writer.newLine();
                        writer.append(footer);
                    }
                    writer.close();
                    //System.out.println(", " + lineNum + " lines written to file");
                    lineNum = 1;
                    i++;
                    splitFile = Paths.get(newDir.getPath() + "\\" + fileNoExt + "_" + String.format("%02d", i) + ext);
                    writer = Files.newBufferedWriter(splitFile, StandardOpenOption.CREATE);
                } else {
                    writer.newLine();
                    lineNum++;
                }
            }
            if (lineNum <= maxRows) // early exit
            {
                if (footer != null && footer.length() > 0) {
                    writer.newLine();
                    lineNum++;
                    writer.append(footer);
                }
            }
            writer.close();
            //System.out.println(", " + lineNum + " lines written to file");
        }

        //System.out.println("file '" + bigFile.getName() + "' split into " + i + " files");
    }

    public void join(String FilePath) {
        long leninfile = 0, leng = 0;
        int count = 1, data = 0;
        try {
            File filename = new File(FilePath);
            //RandomAccessFile outfile = new RandomAccessFile(filename,"rw");

            OutputStream outfile = new BufferedOutputStream(new FileOutputStream(filename));
            while (true) {
                filename = new File(FilePath);
                if (filename.exists()) {
                    //RandomAccessFile infile = new RandomAccessFile(filename,"r");
                    InputStream infile = new BufferedInputStream(new FileInputStream(filename));
                    data = infile.read();
                    while (data != -1) {
                        outfile.write(data);
                        data = infile.read();
                    }
                    leng++;
                    infile.close();
                    count++;
                } else {
                    break;
                }
            }
            outfile.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void split(String FilePath, long splitlen) {
        long leninfile = 0, leng = 0;
        int count = 1, data;
        try {
            File filename = new File(FilePath);
            //RandomAccessFile infile = new RandomAccessFile(filename, "r");
            InputStream infile = new BufferedInputStream(new FileInputStream(filename));
            data = infile.read();
            while (data != -1) {
                filename = new File(FilePath + count + ".sp");
                //RandomAccessFile outfile = new RandomAccessFile(filename, "rw");
                OutputStream outfile = new BufferedOutputStream(new FileOutputStream(filename));
                while (data != -1 && leng < splitlen) {
                    outfile.write(data);
                    leng++;
                    data = infile.read();
                }
                leninfile += leng;
                leng = 0;
                outfile.close();
                count++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void fileSplit4(File theFile) throws IOException {
        Integer count = 0;
        LineIterator it = FileUtils.lineIterator(theFile, "UTF-8");
        try {
            while (it.hasNext()) {
                String line = it.nextLine();
                //System.out.println(line);
                count++;
                if (count == 100) {
                    break;
                }
                // do something with line
            }
        } finally {
            LineIterator.closeQuietly(it);
        }

    }

    public void fileSplit5(String filePath,String outputfilePath) throws IOException {
        FileInputStream inputStream = null;
        Scanner sc = null;
        Integer count = 0;
        Integer fileNumber = 0;

        String str="";

        try {
            inputStream = new FileInputStream(filePath);
            sc = new Scanner(inputStream, "UTF-8");
            while (sc.hasNextLine()) {
                count = count + 1;
                String line = sc.nextLine();
                //System.out.println(line);
                str+=line+"\n”";
                if (count == 50000) {
                    StringToFile(str,  path+"output"+fileNumber+".ttl");
                    fileNumber++;
                    break;
                }

                // System.out.println(line);
            }
            // note that Scanner suppresses exceptions
            if (sc.ioException() != null) {
                throw sc.ioException();
            }
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            if (sc != null) {
                sc.close();
            }
        }

    }

    public void StringToFile(String str, String fileName)
            throws IOException {
  
        FileOutputStream outputStream = new FileOutputStream(fileName);
        byte[] strToBytes = str.getBytes();
        outputStream.write(strToBytes);

        outputStream.close();
    }

}
