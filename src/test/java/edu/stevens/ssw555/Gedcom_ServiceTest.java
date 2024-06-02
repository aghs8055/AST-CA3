package edu.stevens.ssw555;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import java.io.*;
import java.nio.file.*;
import java.text.ParseException;
import java.util.*;

public class Gedcom_ServiceTest {
    private static final PrintStream originalOut = System.out;
    private static final InputStream originalIn = System.in;
    private static final PrintStream originalErr = System.err;
    private ByteArrayOutputStream outContent;
    private ByteArrayOutputStream errContent;
    private Path tempFile;

    @Before
    public void setUp() throws IOException {
        // Create a temporary file
        tempFile = Files.createTempFile("testFile", ".txt");
        String tempFilePath = tempFile.toString();

        // Simulate user input for the file path
        ByteArrayInputStream inContent = new ByteArrayInputStream(tempFilePath.getBytes());
        System.setIn(inContent);

        // Call the method to create the output file
        Gedcom_Service.createOutputFile();

        // Read and parse the GEDCOM file
        Gedcom_Service.readAndParseFile("src/test/java/edu/stevens/ssw555/tests/gedcom_service_test.ged");


        // Redirect the standard output and standard error to capture printed output
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        errContent = new ByteArrayOutputStream();
        System.setErr(new PrintStream(errContent));
    }

    @After
    public void tearDown() throws IOException {
        // Restore the original System.in, System.out, and System.err
        System.setOut(originalOut);
        System.setIn(originalIn);
        System.setErr(originalErr);

        // Delete the temporary file
        Files.deleteIfExists(tempFile);
    }

    public void assertStreamOutput(String expectedOutput) {
        assertTrue(outContent.toString().contains(expectedOutput));
    }

    public void assertOutputFile(String expectedOutput) throws IOException {
        String content = new String(Files.readAllBytes(Paths.get("GedcomService_output.txt")));
        assertTrue(content.contains(expectedOutput));
    }

    public void assertOutput(String expectedOutput) {
        assertStreamOutput(expectedOutput);
        try {
            assertOutputFile(expectedOutput);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void assertErrorOutput(String expectedOutput) {
        assertTrue(errContent.toString().contains(expectedOutput));
    }

    public void assertEmptyOutputStream() {
        assertEquals("", outContent.toString());
    }

    public void assertEmptyOutputFile() {
        try {
            assertEquals("", new String(Files.readAllBytes(Paths.get("GedcomService_output.txt"))));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void assertEmptyOutput() {
        assertEmptyOutputStream();
        assertEmptyOutputFile();
    }

    @Test
    public void testMain() throws IOException, ParseException {
        tempFile = Files.createTempFile("testFile", ".txt");
        String tempFilePath = tempFile.toString();

        String input_file = "src/test/java/edu/stevens/ssw555/tests/gedcom_service_test.ged\n";

        ByteArrayInputStream inContent1 = new ByteArrayInputStream(input_file.getBytes());
        ByteArrayInputStream inContent2 = new ByteArrayInputStream(tempFilePath.getBytes());

        SequenceInputStream sequenceInputStream = new SequenceInputStream(
                inContent1, inContent2
        );
        System.setIn(sequenceInputStream);

        Gedcom_Service.main(new String[0]);

        Files.deleteIfExists(tempFile);

        assertOutputFile("ERROR:INDIVIDUAL: User Story US03: Birth Before Death \n" +
                "Individual: @I2@ - Bob Williams was born after death\n" +
                "DOB: 10/19/1997 DOD: 05/12/1995\n" +
                "\n" +
                "ERROR:INDIVIDUAL: User Story US03: Birth Before Death \n" +
                "Individual: @I8@ - Bob Williams was born after death\n" +
                "DOB: 10/19/1997 DOD: 05/12/1995\n" +
                "\n" +
                "ERROR:FAMILY: User Story US04: Marriage Before Divorce \n" +
                "Family: @F1@\n" +
                "Individual: @I1@: Emily Williams@I2@: Bob Williams marriage date is before divorce date.\n" +
                "Marriage Date: 07/22/1980 Divorce Date: 07/19/1975\n" +
                "\n" +
                "ERROR: User Story US08: Birth Before Marriage Date \n" +
                "Family ID: @F1@\n" +
                "Individual: @I3@: Emma Davis Has been born before parents' marriage\n" +
                "DOB: 04/18/1960 Parents Marriage Date: 07/22/1980\n" +
                "\n" +
                "\n" +
                "ERROR: User Story US08: Birth Before Marriage Date \n" +
                "Family ID: @F1@\n" +
                "Individual: @I4@: Robert Jones Has been born before parents' marriage\n" +
                "DOB: 07/11/1960 Parents Marriage Date: 07/22/1980\n" +
                "\n" +
                "\n" +
                "ERROR: User Story US08: Birth Before Marriage Date \n" +
                "Family ID: @F4@\n" +
                "Individual: @I9@: Emma Davis Has been born before parents' marriage\n" +
                "DOB: 04/18/1960 Parents Marriage Date: 10/6/2015\n" +
                "\n" +
                "\n" +
                "ERROR: User Story US20: Aunts and Uncles\n" +
                "Individual: @I6@ - Helen Jones is married to either their aunt or uncle @I3@ - Emma Davis\n" +
                "\n" +
                "\n"
        );
    }

    @Test
    public void testMainWithError() throws IOException, ParseException {
        tempFile = Files.createTempFile("testFile", ".txt");
        String tempFilePath = tempFile.toString();

        String invalid_input_file = "test\n";
        String input_file = "src/test/java/edu/stevens/ssw555/tests/gedcom_service_test.ged\n";

        ByteArrayInputStream inContent1 = new ByteArrayInputStream(invalid_input_file.getBytes());
        ByteArrayInputStream inContent2 = new ByteArrayInputStream((tempFilePath + "\n").getBytes());
        ByteArrayInputStream inContent3 = new ByteArrayInputStream(input_file.getBytes());
        ByteArrayInputStream inContent4 = new ByteArrayInputStream((tempFilePath + "\n").getBytes());

        SequenceInputStream sequenceInputStream = new SequenceInputStream(
                new SequenceInputStream(inContent1, inContent2), new SequenceInputStream(inContent3, inContent4)
        );
        System.setIn(sequenceInputStream);

        Gedcom_Service.main(new String[0]);

        Files.deleteIfExists(tempFile);

        assertStreamOutput("Please Enter the Input File Path with filename: \n" +
                "Please Enter Output File Path: \n" +
                "File Not Found. Please reenter path\n" +
                "Please Enter the Input File Path with filename: \n" +
                "Please Enter Output File Path: \n" +
                "\n" +
                "\n" +
                "ERROR:INDIVIDUAL: User Story US03: Birth Before Death \n" +
                "Individual: @I2@ - Bob Williams was born after death\n" +
                "DOB: 10/19/1997 DOD: 05/12/1995\n" +
                "\n" +
                "\n" +
                "\n" +
                "ERROR:INDIVIDUAL: User Story US03: Birth Before Death \n" +
                "Individual: @I8@ - Bob Williams was born after death\n" +
                "DOB: 10/19/1997 DOD: 05/12/1995\n" +
                "\n" +
                "ERROR:FAMILY: User Story US04: Marriage Before Divorce \n" +
                "Family: @F1@\n" +
                "Individual: @I1@: Emily Williams@I2@: Bob Williams marriage date is before divorce date.\n" +
                "Marriage Date: 07/22/1980 Divorce Date: 07/19/1975\n" +
                "\n" +
                "ERROR: User Story US08: Birth Before Marriage Date \n" +
                "Family ID: @F1@\n" +
                "Individual: @I3@: Emma Davis Has been born before parents' marriage\n" +
                "DOB: 04/18/1960 Parents Marriage Date: 07/22/1980\n" +
                "\n" +
                "\n" +
                "ERROR: User Story US08: Birth Before Marriage Date \n" +
                "Family ID: @F1@\n" +
                "Individual: @I4@: Robert Jones Has been born before parents' marriage\n" +
                "DOB: 07/11/1960 Parents Marriage Date: 07/22/1980\n" +
                "\n" +
                "\n" +
                "ERROR: User Story US08: Birth Before Marriage Date \n" +
                "Family ID: @F4@\n" +
                "Individual: @I9@: Emma Davis Has been born before parents' marriage\n" +
                "DOB: 04/18/1960 Parents Marriage Date: 10/6/2015\n" +
                "\n" +
                "\n" +
                "ERROR: User Story US20: Aunts and Uncles\n" +
                "Individual: @I6@ - Helen Jones is married to either their aunt or uncle @I3@ - Emma Davis\n" +
                "\n" +
                "\n"
        );
        assertOutputFile("ERROR:INDIVIDUAL: User Story US03: Birth Before Death \n" +
                "Individual: @I2@ - Bob Williams was born after death\n" +
                "DOB: 10/19/1997 DOD: 05/12/1995\n" +
                "\n" +
                "ERROR:INDIVIDUAL: User Story US03: Birth Before Death \n" +
                "Individual: @I8@ - Bob Williams was born after death\n" +
                "DOB: 10/19/1997 DOD: 05/12/1995\n" +
                "\n" +
                "ERROR:FAMILY: User Story US04: Marriage Before Divorce \n" +
                "Family: @F1@\n" +
                "Individual: @I1@: Emily Williams@I2@: Bob Williams marriage date is before divorce date.\n" +
                "Marriage Date: 07/22/1980 Divorce Date: 07/19/1975\n" +
                "\n" +
                "ERROR: User Story US08: Birth Before Marriage Date \n" +
                "Family ID: @F1@\n" +
                "Individual: @I3@: Emma Davis Has been born before parents' marriage\n" +
                "DOB: 04/18/1960 Parents Marriage Date: 07/22/1980\n" +
                "\n" +
                "\n" +
                "ERROR: User Story US08: Birth Before Marriage Date \n" +
                "Family ID: @F1@\n" +
                "Individual: @I4@: Robert Jones Has been born before parents' marriage\n" +
                "DOB: 07/11/1960 Parents Marriage Date: 07/22/1980\n" +
                "\n" +
                "\n" +
                "ERROR: User Story US08: Birth Before Marriage Date \n" +
                "Family ID: @F4@\n" +
                "Individual: @I9@: Emma Davis Has been born before parents' marriage\n" +
                "DOB: 04/18/1960 Parents Marriage Date: 10/6/2015\n" +
                "\n" +
                "\n" +
                "ERROR: User Story US20: Aunts and Uncles\n" +
                "Individual: @I6@ - Helen Jones is married to either their aunt or uncle @I3@ - Emma Davis\n" +
                "\n" +
                "\n"
        );
    }

    @Test
    public void testCreateOutputFileWithError() throws IOException {
        tempFile = Files.createTempFile("testFile", ".txt");
        String tempFilePath = tempFile.toString();

        String invalid_output_file = "test\n";

        ByteArrayInputStream inContent1 = new ByteArrayInputStream(invalid_output_file.getBytes());
        ByteArrayInputStream inContent2 = new ByteArrayInputStream((tempFilePath + "\n").getBytes());

        SequenceInputStream sequenceInputStream = new SequenceInputStream(
                inContent1, inContent2
        );
        System.setIn(sequenceInputStream);

        Gedcom_Service.createOutputFile();

        Files.deleteIfExists(tempFile);

        assertStreamOutput("Please Enter Output File Path: \n" +
                "The Path You Entered Does Not Exist.Reenter path\n" +
                "Please Enter Output File Path: \n");
    }

    @Test
    public void testBirthBeforeDeathWithoutError() throws IOException {
        // Prepare the individuals data
        HashMap<String, Individual> individuals = new HashMap<>();
        Individual individual = new Individual("Ind1");
        individual.setName("David Brown");
        individual.setBirth("01/01/2000");
        individual.setDeath("01/01/2001");
        individuals.put(individual.getId(), individual);

        // Call the method to be tested
        Gedcom_Service.birthBeforeDeath(individuals);

        assertEmptyOutput();
    }

    @Test
    public void testBirthBeforeDeathWithoutErrorWithEqualBirthAndDeath() throws IOException {
        // Prepare the individuals data
        HashMap<String, Individual> individuals = new HashMap<>();
        Individual individual = new Individual("Ind1");
        individual.setName("David Brown");
        individual.setBirth("01/01/2001");
        individual.setDeath("01/01/2001");
        individuals.put(individual.getId(), individual);

        // Call the method to be tested
        Gedcom_Service.birthBeforeDeath(individuals);

        assertEmptyOutput();
    }

    @Test
    public void testBirthBeforeDeathWithoutDeath() throws IOException {
        // Prepare the individuals data
        HashMap<String, Individual> individuals = new HashMap<>();
        Individual individual = new Individual("Ind1");
        individual.setName("David Brown");
        individual.setBirth("01/01/2000");
        individuals.put(individual.getId(), individual);

        // Call the method to be tested
        Gedcom_Service.birthBeforeDeath(individuals);

        assertEmptyOutput();
    }

    @Test
    public void testBirthBeforeDeathWithError() throws IOException {
        // Prepare the individuals data
        HashMap<String, Individual> individuals = new HashMap<>();
        Individual individual = new Individual("Ind1");
        individual.setName("David Brown");
        individual.setBirth("01/01/2000");
        individual.setDeath("01/01/1999");
        individuals.put(individual.getId(), individual);

        // Call the method to be tested
        Gedcom_Service.birthBeforeDeath(individuals);

        assertOutput("ERROR:INDIVIDUAL: User Story US03: Birth Before Death \n");
        assertOutput("Individual: Ind1 - David Brown was born after death\n");
        assertOutput("DOB: 01/01/2000 DOD: 01/01/1999\n");
    }

    @Test
    public void testBirthBeforeDeathWithParseError() throws IOException {
        // Prepare the individuals data
        HashMap<String, Individual> individuals = new HashMap<>();
        Individual individual = new Individual("Ind1");
        individual.setName("David Brown");
        individual.setBirth("test");
        individuals.put(individual.getId(), individual);

        // Call the method to be tested
        Gedcom_Service.birthBeforeDeath(individuals);

        assertEmptyOutput();
        assertErrorOutput("java.text.ParseException: Unparseable date: \"test\"");
    }

    @Test
    public void testMarriageBeforeDivorceWithoutError() throws IOException {
        // Prepare individuals data
        HashMap<String, Individual> indivisuals = new HashMap<>();
        Individual individual = new Individual("Ind1");
        individual.setName("David Brown");
        indivisuals.put(individual.getId(), individual);
        individual = new Individual("Ind2");
        individual.setName("Mary Brown");
        indivisuals.put(individual.getId(), individual);

        // Prepare the families data
        HashMap<String, Family> families = new HashMap<>();
        Family family = new Family("F1");
        family.setMarriage("01/01/2000");
        family.setDivorce("01/01/2001");
        family.setHusb("Ind1");
        family.setWife("Ind2");
        families.put(family.getId(), family);

        // Call the method to be tested
        Gedcom_Service.Marriagebeforedivorce(indivisuals, families);

        assertEmptyOutput();
    }

    @Test
    public void testMarriageBeforeDivorceWithoutDivorce() throws IOException {
        // Prepare individuals data
        HashMap<String, Individual> indivisuals = new HashMap<>();
        Individual individual = new Individual("Ind1");
        individual.setName("David Brown");
        indivisuals.put(individual.getId(), individual);
        individual = new Individual("Ind2");
        individual.setName("Mary Brown");
        indivisuals.put(individual.getId(), individual);

        // Prepare the families data
        HashMap<String, Family> families = new HashMap<>();
        Family family = new Family("F1");
        family.setMarriage("01/01/2000");
        family.setHusb("Ind1");
        family.setWife("Ind2");
        families.put(family.getId(), family);

        // Call the method to be tested
        Gedcom_Service.Marriagebeforedivorce(indivisuals, families);

        assertEmptyOutput();
    }

    @Test
    public void testMarriageBeforeDivorceWithError() throws IOException {
        // Prepare individuals data
        HashMap<String, Individual> indivisuals = new HashMap<>();
        Individual individual = new Individual("Ind1");
        individual.setName("David Brown");
        indivisuals.put(individual.getId(), individual);
        individual = new Individual("Ind2");
        individual.setName("Mary Brown");
        indivisuals.put(individual.getId(), individual);


        // Prepare the families data
        HashMap<String, Family> families = new HashMap<>();
        Family family = new Family("F1");
        family.setMarriage("01/01/2000");
        family.setDivorce("01/01/1999");
        family.setHusb("Ind1");
        family.setWife("Ind2");
        families.put(family.getId(), family);

        // Call the method to be tested
        Gedcom_Service.Marriagebeforedivorce(indivisuals, families);

        assertOutput("ERROR:FAMILY: User Story US04: Marriage Before Divorce \n");
        assertOutput("Family: F1\n");
        assertOutput("Individual: Ind1: David BrownInd2: Mary Brown marriage date is before divorce date.\n");
        assertOutput("Marriage Date: 01/01/2000 Divorce Date: 01/01/1999\n");
    }

    @Test
    public void testMarriageBeforeDivorceWithParseError() throws IOException {
        // Prepare individuals data
        HashMap<String, Individual> indivisuals = new HashMap<>();
        Individual individual = new Individual("Ind1");
        individual.setName("David Brown");
        indivisuals.put(individual.getId(), individual);
        individual = new Individual("Ind2");
        individual.setName("Mary Brown");
        indivisuals.put(individual.getId(), individual);

        // Prepare the families data
        HashMap<String, Family> families = new HashMap<>();
        Family family = new Family("F1");
        family.setMarriage("01/01/2000");
        family.setDivorce("test");
        family.setHusb("Ind1");
        family.setWife("Ind2");
        families.put(family.getId(), family);

        // Call the method to be tested
        Gedcom_Service.Marriagebeforedivorce(indivisuals, families);

        assertEmptyOutput();
        assertErrorOutput("java.text.ParseException: Unparseable date: \"test\"");
    }

    @Test
    public void testBirthBeforeMarriageOfParentsWithoutError() throws IOException {
        // Prepare individuals data
        HashMap<String, Individual> indivisuals = new HashMap<>();
        Individual individual = new Individual("Ind1");
        individual.setName("David Brown");
        individual.setBirth("01/01/2000");
        indivisuals.put(individual.getId(), individual);
        individual = new Individual("Ind2");
        individual.setName("Mary Brown");
        individual.setBirth("01/01/2000");
        indivisuals.put(individual.getId(), individual);
        individual = new Individual("Ind3");
        individual.setName("John Brown");
        individual.setBirth("01/01/2020");
        indivisuals.put(individual.getId(), individual);

        // Prepare the families data
        HashMap<String, Family> families = new HashMap<>();
        Family family = new Family("F1");
        family.setMarriage("01/01/2001");
        family.setHusb("Ind1");
        family.setWife("Ind2");
        family.setChild(new ArrayList<>(Collections.singletonList("Ind3")));
        families.put(family.getId(), family);

        // Call the method to be tested
        Gedcom_Service.birthbeforemarriageofparent(indivisuals, families);

        assertEmptyOutput();
    }

    @Test
    public void testBirthBeforeMarriageOfParentsWithDivorceWithoutError() throws IOException {
        // Prepare individuals data
        HashMap<String, Individual> indivisuals = new HashMap<>();
        Individual individual = new Individual("Ind1");
        individual.setName("David Brown");
        individual.setBirth("01/01/2000");
        indivisuals.put(individual.getId(), individual);
        individual = new Individual("Ind2");
        individual.setName("Mary Brown");
        individual.setBirth("01/01/2000");
        indivisuals.put(individual.getId(), individual);
        individual = new Individual("Ind3");
        individual.setName("John Brown");
        individual.setBirth("01/01/2018");
        indivisuals.put(individual.getId(), individual);

        // Prepare the families data
        HashMap<String, Family> families = new HashMap<>();
        Family family = new Family("F1");
        family.setMarriage("01/01/2001");
        family.setDivorce("01/01/2020");
        family.setHusb("Ind1");
        family.setWife("Ind2");
        family.setChild(new ArrayList<>(Collections.singletonList("Ind3")));
        families.put(family.getId(), family);

        // Call the method to be tested
        Gedcom_Service.birthbeforemarriageofparent(indivisuals, families);

        assertEmptyOutput();
    }

    @Test
    public void testBirthBeforeMarriageOfParentsWithoutChild() throws IOException {
        // Prepare individuals data
        HashMap<String, Individual> indivisuals = new HashMap<>();
        Individual individual = new Individual("Ind1");
        individual.setName("David Brown");
        individual.setBirth("01/01/2000");
        indivisuals.put(individual.getId(), individual);
        individual = new Individual("Ind2");
        individual.setName("Mary Brown");
        individual.setBirth("01/01/2000");
        indivisuals.put(individual.getId(), individual);
        individual = new Individual("Ind3");
        individual.setName("John Brown");
        individual.setBirth("01/01/2018");
        indivisuals.put(individual.getId(), individual);

        // Prepare the families data
        HashMap<String, Family> families = new HashMap<>();
        Family family = new Family("F1");
        family.setMarriage("01/01/2001");
        family.setDivorce("01/01/2020");
        family.setHusb("Ind1");
        family.setWife("Ind2");
        families.put(family.getId(), family);

        // Call the method to be tested
        Gedcom_Service.birthbeforemarriageofparent(indivisuals, families);

        assertEmptyOutput();
    }

    @Test
    public void testBirthBeforeMarriageOfParentsWithError() throws IOException {
        // Prepare individuals data
        HashMap<String, Individual> indivisuals = new HashMap<>();
        Individual individual = new Individual("Ind1");
        individual.setName("David Brown");
        individual.setBirth("01/01/2000");
        indivisuals.put(individual.getId(), individual);
        individual = new Individual("Ind2");
        individual.setName("Mary Brown");
        individual.setBirth("01/01/2000");
        indivisuals.put(individual.getId(), individual);
        individual = new Individual("Ind3");
        individual.setName("John Brown");
        individual.setBirth("01/01/2000");
        indivisuals.put(individual.getId(), individual);

        // Prepare the families data
        HashMap<String, Family> families = new HashMap<>();
        Family family = new Family("F1");
        family.setMarriage("01/01/2001");
        family.setHusb("Ind1");
        family.setWife("Ind2");
        family.setChild(new ArrayList<>(Collections.singletonList("Ind3")));
        families.put(family.getId(), family);

        // Call the method to be tested
        Gedcom_Service.birthbeforemarriageofparent(indivisuals, families);

        assertOutput("ERROR: User Story US08: Birth Before Marriage Date \n");
        assertOutput("Family ID: F1\n");
        assertOutput("Individual: Ind3: John Brown Has been born before parents' marriage\n");
        assertOutput("DOB: 01/01/2000 Parents Marriage Date: 01/01/2001\n\n");
    }

    @Test
    public void testBirthBeforeDeathOfParentsWithDivorceWithError() throws IOException {
        // Prepare individuals data
        HashMap<String, Individual> indivisuals = new HashMap<>();
        Individual individual = new Individual("Ind1");
        individual.setName("David Brown");
        individual.setBirth("01/01/2000");
        indivisuals.put(individual.getId(), individual);
        individual = new Individual("Ind2");
        individual.setName("Mary Brown");
        individual.setBirth("01/01/2000");
        indivisuals.put(individual.getId(), individual);
        individual = new Individual("Ind3");
        individual.setName("John Brown");
        individual.setBirth("01/01/2022");
        indivisuals.put(individual.getId(), individual);

        // Prepare the families data
        HashMap<String, Family> families = new HashMap<>();
        Family family = new Family("F1");
        family.setMarriage("01/01/2001");
        family.setDivorce("01/01/2020");
        family.setHusb("Ind1");
        family.setWife("Ind2");
        family.setChild(new ArrayList<>(Collections.singletonList("Ind3")));
        families.put(family.getId(), family);

        // Call the method to be tested
        Gedcom_Service.birthbeforemarriageofparent(indivisuals, families);

        assertOutput("ERROR: User Story US08: Birth After Divorce Date\n");
        assertOutput("Family ID: F1\n");
        assertOutput("Individual: Ind3: John Brown Has been born after parents' divorce\n");
        assertOutput("DOB: 01/01/2022 Parents Divorce Date: 01/01/2020\n\n");
    }

    @Test
    public void testBirthBeforeMarriageOfParentsWithParseError() throws IOException {
        // Prepare individuals data
        HashMap<String, Individual> indivisuals = new HashMap<>();
        Individual individual = new Individual("Ind1");
        individual.setName("David Brown");
        individual.setBirth("01/01/2000");
        indivisuals.put(individual.getId(), individual);
        individual = new Individual("Ind2");
        individual.setName("Mary Brown");
        individual.setBirth("01/01/2000");
        indivisuals.put(individual.getId(), individual);
        individual = new Individual("Ind3");
        individual.setName("John Brown");
        individual.setBirth("01/01/2020");
        indivisuals.put(individual.getId(), individual);

        // Prepare the families data
        HashMap<String, Family> families = new HashMap<>();
        Family family = new Family("F1");
        family.setMarriage("test");
        family.setHusb("Ind1");
        family.setWife("Ind2");
        family.setChild(new ArrayList<>(Collections.singletonList("Ind3")));
        families.put(family.getId(), family);

        // Call the method to be tested
        Gedcom_Service.birthbeforemarriageofparent(indivisuals, families);

        assertEmptyOutput();
        assertErrorOutput("java.text.ParseException: Unparseable date: \"test\"");
    }

    @Test
    public void testMaleLastNameWithoutError1() throws ParseException, IOException {
        // Prepare the families data
        HashMap<String, Family> families = new HashMap<>();
        Family family = new Family("@F4@");
        family.setHusb("@I7@");
        family.setWife("@I8@");
        family.setChild(new ArrayList<>(Arrays.asList("@I9@")));
        families.put(family.getId(), family);

        family = new Family("@F5@");
        family.setHusb("@I9@");
        family.setWife("@I10@");
        families.put(family.getId(), family);

        // Call the method to be tested
        Gedcom_Service.Malelastname(families);

        assertEmptyOutput();
    }

    @Test
    public void testMaleLastNameWithoutError2() throws IOException, ParseException {
        // Prepare the families data
        HashMap<String, Family> families = new HashMap<>();
        Family family = new Family("@F1@");
        family.setHusb("@I1@");
        family.setWife("@I2@");
        family.setChild(new ArrayList<>(Arrays.asList("@I3@", "@I4@")));
        families.put(family.getId(), family);

        // Call the method to be tested
        Gedcom_Service.Malelastname(families);

        assertEmptyOutput();
    }

    @Test
    public void testAuntsAndUnclesNameWithoutError1() throws Exception {
        // Prepare the families data
        HashMap<String, Family> families = new HashMap<>();
        Family family = new Family("@F1@");
        family.setHusb("@I1@");
        family.setWife("@I2@");
        family.setChild(new ArrayList<>(Arrays.asList("@I4@")));
        families.put(family.getId(), family);

        family = new Family("@F2@");
        family.setHusb("@I4@");
        family.setWife("@I5@");
        family.setChild(new ArrayList<>(Collections.singletonList("@I6@")));
        families.put(family.getId(), family);

        family = new Family("@F3@");
        family.setHusb("@I6@");
        family.setWife("@I4@");
        families.put(family.getId(), family);

        // Call the method to be tested
        Gedcom_Service.AuntsandUnclesname(families);

        assertEmptyOutput();
    }


    @Test
    public void testAuntsAndUnclesNameWithoutError2() throws Exception {
        // Prepare the families data
        HashMap<String, Family> families = new HashMap<>();
        Family family = new Family("@F1@");
        family.setHusb("@I1@");
        family.setWife("@I2@");
        families.put(family.getId(), family);

        family = new Family("@F2@");
        family.setHusb("@I5@");
        family.setWife("@I4@");
        families.put(family.getId(), family);

        family = new Family("@F3@");
        family.setHusb("@I5@");
        family.setWife("@I6@");
        families.put(family.getId(), family);

        // Call the method to be tested
        Gedcom_Service.AuntsandUnclesname(families);

        assertEmptyOutput();
    }

    @Test
    public void testAuntsAndUnclesNameWithoutError3() throws Exception {
        // Prepare the families data
        HashMap<String, Family> families = new HashMap<>();
        Family family = new Family("@F1@");
        family.setHusb("@I1@");
        family.setWife("@I2@");
        families.put(family.getId(), family);

        family = new Family("@F2@");
        family.setHusb("@I4@");
        family.setWife("@I5@");
        families.put(family.getId(), family);

        family = new Family("@F3@");
        family.setHusb("@I5@");
        family.setWife("@I6@");
        families.put(family.getId(), family);

        // Call the method to be tested
        Gedcom_Service.AuntsandUnclesname(families);

        assertEmptyOutput();
    }

    @Test
    public void testAuntsAndUnclesNameWithoutError4() throws Exception {
        // Prepare the families data
        HashMap<String, Family> families = new HashMap<>();
        Family family = new Family("@F1@");
        family.setHusb("@I1@");
        family.setWife("@I2@");
        family.setChild(new ArrayList<>(Arrays.asList("@I4@")));
        families.put(family.getId(), family);

        family = new Family("@F2@");
        family.setHusb("@I5@");
        family.setWife("@I4@");
        family.setChild(new ArrayList<>(Collections.singletonList("@I6@")));
        families.put(family.getId(), family);

        family = new Family("@F3@");
        family.setHusb("@I6@");
        family.setWife("@I4@");
        families.put(family.getId(), family);

        // Call the method to be tested
        Gedcom_Service.AuntsandUnclesname(families);

        assertEmptyOutput();
    }

    @Test
    public void testAuntsAndUnclesNameWithError1() throws Exception {
        // Prepare the families data
        HashMap<String, Family> families = new HashMap<>();
        Family family = new Family("@F1@");
        family.setHusb("@I1@");
        family.setWife("@I2@");
        family.setChild(new ArrayList<>(Arrays.asList("@I3@", "@I4@")));
        families.put(family.getId(), family);

        family = new Family("@F2@");
        family.setHusb("@I5@");
        family.setWife("@I4@");
        family.setChild(new ArrayList<>(Collections.singletonList("@I6@")));
        families.put(family.getId(), family);

        family = new Family("@F3@");
        family.setHusb("@I6@");
        family.setWife("@I3@");
        families.put(family.getId(), family);

        // Call the method to be tested
        Gedcom_Service.AuntsandUnclesname(families);

        assertOutput("ERROR: User Story US20: Aunts and Uncles\n");
        assertOutput("Individual: @I6@ - Helen Jones is married to either their aunt or uncle @I3@ - Emma Davis\n\n");
    }

    @Test
    public void testAuntsAndUnclesNameWithError2() throws Exception {
        // Prepare the families data
        HashMap<String, Family> families = new HashMap<>();
        Family family = new Family("@F1@");
        family.setHusb("@I1@");
        family.setWife("@I2@");
        family.setChild(new ArrayList<>(Arrays.asList("@I3@", "@I4@")));
        families.put(family.getId(), family);

        family = new Family("@F2@");
        family.setHusb("@I4@");
        family.setWife("@I5@");
        family.setChild(new ArrayList<>(Collections.singletonList("@I6@")));
        families.put(family.getId(), family);

        family = new Family("@F3@");
        family.setHusb("@I3@");
        family.setWife("@I6@");
        families.put(family.getId(), family);

        // Call the method to be tested
        Gedcom_Service.AuntsandUnclesname(families);

        assertOutput("ERROR: User Story US20: Aunts and Uncles\n");
        assertOutput("Individual: @I6@ - Helen Jones is married to either their aunt or uncle @I3@ - Emma Davis\n\n");
    }

    @Test
    public void testUniqueFamilyNameBySpousesWithoutError1() throws IOException {
        // Prepare the families data
        HashMap<String, Family> families = new HashMap<>();
        Family family = new Family("F1");
        family.setHusb("Ind1");
        family.setWife("Ind2");
        family.setMarriage("01/01/2001");
        families.put(family.getId(), family);
        family = new Family("F2");
        family.setHusb("Ind1");
        family.setWife("Ind2");
        family.setMarriage("01/01/2000");
        families.put(family.getId(), family);

        // Prepare the individuals data
        HashMap<String, Individual> individuals = new HashMap<>();
        Individual individual = new Individual("Ind1");
        individual.setName("David Brown");
        individuals.put(individual.getId(), individual);
        individual = new Individual("Ind2");
        individual.setName("Mary Brown");
        individuals.put(individual.getId(), individual);

        // Call the method to be tested
        Gedcom_Service.uniqueFamilynameBySpouses(individuals, families);

        assertEmptyOutput();
    }

    @Test
    public void testUniqueFamilyNameBySpousesWithoutError2() throws IOException {
        // Prepare the families data
        HashMap<String, Family> families = new HashMap<>();
        Family family = new Family("F1");
        family.setHusb("Ind2");
        family.setWife("Ind2");
        family.setMarriage("01/01/2000");
        families.put(family.getId(), family);
        family = new Family("F2");
        family.setHusb("Ind1");
        family.setWife("Ind2");
        family.setMarriage("01/01/2000");
        families.put(family.getId(), family);

        // Prepare the individuals data
        HashMap<String, Individual> individuals = new HashMap<>();
        Individual individual = new Individual("Ind1");
        individual.setName("David Brown");
        individuals.put(individual.getId(), individual);
        individual = new Individual("Ind2");
        individual.setName("Mary Brown");
        individuals.put(individual.getId(), individual);

        // Call the method to be tested
        Gedcom_Service.uniqueFamilynameBySpouses(individuals, families);

        assertEmptyOutput();
    }

    @Test
    public void testUniqueFamilyNameBySpousesWithoutError3() throws IOException {
        // Prepare the families data
        HashMap<String, Family> families = new HashMap<>();
        Family family = new Family("F1");
        family.setHusb("Ind1");
        family.setWife("Ind1");
        family.setMarriage("01/01/2000");
        families.put(family.getId(), family);
        family = new Family("F2");
        family.setHusb("Ind1");
        family.setWife("Ind2");
        family.setMarriage("01/01/2000");
        families.put(family.getId(), family);

        // Prepare the individuals data
        HashMap<String, Individual> individuals = new HashMap<>();
        Individual individual = new Individual("Ind1");
        individual.setName("David Brown");
        individuals.put(individual.getId(), individual);
        individual = new Individual("Ind2");
        individual.setName("Mary Brown");
        individuals.put(individual.getId(), individual);

        // Call the method to be tested
        Gedcom_Service.uniqueFamilynameBySpouses(individuals, families);

        assertEmptyOutput();
    }

    @Test
    public void testUniqueFamilyNameBySpousesWithoutWife () throws IOException {
        // Prepare the families data
        HashMap<String, Family> families = new HashMap<>();
        Family family = new Family("F1");
        family.setHusb("Ind1");
        family.setMarriage("01/01/2001");
        families.put(family.getId(), family);
        family = new Family("F2");
        family.setHusb("Ind1");
        family.setWife("Ind2");
        family.setMarriage("01/01/2000");
        families.put(family.getId(), family);

        // Prepare the individuals data
        HashMap<String, Individual> individuals = new HashMap<>();
        Individual individual = new Individual("Ind1");
        individual.setName("David Brown");
        individuals.put(individual.getId(), individual);
        individual = new Individual("Ind2");
        individual.setName("Mary Brown");
        individuals.put(individual.getId(), individual);

        // Call the method to be tested
        Gedcom_Service.uniqueFamilynameBySpouses(individuals, families);

        assertEmptyOutput();
    }

    @Test
    public void testUniqueFamilyNameBySpousesWithoutHusb () throws IOException {
        // Prepare the families data
        HashMap<String, Family> families = new HashMap<>();
        Family family = new Family("F1");
        family.setWife("Ind2");
        family.setMarriage("01/01/2001");
        families.put(family.getId(), family);
        family = new Family("F2");
        family.setHusb("Ind1");
        family.setWife("Ind2");
        family.setMarriage("01/01/2000");
        families.put(family.getId(), family);

        // Prepare the individuals data
        HashMap<String, Individual> individuals = new HashMap<>();
        Individual individual = new Individual("Ind1");
        individual.setName("David Brown");
        individuals.put(individual.getId(), individual);
        individual = new Individual("Ind2");
        individual.setName("Mary Brown");
        individuals.put(individual.getId(), individual);

        // Call the method to be tested
        Gedcom_Service.uniqueFamilynameBySpouses(individuals, families);

        assertEmptyOutput();
    }

    @Test
    public void testUniqueFamilyNameBySpousesWithError() throws IOException {
        // Prepare the families data
        HashMap<String, Family> families = new HashMap<>();
        Family family = new Family("F1");
        family.setHusb("Ind1");
        family.setWife("Ind2");
        family.setMarriage("01/01/2000");
        families.put(family.getId(), family);
        family = new Family("F2");
        family.setHusb("Ind1");
        family.setWife("Ind2");
        family.setMarriage("01/01/2000");
        families.put(family.getId(), family);

        // Prepare the individuals data
        HashMap<String, Individual> individuals = new HashMap<>();
        Individual individual = new Individual("Ind1");
        individual.setName("David Brown");
        individuals.put(individual.getId(), individual);
        individual = new Individual("Ind2");
        individual.setName("Mary Brown");
        individuals.put(individual.getId(), individual);

        // Call the method to be tested
        Gedcom_Service.uniqueFamilynameBySpouses(individuals, families);

        assertOutput("ERROR: User Story US24: Unique Families By Spouse :\n");
        assertOutput("F2: Husbund Name: David Brown,Wife Name: Mary Brown and F1: Husbund Name: David Brown,Wife Name: Mary Brown\n");
        assertOutput("F1: Husbund Name: David Brown,Wife Name: Mary Brown and F2: Husbund Name: David Brown,Wife Name: Mary Brown\n");
        assertOutput("have same spouses and marriage dates :01/01/2000\n\n");
    }

    @Test
    public void testGetMonth() {
        assertEquals("01", Gedcom_Service.getMonth("JAN"));
        assertEquals("02", Gedcom_Service.getMonth("FEB"));
        assertEquals("03", Gedcom_Service.getMonth("MAR"));
        assertEquals("04", Gedcom_Service.getMonth("APR"));
        assertEquals("05", Gedcom_Service.getMonth("MAY"));
        assertEquals("06", Gedcom_Service.getMonth("JUN"));
        assertEquals("07", Gedcom_Service.getMonth("JUL"));
        assertEquals("08", Gedcom_Service.getMonth("AUG"));
        assertEquals("09", Gedcom_Service.getMonth("SEP"));
        assertEquals("10", Gedcom_Service.getMonth("OCT"));
        assertEquals("11", Gedcom_Service.getMonth("NOV"));
        assertEquals("12", Gedcom_Service.getMonth("DEC"));
        assertNull(Gedcom_Service.getMonth("FAKE"));
    }
}
