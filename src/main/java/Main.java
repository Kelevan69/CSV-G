import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        // Выполнение задачи 1
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileNameCSV = "data.csv";

        List<Employee> listCSV = parseCSV(columnMapping, fileNameCSV);
        if (listCSV != null) {
            String jsonCSV = listToJson(listCSV);
            writeString(jsonCSV, "data.json");
        }

        // Выполнение задачи 2
        List<Employee> listXML = parseXML("data.xml");
        if (listXML != null) {
            String jsonXML = listToJson(listXML);
            writeString(jsonXML, "data2.json");
        }
    }

    private static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        try (FileReader fileReader = new FileReader(fileName)) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);

            CsvToBean<Employee> csvToBean = new CsvToBeanBuilder<Employee>(fileReader)
                    .withMappingStrategy(strategy)
                    .build();

            return csvToBean.parse();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static List<Employee> parseXML(String fileName) {
        List<Employee> employeeList = new ArrayList<>();
        try {
            File inputFile = new File(fileName);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getElementsByTagName("employee");

            for (int temp = 0; temp < nodeList.getLength(); temp++) {
                Node node = nodeList.item(temp);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;

                    long id = Long.parseLong(element.getElementsByTagName("id").item(0).getTextContent());
                    String firstName = element.getElementsByTagName("firstName").item(0).getTextContent();
                    String lastName = element.getElementsByTagName("lastName").item(0).getTextContent();
                    String country = element.getElementsByTagName("country").item(0).getTextContent();
                    int age = Integer.parseInt(element.getElementsByTagName("age").item(0).getTextContent());

                    Employee employee = new Employee(id, firstName, lastName, country, age);
                    employeeList.add(employee);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return employeeList;
    }

    private static String listToJson(List<Employee> list) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.setPrettyPrinting().create();
        Type listType = new TypeToken<List<Employee>>() {}.getType();
        return gson.toJson(list, listType);
    }

    private static void writeString(String json, String fileName) {
        try (FileWriter fileWriter = new FileWriter(fileName)) {
            fileWriter.write(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}