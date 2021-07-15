package com.github.supermoonie.jbrwoserspider;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.github.supermoonie.jbrwoserspider.browser.JCefClient;
import com.github.supermoonie.jbrwoserspider.handler.AppHandler;
import com.github.supermoonie.jbrwoserspider.listener.GlobalKeyListener;
import com.github.supermoonie.jbrwoserspider.loader.CefLoader;
import com.github.supermoonie.jbrwoserspider.setting.UrlSettings;
import com.github.supermoonie.jbrwoserspider.util.Folders;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.cef.CefApp;
import org.cef.CefClient;
import org.cef.CefSettings;
import org.cef.JCefLoader;
import org.jnativehook.GlobalScreen;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.security.Security;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author super_w
 * @since 2021/7/11
 */
@Slf4j
public class App {

    public static final String UA = "";

    @Getter
    private static App instance;
    @Getter
    private final ScheduledExecutorService executor;
    @Getter
    private MainFrame mainFrame;

    public static void main(String[] args) {
        try {
            Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
            logger.setLevel(Level.OFF);
            logger.setUseParentHandlers(false);
            GlobalScreen.registerNativeHook();
            instance = new App();
            GlobalScreen.addNativeKeyListener(new GlobalKeyListener());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            System.exit(0);
        }
    }

    private App() throws Exception {
        Security.addProvider(new BouncyCastleProvider());
        JFrame.setDefaultLookAndFeelDecorated(true);
        JDialog.setDefaultLookAndFeelDecorated(true);
        if (SystemUtils.IS_OS_MAC) {
            System.setProperty("apple.laf.useScreenMenuBar", "true");
        }
        FlatLightLaf.setup();
        FlatDarkLaf.setup();
        UIManager.setLookAndFeel(FlatLightLaf.class.getName());
        log.info("look and feel set");
        // init cef
        CefApp.addAppHandler(new AppHandler(null));
        CefSettings settings = cefSettings();
        if (SystemUtils.IS_OS_WINDOWS) {
            CefLoader.installAndLoadCef(settings);
            log.info("windows cef install");
        } else if (SystemUtils.IS_OS_MAC) {
            JCefLoader.installAndLoadCef(settings);
            log.info("macos cef install");
        }
        // init executor
        executor = new ScheduledThreadPoolExecutor(
                10,
                new BasicThreadFactory.Builder()
                        .namingPattern("schedule-exec-%d")
                        .daemon(false)
                        .uncaughtExceptionHandler((thread, throwable) -> {
                            String error = String.format("thread: %s, error: %s", thread.toString(), throwable.getMessage());
                            log.error(error, throwable);
                        }).build(), (r, executor) -> log.warn("Thread: {} reject by {}", r.toString(), executor.toString()));
        log.info("executor init");
        CefApp.getInstance().createClient();
        SwingUtilities.invokeLater(() -> {
            // main frame
            mainFrame = new MainFrame();
            log.info("main frame init");
            mainFrame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    mainFrame.dispose();
                    System.exit(0);
                }
            });
            JCefClient.getInstance().createBrowser(UrlSettings.HOME, false, false, null);
            log.info("CefClient init");
        });
    }

    private CefSettings cefSettings() {
        File cefPath = Folders.crateTempFolder(".cef");
        CefSettings settings = new CefSettings();
//        settings.windowless_rendering_enabled = false;
        settings.cache_path = cefPath.getAbsolutePath();
        String debugLogPath = cefPath.getAbsolutePath() + File.separator + "debug.log";
        settings.log_file = debugLogPath;
        new File(debugLogPath).deleteOnExit();
        settings.persist_session_cookies = true;
        settings.user_agent = UA;
        settings.background_color = settings.new ColorType(100, 255, 255, 255);
        return settings;
    }
}
