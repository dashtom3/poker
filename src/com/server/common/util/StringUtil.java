package com.server.common.util;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by joseph on 16/11/2.
 */
public class StringUtil {

    public static String ToXml(LinkedHashMap<String, Object> m_values)
    {
        //数据为空时不能转化为xml格式
        if (0 == m_values.size())
        {
//            Log.Error(this.GetType().ToString(), "WxPayData数据为空!");
//            throw new WxPayException("WxPayData数据为空!");
        }

        String xml = "<xml>";
        for(Map.Entry<String,Object> pair:m_values.entrySet())
        {
            //字段值不能为null，会影响后续流程
            if (pair.getValue() == null)
            {
//                Log.Error(this.GetType().ToString(), "WxPayData内部含有值为null的字段!");
//                throw new WxPayException("WxPayData内部含有值为null的字段!");
            }

            if (pair.getValue() instanceof Integer)
            {
                xml += "<" + pair.getKey() + ">" + pair.getValue() + "</" + pair.getKey() + ">";
            }
            else if (pair.getValue() instanceof String)
        {
            xml += "<" + pair.getKey() + ">" + "<![CDATA[" + pair.getValue() + "]]></" + pair.getKey() + ">";
        }
        else//除了string和int类型不能含有其他数据类型
        {
//            Log.Error(this.GetType().ToString(), "WxPayData字段数据类型错误!");
//            throw new WxPayException("WxPayData字段数据类型错误!");
        }
        }
        xml += "</xml>";
        return xml;
    }


    public LinkedHashMap<String, Object> fromXml(String xml) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(xml)));
            Element root = doc.getDocumentElement();
            NodeList nodeList = root.getChildNodes();
            if (nodeList != null) {
                for (int i = 0; i < nodeList.getLength(); i++) {
                    Node node = nodeList.item(i);
                    if (node != null && node.getFirstChild() != null)
                        map.put(node.getNodeName(), node.getFirstChild().getNodeValue());
                }
            }
            return map;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
