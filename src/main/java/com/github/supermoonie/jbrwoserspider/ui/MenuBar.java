//package com.github.supermoonie.jbrwoserspider.ui;
//
//import com.github.supermoonie.video.browser.App;
//import com.github.supermoonie.video.browser.dialog.DevToolsDialog;
//import com.github.supermoonie.video.browser.dialog.DownloadDialog;
//import lombok.Getter;
//
//import javax.swing.*;
//import java.awt.event.ComponentAdapter;
//import java.awt.event.ComponentEvent;
//
///**
// * @author supermoonie
// * @since 2021/3/1
// */
//@Getter
//public class MenuBar extends JMenuBar {
//
//    private final JFrame owner;
//    private final DownloadDialog downloadDialog;
//
//    public MenuBar(JFrame owner) {
//        this.owner = owner;
//        this.downloadDialog = new DownloadDialog(this.owner);
//        JMenu testMenu = new JMenu("Tests");
//        final JMenuItem showDevTools = new JMenuItem("Show Right DevTools");
//        showDevTools.addActionListener(e -> {
//            DevToolsDialog devToolsDlg =
//                    new DevToolsDialog(MenuBar.this.owner, "DEV Tools", App.MAIN_FRAME.getCurrentBrowser());
//            devToolsDlg.addComponentListener(new ComponentAdapter() {
//                @Override
//                public void componentHidden(ComponentEvent e) {
//                    showDevTools.setEnabled(true);
//                }
//            });
//            devToolsDlg.setVisible(true);
//            showDevTools.setEnabled(false);
//        });
//        testMenu.add(showDevTools);
//
//        final JMenuItem showWorkDevTools = new JMenuItem("Show Left DevTools");
//        showWorkDevTools.addActionListener(e -> {
//            DevToolsDialog devToolsDlg =
//                    new DevToolsDialog(MenuBar.this.owner, "DEV Tools", App.MAIN_FRAME.getWorkBrowser());
//            devToolsDlg.addComponentListener(new ComponentAdapter() {
//                @Override
//                public void componentHidden(ComponentEvent e) {
//                    showDevTools.setEnabled(true);
//                }
//            });
//            devToolsDlg.setVisible(true);
//            showDevTools.setEnabled(false);
//        });
//        testMenu.add(showWorkDevTools);
//
//        JMenuItem downloadsMenuItem = new JMenuItem("Downloads");
//        downloadsMenuItem.addActionListener(e -> {
//            downloadDialog.setSize(800, 600);
//            downloadDialog.setLocationRelativeTo(null);
//            downloadDialog.setVisible(true);
//        });
//        testMenu.add(downloadsMenuItem);
//
//
//
//        add(testMenu);
//
//    }
//}
