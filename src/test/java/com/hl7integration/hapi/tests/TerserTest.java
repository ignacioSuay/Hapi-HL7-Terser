package com.hl7integration.hapi.tests;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.parser.PipeParser;
import ca.uhn.hl7v2.util.Terser;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TerserTest {

    Message message;

    Terser terser;

    @Before
    public void setup() throws Exception{
        String m = "MSH|^~\\&|hl7Integration|hl7Integration|||||ADT^A01|||2.3|\r" +
                "EVN|A01|20130617154644\r" +
                "PID|1|465 306 5961||407623|Wood^Patrick^^^MR||19700101|1|||High Street^^Oxford^^Ox1 4DP~George St^^Oxford^^Ox1 5AP|||||||\r" +
                "NK1|1|Wood^John^^^MR|Father||999-9999\r"+
                "NK1|2|Jones^Georgie^^^MSS|MOTHER||999-9999\r"+
                "PV1|1||Location||||||||||||||||261938_6_201306171546|||||||||||||||||||||||||20130617134644|||||||||";


        //Parse the message
        PipeParser pipeParser = new PipeParser();
        this.message = pipeParser.parse(m);
        this.terser = new Terser(message);
    }

    @Test
    public void testAccessSimpleFields() throws Exception{

        //Patient Id
        assertEquals("465 306 5961", terser.get("/PID-2"));
        //Dob
        assertEquals("19700101", terser.get("/PID-7"));
        //Patient Surname
        assertEquals("Wood", terser.get("/PID-5-1"));
        //Patient First Name
        assertEquals("Patrick", terser.get("/PID-5-2"));
    }

    @Test
    public void testAccessSegmentRepetitions() throws Exception{

        //First Next of Kin Id
        assertEquals("1", terser.get("NK1(0)-1"));

        //Second Next of Kin Id
        assertEquals("2", terser.get("NK1(1)-1"));
    }

    @Test
    public void testAccessFieldRepetitions() throws Exception{
        //Primary Address: Street
        assertEquals("High Street", terser.get("/PID-11(0)-1"));
        //Primary Address: PostCode
        assertEquals("Ox1 4DP", terser.get("/PID-11(0)-5"));
        //Secondary Address: Street
        assertEquals("George St", terser.get("/PID-11(1)-1"));
        //Secondary Address: PostCode
        assertEquals("Ox1 5AP", terser.get("/PID-11(1)-5"));
    }

    @Test
    public void testUpdateSimpleFields() throws Exception{
        //Update Patient Id
        terser.set("/PID-2", "123456");
        assertEquals("123456", terser.get("/PID-2"));

        //Update Dob
        terser.set("/PID-7", "19800101");
        assertEquals("19800101", terser.get("/PID-7"));
    }

    @Test
    public void testSetComponents() throws Exception{
        //Patient Surname
        terser.set("/PID-5-1", "Jones");
        assertEquals("Jones", terser.get("/PID-5-1"));

    }

    @Test
    public void testSetTypeFields() throws Exception{
        //Add a secondary address
        terser.set("/PID-11(1)-1", "Wellington Avenue");
        assertEquals("Wellington Avenue", terser.get("/PID-11(1)-1"));
    }

    @Test
    public void testRecordRepetitions() throws Exception{
        //Add a secondary address
        terser.set("/PID-11(1)-1", "Wellington Avenue");

        List<String> listStrings = new ArrayList<String>();
        int maxRepetitions = 5;
        for (int i= 0; i < maxRepetitions; i++ ){
            String value =  terser.get("PID-11("+i+")-1");
            if(value != null){
                listStrings.add(value);
            }else{
                break;
            }
        }

        assertEquals("High Street", listStrings.get(0));
        assertEquals("Wellington Avenue", listStrings.get(1));
    }

}