//   Copyright 2012-2013 Fraunhofer FOKUS
//
//   Licensed under the Apache License, Version 2.0 (the "License");
//   you may not use this file except in compliance with the License.
//   You may obtain a copy of the License at
//
//       http://www.apache.org/licenses/LICENSE-2.0
//
//   Unless required by applicable law or agreed to in writing, software
//   distributed under the License is distributed on an "AS IS" BASIS,
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//   See the License for the specific language governing permissions and
//   limitations under the License.
package de.fraunhofer.fokus.fuzzing.fuzzino;

import static de.fraunhofer.fokus.fuzzing.fuzzino.TestUtil.*;
import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

import de.fraunhofer.fokus.fuzzing.fuzzino.FuzzedValue;
import de.fraunhofer.fokus.fuzzing.fuzzino.Fuzzino;
import de.fraunhofer.fokus.fuzzing.fuzzino.heuristics.generators.StringGeneratorFactory;
import de.fraunhofer.fokus.fuzzing.fuzzino.heuristics.generators.string.AllBadStringsGenerator;
import de.fraunhofer.fokus.fuzzing.fuzzino.request.java.RequestFactory;
import de.fraunhofer.fokus.fuzzing.fuzzino.response.GeneratorPart;
import de.fraunhofer.fokus.fuzzing.fuzzino.response.StringResponse;
import de.fraunhofer.fokus.fuzzing.fuzzino.response.XmlResponseDocument;

public class BugTests {

	@Test
	public void testBug_2012_06_11_16_47() {
		String requestFilename = "./testdata/bugs/2012-06-11_16-47.request.xml";
		XmlResponseDocument responseDoc = getResponseDocForRequest(requestFilename);
		
		checkResponseDocForNumStringResponses(responseDoc, 1);
		StringResponse stringResponse = getStringResponseFromResponseDoc(responseDoc, 0);

		assertTrueWithPrefix("No seed in response.",
				   stringResponse.getSeed() != null);

		checkResponseForNumGeneratorParts(stringResponse, 1);
		
		GeneratorPart generatorPart = getGeneratorPartFromResponseByName(stringResponse, "BadNumbersAsString");
		checkGeneratorPartForNumFuzzedValues(generatorPart, 10);
		
		checkResponseForNumOperatorParts(stringResponse, 0);

		checkResponseForWarningsPart(stringResponse, false);
		
		checkResponseDocForErrorResponse(responseDoc, false);
		checkResponseDocForNumNumberResponses(responseDoc, 0);
		checkResponseDocForNumCollectionResponses(responseDoc, 0);
		checkResponseDocForNumStructureResponses(responseDoc, 0);
	}
	
	@Test
	public void testBug_2012_10_10_19_08() {
		String requestFilename = "./testdata/bugs/2012-10-10_19-08.request.xml";
		XmlResponseDocument responseDoc = getResponseDocForRequest(requestFilename);
		
		checkResponseDocForNumStringResponses(responseDoc, 1);
		StringResponse stringResponse = getStringResponseFromResponseDoc(responseDoc, 0);

		assertTrueWithPrefix("No seed in response.",
				   stringResponse.getSeed() != null);

		checkResponseForNumGeneratorParts(stringResponse, 1);
		
		GeneratorPart generatorPart = getGeneratorPartFromResponseByName(stringResponse, "BadStrings");
		checkGeneratorPartForNumFuzzedValues(generatorPart, 40);
		
		checkResponseForNumOperatorParts(stringResponse, 0);

		checkResponseForWarningsPart(stringResponse, false);
		
		checkResponseDocForErrorResponse(responseDoc, false);
		checkResponseDocForNumNumberResponses(responseDoc, 0);
		checkResponseDocForNumCollectionResponses(responseDoc, 0);
		checkResponseDocForNumStructureResponses(responseDoc, 0);
	}
	
	@Test
	public void testBug_2012_10_10_19_08_delete() {
		String requestFilename = "./testdata/bugs/2012-10-10_19-08.request.xml";
		Fuzzino.main(new String[]{requestFilename});
		
		String responseFilename = "./testdata/bugs/2012-10-10_19-08.response.xml";
		
		File response = new File(responseFilename);
		if (response.exists()) {
			assertTrue("Response file could not be deleted",
					   response.delete());
		}
	}
	
	@Test
	public void testBug_2012_10_10_19_08_callLibraryTwice() {
		String requestFilename1 = "./testdata/bugs/2012-10-10_19-08.request.xml";
		String reqeustFilename2 = "./testdata/bugs/2012-06-11_16-47.request.xml";
		Fuzzino.main(new String[]{requestFilename1});
		Fuzzino.main(new String[]{reqeustFilename2});
	}
	
	@Test
	public void testBug_2012_10_10_19_08_processXmlString() {
		String xmlRequest = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                            "<request xmlns=\"http://library.fuzzing.fokus.fraunhofer.de/request\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://library.fuzzing.fokus.fraunhofer.de/request ./fuzzingRequest.xsd\">" +
				            "<string name=\"SimpleStringRequest\" maxValues=\"40\">\n" +
                            "<specification minLength=\"1\" maxLength=\"5\" nullTerminated=\"true\" encoding=\"UTF8\" />\n" +
				            "<generator>BadStrings</generator>\n" + 
                            "</string>\n" +
				            "</request>";
		
		String xmlResponse = Fuzzino.processXmlString(xmlRequest);
		
		assertTrue("xmlResponse is null", xmlResponse != null);
	}
	
	/**
	 * Bug where output of AllBadStrings contains \x1 instead of \x01.
	 */
	@Test
	public void testBug_IllegalEncodingFormat() {
		AllBadStringsGenerator allBadStringsGenerator = StringGeneratorFactory.INSTANCE.createAllBadStrings(RequestFactory.INSTANCE.createStringSpecification(), 0);
		int i=0;
		for (FuzzedValue<String> fuzzedValue : allBadStringsGenerator) {
			String str = fuzzedValue.getValue();
			assertFalse(i + ": Wrong encoding, ends with \\x1 instead of \\x01. '" + fuzzedValue + "'", 
					str.endsWith("\\x1"));
			i++;
		}
	}
	
	@Test
	public void testBug_2013_02_04_14_38_manyValuesRequested_5000() {
		String requestFilename = "./2013-02-04_14_38_5000.request.xml";
		
		try {
			Fuzzino.main(new String[]{requestFilename});
		} catch (Exception e) {
			fail(e.toString());
		}
	}
	
	@Test
	public void testBug_2013_02_04_14_38_manyValuesRequested_750() {
		String requestFilename = "./2013-02-04_14_38_750.request.xml";
		
		try {
			Fuzzino.main(new String[]{requestFilename});
		} catch (Exception e) {
			fail(e.toString());
		}
	}
	
}
