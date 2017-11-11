package pwr.chrzescijanek.filip.ml.classifier.ila;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import pwr.chrzescijanek.filip.ml.classifier.AbstractClassifier;
import pwr.chrzescijanek.filip.ml.classifier.ila.model.Rule;
import pwr.chrzescijanek.filip.ml.data.DataSet;
import pwr.chrzescijanek.filip.ml.data.attribute.DiscreteAttribute;
import pwr.chrzescijanek.filip.ml.data.record.AttributeValuePair;
import pwr.chrzescijanek.filip.ml.data.record.Record;
import pwr.chrzescijanek.filip.ml.data.record.TestRecord;

public class InductiveLearning extends AbstractClassifier {

	private List<Rule> rules = Collections.emptyList();

	@Override
	protected void assignClass(final TestRecord tr) {
		rules.stream().filter(r -> r.isFulfilledBy(tr)).findFirst().ifPresent(r -> tr.setAssignedClazz(r.getClazz()));
	}
	
	@Override
	protected void buildModel(final DataSet tds) {
		List<Rule> newRules = new ArrayList<>();
		List<DiscreteAttribute> attributes = checkAttributes(tds);
		List<Map.Entry<String, List<List<AttributeValuePair>>>> subtables = createSubtables(tds);
		for (Map.Entry<String, List<List<AttributeValuePair>>> subtable : subtables) {
			newRules.addAll(processSubtable(attributes, subtables, subtable));
		}
		this.rules = newRules;
	}

	private List<DiscreteAttribute> checkAttributes(final DataSet tds) {
		List<DiscreteAttribute> attributes = tds.getAttributes().stream().map(a -> {
			if (a instanceof DiscreteAttribute) {
				return (DiscreteAttribute) a;
			}
			throw new IllegalArgumentException("Attributes must have discrete values!");
		}).collect(Collectors.toList());
		return attributes;
	}
	
	private List<Map.Entry<String, List<List<AttributeValuePair>>>> createSubtables(DataSet tds) {
		Map<String, List<Record>> recordsGrouped = tds.getRecords()
				.stream()
				.collect(Collectors.groupingBy(Record::getClazz));
		Map<String, List<List<AttributeValuePair>>> mapped = recordsGrouped
				.entrySet()
				.stream()
				.collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue().stream().map(r -> r.getValues()).collect(Collectors.toList())));
		return mapped
				.entrySet()
				.stream()
				.collect(Collectors.toList());
	}

	private List<Rule> processSubtable(List<DiscreteAttribute> attributes,
			List<Map.Entry<String, List<List<AttributeValuePair>>>> subtables,
			Map.Entry<String, List<List<AttributeValuePair>>> subtable) {
		List<Rule> newRules = new ArrayList<>();
		List<List<AttributeValuePair>> thiz  = new ArrayList<>(subtable.getValue());
		List<List<AttributeValuePair>> other = subtables
				.stream()
				.filter(e -> !e.getKey().equals(subtable.getKey()))
				.flatMap(e -> e.getValue().stream())
				.collect(Collectors.toList());
		
		for (int j = 1; j <= attributes.size() && !thiz.isEmpty(); j++) {
			List<List<String>> combinations = getCombinations(attributes, j);
			while(!thiz.isEmpty()) {
				Map.Entry<List<AttributeValuePair>, Long> max = null;
				for (List<String> combo : combinations) {
					max = findMax(attributes, subtables, subtable, thiz, other, max, combo);
				}
				if (Objects.isNull(max)) {
					break;
				}		
				final List<AttributeValuePair> finalMax = max.getKey(); 
				thiz = thiz.stream().filter(list -> !list.containsAll(finalMax)).collect(Collectors.toList());
				if (newRules.stream().noneMatch(r -> r.getCondition().equals(finalMax))) {
					newRules.add(new Rule(finalMax, subtable.getKey()));
				}
			}
		}
		return newRules;
	}
	
	private List<List<String>> getCombinations(List<DiscreteAttribute> attributes, int length) {
		List<String> names = attributes.stream().map(a -> a.getName()).collect(Collectors.toList());
		
		List<List<String>> subsets = new ArrayList<>();

		int[] indices = new int[length];                  
		
		if (length <= names.size()) {
		    for (int i = 0; (indices[i] = i) < length - 1; i++);  
		    subsets.add(getSubset(names, indices));
		    while(true) {
		        int i;
		        for (i = length - 1; i >= 0 && indices[i] == names.size() - length + i; i--); 
		        if (i < 0) {
		            break;
		        }
		        indices[i]++;   
		        for (++i; i < length; i++) {
		            indices[i] = indices[i - 1] + 1; 
		        }
		        subsets.add(getSubset(names, indices));
		    }
		}
		
		return subsets;
	}

	private List<String> getSubset(List<String> names, int[] subset) {
	    List<String> result = new ArrayList<>();
		for (int i = 0; i < subset.length; i++) {
	        result.add(names.get(subset[i]));
	    }
	    return result;
	}

	private Map.Entry<List<AttributeValuePair>, Long> findMax(List<DiscreteAttribute> attributes,
			List<Map.Entry<String, List<List<AttributeValuePair>>>> subtables,
			Map.Entry<String, List<List<AttributeValuePair>>> subtable, List<List<AttributeValuePair>> thiz,
			List<List<AttributeValuePair>> other, Map.Entry<List<AttributeValuePair>, Long> max, List<String> combo) {
		List<List<AttributeValuePair>> thizCombo = filterCombination(thiz, combo);
		List<List<AttributeValuePair>> otherCombo = filterDistinctCombination(other, combo);
			
		Map<List<AttributeValuePair>, Long> candidates;
		if (combo.size() != attributes.size()) {
			candidates = getCandidates(thizCombo, otherCombo);
		} else {
			candidates = handlePossibleConflict(subtables, subtable, thizCombo);
		}
		
		Map.Entry<List<AttributeValuePair>, Long> currentMax = candidates.entrySet()
				.stream()
				.max((e1, e2) -> e1.getValue().compareTo(e2.getValue()))
				.orElse(null);
		
		if (max == null || currentMax != null && max.getValue() < currentMax.getValue()) {
			max = currentMax;
		}
		return max;
	}

	private List<List<AttributeValuePair>> filterCombination(List<List<AttributeValuePair>> thiz, List<String> combo) {
		List<List<AttributeValuePair>> thizCombo = thiz
				.stream()
				.map(list -> list
						.stream()
						.filter(pair -> combo.contains(pair.getAttributeName()))
						.collect(Collectors.toList()))
				.collect(Collectors.toList());
		return thizCombo;
	}

	private List<List<AttributeValuePair>> filterDistinctCombination(List<List<AttributeValuePair>> other,
			List<String> combo) {
		List<List<AttributeValuePair>> otherCombo = other
				.stream()
				.map(list -> list
						.stream()
						.filter(pair -> combo.contains(pair.getAttributeName()))
						.collect(Collectors.toList()))
				.distinct()
				.collect(Collectors.toList());
		return otherCombo;
	}

	private Map<List<AttributeValuePair>, Long> getCandidates(List<List<AttributeValuePair>> thizCombo,
			List<List<AttributeValuePair>> otherCombo) {
		Map<List<AttributeValuePair>, Long> candidates;
		candidates = thizCombo
			.stream()
			.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
			.entrySet()
			.stream()
			.filter(e -> !otherCombo.contains(e.getKey()))
			.collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
		return candidates;
	}

	private Map<List<AttributeValuePair>, Long> handlePossibleConflict(
			List<Map.Entry<String, List<List<AttributeValuePair>>>> subtables,
			Map.Entry<String, List<List<AttributeValuePair>>> subtable, List<List<AttributeValuePair>> thizCombo) {
		Map<List<AttributeValuePair>, Long> candidates = thizCombo
				.stream()
				.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
				.entrySet()
				.stream()
				.filter(e -> subtables
						.stream()
						.filter(entry -> !entry.getKey().equals(subtable.getKey()))
						.map(entry -> entry.getValue().stream().filter(list -> list.equals(e.getKey())).count())
						.allMatch(c -> c <= e.getValue()))
				.collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
		return candidates;
	}

}
