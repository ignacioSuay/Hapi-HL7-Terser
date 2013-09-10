package com.hl7integration.hapi.tests;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.parser.PipeParser;
import ca.uhn.hl7v2.util.Terser;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class TerserTest {

    @Test
    public void testTerser(){

        try {

            String m = "MSH|^~\\&|hl7Integration|hl7Integration|||||ADT^A01|||2.3|\r" +
                    "EVN|A01|20130617154644\r" +
                    "PID|1|465 306 5961||407623|Wood^Patrick^^^MR||19700101|1|||High Street^^Oxford^^Ox1 4DP~George St^^Oxford^^Ox1 5AP|||||||\r" +
                    "NK1|1|\r"+
                    "NK1|2|\r"+
                    "PV1|1||Location||||||||||||||||261938_6_201306171546|||||||||||||||||||||||||20130617134644|||||||||";


            //Create the Terser
            PipeParser pipeParser = new PipeParser();
            Message message = pipeParser.parse(m);
            Terser terser = new Terser(message);

            //Use Terser to get simple fields
            assert terser.get("/PID-2").equals("465 306 5961");      //Patient Id
            assert terser.get("/PID-7").equals("19700101");          //Dob

            //Use Terser to get components
            assert terser.get("/PID-5-1").equals("Wood");            //Patient Surname
            assert terser.get("/PID-5-2").equals("Patrick");         //Patient First Name

            //Use terser to get segment repetitions
            assert terser.get("NK1(0)-1").equals("1");               //First Next of Kin Id
            assert terser.get("NK1(1)-1").equals("2");               //Second Next of Kin Id

            //Use terser to get field repetitions
            assert terser.get("/PID-11(0)-1").equals("High Street"); //Primary Address: Street
            assert terser.get("/PID-11(0)-5").equals("Ox1 4DP");     //Primary Address: PostCode

            assert terser.get("/PID-11(1)-1").equals("George St");   //Secondary Address: Street
            assert terser.get("/PID-11(1)-5").equals("Ox1 5AP");     //Secondary Address: PostCode

            //Use terser to set simple Fields
            terser.set("/PID-2", "123456");
            assert terser.get("/PID-2").equals("123456");            //Patient Id

            //Use terser to set components
            terser.set("/PID-5-1", "Jones");
            assert terser.get("/PID-5-1").equals("Jones");            //Patient Surname

            //Use terser to set type Fields
            terser.set("/PID-11(1)-1", "Wellington Avenue");
            assert terser.get("/PID-11(1)-1").equals("Wellington Avenue");   //Secondary Address: Street

            //Record repetitions in a String List
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

            assert listStrings.get(0).equals("High Street");
            assert listStrings.get(1).equals("Wellington Avenue");



        } catch (HL7Exception e) {
            e.printStackTrace();
        }

    }

}