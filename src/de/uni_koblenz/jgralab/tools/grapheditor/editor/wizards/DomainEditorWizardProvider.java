package de.uni_koblenz.jgralab.tools.grapheditor.editor.wizards;

import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.pcollections.PCollection;
import org.pcollections.PMap;

import de.uni_koblenz.jgralab.Record;
import de.uni_koblenz.jgralab.schema.BasicDomain;
import de.uni_koblenz.jgralab.schema.BooleanDomain;
import de.uni_koblenz.jgralab.schema.CollectionDomain;
import de.uni_koblenz.jgralab.schema.Domain;
import de.uni_koblenz.jgralab.schema.EnumDomain;
import de.uni_koblenz.jgralab.schema.MapDomain;
import de.uni_koblenz.jgralab.schema.RecordDomain;

/**
 * Utility class for opening the right wizard editor for a given domain
 * 
 * @author kheckelmann
 * 
 */
public class DomainEditorWizardProvider {

	public static Object getEditResult(Domain dom, Object value) {
		if (dom instanceof BooleanDomain) {
			return getEditBooleanWizardResult((BooleanDomain) dom,
					(Boolean) value);
		} else if (dom instanceof CollectionDomain) {
			return getEditCollectionWizardResult((CollectionDomain) dom,
					(PCollection<?>) value);
		} else if (dom instanceof EnumDomain) {
			return getEditEnumWizardResult((EnumDomain) dom, value);
		} else if (dom instanceof BasicDomain) {
			return getEditBasicWizardResult((BasicDomain) dom, value);
		} else if (dom instanceof MapDomain) {
			return getEditMapWizardResult((MapDomain) dom, (PMap<?, ?>) value);
		} else if (dom instanceof RecordDomain) {
			return getEditRecordWizardResult((RecordDomain) dom, (Record) value);
		}

		return null;
	}

	private static Object getEditCollectionWizardResult(
			CollectionDomain coldom, PCollection<?> value) {
		EditCollectionDomainWizard wiz = new EditCollectionDomainWizard(coldom,
				(PCollection<?>) value);
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.getShell();
		WizardDialog dialog = new WizardDialog(shell, wiz);
		dialog.open();
		return wiz.getValue();
	}

	private static Object getEditEnumWizardResult(EnumDomain dom, Object value) {
		EditEnumDomainWizard wiz = new EditEnumDomainWizard(dom, value);
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.getShell();
		WizardDialog dialog = new WizardDialog(shell, wiz);
		dialog.open();
		return wiz.getValue();
	}

	private static Object getEditBasicWizardResult(BasicDomain dom, Object value) {
		EditBasicDomainWizard wiz = new EditBasicDomainWizard(dom, value);
		Shell parentShell = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getShell();
		WizardDialog dialog = new WizardDialog(parentShell, wiz);
		dialog.open();
		return wiz.getValue();
	}

	private static Object getEditMapWizardResult(MapDomain dom, PMap<?, ?> value) {
		EditMapDomainWizard wiz = new EditMapDomainWizard(dom, value);
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.getShell();
		WizardDialog dialog = new WizardDialog(shell, wiz);
		dialog.open();
		return wiz.getValue();
	}

	private static Object getEditRecordWizardResult(RecordDomain dom,
			Record value) {
		EditRecordDomainWizard wiz = new EditRecordDomainWizard(dom, value);
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.getShell();
		WizardDialog dialog = new WizardDialog(shell, wiz);
		dialog.open();
		return wiz.getValue();
	}

	private static Object getEditBooleanWizardResult(BooleanDomain dom,
			Boolean value) {
		EditBooleanDomainWizard wiz = new EditBooleanDomainWizard(dom, value);
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.getShell();
		WizardDialog dialog = new WizardDialog(shell, wiz);
		dialog.open();
		return wiz.getValue();
	}
}
