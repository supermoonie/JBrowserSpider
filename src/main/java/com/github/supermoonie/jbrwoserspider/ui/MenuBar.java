package com.github.supermoonie.jbrwoserspider.ui;

import lombok.Getter;

import javax.swing.*;

/**
 * @author supermoonie
 * @since 2021/3/1
 */
@Getter
public class MenuBar extends JMenuBar {

    private final JFrame owner;

    public MenuBar(JFrame owner) {
        this.owner = owner;
        JMenu browserSpider = new JMenu("JBrowserSpider");
        final JMenuItem appearance = new JMenuItem("Appearance");
        appearance.addActionListener(e -> {

        });
        browserSpider.add(appearance);

        add(browserSpider);
    }
}
