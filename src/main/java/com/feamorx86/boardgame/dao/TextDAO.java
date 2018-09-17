package com.feamorx86.boardgame.dao;

import com.feamorx86.boardgame.model.TextData;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.annotation.PostConstruct;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by Home on 28.08.2017.
 */
@Transactional
@Scope(value = "singleton")
@Repository
public class TextDAO {
    @Autowired
    private SessionFactory sessionFactory;

    private HashMap<String, String> strings = new HashMap<>();

    @PostConstruct
    private void initialize() {
        loadStrings();
    }

    public String getString(String id) {
        String result = strings.get(id.toLowerCase());
        return result == null ? "" : result;
    }

    public String getStringAndFormat(String id, Object ... args) {
        String result = strings.get(id.toLowerCase());
        result = result == null ? "" : String.format(result, args);
        return result;
    }

    public void loadStrings() {
        strings.clear();
        File xmlFile = new File("./src/main/resources/static/strings.xml");
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
            Document doc = builder.parse(xmlFile);
            doc.getDocumentElement().normalize();
            NodeList nodeList= doc.getElementsByTagName("string");
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                String id = node.getAttributes().getNamedItem("name").getNodeValue();
                String text = node.getTextContent();
                strings.put(id.toLowerCase(), text);
            }
        } catch (SAXException | ParserConfigurationException | IOException e1) {
            e1.printStackTrace();
        }
    }

    public TextData get(int id) {
        TextData result = sessionFactory.getCurrentSession().get(TextData.class, id);
        return result;
    }

    public TextData create(int type, String text) {
        TextData data = new TextData();
        data.setType(type);
        data.setText(text);
        sessionFactory.getCurrentSession().save(data);
        return data;
    }

    public void save(TextData data) {
        sessionFactory.getCurrentSession().save(data);
    }

    public void update(TextData data) {
        sessionFactory.getCurrentSession().update(data);
    }

    public void update(int id, int type, String text) {
        TextData data = new TextData();
        data.setId(id);
        data.setType(type);
        data.setText(text);
        update(data);
    }
}
