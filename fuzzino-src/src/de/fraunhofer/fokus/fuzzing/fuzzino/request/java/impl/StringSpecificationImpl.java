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
package de.fraunhofer.fokus.fuzzing.fuzzino.request.java.impl;

import de.fraunhofer.fokus.fuzzing.fuzzino.exceptions.IllegalStringEncodingException;
import de.fraunhofer.fokus.fuzzing.fuzzino.exceptions.IllegalStringTypeException;
import de.fraunhofer.fokus.fuzzing.fuzzino.request.RequestSpecification;
import de.fraunhofer.fokus.fuzzing.fuzzino.request.java.NumberSpecification;
import de.fraunhofer.fokus.fuzzing.fuzzino.request.java.NumberType;
import de.fraunhofer.fokus.fuzzing.fuzzino.request.java.RequestFactory;
import de.fraunhofer.fokus.fuzzing.fuzzino.request.java.StringEncoding;
import de.fraunhofer.fokus.fuzzing.fuzzino.request.java.StringSpecification;
import de.fraunhofer.fokus.fuzzing.fuzzino.request.java.StringType;
import de.fraunhofer.fokus.fuzzing.fuzzino.response.java.IllegalRequestFormat;
import de.fraunhofer.fokus.fuzzing.fuzzino.response.java.ResponseFactory;
import de.fraunhofer.fokus.fuzzing.fuzzino.response.java.WarningsSection;
import de.fraunhofer.fokus.fuzzing.fuzzino.util.ValidationResult;

public class StringSpecificationImpl implements StringSpecification {
	
	private static final long serialVersionUID = -6918660757447213918L;
	protected StringType type = StringType.STRING;
	protected int minLength = 0;
	protected int maxLength = Integer.MAX_VALUE;
	protected boolean nullTerminated;
	protected StringEncoding encoding;
	protected String regEx;
	protected boolean isEMFBased = false;
	protected boolean validType = true;
	protected boolean validEncoding = true;
	protected boolean ignoreLengths = false;

	@Override
	public void setWithEMFObject(RequestSpecification emfRequestSpecification) {
		try {
			setType(StringType.get(emfRequestSpecification.getType()));
		} catch (IllegalStringTypeException e) {
			validType = false;
		}
		
		if (emfRequestSpecification.isSetMinLength()) {
			setMinLength(emfRequestSpecification.getMinLength());
		}
		
		if (emfRequestSpecification.isSetMaxLength()) {
			setMaxLength(emfRequestSpecification.getMaxLength());
		}
		
		if (!emfRequestSpecification.isSetMinLength() && !emfRequestSpecification.isSetMaxLength()) {
			setIgnoreLengths(true);
		}
		
		setIsNullTerminated(emfRequestSpecification.isNullTerminated());
		try {
			setEncoding(StringEncoding.get(emfRequestSpecification.getEncoding()));
		} catch (IllegalStringEncodingException e) {
			validEncoding = false;
		}
		regEx = ""; // FIXME emfRequestSpecification.getMixed()
	}
	
	@Override
	public StringType getType() {
		return type;
	}
	
	@Override
	public void setType(StringType value) {
		type = value;
	}
	
	@Override	public int getMinLength() {
		return minLength;
	}
	
	@Override
	public void setMinLength(int value) {
		minLength = value;
	}

	@Override
	public int getMaxLength() {
		return maxLength;
	}
	
	@Override
	public void setMaxLength(int value) {
		maxLength = value;
	}

	@Override
	public boolean isNullTerminated() {
		return nullTerminated;
	}
	
	@Override
	public void setIsNullTerminated(boolean value) {
		nullTerminated = value;
	}

	@Override
	public StringEncoding getEncoding() {
		return encoding;
	}
	
	@Override
	public void setEncoding(StringEncoding value) {
		encoding = value;
	}

	@Override
	public String getRegEx() {
		return regEx;
	}
	
	@Override
	public void setRegEx(String value) {
		regEx = value;
	}

	@Override
	public ValidationResult validate() {
		WarningsSection warningsPart = ResponseFactory.INSTANCE.createWarnings();
		boolean validLength = true;

		if (!ignoreLengths() && getMinLength() < 0) {
			validLength = false;
			IllegalRequestFormat illegalRequestFormat = 
					ResponseFactory.INSTANCE.createIllegalRequestFormatWithWrongAttribute("specification", "minLength");
			warningsPart.getIllegalRequestFormats().add(illegalRequestFormat);
		}
		if (!ignoreLengths() && ((getMaxLength() < 0) || (getMaxLength() < getMinLength()))) {
			validLength = false;
			IllegalRequestFormat illegalRequestFormat = 
					ResponseFactory.INSTANCE.createIllegalRequestFormatWithWrongAttribute("specification", "maxLength");
			warningsPart.getIllegalRequestFormats().add(illegalRequestFormat);
		}

		if (!validType) {
			IllegalRequestFormat illegalRequestFormat = 
					ResponseFactory.INSTANCE.createIllegalRequestFormatWithWrongAttribute("specification", "type");
			warningsPart.getIllegalRequestFormats().add(illegalRequestFormat);
		}

		if (!validEncoding) {
			IllegalRequestFormat illegalRequestFormat = 
					ResponseFactory.INSTANCE.createIllegalRequestFormatWithWrongAttribute("specification", "encoding");
			warningsPart.getIllegalRequestFormats().add(illegalRequestFormat);
		}

		boolean result = validLength && validType && validEncoding;
		
		return new ValidationResult(result, warningsPart);
	}
	
	@Override
	public String toString() {
		return "[StringSpecification type:" + type + " minLength:" + minLength + " maxLength:" + maxLength + 
			   " nullTerminated:" + nullTerminated + " encoding:" + encoding + "]";
	}

	@Override
	public NumberSpecification createPositiveNumberSpec() {
		if (getMaxLength() == 0) {
			throw new UnsupportedOperationException("Cannot create positive NumberSpecification: maxLength is 0.");
		}
		NumberSpecification numberSpec = RequestFactory.INSTANCE.createNumberSpecification(NumberType.INTEGER);
		
		if (ignoreLengths()) {
			numberSpec.setIgnoreMinMaxValues(true);
		} else {
			long minValue = 0;
			if (getMinLength() > 0) {
				minValue = (long) Math.pow(10, getMinLength()-1);
			} 
			numberSpec.setMinValue(minValue);

			long maxValue = (long) Math.pow(10, getMaxLength())-1;
			numberSpec.setMaxValue(maxValue);
		}

		return numberSpec;
	}

	@Override
	public NumberSpecification createNegativeNumberSpec() {
		if (getMaxLength() <= 1) {
			throw new UnsupportedOperationException("Cannot create negative NumberSpecification: maxLength is <= 1.");
		}
		NumberSpecification numberSpec = RequestFactory.INSTANCE.createNumberSpecification(NumberType.INTEGER);
		
		if (ignoreLengths()) {
			numberSpec.setIgnoreMinMaxValues(true);
		} else {
			long maxValue = -1;
			if (getMinLength() > 2) {
				maxValue = (long) -Math.pow(10, getMinLength()-2);
			} 
			numberSpec.setMaxValue(maxValue);

			long minValue = (long) -Math.pow(10, getMaxLength()-1) + 1;
			numberSpec.setMinValue(minValue);
		}

		return numberSpec;
	}

	@Override
	public void setIgnoreLengths(boolean value) {
		ignoreLengths = value;
	}

	@Override
	public boolean ignoreLengths() {
		return ignoreLengths;
	}

}
