package eoraupdater;

import Mega.MegaHandler;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import org.apache.commons.io.FileUtils;
/**
 *
 * @author Anibal
 */
public class DownloadThread extends Thread{
    
    private JProgressBar progressBar;
    private JLabel state;
    
    private int megaLinkCount;
    private int updateType;//1 - just jar; 2 - jar + lib; 3 - extra files such as sound or DB but settings.cfg; 4 - 1+3 modes; 5 - full update but settings.cfg
    private String[] megaLinks;
    
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
            megaLinkCount = Integer.parseInt(br.readLine());
            updateType = Integer.parseInt(br.readLine());
            megaLinks = new String[megaLinkCount];
            
            //Delete version file
            versionDest.delete();
            
            //Set the constant to increase the progress bar
            increaseConstant = 100/megaLinkCount;
            
            //Switch from functions depending on the update type
            
            
        } catch (MalformedURLException ex) {
            Logger.getLogger(DownloadThread.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(DownloadThread.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    private void getMegaLinks(){
        
        /*
        Links order:
        
        - 0: .jar file or "NOTNEW" if there is no update for it
        - 1-19: lib files or "NOTNEW" if there is no update for it
        - 20-rest: extra files
        */
        
        try {
            
            //Read mega links from server
            String uri = "http://anibal.grupoedin.com/EORA/megaRoutes.pth";
            URL megaRoutes = new URL(uri);
            File megaRoutesDest = new File("megaRoutes.pth");
            FileUtils.copyURLToFile(megaRoutes, megaRoutesDest);
            
            //Get the mega links from file
            BufferedReader br = new BufferedReader(new FileReader(megaRoutesDest));
            for (int i = 0; i < megaLinks.length; i++)
                megaLinks[i] = br.readLine();
            
            //Delete mega links file
            megaRoutesDest.delete();
            
        } catch (MalformedURLException ex) {
            Logger.getLogger(DownloadThread.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(DownloadThread.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    private void downloadJar(){
        
        
        
    }
    
    @Override
    public void run(){
        
        
        
    }
    
}
