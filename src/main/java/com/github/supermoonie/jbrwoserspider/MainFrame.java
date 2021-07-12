package com.github.supermoonie.jbrwoserspider;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.extras.FlatSVGUtils;
import com.github.supermoonie.jbrwoserspider.ui.HomeTab;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;

/**
 * @author super_w
 * @since 2021/7/11
 */
public class MainFrame extends JFrame {

    @Getter
    private final JTabbedPane tabbedPane;
    @Getter
    private final HomeTab homeTab;

    public MainFrame() throws HeadlessException {
        // 设置图标
        setIconImages(FlatSVGUtils.createWindowIconImages("/icons/JBrowserSpider.svg"));
        tabbedPane = new JTabbedPane();
        homeTab = new HomeTab();
        tabbedPane.addTab("Home", new FlatSVGIcon( "icons/homeFolder.svg", 16, 16), homeTab, "主页");
        getContentPane().add(tabbedPane);
        pack();
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setResizable(true);
        setLocationRelativeTo(null);
        setVisible(true);
    }


}
