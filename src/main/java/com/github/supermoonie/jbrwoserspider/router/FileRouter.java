package com.github.supermoonie.jbrwoserspider.router;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.github.supermoonie.jbrwoserspider.App;
import com.github.supermoonie.jbrwoserspider.router.req.FileMoveRequest;
import com.github.supermoonie.jbrwoserspider.router.req.FileSelectRequest;
import com.github.supermoonie.jbrwoserspider.router.res.FileSelectResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.browser.CefMessageRouter;
import org.cef.callback.CefQueryCallback;
import org.cef.callback.CefRunFileDialogCallback;
import org.cef.handler.CefDialogHandler;
import org.cef.handler.CefMessageRouterHandlerAdapter;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * @author supermoonie
 * @since 2021/3/3
 */
@Slf4j
public class FileRouter extends CefMessageRouterHandlerAdapter {

    private static final String FILE_FOLDER_SELECT = "file:folder_select:";
    private static final String FILE_DIALOG = "file:dialog:";
    private static final String FILE_USER_HOME = "file:user_home";
    private static final String FILE_OPEN_DIRECTORY = "file:open_directory:";
    private static final String FILE_MOVE = "file:move:";

    private static CefMessageRouter fileRouter;

    private FileRouter() {

    }

    public static CefMessageRouter getInstance() {
        if (null == fileRouter) {
            synchronized (BrowserRouter.class) {
                if (null == fileRouter) {
                    fileRouter = CefMessageRouter.create(new CefMessageRouter.CefMessageRouterConfig("fileQuery", "cancelFileQuery"));
                    fileRouter.addHandler(new FileRouter(), true);
                }
            }
        }
        return fileRouter;
    }

    @Override
    public boolean onQuery(CefBrowser browser,
                           CefFrame frame,
                           long queryId,
                           String request,
                           boolean persistent,
                           CefQueryCallback callback) {
        try {
            if (request.equals(FILE_USER_HOME)) {
                String userHome = SystemUtils.getUserHome().getAbsolutePath();
                callback.success(userHome);
                return true;
            } else if (request.startsWith(FILE_DIALOG)) {
                onDialog(browser, request, callback);
                return true;
            } else if (request.startsWith(FILE_OPEN_DIRECTORY)) {
                onOpenDirectory(request, callback);
                return true;
            } else if (request.startsWith(FILE_FOLDER_SELECT)) {
                onFolderSelect(request, callback);
                return true;
            } else if (request.startsWith(FILE_MOVE)) {
                onFileMove(request, callback);
                return true;
            }
            callback.failure(404, "no cmd found");
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            callback.failure(500, e.getMessage());
            return false;
        }
    }

    private void onFileMove(String request, CefQueryCallback callback) {
        String req = request.replace(FILE_MOVE, "");
        if (StringUtils.isEmpty(req)) {
            callback.failure(405, "cmd: " + FILE_MOVE + " args is empty!");
            return;
        }
        SwingUtilities.invokeLater(() -> {
            try {
                List<FileMoveRequest> moveRequestList = JSONObject.parseObject(req, new TypeReference<List<FileMoveRequest>>() {
                });
                List<String> targetList = new ArrayList<>();
                for (FileMoveRequest moveRequest : moveRequestList) {
                    File src = new File(moveRequest.getFrom().replace("file://", ""));
                    if (!src.exists()) {
                        targetList.add("");
                        continue;
                    }
                    File target = new File(moveRequest.getTo().replace("file://", ""));
                    if (target.exists() && !target.delete()) {
                        throw new RuntimeException(target.getAbsolutePath() + " delete fail");
                    }
                    FileUtils.moveFile(src, target);
                    targetList.add(target.getAbsolutePath());
                }
                callback.success(JSON.toJSONString(targetList));
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                callback.failure(500, e.getMessage());
            }
        });

    }

    private void onFolderSelect(String request, CefQueryCallback callback) {
        SwingUtilities.invokeLater(() -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fileChooser.setDialogTitle("选择");
            fileChooser.setApproveButtonText("选择");
            fileChooser.setControlButtonsAreShown(true);
            fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
            String req = request.replace(FILE_FOLDER_SELECT, "");
            if (StringUtils.isNotEmpty(req)) {
                File folder = new File(req);
                if (folder.exists() && folder.isDirectory()) {
                    fileChooser.setSelectedFile(folder);
                }
            }
            if (JFileChooser.APPROVE_OPTION == fileChooser.showOpenDialog(App.getInstance().getMainFrame())) {
                File folder = fileChooser.getSelectedFile();
                callback.success(folder.getAbsolutePath());
            }
        });
    }

    private void onOpenDirectory(String request, CefQueryCallback callback) {
        SwingUtilities.invokeLater(() -> {
            try {
                String req = request.replace(FILE_OPEN_DIRECTORY, "");
                if (StringUtils.isEmpty(req)) {
                    callback.failure(405, "cmd: " + FILE_OPEN_DIRECTORY + " args is empty!");
                    return;
                }
                File file = new File(req.replace("file://", ""));
                if (file.isDirectory()) {
                    Desktop.getDesktop().open(file);
                } else {
                    Desktop.getDesktop().open(file.getParentFile());
                }
                callback.success("success");
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                callback.failure(500, e.getMessage());
            }

        });

    }

    private void onDialog(CefBrowser browser, String request, CefQueryCallback callback) {
        SwingUtilities.invokeLater(() -> {
            String req = request.replace(FILE_DIALOG, "");
            if (StringUtils.isEmpty(req)) {
                callback.failure(405, "cmd: " + FILE_DIALOG + " args is empty!");
                return;
            }
            final FileSelectRequest fileSelectRequest = JSONObject.parseObject(req, FileSelectRequest.class);
            CefRunFileDialogCallback dialogCallBack = (selectedAcceptFilter, filePaths) -> {
                if (filePaths.size() == 0) {
                    callback.success("[]");
                    return;
                }
                List<FileSelectResponse> responseList = new ArrayList<>();
                for (String path : filePaths) {
                    File file = new File(path);
                    FileSelectResponse res = new FileSelectResponse();
                    res.setPath("file://" + path);
                    res.setFileName(file.getName());
                    res.setSize(file.length());
                    res.setModifyDate(file.lastModified());
                    if (2 != fileSelectRequest.getSelectType() && fileSelectRequest.getIsImage()) {
                        try {
                            BufferedImage image = ImageIO.read(file);
                            res.setWidth(image.getWidth());
                            res.setHeight(image.getHeight());
                        } catch (Exception ignore) {
                        }
                    }
                    responseList.add(res);
                }
                callback.success(JSON.toJSONString(responseList));
            };
            CefDialogHandler.FileDialogMode mode;
            if (1 == fileSelectRequest.getSelectType()) {
                mode = CefDialogHandler.FileDialogMode.FILE_DIALOG_OPEN_MULTIPLE;
            } else if (2 == fileSelectRequest.getSelectType()) {
                mode = CefDialogHandler.FileDialogMode.FILE_DIALOG_SAVE;
            } else {
                mode = CefDialogHandler.FileDialogMode.FILE_DIALOG_OPEN;
            }
            Vector<String> acceptFilters = null;
            if (null != fileSelectRequest.getExtensionFilter() && !fileSelectRequest.getExtensionFilter().isEmpty()) {
                acceptFilters = new Vector<>(fileSelectRequest.getExtensionFilter());
            }
            browser.runFileDialog(mode, fileSelectRequest.getTitle(), fileSelectRequest.getDefaultFilePath(), acceptFilters, 0, dialogCallBack);
        });

    }
}
