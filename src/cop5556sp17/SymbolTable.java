package cop5556sp17;

import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Stack;
import cop5556sp17.AST.Dec;


public class SymbolTable {

	// TODO add fields
	int current_scope;
	int next_scope;
	Stack<Integer> scope_stack;
	Map<String, LinkedList<decScopePair>> map;

	/**
	 * to be called when block entered
	 */
	public void enterScope() {
		current_scope = ++next_scope;
		scope_stack.push(current_scope);
	}

	/**
	 * leaves scope
	 */
	public void leaveScope() {
		scope_stack.pop();

		if (scope_stack.size() != 0) {
			current_scope = scope_stack.peek();
		}
	}

	public boolean insert(String ident, Dec dec) {

		decScopePair entry = new decScopePair(current_scope, dec);
		if (map.containsKey(ident)) {
			LinkedList<decScopePair> list = map.get(ident);
			for (decScopePair c : list) {
				if (c.equals(entry)) {
					return false;
				}
			}
			list.addFirst(entry);
		} else {
			LinkedList<decScopePair> list = new LinkedList<>();
			list.addFirst(entry);
			map.put(ident, list);
		}

		return true;
	}

	public Dec lookup(String ident) {

		if (map.containsKey(ident)) {

			LinkedList<decScopePair> decList = map.get(ident);
			List<Integer> list = new ArrayList<Integer>(scope_stack);
			Collections.reverse(list);

			for (Integer latestDeclaredScope : list) {

				for (decScopePair c : decList) {

					if (c.scope == latestDeclaredScope) {
						return c.dec;
					}
				}
			}
			return null;

		} else {
			return null;
		}
	}

	public SymbolTable() {
		map = new HashMap<>();
		scope_stack = new Stack<Integer>();
		scope_stack.push(0);
	}

	@Override
	public String toString() {
		// TODO: IMPLEMENT THIS
		StringBuilder sb = new StringBuilder();

		Iterator <Map.Entry<String, LinkedList<decScopePair>>> it = map.entrySet().iterator();

		while (it.hasNext())
		{
			int count=0;

			LinkedList<decScopePair> temp =it.next().getValue();

			for (decScopePair dsp : temp)
			{
				sb.append(dsp.toString());

			}

			sb.append("\n");
		}

		return sb.toString();
	}

	class decScopePair {
		int scope;
		Dec dec;

		decScopePair(int scope, Dec d) {
			this.scope = scope;
			this.dec = d;
		}

		@Override
		public int hashCode() {
			return (String.valueOf(scope)).hashCode();
		}

		@Override
		public boolean equals(Object obj) {

			decScopePair second;

			if (!(obj instanceof decScopePair)) {
				return false;
			}
			else
				second = (decScopePair) obj;

			if (this == obj) {
				return true;
			}

			if (!this.dec.equals(second.dec)) {
				return false;
			}

			if (this.scope != second.scope) {
				return false;
			}

			return true;
		}
	}



}
