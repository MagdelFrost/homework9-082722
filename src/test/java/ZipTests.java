import com.codeborne.pdftest.PDF;
import com.codeborne.xlstest.XLS;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipFile;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;

public class ZipTests {

    ClassLoader classLoader = ZipTests.class.getClassLoader();


    String pdfName = "nirs.pdf";
    String csvName = "csv.csv";
    String xlsxName = "test.xls";
    String zipName = "zipTest.zip";
    String jsonName = "iWas.json";

    @Test
    void  xlsTest() throws Exception {
        try(InputStream xlsFileStream = getFile(zipName, xlsxName)) {
            XLS xls = new XLS(xlsFileStream);
            assertThat(xls.excel
                    .getSheetAt(0)
                    .getRow(0)
                    .getCell(0)
                    .getStringCellValue()).contains("I");
        }
    }

    @Test
    void pdfTest() throws Exception {
        try(InputStream pdfFileStream = getFile(zipName, pdfName)) {
            PDF pdf = new PDF(pdfFileStream);
            assertThat(pdf.text).contains("Рис. 1");
        }
    }

    @Test
    void csvTest() throws Exception {
        try(InputStream csvFileStream = getFile(zipName, csvName)) {
            CSVReader csvReader = new CSVReader(new InputStreamReader(csvFileStream, UTF_8));
            List<String[]> csv = csvReader.readAll();
            assertThat(csv).contains(
                    new String[]{"Я", " РОЖДЕН", " ДЛЯ", " ТЕСТА"}
            );
        }
    }

    @Test
    void jsonTest() throws Exception {
        InputStream is = classLoader.getResourceAsStream(jsonName);
        ObjectMapper objectMapper = new ObjectMapper();
        IWas iWas = objectMapper.readValue(is, IWas.class);
        assertThat(iWas.firstCharacter).isEqualTo("I");
        assertThat(iWas.secondCharacter).isEqualTo("WAS");
        assertThat(iWas.thirdCharacter).isEqualTo("BORN");
        List<String> list = Arrays.asList("FOR","TEST");
        assertThat(iWas.fifthSixthCharacter).isEqualTo(list);
    }

    private InputStream getFile(String zipName, String fileName) throws Exception {
        URL zipPath = classLoader.getResource(zipName);
        File zipFile = new File(zipPath.toURI());
        ZipFile zip = new ZipFile(zipFile);
        return zip.getInputStream(zip.getEntry(fileName));
    }
}