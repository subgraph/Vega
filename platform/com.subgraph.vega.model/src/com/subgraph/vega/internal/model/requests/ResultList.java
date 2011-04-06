package com.subgraph.vega.internal.model.requests;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import com.subgraph.vega.api.model.requests.IRequestLogRecord;

public class ResultList implements List<IRequestLogRecord> {

	private final List<RequestLogRecord> records;

	ResultList(List<RequestLogRecord> records) {
		this.records = records;
	}

	@Override
	public boolean add(IRequestLogRecord e) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void add(int index, IRequestLogRecord element) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addAll(Collection<? extends IRequestLogRecord> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addAll(int index, Collection<? extends IRequestLogRecord> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		records.clear();
	}

	@Override
	public boolean contains(Object o) {
		return records.contains(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return records.containsAll(c);
	}

	@Override
	public IRequestLogRecord get(int index) {
		return records.get(index);
	}

	@Override
	public int indexOf(Object o) {
		return records.indexOf(o);
	}

	@Override
	public boolean isEmpty() {
		return records.isEmpty();
	}

	@Override
	public Iterator<IRequestLogRecord> iterator() {
		return new Iterator<IRequestLogRecord>() {
			final Iterator<RequestLogRecord> it = records.iterator();
			@Override
			public boolean hasNext() {
				return it.hasNext();
			}

			@Override
			public IRequestLogRecord next() {
				return it.next();
			}

			@Override
			public void remove() {
				it.remove();
			}
		};
	}

	@Override
	public int lastIndexOf(Object o) {
		return records.lastIndexOf(o);
	}

	@Override
	public ListIterator<IRequestLogRecord> listIterator() {
		return listIterator(0);
	}

	@Override
	public ListIterator<IRequestLogRecord> listIterator(final int index) {
		return new ListIterator<IRequestLogRecord>() {
			final ListIterator<RequestLogRecord> it = records.listIterator(index);
			@Override
			public void add(IRequestLogRecord r) {
				throw new UnsupportedOperationException();
			}

			@Override
			public boolean hasNext() {
				return it.hasNext();
			}

			@Override
			public boolean hasPrevious() {
				return it.hasPrevious();
			}

			@Override
			public IRequestLogRecord next() {
				return it.next();
			}

			@Override
			public int nextIndex() {
				return it.nextIndex();
			}

			@Override
			public IRequestLogRecord previous() {
				return it.previous();
			}

			@Override
			public int previousIndex() {
				return it.previousIndex();
			}

			@Override
			public void remove() {
				it.remove();
			}

			@Override
			public void set(IRequestLogRecord r) {
				throw new UnsupportedOperationException();
			}
		};
	}

	@Override
	public boolean remove(Object o) {
		throw new UnsupportedOperationException();
	}

	@Override
	public IRequestLogRecord remove(int index) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public IRequestLogRecord set(int index, IRequestLogRecord element) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int size() {
		return records.size();
	}

	@Override
	public List<IRequestLogRecord> subList(int fromIndex, int toIndex) {
		return new ResultList(records.subList(fromIndex, toIndex));
	}

	@Override
	public Object[] toArray() {
		return records.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return records.toArray(a);
	}
}
