package de.uni_koblenz.jgralab.tools.grapheditor.editor.domainhandling;

import java.util.Map;

import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.widgets.Text;

import de.uni_koblenz.jgralab.Record;
import de.uni_koblenz.jgralab.exception.GraphException;
import de.uni_koblenz.jgralab.impl.RecordImpl;
import de.uni_koblenz.jgralab.schema.BasicDomain;
import de.uni_koblenz.jgralab.schema.DoubleDomain;
import de.uni_koblenz.jgralab.schema.EnumDomain;
import de.uni_koblenz.jgralab.schema.IntegerDomain;
import de.uni_koblenz.jgralab.schema.LongDomain;
import de.uni_koblenz.jgralab.schema.RecordDomain;
import de.uni_koblenz.jgralab.schema.RecordDomain.RecordComponent;
import de.uni_koblenz.jgralab.schema.StringDomain;

/**
 * Collection of convenience methods to deal with JGraLab domains
 * 
 * @author kheckelmann
 * 
 */
public class DomainEditorUtil {

	/**
	 * Add a VerifyListener to a text field, depending on the domain it displays
	 * 
	 * @param dom
	 *            the BasicDomain the listener should verify values for
	 * @param text
	 *            the text field to add the listener for the given domain
	 */
	public static void addVerifyListener(BasicDomain dom, Text text) {
		if (dom instanceof IntegerDomain) {
			text.addVerifyListener(new VerifyListener() {
				@Override
				public void verifyText(VerifyEvent e) {
					if (e.character == '\b') {
						return;
					}
					String input = ((Text) e.widget).getText() + e.text;
					try {
						Integer.parseInt(input);
					} catch (NumberFormatException ex) {
						e.doit = false;
					}
				}
			});
		}
		// LongDomain
		else if (dom instanceof LongDomain) {
			text.addVerifyListener(new VerifyListener() {
				@Override
				public void verifyText(VerifyEvent e) {
					if (e.character == '\b') {
						return;
					}
					String input = ((Text) e.widget).getText() + e.text;
					try {
						Long.parseLong(input);
					} catch (NumberFormatException ex) {
						e.doit = false;
					}
				}
			});
		}
		// DoubleDomain
		else if (dom instanceof DoubleDomain) {
			text.addVerifyListener(new VerifyListener() {
				@Override
				public void verifyText(VerifyEvent e) {
					if (e.character == '\b') {
						return;
					}
					String input = ((Text) e.widget).getText() + e.text;
					try {
						Double.parseDouble(input);
					} catch (NumberFormatException ex) {
						e.doit = false;
					}
				}
			});
		}
	}

	/**
	 * Creates a new Enum const for the given EnumDomain and value
	 * 
	 * @param domain
	 *            the EnumDomain to create a const of
	 * @param value
	 *            a String value representing the enum const
	 * @return the created enum const
	 */
	public static Object createEnum(EnumDomain domain, Object value) {
		for (String cn : domain.getConsts()) {
			if (cn.equals(value)) {
				return cn;
			}
		}
		throw new GraphException("No such enum constant '" + value
				+ "' in EnumDomain " + domain);
	}

	/**
	 * Creates a new Record for the RecordDomain with the given components
	 * 
	 * @param domain
	 *            the RecordDomain to crate a new Record for
	 * @param compVal
	 *            the components of the Record as map
	 * @return the created Record
	 */
	public static Record createRecord(RecordDomain domain,
			Map<String, Object> compVal) {
		RecordImpl record = RecordImpl.empty();
		for (RecordComponent c : domain.getComponents()) {
			assert (compVal.containsKey(c.getName()));
			record = record.plus(c.getName(), compVal.get(c.getName()));
		}
		return record;
	}

	/**
	 * Parses a text into a value depending on the given domain, e.g. a
	 * LongDomain results in a long value
	 * 
	 * @param text
	 *            the string to parse
	 * @param domain
	 *            the BasicDomain indicating the type
	 * @return the parsed value
	 */
	public static Object parseDomainValue(String text, BasicDomain domain) {
		if (domain instanceof StringDomain) {
			return text;
		}
		if (text.equals("")) {
			text = domain.getInitialValue();
		}
		if (domain instanceof IntegerDomain) {
			return Integer.parseInt(text);
		} else if (domain instanceof LongDomain) {
			return Long.parseLong(text);
		} else if (domain instanceof DoubleDomain) {
			return Double.parseDouble(text);
		}
		return null;
	}
}
