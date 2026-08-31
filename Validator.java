import java.io.File;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.xml.sax.SAXException;

public class ValidatorTest {
    public static void main(String[] args) {
        try {
            SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
            Schema schema = factory.newSchema(new File("dist/game/data/xsd/items.xsd"));
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(new File("dist/game/data/stats/items/custom/tattoos.xml")));
            System.out.println("tattoos.xml is VALID!");
        } catch (SAXException e) {
            System.out.println("VALIDATION ERROR: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
