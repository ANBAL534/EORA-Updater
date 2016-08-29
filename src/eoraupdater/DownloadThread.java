package eoraupdater;

import Mega.MegaHandler;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import net.lingala.zip4j.core.ZipFile;
import org.apache.commons.io.FileUtils;
/**
 *
 * @author Anibal
 */
public class DownloadThread extends Thread{
    
    /*
    It looks for current.ver file at server, inside it needs to be:
    
    014 //New version number
    7   //Number of files inside the zip
    
    Depending on the host O.S it will create the filename expected:
    
    Ratting_Advisor_X-X-X.zip           //For Windows
    Ratting_Advisor_Linux_Mac_X-X-X.zip //For Linux/Mac
    
    The X needs to be substitued with the current.ver first line digits in order.
    
    Then it looks for megaRoutes.pth file at the server, inside has 2 Mega.nz links
    One for the windows zip version, and another for Linux/Mac zip version
    
    https://mega.nz/#!itp2iCIZ!HiMtYsWeILHaWau96c8Ms_PReXlZxtz5dvxv71pSahE //They are the same link, just for example
    https://mega.nz/#!itp2iCIZ!HiMtYsWeILHaWau96c8Ms_PReXlZxtz5dvxv71pSahE
    
    Then, the updater download te zip into UpdateCache folder and decompress it.
    Deletes the zip that are no longer necessary and proceeds to copy the decompressed
    content to the original folder, except the settings.cfg file.
    */
    
    //Variables
    private JProgressBar progressBar;
    private JLabel state;
    
    private int totalFinalFileNumber;
    private String megaLink;
    private String version;
    private String endFileName;
    
    private int increaseConstant;
    
    private MegaHandler mh;
    
    public DownloadThread(JProgressBar jProgressBar, JLabel stateLabel){
        
        progressBar = jProgressBar;
        state = stateLabel;
        
        progressBar.setMaximum(100);
        progressBar.setMinimum(0);
        
        mh = new MegaHandler("", "");
        
    }
    
    private void increaseProgress(){
        
        progressBar.setValue(progressBar.getValue() + increaseConstant);
        
    }
    
    private void getVersionInfo(){
        
        try {
            
            //Get the current EORA version
            state.setText("Getting update information...");
            progressBar.setIndeterminate(true);
            
            String uri = "http://anibal.grupoedin.com/EORA/current.ver";
            URL versionUrl = new URL(uri);
            File versionDest = new File("current.ver");
            FileUtils.copyURLToFile(versionUrl, versionDest);
            
            //Read the current version information
            BufferedReader br = new BufferedReader(new FileReader(versionDest));
            version = br.readLine();
            totalFinalFileNumber = Integer.parseInt(br.readLine());
            br.close();
            
            //Set the receiving file name depending OS
            if(System.getProperty("os.name").toLowerCase().contains("win")){
                endFileName = "Ratting_Advisor_" + version.charAt(0) + "-" + version.charAt(1) + "-" + version.charAt(2) + ".zip";
            }else{
                endFileName = "Ratting_Advisor_Linux_Mac_" + version.charAt(0) + "-" + version.charAt(1) + "-" + version.charAt(2) + ".zip";
            }
            
            //Delete version file
            versionDest.delete();
            
            //Set the constant to increase the progress bar
            increaseConstant = 100/totalFinalFileNumber;
            
        } catch (Exception e) {
            int result = JOptionPane.showConfirmDialog(null, "The found a problem while getting the version information and can't continue.\nAt this stage of the update, your Ratting Advisor hasn't be modified, it's safe to use.\nDo you want to see error output to send it to the developer and blame him for his errors?\n\nPress NO to launch the old version of Ratting Advisor.\n", "Something went wrong", JOptionPane.YES_NO_OPTION);
            if(result == 0){
                
                JOptionPane.showMessageDialog(null, "Error Output:\n\n" + e.getMessage() + "\n**************\n" + e.getStackTrace() + "\n\nPress OK to launch the old version of Ratting Advisor.\n", "Error Output", JOptionPane.OK_OPTION);
                
            }
            
            try {
                Runtime.getRuntime().exec("java -jar \"Ratting Advisor.jar\"");
            } catch (IOException ex) {
                Logger.getLogger(DownloadThread.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            System.exit(-1);
            
        }
        
    }
    
    private void getMegaLink(){
        
        try {
            
            //Read mega links from server
            String uri = "http://anibal.grupoedin.com/EORA/megaRoutes.pth";
            URL megaRoutes = new URL(uri);
            File megaRoutesDest = new File("megaRoutes.pth");
            FileUtils.copyURLToFile(megaRoutes, megaRoutesDest);
            
            //Get the mega link from file
            BufferedReader br = new BufferedReader(new FileReader(megaRoutesDest));            
            if(System.getProperty("os.name").toLowerCase().contains("win")){
                megaLink = br.readLine();
            }else{
                br.readLine();//As the second link is the Linux/Mac one and the first the Windows one
                megaLink = br.readLine();
            }
            
            br.close();
            
            //Delete mega links file
            megaRoutesDest.delete();
            
        } catch (Exception e) {
            int result = JOptionPane.showConfirmDialog(null, "The found a problem while getting the update download link and can't continue.\nAt this stage of the update, your Ratting Advisor hasn't be modified, it's safe to use.\nDo you want to see error output to send it to the developer and blame him for his errors?\n\nPress NO to launch the old version of Ratting Advisor.\n", "Something went wrong", JOptionPane.YES_NO_OPTION);
            if(result == 0){
                
                JOptionPane.showMessageDialog(null, "Error Output:\n\n" + e.getMessage() + "\n**************\n" + e.getStackTrace() + "\n\nPress OK to launch the old version of Ratting Advisor.\n", "Error Output", JOptionPane.OK_OPTION);
                
            }
            
            try {
                Runtime.getRuntime().exec("java -jar \"Ratting Advisor.jar\"");
            } catch (IOException ex) {
                Logger.getLogger(DownloadThread.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            System.exit(-1);
            
        }
        
    }
    
    private void downloadAndUnzip(){
        
        try {
            
            //Set the information of the operation beign done
            state.setText("Downloading Update...");
            progressBar.setIndeterminate(true);
            
            //Donwload the Zipfile with the new version
            mh.download_verbose(megaLink, "UpdateCache/");
            
            //Unzip the downloaded Zipfile
            state.setText("Decompressing Update...");
            progressBar.setIndeterminate(true);
            ZipFile zf = new ZipFile("UpdateCache/" + endFileName);
            zf.extractAll("UpdateCache/");
            
            //Delete the Zipfile
            new File("UpdateCache/" + endFileName).delete();
            
        } catch (Exception e) {
            int result = JOptionPane.showConfirmDialog(null, "The found a problem while downloading and uncompressing files and can't continue.\nAt this stage of the update, your Ratting Advisor hasn't be modified, it's safe to use.\nDo you want to see error output to send it to the developer and blame him for his errors?\n\nPress NO to launch the old version of Ratting Advisor.\n", "Something went wrong", JOptionPane.YES_NO_OPTION);
            if(result == 0){
                
                JOptionPane.showMessageDialog(null, "Error Output:\n\n" + e.getMessage() + "\n**************\n" + e.getStackTrace() + "\n\nPress OK to launch the old version of Ratting Advisor.\n", "Error Output", JOptionPane.OK_OPTION);
                
            }
            
            try {
                Runtime.getRuntime().exec("java -jar \"Ratting Advisor.jar\"");
            } catch (IOException ex) {
                Logger.getLogger(DownloadThread.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            System.exit(-1);
            
        }
        
    }
    
    private void copyNewFiles(){
        
        state.setText("Updating Files...");
        progressBar.setIndeterminate(false);
        progressBar.setValue(0);
        
        try {
            
            File cacheBaseFolder = new File("UpdateCache/");
            File cacheLibFolder = new File("UpdateCache/lib");
            File currentBaseFolder = new File("./");
            File currentLibFolder = new File("lib/");
            File[] folderFiles;

            //Copy the base files except settings to the working directory
            folderFiles = cacheBaseFolder.listFiles();
            for (int i = 0; i < folderFiles.length; i++) {

                if(!folderFiles[i].isDirectory() && !folderFiles[i].getName().endsWith("cfg"))
                    FileUtils.copyFileToDirectory(folderFiles[i], currentBaseFolder);
                
                increaseProgress();
                Thread.sleep(200);//Just to be prettier ;D

            }
            
            //Copy the lib folder content to working directory
            folderFiles = cacheLibFolder.listFiles();
            for (int i = 0; i < folderFiles.length; i++) {

                if(!folderFiles[i].isDirectory())
                    FileUtils.copyFileToDirectory(folderFiles[i], currentLibFolder);
                
                increaseProgress();
                Thread.sleep(200);//Just to be prettier ;D

            }
            
        } catch (Exception e) {
            int result = JOptionPane.showConfirmDialog(null, "The found a problem while updating files and can't continue.\nAt this stage of the update, your Ratting Advisor may be corrupt, redownload it completely.\nDo you want to see error output to send it to the developer and blame him for his errors?\n\nPress NO to launch the old version of Ratting Advisor.\n", "Something went wrong", JOptionPane.YES_NO_OPTION);
            if(result == 0){
                
                JOptionPane.showMessageDialog(null, "Error Output:\n\n" + e.getMessage() + "\n**************\n" + e.getStackTrace() + "\n\nPress OK to launch the old version of Ratting Advisor.\n", "Error Output", JOptionPane.OK_OPTION);
                
            }
            
            try {
                Runtime.getRuntime().exec("java -jar \"Ratting Advisor.jar\"");
            } catch (IOException ex) {
                Logger.getLogger(DownloadThread.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            System.exit(-1);
            
        }
        
    }
    
    @Override
    public void run(){
        
        getVersionInfo();
        getMegaLink();
        downloadAndUnzip();
        copyNewFiles();
        JOptionPane.showMessageDialog(null, "The program updated correctly.\nPress OK to launch the Ratting Advisor", "Update Finished", JOptionPane.INFORMATION_MESSAGE);

        try {
            Runtime.getRuntime().exec("java -jar \"Ratting Advisor.jar\"");
        } catch (IOException ex) {
            Logger.getLogger(DownloadThread.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.exit(0);
        
    }
    
}
