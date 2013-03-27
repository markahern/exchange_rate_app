package controllers;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import play.mvc.*;
import views.html.*;

import javax.xml.parsers.*;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static play.libs.Json.toJson;


public class Application extends Controller {

    public static Result index() {
        return ok(index.render());
    }

    public static Result getNinetyDaysExchangeRates() throws IOException, ParserConfigurationException, SAXException, ParseException {


        //First get and parse the document
        URL exchangeRatesURL;
        exchangeRatesURL = new URL("http://www.ecb.europa.eu/stats/eurofxref/eurofxref-hist-90d.xml");
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(exchangeRatesURL.openStream());


        /*Each currencies 90 day data is in a sorted map with the date as the key
        Each of these maps is to be put in another map with the currency as the key.*/
        Map<String, SortedMap> euroExchangeNinetyDays = new TreeMap<String, SortedMap>();


        // Walk the dom and populate euroExchangeNinedays
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
            for(int j=0; j<ratesByCurrencyNodes.getLength(); j++){
                String currency = ((Element)ratesByCurrencyNodes.item(j)).getAttribute("currency");
                Float rate = Float.parseFloat(((Element) ratesByCurrencyNodes.item(j)).getAttribute("rate"));
                if(i == 0){
                    euroExchangeNinetyDays.put(currency, new TreeMap<Date, Float>());
                }
                euroExchangeNinetyDays.get(currency).put(day, rate);
            }
        }

        //return it as json
        return ok(toJson(euroExchangeNinetyDays));
    }
}
