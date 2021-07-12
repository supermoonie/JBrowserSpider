package com.github.supermoonie.jbrwoserspider;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.github.supermoonie.jbrwoserspider.handler.AppHandler;
import com.github.supermoonie.jbrwoserspider.loader.CefLoader;
import com.github.supermoonie.jbrwoserspider.util.Folders;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.cef.CefApp;
import org.cef.CefClient;
import org.cef.CefSettings;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.security.Security;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * @author super_w
 * @since 2021/7/11
 */
@Slf4j
public class App {

    @Getter
    private static App instance;
    @Getter
    public final ScheduledExecutorService executor;
    @Getter
    private MainFrame mainFrame;
    @Getter
    private final CefClient defaultCefClient;

    public static void main(String[] args) {
        try {
            instance = new App();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private App() throws Exception {
        Security.addProvider(new BouncyCastleProvider());
        JFrame.setDefaultLookAndFeelDecorated(true);
        JDialog.setDefaultLookAndFeelDecorated(true);
        FlatLightLaf.setup();
        FlatDarkLaf.setup();
        UIManager.setLookAndFeel(FlatLightLaf.class.getName());
        // init cef
        CefApp.addAppHandler(new AppHandler(null));
        File cefPath = Folders.crateTempFolder(".cef");
        CefSettings settings = new CefSettings();
        settings.windowless_rendering_enabled = false;
        settings.cache_path = cefPath.getAbsolutePath();
        String debugLogPath = cefPath.getAbsolutePath() + File.separator + "debug.log";
        settings.log_file = debugLogPath;
        new File(debugLogPath).deleteOnExit();
        settings.persist_session_cookies = true;
        settings.user_agent = "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.77 Safari/537.36";
        settings.background_color = settings.new ColorType(100, 255, 255, 255);
        CefLoader.installAndLoadCef(settings);
        defaultCefClient = CefApp.getInstance().createClient();
        executor = new ScheduledThreadPoolExecutor(5);
        SwingUtilities.invokeLater(() -> {
            // main frame
            mainFrame = new MainFrame();
            mainFrame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    CefApp.getInstance().dispose();
                    mainFrame.dispose();
                    System.exit(0);
                }
            });
        });
    }
}
