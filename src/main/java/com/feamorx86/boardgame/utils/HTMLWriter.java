package com.feamorx86.boardgame.utils;

import org.springframework.util.StringUtils;
import org.springframework.web.util.HtmlUtils;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by Home on 25.08.2017.
 */
public class HTMLWriter {
    private StringBuilder builder = new StringBuilder();

    public HTMLWriter fromFile(String path) {
        File file = new File(path);
        if (file.exists()) {
            try {
                FileInputStream stream = new FileInputStream(file);
                InputStreamReader reader = new InputStreamReader(stream);
                char buffer[] = new char[2048];
                int read;
                try {
                    while ((read = reader.read(buffer)) > 0) {
                        builder.append(buffer, 0, read);
                    }
                } catch (IOException ioex) {
                    ioex.printStackTrace();
                } finally {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        stream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }
        }
        return this;
    }

    public HTMLWriter start() {
        builder.setLength(0);
        tagStack.clear();
        return this;
    }

    public HTMLWriter startPage() {
        builder.append("<html><head><meta charset=\"utf-8\"/></head><body>");
        return this;
    }

    public HTMLWriter endPage() {
        builder.append("</body></html>");
        return this;
    }

    public HTMLWriter title(String title) {
        String escapedTitle = HtmlUtils.htmlEscape(title);
        builder.append("<h1>").append(escapedTitle).append("</h1>");
        return this;
    }

    public HTMLWriter subtitle(String title) {
        String escapedTitle = HtmlUtils.htmlEscape(title);
        builder.append("<h2>").append(escapedTitle).append("</h2>");
        return this;
    }

    public HTMLWriter ln() {
        builder.append("<br>");
        return this;
    }

    public HTMLWriter line() {
        builder.append("<hr>");
        return this;
    }

    public HTMLWriter writeln(String text) {
        String escapedText = HtmlUtils.htmlEscape(text);
        builder.append("<p>").append(escapedText).append("</p>");
        return this;
    }

    public HTMLWriter writeln(String text, String pAttributes) {
        String escapedText = HtmlUtils.htmlEscape(text);
        if (StringUtils.isEmpty(pAttributes)) {
            builder.append("<p>");
        } else {
            builder.append("<p ").append(pAttributes).append(" >");
        }
        builder.append(escapedText).append("</p>");
        return this;
    }

    public HTMLWriter writeMultiline(String text) {
        String[] lines =text.split("\\r?\\n");
        for (String line : lines) {
            builder.append("<p>").append(HtmlUtils.htmlEscape(line)).append("</p>");
        }
        return this;
    }

    public HTMLWriter writeMultiline(String text, String pAttributes) {
        String[] lines =text.split("\\r?\\n");
        for (String line : lines) {
            builder.append("<p ").append(pAttributes).append(" >").append(HtmlUtils.htmlEscape(line)).append("</p>");
        }
        return this;
    }

    public HTMLWriter appendHtml(String htmlCode) {
        builder.append(htmlCode);
        return this;
    }

    public StringBuilder getBuilder() {
        return  builder;
    }

    public HTMLWriter ref(String text, String ref) {
        String escapedText = HtmlUtils.htmlEscape(text);
        builder.append("<a href=\""+ref+"\">").append(escapedText).append("</a>");
        return this;
    }

    public HTMLWriter refline(String text, String ref) {
        String escapedText = HtmlUtils.htmlEscape(text);
        builder.append("<a href=\""+ref+"\"><p>").append(escapedText).append("</p></a>");
        return this;
    }

    public static String makeRefTag(String text, String ref) {
        String escapedText = HtmlUtils.htmlEscape(text);
        StringBuilder result = new StringBuilder();
        result.append("<a href=\"").append(ref).append("\">").append(escapedText).append("</a>");
        return result.toString();
    }
    public HTMLWriter startTable(String ... headers) {
        builder.append("<table border=\"1\">");
        if (headers!=null) {
            builder.append("<tr>");
            for(String th : headers) {
                builder.append("<th>").append(HtmlUtils.htmlEscape(th)).append("</th>");
            }
            builder.append("</tr>");
        }
        return this;
    }

    public HTMLWriter tableLine(String ... columns) {
        builder.append("<tr>");
        if (columns!=null) {
            for(String th : columns) {
                builder.append("<td>").append(th).append("</td>");
            }
        }
        builder.append("</tr>");
        return this;
    }

    public HTMLWriter openTableLine(String ... columns) {
        builder.append("<tr>");
        if (columns!=null) {
            for(String th : columns) {
                builder.append("<td>").append(th).append("</td>");
            }
        }
        return this;
    }

    public HTMLWriter closeTableLine() {
        builder.append("</tr>");
        return this;
    }

    public HTMLWriter endTable() {
        builder.append("</table>");
        return this;
    }

    public HTMLWriter tag(String tag, String text){
        builder.append("<").append(tag).append(">").append(HtmlUtils.htmlEscape(text)).append("</").append(tag).append(">");
        return this;
    }

    public HTMLWriter tagNoEscape(String tag, String noEscapeText){
        builder.append("<").append(tag).append(">").append(noEscapeText).append("</").append(tag).append(">");
        return this;
    }

    public HTMLWriter startForm(String action, String method) {
        builder.append("<form action=\"").append(action).append("\" ");
        if (StringUtils.isEmpty(method)) {
            method = "GET";
        }
        builder.append(" method=\"").append(method).append("\">");
        return this;
    }

    public HTMLWriter endForm() {
        builder.append("</form>");
        return this;
    }

    public HTMLWriter button(String name, String value) {
        return input("button", name, value, null, null);
    }

    public HTMLWriter edit(String name, String value) {
        return input("text", name, value, null, null);
    }

    public HTMLWriter startInput(String type, String name, String value, String attributes) {
        builder.append("<input ");
        if (!StringUtils.isEmpty(type)) {
            builder.append("type=\"").append(type).append("\" ");
        }
        if (!StringUtils.isEmpty(name)) {
            builder.append("name=\"").append(name).append("\" ");
        }
        if (!StringUtils.isEmpty(value)) {
            builder.append("value=\"").append(value).append("\" ");
        }
        if (!StringUtils.isEmpty(attributes)) {
            builder.append(attributes).append(" ");
        }
        builder.append(">");
        return this;
    }

    public HTMLWriter endInput() {
        builder.append("</input>");
        return this;
    }

    public HTMLWriter textArea(String name, boolean autoFocus, boolean disabled, Integer cols, Integer rows, String attributes, String text) {
        builder.append("<textarea ");
        if (!StringUtils.isEmpty(name)) builder.append("name=\"").append(HtmlUtils.htmlEscape(name)).append("\" ");
        if (autoFocus) builder.append("autofocus ");
        if (disabled) builder.append("disabled ");

        if (cols != null) builder.append("cols=\"" + cols.intValue() + "\" ");
        if (rows != null) builder.append("rows=\"" + rows.intValue() + "\" ");
        if (!StringUtils.isEmpty(attributes)) builder.append(attributes);
        builder.append(">");

        String escapedText = HtmlUtils.htmlEscape(text);
        builder.append(escapedText);

        builder.append("</textarea>");
        return this;
    }

    public HTMLWriter input(String type, String name, String value, String attributes, String rawText) {
        builder.append("<input ");
        if (!StringUtils.isEmpty(type)) {
            builder.append("type=\"").append(type).append("\" ");
        }
        if (!StringUtils.isEmpty(name)) {
            builder.append("name=\"").append(name).append("\" ");
        }
        if (!StringUtils.isEmpty(value)) {
            builder.append("value=\"").append(value).append("\" ");
        }
        if (!StringUtils.isEmpty(attributes)) {
            builder.append(attributes).append(" ");
        }
        if (!StringUtils.isEmpty(rawText)) {
            builder.append(">").append(rawText).append("</input>");
        } else{
            builder.append("/>");
        }
        return this;
    }

    public HTMLWriter submit(String title) {
        builder
                .append("<br>")
                .append("<input type=\"submit\" value=\"").append(title).append("\" />");
        return this;
    }

    private ArrayList<String> tagStack = new ArrayList<String>();

    public HTMLWriter push(String tag) {
        builder.append("<").append(tag).append(">");
        tagStack.add(tag);
        return this;
    }

    public HTMLWriter push(String tag, String rawText) {
        push(tag);
        builder.append(rawText);
        return this;
    }

    public HTMLWriter pop() {
        if (tagStack.size() > 0) {
            builder.append("</").append(tagStack.remove(tagStack.size()-1)).append(">");
        }
        return this;
    }

    public HTMLWriter pop(String rawText) {
        builder.append(rawText);
        return pop();
    }

    public HTMLWriter select(Integer size, String name, String[] optionValues, String[] optionNames, int selectedId) {
        builder.append("<br>").append("<select ");
        if (size != null) {
            builder.append("size=\"").append(size).append("\" ");
        }
        if (!StringUtils.isEmpty(name)) {
            builder.append("name=\"").append(name).append("\" ");
        }
        builder.append(">");
        if (optionValues != null && optionNames != null && optionValues.length == optionNames.length) {
            for(int i = 0; i<optionValues.length; i++) {
                if (selectedId == i) {
                    builder.append("<option selected value=\"").append(optionValues[i]).append("\">").append(optionNames[i]).append("</option>");
                } else {
                    builder.append("<option value=\"").append(optionValues[i]).append("\">").append(optionNames[i]).append("</option>");
                }
            }
        } else {
            builder.append("<p>Fail to add items. Values or Names is null or different length.</p>");
        }
        builder.append("</select>");
        return  this;
    }

    public HTMLWriter select(Integer size, String name, String[] optionValues, String[] optionNames, String selectedValue) {
        builder.append("<br>").append("<select ");
        if (size != null) {
            builder.append("size=\"").append(size).append("\" ");
        }
        if (!StringUtils.isEmpty(name)) {
            builder.append("name=\"").append(name).append("\" ");
        }
        builder.append(">");
        if (optionValues != null && optionNames != null && optionValues.length == optionNames.length) {
            for(int i = 0; i<optionValues.length; i++) {
                if (selectedValue.equals(optionValues[i])) {
                    builder.append("<option selected value=\"").append(optionValues[i]).append("\">").append(optionNames[i]).append("</option>");
                } else {
                    builder.append("<option value=\"").append(optionValues[i]).append("\">").append(optionNames[i]).append("</option>");
                }
            }
        } else {
            builder.append("<p>Fail to add items. Values or Names is null or different length.</p>");
        }
        builder.append("</select>");
        return  this;
    }

    @Override
    public String toString() {
        return builder.toString();
    }

    public static String escapeHtml(String text) {
        return HtmlUtils.htmlEscape(text);
    }

}