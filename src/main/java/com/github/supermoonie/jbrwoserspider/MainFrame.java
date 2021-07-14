package com.github.supermoonie.jbrwoserspider;

import com.formdev.flatlaf.extras.FlatSVGUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;

import static com.formdev.flatlaf.FlatClientProperties.TABBED_PANE_TAB_CLOSABLE;
import static com.formdev.flatlaf.FlatClientProperties.TABBED_PANE_TAB_CLOSE_TOOLTIPTEXT;

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
        tabbedPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
        tabbedPane.putClientProperty(TABBED_PANE_TAB_CLOSABLE, true);
        tabbedPane.putClientProperty(TABBED_PANE_TAB_CLOSE_TOOLTIPTEXT, "Close");
        tabbedPane.addChangeListener(e -> {
            String title = tabbedPane.getToolTipTextAt(tabbedPane.getSelectedIndex());
            setTitle(title);
        });
        getContentPane().add(tabbedPane);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setSize(new Dimension(800, 600));
        setResizable(true);
        setLocationRelativeTo(null);
        setVisible(true);
    }

}
