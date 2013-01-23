package de.uni_koblenz.jgralab.tools.grapheditor.properties;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import de.uni_koblenz.jgralab.GraphElement;
import de.uni_koblenz.jgralab.schema.Attribute;
import de.uni_koblenz.jgralab.schema.BasicDomain;
import de.uni_koblenz.jgralab.schema.BooleanDomain;
import de.uni_koblenz.jgralab.schema.CollectionDomain;
import de.uni_koblenz.jgralab.schema.EnumDomain;
import de.uni_koblenz.jgralab.schema.MapDomain;
import de.uni_koblenz.jgralab.schema.RecordDomain;
import de.uni_koblenz.jgralab.tools.grapheditor.editor.GraphEditor;
import de.uni_koblenz.jgralab.tools.grapheditor.editor.domainhandling.DomainEditorUtil;
import de.uni_koblenz.jgralab.tools.grapheditor.operations.OperationProvider;

/**
 * Editing support for basic domains in attribute table
 * 
 * @author kheckelmann
 * 
 */
public class BasicDomainEditingSupport extends EditingSupport {

	private final TableViewer tableViewer;
	private final GraphEditor editor;

	public BasicDomainEditingSupport(TableViewer viewer, GraphEditor v) {
		super(viewer);
		this.tableViewer = viewer;
		this.editor = v;
	}

	/**
	 * Editors depend on domains
	 * 
	 * @param element
	 * @return
	 */
	@Override
	protected CellEditor getCellEditor(Object element) {
		AttributeTableRow row = (AttributeTableRow) element;
		Attribute a = row.attribute;
		if (a.getDomain() instanceof CollectionDomain) {
			return null;
		} else if (a.getDomain() instanceof MapDomain) {
			return null;
		} else if (a.getDomain() instanceof RecordDomain) {
			return null;
		} else if (a.getDomain() instanceof EnumDomain) {
			Object[] objectarray = ((EnumDomain) a.getDomain()).getConsts()
					.toArray();
			String[] stringarray = new String[objectarray.length];
			for (int i = 0; i < objectarray.length; i++) {
				stringarray[i] = (String) objectarray[i];
			}
			ComboBoxCellEditor cedit = new ComboBoxCellEditor(
					this.tableViewer.getTable(), stringarray);
			return cedit;
		} else if (a.getDomain() instanceof BooleanDomain) {
			return new CheckboxCellEditor(this.tableViewer.getTable());
		}

		TextCellEditor edi = new TextCellEditor(this.tableViewer.getTable());
		return edi;
	}

	/**
	 * Complex domain can not become edited directly
	 * 
	 * @param element
	 * @return
	 */
	@Override
	protected boolean canEdit(Object element) {
		AttributeTableRow row = (AttributeTableRow) element;
		Attribute a = row.attribute;
		if (a.getDomain() instanceof CollectionDomain) {
			return false;
		}
		if (a.getDomain() instanceof MapDomain) {
			return false;
		}
		if (a.getDomain() instanceof RecordDomain) {
			return false;
		}
		return true;
	}

	/**
	 * Return the value displayed in the edit field
	 * 
	 * @param element
	 * @return
	 */
	@Override
	protected Object getValue(Object element) {
		AttributeTableRow row = (AttributeTableRow) element;
		Attribute a = row.attribute;
		GraphElement<?, ?> grel = (GraphElement<?, ?>) row.element;
		Object value = grel.getAttribute(a.getName());
		// - BooleanDomain
		if (row.attribute.getDomain() instanceof BooleanDomain) {
			return value;
		}
		// - EnumDomain
		else if (row.attribute.getDomain() instanceof EnumDomain) {
			if (value == null) {
				return -1;
			}
			EnumDomain enumDom = (EnumDomain) row.attribute.getDomain();
			int i = 0;
			for (int j = 0; i < enumDom.getConsts().size(); i++) {
				if (value.toString().equals(enumDom.getConsts().get(j))) {
					i = j;
					break;
				}
			}
			return i;
		}
		// - StringDomain, IntegerDomain, LongDomain or DoubleDomain
		else {
			if (value == null) {
				return "";
			}
			return value + "";
		}
	}

	/**
	 * Set the value entered in the edit field
	 * 
	 * @param element
	 * @param value
	 */
	@Override
	protected void setValue(Object element, Object value) {
		AttributeTableRow row = (AttributeTableRow) element;
		Attribute a = row.attribute;
		// - BooleanDomain
		if (a.getDomain() instanceof BooleanDomain) {
			OperationProvider.doChangeAttributeOperation(row.element, a, value,
					this.editor, this.tableViewer);
		}
		// - EnumDomain
		else if (a.getDomain() instanceof EnumDomain) {
			Integer val = (Integer) value;
			if (val == -1) {
				OperationProvider.doChangeAttributeOperation(row.element, a,
						null, this.editor, this.tableViewer);
			} else {
				String constval = ((EnumDomain) a.getDomain()).getConsts().get(
						val);
				OperationProvider.doChangeAttributeOperation(row.element, a,
						DomainEditorUtil.createEnum((EnumDomain) a.getDomain(),
								constval), this.editor, this.tableViewer);
			}
		}
		// - StringDomain, IntegerDomain, LongDomain or DoubleDomain
		else {
			try {
				OperationProvider.doChangeAttributeOperation(row.element, a,
						DomainEditorUtil.parseDomainValue((String) value,
								(BasicDomain) a.getDomain()), this.editor,
						this.tableViewer);
			} catch (NumberFormatException ex) {
				Shell shell = PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getShell();
				MessageBox mb = new MessageBox(shell, SWT.ERROR);
				mb.setText("Error while parsing a number:");
				mb.setMessage("Can't parse " + value + " to "
						+ a.getDomain().getQualifiedName() + ".");
				mb.open();
				return;
			}
		}
	}

}
