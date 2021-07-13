package com.github.supermoonie.jbrwoserspider;

import com.formdev.flatlaf.extras.FlatSVGUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;

/**
 * @author super_w
 * @since 2021/7/11
 */
@Slf4j
public class MainFrame extends JFrame {

    @Getter
    private final JTabbedPane tabbedPane;

    public MainFrame() throws HeadlessException {
        // 设置图标
        setIconImages(FlatSVGUtils.createWindowIconImages("/icons/JBrowserSpider.svg"));
        tabbedPane = new JTabbedPane();
        tabbedPane.addChangeListener(e -> {
            String title = tabbedPane.getToolTipTextAt(tabbedPane.getSelectedIndex());
            setTitle(title);
        });
        getContentPane().add(tabbedPane);
        pack();
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setResizable(true);
        setLocationRelativeTo(null);
        setVisible(true);
    }

}
