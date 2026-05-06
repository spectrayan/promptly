package com.promptly.scanner.infrastructure.persistence.r2dbc.converter;

import com.promptly.scanner.domain.model.Finding;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Wrapper type for {@code List<Finding>} used in R2DBC entities.
 * <p>
 * Allows R2DBC converters to target this specific type precisely,
 * handling JSON serialization via the Spring-managed {@code ObjectMapper}
 * in {@link FindingListReadingConverter} / {@link FindingListWritingConverter}.
 *
 * @see FindingListReadingConverter
 * @see FindingListWritingConverter
 */
public class FindingList implements Iterable<Finding> {

    private final List<Finding> findings;

    public FindingList(List<Finding> findings) {
        this.findings = findings != null ? findings : new ArrayList<>();
    }

    public FindingList() {
        this(new ArrayList<>());
    }

    public List<Finding> toList() {
        return findings;
    }

    public int size() {
        return findings.size();
    }

    public boolean isEmpty() {
        return findings.isEmpty();
    }

    @Override
    public Iterator<Finding> iterator() {
        return findings.iterator();
    }

    @Override
    public String toString() {
        return findings.toString();
    }
}
