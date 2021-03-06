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
package de.fraunhofer.fokus.fuzzing.fuzzino.response.java.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import de.fraunhofer.fokus.fuzzing.fuzzino.response.ResponseFactory;
import de.fraunhofer.fokus.fuzzing.fuzzino.response.java.FuzzedValuesByOperators;
import de.fraunhofer.fokus.fuzzing.fuzzino.response.java.OperatorSpecificFuzzedValues;

public class FuzzedValuesByOperatorsImpl<T> implements FuzzedValuesByOperators<T> {

	protected List<OperatorSpecificFuzzedValues<T>> operatorSections;
	
	@Override
	public List<OperatorSpecificFuzzedValues<T>> getOperatorSpecificSections() {
		return (operatorSections == null ? null : Collections.unmodifiableList(operatorSections));
	}
	
	@Override
	public void setOperatorSpecificSections(Collection<OperatorSpecificFuzzedValues<T>> value) {
		operatorSections = new ArrayList<>(value);
	}

	@Override
	public void addOperatorSpecificSection(OperatorSpecificFuzzedValues<T> value) {
		if (operatorSections == null) {
			operatorSections = new ArrayList<>();
		}
		operatorSections.add(value);
	}

	@Override
	public de.fraunhofer.fokus.fuzzing.fuzzino.response.AllOperatorsBasedPart getEMFRepresentation() {
		de.fraunhofer.fokus.fuzzing.fuzzino.response.AllOperatorsBasedPart 
		  emfAllOperatorsBasedPart = ResponseFactory.eINSTANCE.createAllOperatorsBasedPart();
		
		for (OperatorSpecificFuzzedValues<T> operatorPart : operatorSections) {
			emfAllOperatorsBasedPart.getOperatorParts().add(operatorPart.getEMFRepresentation());
		}
		
		return emfAllOperatorsBasedPart;
	}
	
	@Override
	public String toString() {
		return "[AllOperatorsBasedPart operatorParts: " + (operatorSections == null ? "0" : operatorSections.size()) + "]";
	}

	@Override
	public Iterator<OperatorSpecificFuzzedValues<T>> iterator() {
		return operatorSections.iterator();
	}

}
