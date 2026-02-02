package sportclub.domain.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.File;
import java.io.IOException;
import java.util.List;
import sportclub.domain.model.Member;

public class ImportExportService {
    private final ObjectMapper jsonMapper;
    private final XmlMapper xmlMapper;
    private final CsvMapper csvMapper;

    public ImportExportService() {
        jsonMapper = new ObjectMapper();
        jsonMapper.registerModule(new JavaTimeModule());
        jsonMapper.enable(SerializationFeature.INDENT_OUTPUT);

        xmlMapper = new XmlMapper();
        xmlMapper.registerModule(new JavaTimeModule());

        csvMapper = new CsvMapper();
        csvMapper.registerModule(new JavaTimeModule());
    }

    public boolean exportToJson(List<Member> members, String filename) {
        try {
            jsonMapper.writeValue(new File(filename), members);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public boolean exportToXml(List<Member> members, String filename) {
        try {
            xmlMapper.writeValue(new File(filename), members);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public boolean exportToCsv(List<Member> members, String filename) {
        try {
            CsvSchema schema = csvMapper.schemaFor(Member.class).withHeader();
            csvMapper.writer(schema).writeValue(new File(filename), members);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public List<Member> importFromJson(String filename) throws IOException {
        return jsonMapper.readValue(
                new File(filename),
                jsonMapper.getTypeFactory().constructCollectionType(List.class, Member.class));
    }

    public List<Member> importFromXml(String filename) throws IOException {
        return xmlMapper.readValue(
                new File(filename),
                xmlMapper.getTypeFactory().constructCollectionType(List.class, Member.class));
    }

    public List<Member> importFromCsv(String filename) throws IOException {
        CsvSchema schema = csvMapper.schemaFor(Member.class).withHeader();
        return csvMapper
                .readerFor(Member.class)
                .with(schema)
                .<Member>readValues(new File(filename))
                .readAll();
    }
}
