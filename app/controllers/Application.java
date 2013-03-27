package controllers;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import play.mvc.*;

import javax.xml.parsers.*;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static play.libs.Json.toJson;


public class Application extends Controller {
  
    public static Result index() throws IOException, ParserConfigurationException, SAXException, ParseException {
        URL exchangeRatesURL;
        exchangeRatesURL = new URL("http://www.ecb.europa.eu/stats/eurofxref/eurofxref-hist-90d.xml");
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(exchangeRatesURL.openStream());
        SortedMap<Date, Map> euroExchangeNinetyDays = new TreeMap<Date, Map>();
        NodeList rates = doc.getElementsByTagName("Cube");
        String output = "";
        Node rootCube = doc.getElementsByTagName("Cube").item(0);
        NodeList ratesByDate = rootCube.getChildNodes();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");


        for(int i=0; i<ratesByDate.getLength(); i++){
            Element ratesForDayElement;
            if(ratesByDate.item(i) instanceof Element){
                ratesForDayElement = (Element)ratesByDate.item(i);
            } else {
                continue;
            }

            Date day = sdf.parse(ratesForDayElement.getAttribute("time"));
            NodeList ratesByCurrencyNodes = ratesForDayElement.getChildNodes();
            Map<String, Float> exchangeRates = new HashMap<String, Float>();
            for(int j=0; j<ratesByCurrencyNodes.getLength(); j++){
                String currency = ((Element)ratesByCurrencyNodes.item(j)).getAttribute("currency");
                Float rate = Float.parseFloat(((Element) ratesByCurrencyNodes.item(j)).getAttribute("rate"));
                exchangeRates.put(currency, rate);
            }
            euroExchangeNinetyDays.put(day, exchangeRates);
        }
        return ok(toJson(euroExchangeNinetyDays));
    }




  
}
