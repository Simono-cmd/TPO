/**
 *
 *  @author Trauth Szymon  S30749
 *
 */

package zad1;

import org.yaml.snakeyaml.*;
import org.yaml.snakeyaml.constructor.Constructor;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class Tools {

    public static Options createOptionsFromYaml(String fileName) throws FileNotFoundException {
        InputStream is = new FileInputStream(new File(fileName));
        Yaml yaml = new Yaml();
        Map<String, Object> data = yaml.load(is);

        String host = (String) data.get("host");
        int port = (int) data.get("port");
        boolean concurMode = (boolean) data.get("concurMode");
        boolean showSendRes = (boolean) data.get("showSendRes");
        Map<String, List<String>> clientsMap = (Map<String, List<String>>) data.get("clientsMap");

        return new Options(host, port, concurMode, showSendRes, clientsMap);
    }

    public static void main(String[] args) throws FileNotFoundException {
        String fileName = System.getProperty("user.home") + "/PassTimeOptions.yaml";
        System.out.println(createOptionsFromYaml(fileName));
    }


}


