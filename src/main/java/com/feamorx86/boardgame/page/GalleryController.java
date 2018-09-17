package com.feamorx86.boardgame.page;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;

/**
 * Created by feamor on 17.01.2018.
 */
@Controller
public class GalleryController {

    @RequestMapping(method = RequestMethod.GET, value = "./gallery/list_all" )
    public ModelAndView listAll(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("application/json; charset=utf-8");

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode root = mapper.createObjectNode();
        ArrayNode filesNode = mapper.createArrayNode();
        List<String> filesList = listImages();
        for(String file : filesList) {
            filesNode.add(file);
        }
        root.set("files", filesNode);

        String result;
        try {
            result = mapper.writeValueAsString(root);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            result = "{ \"files\" : [] }";
        }
        //response.setStatus(200);
        response.getOutputStream().print(result);
        return null;
    }

    @RequestMapping(method = RequestMethod.POST, value = "./gallery/upload" )
    public ModelAndView upload(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
            Iterator<String> fileNames = multipartRequest.getFileNames();
            while (fileNames.hasNext()) {
                String name = fileNames.next();
                MultipartFile file = multipartRequest.getFile(name);
                if (file != null && !file.isEmpty()) {
                    File saveTo = new File("./src/main/resources/web/static/" + file.getOriginalFilename());
                    if (saveTo.exists()) {
                        saveTo.delete();
                    }
                    BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(saveTo, false));
                    FileCopyUtils.copy(file.getInputStream(), stream);
                    stream.close();
                }
            }
            response.setContentType("application/json; charset=utf-8");
            response.getOutputStream().print("{ \"result\" : \"success\" }");
        } catch (Exception ex) {
            ex.printStackTrace();
            response.setContentType("application/json; charset=utf-8");
            response.getOutputStream().print("{ \"result\" : \"error\", \"text\" : \""+ex.toString()+"\" }");
        }
        return null;
    }

    private ArrayList<String> listImages() {
        ArrayList<String> result = new ArrayList<>();
        File folder = new File("./src/main/resources/web/static");
        File[] imageFiles = folder.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                String fileName = pathname.getName().toLowerCase();
                if (fileName.endsWith(".jpg") | fileName.endsWith(".png")) {
                    return true;
                }
                return false;
            }
        });
        if (imageFiles != null && imageFiles.length > 0) {
            Arrays.sort(imageFiles, new Comparator<File>() {
                @Override
                public int compare(File o1, File o2) {
                    return (int) (o2.lastModified()- o1.lastModified());
                }
            });
            for (final File fileEntry : imageFiles) {
                result.add("./web/static/" + fileEntry.getName());
            }
        }
        return result;
    }
}
